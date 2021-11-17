package com.example.segundo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
data class Usuarios (val user:String, val email:String, val id:String)
class MainActivity : AppCompatActivity() {

    private var autentication : FirebaseAuth?=null
    private var database = FirebaseDatabase.getInstance()
    private var conexion = database.reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        autentication = FirebaseAuth.getInstance()



        //  FUNCION DE REGISTRO
        buttonRegistro.setOnClickListener {
            val email = correoid.text.toString()
            val pasword = contrasenaId.text.toString()
            login(email,pasword)
        }

        // LLAMA A INCIO DE SESION
        buttonInicio.setOnClickListener {
            val email = correoid.text.toString()
            val pasword = contrasenaId.text.toString()
            inicio(email,pasword)
        }
        
    }

    private fun inicio(email: String, pasword: String) {
        autentication!!.signInWithEmailAndPassword(email,pasword).addOnCompleteListener {
            if (it.isSuccessful){
                val idUser = FirebaseAuth.getInstance().currentUser!!.uid
                Toast.makeText(applicationContext, "Has Iniciado sesion", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,SegundaActivity::class.java)
                intent.putExtra("email",email)
                startActivity(intent)
            }else{
                Toast.makeText(applicationContext, "Ha fallado, no se pudo iniciar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(email: String, pasword: String){
        autentication!!.createUserWithEmailAndPassword(email,pasword).addOnCompleteListener(this){ task ->
            if (task.isSuccessful){
                val Uid = FirebaseAuth.getInstance().currentUser!!.uid
                Toast.makeText(applicationContext, "Registrado", Toast.LENGTH_SHORT).show()
              //  conexion.child("users").child(Uid).setValue(Usuarios(user = String(),email,Uid))
                val intent = Intent(this,SegundaActivity::class.java)
                intent.putExtra("email",email)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(applicationContext, "Ha fallado el Registro", Toast.LENGTH_SHORT).show()
            }
            
        }
    }
    
    
    
    
}