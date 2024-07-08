package com.siloamhospitals.caregiverapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.siloamhospitals.caregiverapp.databinding.ActivityMainBinding
import com.siloamhospitals.siloamcaregiver.base.SiloamCaregiver
import com.siloamhospitals.siloamcaregiver.base.SiloamCaregiverUI
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
        // todo add more parameter for fulfill the requirement
        SiloamCaregiver.init(this)
        SiloamCaregiver.init(this)
        SiloamCaregiver.initUser(
            userId = 2000000834,
            organizationId = 2,
            wardId = 0,
            context = this,
            role = SiloamCaregiver.ROLE_DOCTOR
        )
//        2000000834 eka
//        2000000827 tiur
//        2000000761 andreas
//        29000000134 amsal nurse
        binding.buttonOpenCaregiver.setOnClickListener {
            //this function call is for open caregiver history chat
//            SiloamCaregiverUI().openChatHistory(
//                this,
//                hospitalHopeId = 2.toString(),
//                hospitalCode = "SHLV",
//                doctorHopeId = 2000000827,
//                caregiverId = "3467deb0-00de-4815-b2dc-3b89bd54cfe7",
//                patientName = "ROCK LEE",
//                localMrNumber = "1242421",
//                wardName = "Ephilia",
//                roomName = "1661",
//                gender = "M"
//            )

            //navigate to chat room
//            SiloamCaregiverUI().openChatRoom(
//                this,
//                roomName = "General",
//                patientName = "ROCK LEE",
//                caregiverId = "3467deb0-00de-4815-b2dc-3b89bd54cfe7",
//                channelId = "0d9bf688-82c4-4b2c-b954-7358162044b5",
//                doctorHopeId = 2000000827,
//                icon = ""
//            )
            startActivity(Intent(this, SecondActivity::class.java))
        }

    }

}
