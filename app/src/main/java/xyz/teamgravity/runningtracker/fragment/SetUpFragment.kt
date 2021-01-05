package xyz.teamgravity.runningtracker.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import xyz.teamgravity.runningtracker.R
import xyz.teamgravity.runningtracker.databinding.FragmentSetUpBinding
import xyz.teamgravity.runningtracker.helper.constants.Preferences
import javax.inject.Inject

@AndroidEntryPoint
class SetUpFragment : Fragment() {

    private var _binding: FragmentSetUpBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var shp: SharedPreferences

    @set:Inject
    var isSetUp = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSetUpBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (isSetUp) {
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.setUpFragment, true).build()
            findNavController().navigate(SetUpFragmentDirections.actionSetUpFragmentToRunFragment(), navOptions)
        }

        activity?.let { activity ->
            button(activity)
        }
    }

    private fun button(activity: FragmentActivity) {
        onSave(activity)
    }

    // save button
    private fun onSave(activity: FragmentActivity) {
        binding.apply {
            saveB.setOnClickListener {
                nameField.error = null
                weightField.error = null

                nameField.clearFocus()
                weightField.clearFocus()

                val name = nameField.editText?.text.toString().trim()
                val weight = weightField.editText?.text.toString().trim()

                if (name.isEmpty()) {
                    nameField.error = resources.getString(R.string.error_field)
                    nameField.requestFocus()
                    return@setOnClickListener
                }

                if (weight.isEmpty()) {
                    weightField.error = resources.getString(R.string.error_field)
                    weightField.requestFocus()
                    return@setOnClickListener
                }

                shp.edit()
                    .putString(Preferences.USER_NAME, name)
                    .putFloat(Preferences.USER_WEIGHT, weight.toFloat())
                    .putBoolean(Preferences.IS_SET_UP, true)
                    .apply()

                activity.findViewById<MaterialTextView>(R.id.toolbar_t).text = "Let's go $name"

                val navOptions = NavOptions.Builder().setPopUpTo(R.id.setUpFragment, true).build()
                findNavController().navigate(SetUpFragmentDirections.actionSetUpFragmentToRunFragment(), navOptions)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}