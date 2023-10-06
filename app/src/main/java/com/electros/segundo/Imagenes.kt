package com.electros.segundo

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.electros.segundo.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_imagenes.*

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

        //////////***********************
        /*
        val imagen2 = image1+"a"
        storage.reference.child("imagenes/$image1").downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(imageVPica1)
        }.addOnSuccessListener {
            progressBar2.visibility = View.INVISIBLE
            txtPreparando.visibility = View.INVISIBLE
        }
        storage.reference.child("imagenes/$imagen2").downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(imageVPica2)
        }*/
    }
}