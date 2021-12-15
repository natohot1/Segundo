package com.example.segundo

import Paciente
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.segundo.databinding.ActivityMainBinding
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_tabla.*
import org.w3c.dom.Document

class Tabla : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var list = ArrayList<Paciente>()


   // private lateinit var  db : FirebaseFirestore
    private lateinit var  userArrayList: ArrayList<User>
    private lateinit var  myAdapter: MyAdapter
    private lateinit var  recyclerView: RecyclerView
    var historia:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabla)

        //Extraemos el dato historia de SegundaActivity
        val objetoInt: Intent =intent
        historia = objetoInt.getStringExtra("historia").toString()


        val db : FirebaseFirestore = FirebaseFirestore.getInstance()

        var nombre = ""
        var fecha = ""
        var correo = ""




      db.collection(historia!!).get().addOnSuccessListener { resultado ->
          val datos = resultado
          for (documento in resultado){

              nombre = documento.get("nombre").toString()
              fecha = documento.get("fecha").toString()
              correo = documento.get("correo").toString()
              textView3.setText(nombre)

              textView4.setText(fecha)
              textView5.setText(correo)


          }
      }
        
    }

}