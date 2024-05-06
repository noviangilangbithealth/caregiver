package com.siloamhospitals.siloamcaregiver.ui.button

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

internal class CaregiverButtonView(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    private val fab: FloatingActionButton
    init {
        fab = FloatingActionButton(context)
        val fabLayoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM or Gravity.END
        )
        fabLayoutParams.marginEnd = dip(16)
        fabLayoutParams.bottomMargin = dip(16)
        fab.layoutParams = fabLayoutParams
        addView(fab)

        setBackgroundColor(Color.TRANSPARENT)
        ViewCompat.setElevation(this, 30f)
        isClickable = true
        addView(fab)
    }

}
