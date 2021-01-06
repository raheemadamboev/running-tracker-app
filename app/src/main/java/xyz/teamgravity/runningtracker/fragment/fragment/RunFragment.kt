package xyz.teamgravity.runningtracker.fragment.fragment

import android.Manifest
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import xyz.teamgravity.runningtracker.R
import xyz.teamgravity.runningtracker.databinding.FragmentRunBinding
import xyz.teamgravity.runningtracker.helper.adapter.RunAdapter
import xyz.teamgravity.runningtracker.helper.constants.Preferences
import xyz.teamgravity.runningtracker.helper.util.Helper
import xyz.teamgravity.runningtracker.viewmodel.RunSortType
import xyz.teamgravity.runningtracker.viewmodel.RunViewModel
import javax.inject.Inject

@AndroidEntryPoint
class RunFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    companion object {
        private const val LOCATION_PERMISSIONS = 1
    }

    private var _binding: FragmentRunBinding? = null
    private val binding get() = _binding!!

    private val runViewModel by viewModels<RunViewModel>()
    private val args by navArgs<RunFragmentArgs>()

    @Inject
    lateinit var adapter: RunAdapter

    @Inject
    lateinit var shp: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRunBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val isSetUp = shp.getBoolean(Preferences.IS_SET_UP, false)
        if (!args.isSetUp && !isSetUp) {
            findNavController().navigate(RunFragmentDirections.actionRunFragmentToSetUpFragment())
        }

        activity?.let {
            recyclerView()
            button()
            requestPermissions(it)
        }
    }

    private fun recyclerView() {
        binding.recyclerView.adapter = adapter

        runViewModel.runs.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun button() {
        onFilterSpinner()
        onRun()
    }

    // filter spinner
    private fun onFilterSpinner() {
        binding.filterSpinner.apply {
            when (runViewModel.sortType) {
                RunSortType.DATE -> setSelection(0)
                RunSortType.DURATION -> setSelection(1)
                RunSortType.DISTANCE -> setSelection(2)
                RunSortType.AVERAGE_SPEED -> setSelection(3)
                RunSortType.CALORIES_BURNED -> setSelection(4)
            }

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    when (position) {
                        0 -> runViewModel.sortRuns(RunSortType.DATE)
                        1 -> runViewModel.sortRuns(RunSortType.DURATION)
                        2 -> runViewModel.sortRuns(RunSortType.DISTANCE)
                        3 -> runViewModel.sortRuns(RunSortType.AVERAGE_SPEED)
                        4 -> runViewModel.sortRuns(RunSortType.CALORIES_BURNED)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }

    private fun onRun() {
        binding.runB.setOnClickListener {
            findNavController().navigate(RunFragmentDirections.actionRunFragmentToTrackingFragment())
        }
    }

    // request permission
    private fun requestPermissions(activity: FragmentActivity) {
        if (!Helper.hasLocationPermissions(activity)) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                EasyPermissions.requestPermissions(
                    this,
                    resources.getString(R.string.permission_reminder),
                    LOCATION_PERMISSIONS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    resources.getString(R.string.permission_reminder),
                    LOCATION_PERMISSIONS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
        }
    }

    // permission granted
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    // permission denied
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            activity?.let { requestPermissions(it) }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}