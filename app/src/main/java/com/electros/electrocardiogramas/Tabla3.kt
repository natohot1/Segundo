package com.electros.electrocardiogramas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_tabla3.faboton

class Tabla3 : AppCompatActivity(), MiAdaptador2.MyOnClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<User>
    private lateinit var userArrayList3: ArrayList<User>
    private lateinit var miAdaptador2: MiAdaptador2

    private lateinit var db : FirebaseFirestore

    var historia:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabla3)

        title="ELECTROCARDIOGRAMAS"



        val objetoInt: Intent = intent
        historia = objetoInt.getStringExtra("historia").toString()
        userArrayList3 = arrayListOf()

      //  userArrayList3 = midatos.userArrayList2





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
      db.collection("misHistorias")
          .whereEqualTo("historia", miHistria)
          .get()
          .addOnSuccessListener {mios ->
            for (document in mios){
                userArrayList.add(document.toObject(User::class.java))
                miAdaptador2.notifyDataSetChanged()
            }
          }
          .addOnFailureListener{
              Toast.makeText(this, "error datos", Toast.LENGTH_SHORT).show()
          }
          .addOnCompleteListener {

              if (userArrayList.isEmpty()){
                  volver()
              }
          }

        faboton.setOnClickListener{
            val inte2 = Intent(this, SegundaActivity::class.java)

            startActivity(inte2)
        }


    }
    fun volver(){
        Toast.makeText(this, "ERROR NO HAY DATOS", Toast.LENGTH_LONG).show()
        val inte = Intent(this, SegundaActivity::class.java)
        startActivity(inte)
    }

    override fun OnClick(position: Int) {
        val ima1 = userArrayList[position].fotoURL1
        val ima2 = userArrayList[position].fotoURL2
        val inte = Intent(this, Imagenes::class.java)
        inte.putExtra("imagen1",ima1)
        inte.putExtra("imagen2",ima2)
        startActivity(inte)
    }
}