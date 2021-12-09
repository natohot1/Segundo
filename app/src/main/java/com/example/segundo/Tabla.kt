package com.example.segundo

import Paciente
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.segundo.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class Tabla : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var list = ArrayList<Paciente>()

    var historia:String? = null
    var datos = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabla)

        //Extraemos el dato historia de SegundaActivity
        val objetoInt: Intent =intent
        historia = objetoInt.getStringExtra("historia")

        // instnaciamos datos de usuario
        var misDatos:Paciente


        val db : FirebaseFirestore = FirebaseFirestore.getInstance()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db.collection(historia!!).get().addOnSuccessListener { resultado ->
            for (documento in resultado){
               // list.add(Paciente($(documento.no)))

            }
        }
    }
}