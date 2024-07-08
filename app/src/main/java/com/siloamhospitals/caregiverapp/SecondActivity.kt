package com.siloamhospitals.caregiverapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.siloamhospitals.siloamcaregiver.ui.button.CaregiverButtons

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val btnTogle = findViewById<Button>(R.id.btn_toggle)
        var isVisible = true
        btnTogle.setOnClickListener {
            if (isVisible) {
                CaregiverButtons.hide()
            } else {
                CaregiverButtons.show()
            }
            isVisible = !isVisible
        }

    }
}
