package com.siloamhospitals.siloamcaregiver.ext.common

import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils

inline fun <T> T.applyIf(predicate: Boolean, block: T.() -> Unit): T = apply {
    if (predicate) block(this)
}

fun launchDelayedFunction(timeMillis: Long = DateUtils.SECOND_IN_MILLIS, action: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({ action() }, timeMillis)
}