package com.example.segundo

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_imagenes.*
import java.net.URL

class Imagenes : AppCompatActivity() {
    var image1 : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imagenes)

        val storage = Firebase.storage


        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        val objetoInt: Intent = intent
        image1 = objetoInt.getStringExtra("imagen1")
        val imagen2 = image1+"a"
        storage.reference.child("imagenes/$image1").downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(imageVPica1)
        }
        storage.reference.child("imagenes/$imagen2").downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(imageVPica2)
        }

        
    }

}