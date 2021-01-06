package xyz.teamgravity.runningtracker.fragment.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import xyz.teamgravity.runningtracker.R
import xyz.teamgravity.runningtracker.databinding.FragmentTrackingBinding
import xyz.teamgravity.runningtracker.fragment.dialog.CancelTrackingDialog
import xyz.teamgravity.runningtracker.fragment.dialog.GPSDialog
import xyz.teamgravity.runningtracker.helper.constants.MapGoogle
import xyz.teamgravity.runningtracker.helper.constants.Preferences
import xyz.teamgravity.runningtracker.helper.util.Helper
import xyz.teamgravity.runningtracker.model.RunModel
import xyz.teamgravity.runningtracker.service.Polyline
import xyz.teamgravity.runningtracker.service.TrackingService
import xyz.teamgravity.runningtracker.viewmodel.RunViewModel
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(), CancelTrackingDialog.OnCancelTrackingListener, GPSDialog.OnGPSListener {
    companion object {
        private const val CANCEL_DIALOG = "cancelDialog"
        private const val GPS_DIALOG = "gpsDialog"
    }

    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!

    private val runViewModel by viewModels<RunViewModel>()

    @Inject
    lateinit var shp: SharedPreferences

    private lateinit var locationManager: LocationManager

    private var map: GoogleMap? = null
    private var menu: Menu? = null

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()
    private var currentTimeInMillis = 0L
    private var weight = 1F

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        binding.mapView.onCreate(savedInstanceState)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.apply {

            if (savedInstanceState != null) {
                val cancelDialog = parentFragmentManager.findFragmentByTag(CANCEL_DIALOG) as CancelTrackingDialog?
                cancelDialog?.listener = this@TrackingFragment
                val gpsDialog = parentFragmentManager.findFragmentByTag(GPS_DIALOG) as GPSDialog?
                gpsDialog?.listener = this@TrackingFragment
            }

            activity?.let {
                lateInIt(it)
                googleMap()
                button(it)
                subscribeToObservers()
            }
        }
    }

    private fun lateInIt(activity: FragmentActivity) {
        weight = shp.getFloat(Preferences.USER_WEIGHT, 1F)
        locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    // set google map
    private fun googleMap() {
        binding.mapView.getMapAsync { googleMap ->
            map = googleMap
            addAllPolylines()
        }
    }

    private fun button(activity: FragmentActivity) {
        onRun(activity)
        onFinish(activity)
    }

    // get data from service, subscribe
    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner) {
            updateTracking(it)
        }

        TrackingService.pathPoints.observe(viewLifecycleOwner) {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        }

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner) {
            currentTimeInMillis = it
            binding.timerT.text = Helper.formatStopwatch(currentTimeInMillis, true)
        }
    }

    // zoom the map to see whole track
    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints) {
            for (position in polyline) {
                bounds.include(position)
            }
        }

        if (isAdded) {
            binding.apply {
                try {
                    map?.moveCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds.build(),
                            mapView.width,
                            mapView.height,
                            (mapView.height * 0.05F).toInt()
                        )
                    )
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
        }
    }

    // end run, service and save db
    private fun finishRun(activity: FragmentActivity) {
        map?.snapshot { bitmap ->
            var distanceInMeters = 0

            for (polyline in pathPoints) {
                distanceInMeters += Helper.calculatePolylineLength(polyline).toInt()
            }

            val averageSpeed = round((distanceInMeters / 1000F) / (currentTimeInMillis / 1000F / 60 / 60) * 10) / 10F
            val timestamp = System.currentTimeMillis()
            val caloriesBurned = ((distanceInMeters / 1000F) * weight).toInt()

            val run = RunModel(
                image = bitmap,
                averageSpeedInKmh = averageSpeed,
                distanceInMeters = distanceInMeters,
                caloriesBurned = caloriesBurned,
                timestamp = timestamp,
                duration = currentTimeInMillis
            )

            runViewModel.insert(run)
            Snackbar.make(activity.findViewById(R.id.parent_layout), R.string.run_saved, Snackbar.LENGTH_LONG).show()
            stopRun(activity)
        }
    }

    // run button
    private fun toggleRun(activity: FragmentActivity) {
        if (isTracking) {
            commandService(activity, TrackingService.ACTION_PAUSE)
        } else {

            // show enable gps dialog
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val dialog = GPSDialog()
                dialog.listener = this
                dialog.show(parentFragmentManager, GPS_DIALOG)
                return
            }

            menu?.getItem(0)?.isVisible = true
            commandService(activity, TrackingService.ACTION_START_OR_RESUME)
        }
    }

    // update isTracking, buttons from service
    private fun updateTracking(isTracking: Boolean) {
        if (isAdded) {
            binding.apply {
                this@TrackingFragment.isTracking = isTracking
                if (!isTracking && currentTimeInMillis > 0L) {
                    startB.text = resources.getString(R.string.start)
                    finishB.visibility = View.VISIBLE
                } else if (isTracking) {
                    startB.text = resources.getString(R.string.stop)
                    menu?.getItem(0)?.isVisible = true
                    finishB.visibility = View.GONE
                }
            }
        }
    }

    // moves screen(camera) to the user when location changes
    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(pathPoints.last().last(), MapGoogle.MAP_ZOOM))
        }
    }

    // configuration changes add all polyline again
    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(MapGoogle.POLYLINE_COLOR)
                .width(MapGoogle.POLYLINE_WIDTH)
                .addAll(polyline)

            map?.addPolyline(polylineOptions)
        }
    }

    // draw last two coordinates of line
    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polyLineOptions = PolylineOptions()
                .color(MapGoogle.POLYLINE_COLOR)
                .width(MapGoogle.POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)

            map?.addPolyline(polyLineOptions)
        }
    }

    // start service
    private fun commandService(activity: FragmentActivity, action: String) {
        val intent = Intent(activity, TrackingService::class.java)
        intent.action = action
        activity.startService(intent)
    }

    // stop run, kill service
    private fun stopRun(activity: FragmentActivity) {
        binding.timerT.text = resources.getString(R.string.countdown_extended)
        commandService(activity, TrackingService.ACTION_STOP)
        findNavController().navigate(TrackingFragmentDirections.actionTrackingFragmentToRunFragment())
    }

    // run/start button
    private fun onRun(activity: FragmentActivity) {
        binding.startB.setOnClickListener {
            toggleRun(activity)
        }
    }

    // finish button
    private fun onFinish(activity: FragmentActivity) {
        binding.finishB.setOnClickListener {
            zoomToSeeWholeTrack()
            finishRun(activity)
        }
    }

    // dialog positive button
    override fun onCancelTrackingPositiveClick() {
        activity?.let { stopRun(it) }
    }

    // enable gps dialog button
    override fun onGPSPositiveClick() {
        startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.tracking_fragment_menu, menu)
        this.menu = menu
    }

    // check if we run
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (currentTimeInMillis > 0L) {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cancel_tracking -> {
                val dialog = CancelTrackingDialog()
                dialog.listener = this
                dialog.show(parentFragmentManager, CANCEL_DIALOG)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        _binding?.mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        _binding?.mapView?.onPause()
    }

    override fun onStart() {
        super.onStart()
        _binding?.mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        _binding?.mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        _binding?.mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding?.mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        _binding?.mapView?.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}