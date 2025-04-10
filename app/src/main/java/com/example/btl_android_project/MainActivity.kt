package com.example.btl_android_project

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.btl_android_project.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mealSpinner: Spinner
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setUpActionBar()
        setUpBottomNavigation()
        setUpFab()
        initMealDropdown()
    }

    // Declare the launcher at the top of your Activity/Fragment:
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission(),
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            // FCM SDK (and your app) can post notifications.
//        } else {
//            // TODO: Inform user that that your app will not show notifications.
//        }
//    }

//    private fun askNotificationPermission() {
//        // This is only necessary for API level >= 33 (TIRAMISU)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
//                PackageManager.PERMISSION_GRANTED
//            ) {
//                // FCM SDK (and your app) can post notifications.
//            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
//                // TODO: display an educational UI explaining to the user the features that will be enabled
//                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
//                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
//                //       If the user selects "No thanks," allow the user to continue without notifications.
//                println("Permission rationale")
//            } else {
//                // Directly ask for the permission
//                println("Requesting permission")
//                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
//            }
//        }
//
//    }
//
//    fun logRegToken() {
//        Firebase.messaging.getToken().addOnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//                return@addOnCompleteListener
//            }
//
//            val token = task.result
//
//            // Log and toast
//            val msg = "FCM Registration token: $token"
//            Log.d(TAG, token)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    fun runtimeEnableAutoInit() {
//        Firebase.messaging.isAutoInitEnabled = true
//    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setUpBottomNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController?.let {
            binding.navigation.setupWithNavController(it)
        }
    }

    private fun setUpFab(){
        binding.fab.setOnClickListener {
            val action = R.id.action_global_logItemListDialogFragment
            navController?.navigate(action)
        }

        val destinationsToShowFab = setOf(R.id.dashboardFragment)

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in destinationsToShowFab) {
                binding.fab.show()
            } else {
                binding.fab.hide()
            }
        }
    }

    fun showBackButton(show: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(show)
    }

    fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    fun showMealDropdown(show: Boolean) {
        if (show) {
            if (mealSpinner.parent == null) {
                binding.toolbar.addView(mealSpinner)
            }
        } else {
            binding.toolbar.removeView(mealSpinner)
        }
    }

    fun initMealDropdown() {
        mealSpinner = Spinner(this)
        mealSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf(R.string.select_a_meal, R.string.breakfast, R.string.lunch, R.string.dinner, R.string.snack).map { getString(it) }
        )

        val layoutParams = Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
        }
        mealSpinner.layoutParams = layoutParams
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController(R.id.nav_host_fragment).navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    companion object {
        private const val TAG = "MainActivity"
    }
}