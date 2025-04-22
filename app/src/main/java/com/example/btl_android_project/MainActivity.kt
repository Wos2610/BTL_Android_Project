package com.example.btl_android_project

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
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
import com.example.btl_android_project.presentation.log_recipe.DetailIngredientFragmentDirections
import com.example.btl_android_project.presentation.log_recipe.DetailRecipeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mealSpinner: Spinner? = null
    private lateinit var endTextView: TextView
    private lateinit var saveButton: ImageView
    private lateinit var addButton: ImageView
    private lateinit var deleteButton: ImageView

    private var navController: NavController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setUpBottomNavigation()
        setUpActionBar()
        setUpFab()
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
        binding.toolbar.setBackgroundColor(getColor(R.color.dark_black))
        binding.toolbar.setTitleTextColor(getColor(R.color.white))
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)

        setSupportActionBar(binding.toolbar)

        showBackButton()
        setToolbarTitle()
        initMealDropdown()
        initEndTextToolbar()
        initSaveButton()
        initAddButton()
        initDeleteButton()
    }

    private fun setUpBottomNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController?.let {
            binding.navigation.setupWithNavController(it)
        }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.dashboardFragment-> {
                    binding.navigation.visibility = View.VISIBLE
                }
                else -> {
                    binding.navigation.visibility = View.GONE
                }
            }
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

    fun showBackButton() {
        var show = false
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            show = when (destination.id) {
                R.id.dashboardFragment-> false
                R.id.logItemListDialogFragment -> false
                else -> true
            }
            supportActionBar?.setDisplayHomeAsUpEnabled(show)
        }
    }

    fun setToolbarTitle() {
        var title = ""

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            title = when (destination.id) {
                R.id.dashboardFragment -> getString(R.string.app_name)
                R.id.logItemListDialogFragment -> getString(R.string.app_name)
                R.id.logAllFragment -> ""
                R.id.logRecipeFragment -> ""
                R.id.logMealFragment -> ""
                R.id.logFoodFragment -> ""
                R.id.newRecipeFragment -> getString(R.string.new_recipe)
                R.id.ingredientsFragment -> getString(R.string.new_recipe)
                R.id.createMealFragment -> getString(R.string.create_a_meal)
                R.id.detailIngredientFragment -> getString(R.string.ingredient_detail)
                R.id.ingredientsFragment-> getString(R.string.search_ingredient)
                R.id.detailRecipeFragment -> getString(R.string.recipe_detail)
                R.id.createFoodInformationFragment -> getString(R.string.create_food)
                R.id.createFoodNutritionFragment -> getString(R.string.create_food)
                R.id.logRecipeDiaryFragment -> getString(R.string.add_recipe)
                R.id.logFoodDiaryFragment -> getString(R.string.add_food)
                else -> ""
            }
            supportActionBar?.title = title
        }
    }

    fun initMealDropdown() {
        val mealOptions = listOf(
            R.string.select_a_meal,
            R.string.breakfast,
            R.string.lunch,
            R.string.dinner,
            R.string.snack
        ).map { getString(it) }

        if (mealSpinner == null) {
            mealSpinner = Spinner(this).apply {
                adapter = object : ArrayAdapter<String>(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    mealOptions
                ){
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val view = super.getView(position, convertView, parent)
                        (view as? TextView)?.setTextColor(getColor(R.color.white))
                        return view
                    }

                    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val view = super.getDropDownView(position, convertView, parent)
                        return view
                    }
                }

                val layoutParams = Toolbar.LayoutParams(
                    Toolbar.LayoutParams.WRAP_CONTENT,
                    Toolbar.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER
                }
                this.layoutParams = layoutParams
            }
        }

        val destinationsToShowMealDropdown = setOf(R.id.logAllFragment)

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in destinationsToShowMealDropdown) {
                showMealDropdown(true)
            } else {
                showMealDropdown(false)
            }
        }
    }

    fun showMealDropdown(show: Boolean) {
        if (show) {
            mealSpinner.let {
                if (it?.parent == null) {
                    binding.toolbar.addView(it)
                }
            }
        } else {
            mealSpinner?.let {
                binding.toolbar.removeView(it)
            }
        }
    }

    fun initEndTextToolbar(
        defaultText: String? = null,
        defaultClickListener: View.OnClickListener? = null
    ) {
        if (!::endTextView.isInitialized) {
            endTextView = TextView(this).apply {
                layoutParams = Toolbar.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.END
                    marginEnd = 8
                }
                setTextColor(getColor(R.color.white))
                textSize = 16f
                text = defaultText
                visibility = if (defaultText.isNullOrEmpty()) View.GONE else View.VISIBLE
                setOnClickListener(defaultClickListener)
            }
            binding.toolbar.addView(endTextView)
        }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.newRecipeFragment -> {
                    setEndTextToolbarVisibility(true)
                    setEndTextToolbarText(getString(R.string.next))
                }
                R.id.ingredientsFragment -> {
                    setEndTextToolbarVisibility(true)
                    setEndTextToolbarText(getString(R.string.next))
                }
                R.id.createFoodInformationFragment -> {
                    setEndTextToolbarVisibility(true)
                    setEndTextToolbarText(getString(R.string.next))
                }
                else -> {
                    setEndTextToolbarVisibility(false)
                }
            }
        }

    }

    fun setEndTextToolbarText(text: String) {
        if (::endTextView.isInitialized) {
            endTextView.text = text
        }
    }

    fun setEndTextToolbarVisibility(visible: Boolean) {
        if (::endTextView.isInitialized) {
            endTextView.visibility = if (visible) View.VISIBLE else View.GONE
        }
    }

    fun setEndTextClickListener(clickListener: View.OnClickListener?) {
        endTextView.setOnClickListener(clickListener)
    }

    fun initCheckEndButton(){

    }

    fun initSaveButton(
        defaultClickListener: View.OnClickListener? = null,
    ) {
        if (!::saveButton.isInitialized) {
            saveButton = ImageView(this).apply {
                layoutParams = Toolbar.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.END
                    marginEnd = 16
                }

                setImageResource(R.drawable.ic_check)

                val size = 100
                layoutParams.width = size
                layoutParams.height = size
                visibility = View.GONE
                setOnClickListener(defaultClickListener)

                setPadding(8, 8, 8, 8)
            }
            binding.toolbar.addView(saveButton)
        }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.detailIngredientFragment -> setSaveButtonVisibility(true)
                R.id.detailRecipeFragment -> setSaveButtonVisibility(true)
                R.id.createFoodNutritionFragment -> setSaveButtonVisibility(true)
                R.id.createMealFragment -> setSaveButtonVisibility(true)
                R.id.logRecipeDiaryFragment -> setSaveButtonVisibility(true)
                R.id.logFoodDiaryFragment -> setSaveButtonVisibility(true)
                else -> {
                    setSaveButtonVisibility(false)
                }
            }
        }
    }

    fun setSaveButtonVisibility(visible: Boolean) {
        if (::saveButton.isInitialized) {
            saveButton.visibility = if (visible) View.VISIBLE else View.GONE
        }
    }

    fun setSaveButtonClickListener(listener: View.OnClickListener) {
        if (::saveButton.isInitialized) {
            saveButton.setOnClickListener(listener)
        }
    }

    fun initAddButton(
        defaultClickListener: View.OnClickListener? = null,
    ) {
        if (!::addButton.isInitialized) {
            addButton = ImageView(this).apply {
                layoutParams = Toolbar.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.END
                    marginEnd = 16
                }

                setImageResource(R.drawable.ic_add)

                val size = 100
                layoutParams.width = size
                layoutParams.height = size
                visibility = View.GONE
                setOnClickListener(defaultClickListener)

                setPadding(8, 8, 8, 8)
            }
            binding.toolbar.addView(addButton)
        }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.ingredientsFragment -> setAddButtonVisibility(true)
                R.id.detailRecipeFragment -> setAddButtonVisibility(true)
                R.id.createMealFragment -> setAddButtonVisibility(true)
                else -> setAddButtonVisibility(false)
            }
        }
    }

    fun setAddButtonVisibility(visible: Boolean) {
        if (::addButton.isInitialized) {
            addButton.visibility = if (visible) View.VISIBLE else View.GONE
        }
    }

    fun setAddButtonClickListener(listener: View.OnClickListener) {
        if (::addButton.isInitialized) {
            addButton.setOnClickListener(listener)
        }
    }

    fun initDeleteButton(
        defaultClickListener: View.OnClickListener? = null,
    ) {
        if (!::deleteButton.isInitialized) {
            deleteButton = ImageView(this).apply {
                layoutParams = Toolbar.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.END
                }

                setImageResource(R.drawable.ic_delete)

                val size = 100
                layoutParams.width = size
                layoutParams.height = size
                visibility = View.GONE
                setOnClickListener(defaultClickListener)

                setPadding(8, 8, 8, 8)
            }
            binding.toolbar.addView(deleteButton)
        }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.detailRecipeFragment -> {
                    setDeleteButtonVisibility(true)
                }
                else -> {
                    setDeleteButtonVisibility(false)
                }
            }
        }
    }

    fun setDeleteButtonVisibility(visible: Boolean) {
        if (::deleteButton.isInitialized) {
            deleteButton.visibility = if (visible) View.VISIBLE else View.GONE
        }
    }

    fun setDeleteButtonClickListener(listener: View.OnClickListener) {
        if (::deleteButton.isInitialized) {
            deleteButton.setOnClickListener(listener)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController(R.id.nav_host_fragment).popBackStack()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    companion object {
        private const val TAG = "MainActivity"
    }
}