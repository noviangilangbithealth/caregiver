package com.siloamhospitals.siloamcaregiver.ui.button

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.orhanobut.logger.Logger
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.base.SiloamCaregiverUI

class CaregiverButtons private constructor(app: Application) :
    Application.ActivityLifecycleCallbacks by ActivityEmptyLifecycleCallbacks() {

    private var fab: FloatingActionButton? = null // Declare FAB here to access it later

    init {
        app.registerActivityLifecycleCallbacks(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // Check if the activity is not in the list of activities where the FAB should be hidden
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
