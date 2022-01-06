package com.example.segundo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_segunda.*

class Tabla3 : AppCompatActivity(),MiAdaptador2.MyOnClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<User>
    private lateinit var miAdaptador2: MiAdaptador2
    private lateinit var db : FirebaseFirestore
    var historia:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabla3)

        val objetoInt: Intent = intent
        historia = objetoInt.getStringExtra("historia").toString()
        recyclerView = findViewById(R.id.recycler3)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf()

        miAdaptador2 = MiAdaptador2(userArrayList,this@Tabla3)

        recyclerView.adapter = miAdaptador2

        EventChangeListener(historia!!)
    }

    private fun EventChangeListener(miHistria:String) {
        db = FirebaseFirestore.getInstance()
        db.collection(miHistria).addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null){
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for(dc : DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){
                        userArrayList.add(dc.document.toObject(User::class.java))
                    }
                }
                miAdaptador2.notifyDataSetChanged()
            }
        })
    }

    override fun OnClick(position: Int) {
        val ima1 = userArrayList[position].nombreElectro
        val inte = Intent(this,Imagenes::class.java)
        inte.putExtra("imagen1",ima1)
        startActivity(inte)
    }
}