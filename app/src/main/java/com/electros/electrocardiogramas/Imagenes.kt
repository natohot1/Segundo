package com.electros.electrocardiogramas

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.electros.electrocardiogramas.databinding.ActivityImagenesBinding
import com.squareup.picasso.Picasso

class Imagenes : AppCompatActivity() {
    private lateinit var binding: ActivityImagenesBinding
    var image1 : String? = null
    var image2 : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagenesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title="ELECTROCARDIOGRAMAS"

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        val objetoInt: Intent = intent
        image1 = objetoInt.getStringExtra("imagen1")
        image2 = objetoInt.getStringExtra("imagen2")

        Picasso.get().load(image1).into(binding.imageVPica1)
        Picasso.get().load(image2).into(binding.imageVPica2)

        binding.faboton2.setOnClickListener{
            val inte2 = Intent(this, SegundaActivity::class.java)
            startActivity(inte2)
        }
    }
}
