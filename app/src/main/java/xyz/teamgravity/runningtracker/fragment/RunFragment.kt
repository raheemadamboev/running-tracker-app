package xyz.teamgravity.runningtracker.fragment

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import xyz.teamgravity.runningtracker.R
import xyz.teamgravity.runningtracker.databinding.FragmentRunBinding
import xyz.teamgravity.runningtracker.helper.adapter.RunAdapter
import xyz.teamgravity.runningtracker.helper.util.Helper
import xyz.teamgravity.runningtracker.viewmodel.RunViewModel

@AndroidEntryPoint
class RunFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    companion object {
        private const val LOCATION_PERMISSIONS = 1
    }

    private var _binding: FragmentRunBinding? = null
    private val binding get() = _binding!!

    private val runViewModel by viewModels<RunViewModel>()

    private lateinit var adapter: RunAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRunBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let { activity ->
            requestPermissions(activity)
            recyclerView()
        }

        binding.runB.setOnClickListener {
            findNavController().navigate(RunFragmentDirections.actionRunFragmentToTrackingFragment())
        }
    }

    private fun recyclerView() {
        adapter = RunAdapter()
        binding.recyclerView.adapter = adapter

        runViewModel.getAllRunsSortedByDate().observe(viewLifecycleOwner) {
            adapter.submitList(it)
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
            activity?.let { activity ->
                requestPermissions(activity)
            }
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