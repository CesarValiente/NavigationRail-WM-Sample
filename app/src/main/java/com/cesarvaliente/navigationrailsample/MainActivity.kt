package com.cesarvaliente.navigationrailsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.window.FoldingFeature
import androidx.window.WindowInfoRepo
import androidx.window.WindowLayoutInfo
import com.cesarvaliente.navigationrailsample.databinding.ActivityMainBinding
import com.cesarvaliente.navigationrailsample.databinding.ActivityMainNavRailBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var windowInfoRepo: WindowInfoRepo
    private val scope = MainScope()

    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var navRailView: NavigationRailView
    private lateinit var navController: NavController

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        windowInfoRepo = WindowInfoRepo.create(this)
        adjustUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun adjustUI() {
        scope.launch {
            windowInfoRepo.windowLayoutInfo
                .collect { value ->
                    showUI(value)
                    setupNavigation()
                }
        }
    }

    private fun setupNavigation() {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun showUI(windowLayoutInfo: WindowLayoutInfo) {
        if (windowLayoutInfo.displayFeatures.isEmpty()) {
            showBottomNavigation()
        } else {
            (windowLayoutInfo.displayFeatures.component1() as? FoldingFeature)?.apply {
                if (isSeparating) {
                    showNavigationRail()
                }
            }
        }
    }

    private fun showBottomNavigation() {
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavView = binding.bottomNavView

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        bottomNavView.setupWithNavController(navController)
    }

    private fun showNavigationRail() {
        val binding = ActivityMainNavRailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navRailView = binding.navigationRail

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        navRailView.setupWithNavController(navController)
    }
}