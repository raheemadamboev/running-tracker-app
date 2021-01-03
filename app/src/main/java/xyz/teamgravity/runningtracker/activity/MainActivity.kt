package xyz.teamgravity.runningtracker.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import xyz.teamgravity.runningtracker.NavGraphDirections
import xyz.teamgravity.runningtracker.R
import xyz.teamgravity.runningtracker.databinding.ActivityMainBinding
import xyz.teamgravity.runningtracker.service.TrackingService

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        navigateTrackingFragment(intent)

        // find nav controller in activity
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.findNavController()

        // set up navigation bottom
        binding.bottomNavigationView.setupWithNavController(navController)

        // in order to hide bottom navigation view from certain fragments
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.settingsFragment, R.id.runFragment, R.id.statisticsFragment ->
                    binding.bottomNavigationView.visibility = View.VISIBLE
                else ->
                    binding.bottomNavigationView.visibility = View.GONE
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateTrackingFragment(intent)
    }

    private fun navigateTrackingFragment(intent: Intent?) {
        if (intent?.action == TrackingService.ACTION_SHOW_TRACKING_FRAGMENT) {
            navController.navigate(NavGraphDirections.actionGlobalTrackingFragment())
        }
    }
}