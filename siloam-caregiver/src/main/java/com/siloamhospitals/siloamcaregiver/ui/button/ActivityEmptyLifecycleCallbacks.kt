package com.siloamhospitals.siloamcaregiver.ui.button

import android.app.Activity
import android.app.Application
import android.os.Bundle

internal class ActivityEmptyLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        Unit
    }

    override fun onActivityStarted(p0: Activity) {
        Unit
    }

    override fun onActivityResumed(p0: Activity) {
        Unit
    }

    override fun onActivityPaused(p0: Activity) {
        Unit
    }

    override fun onActivityStopped(p0: Activity) {
        Unit
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        Unit
    }

    override fun onActivityDestroyed(p0: Activity) {
        Unit
    }
}
