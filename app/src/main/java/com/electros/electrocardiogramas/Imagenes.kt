package com.electros.electrocardiogramas

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.electros.electrocardiogramas.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_imagenes.*
import kotlinx.android.synthetic.main.activity_tabla3.faboton

class Imagenes : AppCompatActivity() {
    var image1 : String? = null
    var image2 : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imagenes)

        title="ELECTROCARDIOGRAMAS"


        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        val objetoInt: Intent = intent
        image1 = objetoInt.getStringExtra("imagen1")
        image2 = objetoInt.getStringExtra("imagen2")

        //////////***********************

        Picasso.get().load(image1).into(imageVPica1)
        Picasso.get().load(image2).into(imageVPica2)


        faboton2.setOnClickListener{
            val inte2 = Intent(this, SegundaActivity::class.java)
            startActivity(inte2)
        }
    }
}