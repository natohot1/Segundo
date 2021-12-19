package com.example.segundo

import Paciente
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.segundo.databinding.ActivityMainBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_tabla.*
import org.w3c.dom.Document

data class User2(
    val nombre: String = "",
    val fecha: String = "",
    val correo: String = ""
)

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class Tabla : AppCompatActivity() {


   val  db = Firebase.firestore
   // private lateinit var  userArrayList: ArrayList<User>
   // private lateinit var  myAdapter: MyAdapter
   // private lateinit var  recyclerView: RecyclerView
    var historia:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabla)

        //Extraemos el dato historia de SegundaActivity
        val objetoInt: Intent = intent
        historia = objetoInt.getStringExtra("historia").toString()


        val query = db.collection(historia!!)
        val options = FirestoreRecyclerOptions.Builder<User2>().setQuery(query, User2::class.java).setLifecycleOwner(this).build()

        val adapter = object: FirestoreRecyclerAdapter<User2, UserViewHolder>(options){




            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
                val view: View = LayoutInflater.from(this@Tabla).inflate(R.layout.datoslista,parent, false)
                return UserViewHolder(view)
            }

            override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User2) {
                val txNombre: TextView = holder.itemView.findViewById(R.id.txtNombre)
                val txFecha: TextView = holder.itemView.findViewById(R.id.txtFecha)
                val txCorreo: TextView = holder.itemView.findViewById(R.id.txtemail)
                txNombre.text = model.nombre
                txFecha.text = model.fecha
                txCorreo.text = model.correo

            }



        }


            recyclerView.adapter = adapter

            recyclerView.layoutManager = LinearLayoutManager(this)





       // val db : FirebaseFirestore = FirebaseFirestore.getInstance()

        var nombre = ""
        var fecha = ""
        var correo = ""




    //  db.collection(historia!!).get().addOnSuccessListener { resultado ->
    //      val datos = resultado
    //      for (documento in resultado){
//
    //          nombre = documento.get("nombre").toString()
    //          fecha = documento.get("fecha").toString()
    //          correo = documento.get("correo").toString()
    //          textView3.setText(nombre)
//
    //          textView4.setText(fecha)
    //          textView5.setText(correo)
//
//
    //      }
    //  }
//
    }

}