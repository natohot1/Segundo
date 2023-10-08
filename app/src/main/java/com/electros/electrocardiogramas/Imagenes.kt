package com.electros.electrocardiogramas

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_imagenes.*

class Imagenes : AppCompatActivity() {
    var image1 : String? = null
    var image2 : String? = null
    var histpasada: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imagenes)

        title="ELECTROCARDIOGRAMAS"


        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        val objetoInt: Intent = intent
        image1 = objetoInt.getStringExtra("imagen1")
        image2 = objetoInt.getStringExtra("imagen2")
        histpasada = objetoInt.getStringExtra("histo")


        //////////***********************

        Picasso.get().load(image1).into(imageVPica1)
        Picasso.get().load(image2).into(imageVPica2)

        fabicon1.setOnClickListener{
            val inte2 = Intent(this, Tabla3::class.java)
            inte2.putExtra("historia",histpasada)
            startActivity(inte2)
        }


    }
    fun volver(){
        Toast.makeText(this, "ERROR NO HAY DATOS", Toast.LENGTH_LONG).show()
        val inte = Intent(this, SegundaActivity::class.java)
        startActivity(inte)
    }
}