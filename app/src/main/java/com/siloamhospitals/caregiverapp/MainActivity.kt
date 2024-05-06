package com.siloamhospitals.caregiverapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.siloamhospitals.caregiverapp.databinding.ActivityMainBinding
import com.siloamhospitals.siloamcaregiver.base.SiloamCaregiver
import com.siloamhospitals.siloamcaregiver.ui.button.CaregiverButtons

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        CaregiverButtons.init(this.application)
        //setup initial user
        // todo add more parameter for fullfill the requirement
        SiloamCaregiver.init(this)
        SiloamCaregiver.initUser(userId = 2000000761, organizationId = 2, this)
//        2000000834 eka
//        2000000761 andreas
        binding.buttonOpenCaregiver.setOnClickListener {
            //navigate activity
            startActivity(Intent(this, SecondActivity::class.java))
        }

    }

}
