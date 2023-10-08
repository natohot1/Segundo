package com.electros.electrocardiogramas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.electros.electrocardiogramas.R
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegitro = findViewById<Button>(R.id.btnRegistrar)


        btnLogin.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ActivityLogin::class.java)
            startActivity(intent)

        })
        btnRegitro.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)

        })
    }
}