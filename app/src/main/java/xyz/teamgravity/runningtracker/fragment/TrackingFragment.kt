package xyz.teamgravity.runningtracker.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import xyz.teamgravity.runningtracker.R
import xyz.teamgravity.runningtracker.databinding.FragmentTrackingBinding
import xyz.teamgravity.runningtracker.helper.util.Helper
import xyz.teamgravity.runningtracker.service.Polyline
import xyz.teamgravity.runningtracker.service.TrackingService
import xyz.teamgravity.runningtracker.viewmodel.RunViewModel

@AndroidEntryPoint
class TrackingFragment : Fragment() {
    companion object {
        private const val POLYLINE_COLOR = Color.RED
        private const val POLYLINE_WIDTH = 8F
        private const val MAP_ZOOM = 17F
    }

    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!

    private val runViewModel by viewModels<RunViewModel>()

    private var map: GoogleMap? = null
    private var menu: Menu? = null

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()
    private var currentTimeInMillis = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.apply {

            mapView.getMapAsync { googleMap ->
                map = googleMap
                addAllPolylines()
            }

            subscribeToObservers()

            activity?.let { activity ->
                startB.setOnClickListener {
                    toggleRun(activity)
                }
            }
        }
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

    private fun toggleRun(activity: FragmentActivity) {
        if (isTracking) {
            commandService(activity, TrackingService.ACTION_PAUSE)
        } else {
            menu?.getItem(0)?.isVisible = true
            commandService(activity, TrackingService.ACTION_START_OR_RESUME)
        }
    }

    // update tracking
    private fun updateTracking(isTracking: Boolean) {
        if (isAdded) {
            binding.apply {
                this@TrackingFragment.isTracking = isTracking
                if (isTracking) {
                    startB.text = resources.getString(R.string.stop)
                    finishB.visibility = View.GONE
                } else {
                    menu?.getItem(0)?.isVisible = true
                    startB.text = resources.getString(R.string.start)
                    finishB.visibility = View.VISIBLE
                }
            }
        }
    }

    // moves screen(camera) to the user when location changes
    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(pathPoints.last().last(), MAP_ZOOM))
        }
    }

    // configuration changes add all polyline again
    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
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
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)

            map?.addPolyline(polyLineOptions)
        }
    }

    // start service
    private fun commandService(activity: FragmentActivity, action: String) =
        Intent(activity, TrackingService::class.java).also { intent ->
            intent.action = action
            activity.startService(intent)
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
            R.id.cancel_tracking ->
                activity?.let {
                    showCancelDialog(it)
                }
        }
        return super.onOptionsItemSelected(item)
    }

    // show cancel dialog
    private fun showCancelDialog(activity: FragmentActivity) {
        MaterialAlertDialogBuilder(activity, R.style.AlertDialogTheme)
            .setTitle(R.string.cancel_run)
            .setMessage(R.string.wanna_cancel_run)
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton(R.string.yes) { _, _ ->
                stopRun(activity)
            }.setNegativeButton(R.string.no) { _, _ ->

            }.show()
    }

    // stop run
    private fun stopRun(activity: FragmentActivity) {
        commandService(activity, TrackingService.ACTION_STOP)
        findNavController().navigate(TrackingFragmentDirections.actionTrackingFragmentToRunFragment())
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