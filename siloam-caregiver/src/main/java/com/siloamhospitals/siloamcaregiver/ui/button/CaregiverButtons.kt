package com.siloamhospitals.siloamcaregiver.ui.button

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.base.SiloamCaregiverUI
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences

class CaregiverButtons private constructor(app: Application) :
    Application.ActivityLifecycleCallbacks by ActivityEmptyLifecycleCallbacks() {

    private var fab: FloatingActionButton? = null // Declare FAB here to access it later

    private val mPreference by lazy {  AppPreferences(app) }
    private val repository by lazy {  Repository(mPreference) }
    private  lateinit var caregiverButtonViewModel: CaregiverButtonViewModel

    init {
        app.registerActivityLifecycleCallbacks(this)
        app.registerActivityLifecycleCallbacks(this)
    }

    @SuppressLint("ClickableViewAccessibility", "UnsafeOptInUsageError")
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // Check if the activity is not in the list of activities where the FAB should be hidden

        val caregiverButtonsViewModelFactory = viewModelFactory {
            initializer {
                CaregiverButtonViewModel(repository)
            }
        }

        caregiverButtonViewModel = ViewModelProvider(
            activity as ViewModelStoreOwner,
            caregiverButtonsViewModelFactory
        ).get(CaregiverButtonViewModel::class.java)

        if (!shouldHideFab(activity)) {
            val decorView = activity.window.decorView as ViewGroup

            // Add FloatingActionButton
            fab = FloatingActionButton(activity)
            fab?.setImageDrawable(
                ContextCompat.getDrawable(
                    activity,
                    R.drawable.ic_caregiver_chat
                )
            )
            fab?.imageTintList = null
            fab?.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorPrimaryLight)

            // Set layout parameters for positioning at bottom right
            val fabParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            fabParams.gravity = Gravity.BOTTOM or Gravity.END // Position at bottom right
            // Calculate margin based on screen ratio
            val screenWidth = activity.resources.displayMetrics.widthPixels.toFloat()
            val screenHeight = activity.resources.displayMetrics.heightPixels.toFloat()
            val marginRatio = 0.04f // 4% of the screen width as margin
            val marginBottomRatio = 0.10f // 4% of the screen width as margin
            val marginEnd = (screenWidth * marginRatio).toInt()
            val marginBottom = (screenHeight * marginBottomRatio).toInt()

            // Set margins based on screen ratio
            fabParams.rightMargin = marginEnd
            fabParams.bottomMargin = marginBottom

            // Add FAB to the layout
            decorView.addView(fab, fabParams)

//            fab?.doOnPreDraw {
//                // Create a BadgeDrawable instance
//                val badgeDrawable = BadgeDrawable.create(activity)
//                badgeDrawable.number = 10 // Set the badge count
//                badgeDrawable.backgroundColor = ContextCompat.getColor(activity, R.color.colorRedBase) // Set the badge background color
//                badgeDrawable.badgeGravity = BadgeDrawable.TOP_END // Set the badge position
//                badgeDrawable.horizontalOffset = 10.dpToPx(activity) // Set the horizontal offset
//                badgeDrawable.verticalOffset = 10.dpToPx(activity) // Set the vertical offset
//                BadgeUtils.attachBadgeDrawable(badgeDrawable, fab!!, null) // Attach the badge to the FAB
//            }

            val badgeDrawable = BadgeDrawable.create(activity)
            caregiverButtonViewModel.run {
                emitFloatingNotification()
                listenFloatingNotification()
                floatingNotification.observe(activity as LifecycleOwner) { event ->
                    event.getContentIfNotHandled()?.let { data ->
                        fab?.post {
                            if(data.isUrgentMessage) {
                                fab?.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        activity,
                                        R.drawable.ic_urgent_float
                                    )
                                )
                                fab?.imageTintList = null
                                fab?.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorYellowFloatNotif)
                            } else {
                                fab?.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        activity,
                                        R.drawable.ic_caregiver_chat
                                    )
                                )
                                fab?.imageTintList = null
                                fab?.backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorPrimaryLight)
                            }
                            if(data.count > 0) {
                                badgeDrawable.setVisible(true)
                                badgeDrawable.number = data.count // Set the badge count
                                badgeDrawable.backgroundColor = ContextCompat.getColor(activity, R.color.colorRedVibrant) // Set the badge background color
                                badgeDrawable.badgeGravity = BadgeDrawable.TOP_END // Set the badge position
                                badgeDrawable.horizontalOffset = 10.dpToPx(activity) // Set the horizontal offset
                                badgeDrawable.verticalOffset = 10.dpToPx(activity) // Set the vertical offset
                                BadgeUtils.attachBadgeDrawable(badgeDrawable, fab!!, null) // Attach the badge to the FAB
                            } else {
                                badgeDrawable.setVisible(false)
                            }
                        }
                    }
                }
            }

            // Set an onClick listener for the FAB
            fab?.setOnClickListener {
                // Perform your onClick action here
                SiloamCaregiverUI.getInstances().openCaregiver(activity)
            }
        } else {
            // If the activity is in the list of activities where the FAB should be hidden,
            // hide the existing FAB if it exists
            val fab = findFabForActivity(activity)
            fab?.visibility = View.GONE
        }
    }

    // Method to check if the FAB should be hidden for the given activity
    private fun shouldHideFab(activity: Activity): Boolean {
        // List the activities where you want to hide the FAB by their class names
        val activitiesToHideFab = listOf(
            "AuthActivity",
            "CaregiverActivity",
            "ChatroomCaregiverActivity",
            "GroupDetailActivity",
            "RoomTypeCaregiverActivity",
        )

        // Check if the current activity is in the list of activities where the FAB should be hidden
        return activitiesToHideFab.contains(activity::class.java.simpleName)
    }

    private fun findFabForActivity(activity: Activity): FloatingActionButton? {
        val rootView = activity.window.decorView as ViewGroup
        for (i in 0 until rootView.childCount) {
            val child = rootView.getChildAt(i)
            if (child is FloatingActionButton) {
                return child
            }
        }
        return null
    }

    companion object {
        private var instance: CaregiverButtons? = null

        fun init(application: Application) {
            instance = CaregiverButtons(application)
        }

        // Extension function to convert dp to pixels
        private fun Int.dpToPx(activity: Activity): Int {
            val density = activity.resources.displayMetrics.density
            return (this * density).toInt()
        }
    }
}