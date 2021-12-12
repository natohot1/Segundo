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
import org.w3c.dom.Document

class Tabla : AppCompatActivity() {


    private lateinit var  db : FirebaseFirestore
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

        // instnaciamos datos de usuario
        var misDatos:Paciente

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf()

        myAdapter = MyAdapter(userArrayList)

        recyclerView.adapter = myAdapter

        EventChangeListener()

        db.collection(historia!!).get().addOnSuccessListener { resultado ->
            }

    }

    private fun EventChangeListener(){
        db = FirebaseFirestore.getInstance()
        db.collection(historia!!).addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?)
            {
                if(error != null){
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for(dc : DocumentChange in value?.documentChanges!!){
                    if (dc.type == DocumentChange.Type.ADDED){
                        userArrayList.add(dc.document.toObject(User::class.java))
                    }
                }
                myAdapter.notifyDataSetChanged()
            }

        })
    }

}