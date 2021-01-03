package xyz.teamgravity.runningtracker.fragment

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import xyz.teamgravity.runningtracker.R
import xyz.teamgravity.runningtracker.databinding.FragmentSetUpBinding
import xyz.teamgravity.runningtracker.helper.util.Helper

class SetUpFragment : Fragment() {

    private var _binding: FragmentSetUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSetUpBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.continueB.setOnClickListener {
            findNavController().navigate(SetUpFragmentDirections.actionSetUpFragmentToRunFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}