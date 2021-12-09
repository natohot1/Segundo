package com.example.segundo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
class MainActivity : AppCompatActivity() {

    private var autentication : FirebaseAuth?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        autentication = FirebaseAuth.getInstance()
        configurarBotones(buttonInicio,"INICIAR")
        configurarBotones(buttonRegistro,"REGISTRARSE")

        correoid.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.i("begoskndksjnj", "not overide")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(correoid.length() > 7 && contrasenaId.length() >5){
                    buttonInicio.isEnabled = true
                    buttonRegistro.isEnabled = true
                }else{
                    buttonInicio.isEnabled = false
                    buttonRegistro.isEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                Log.i("begoskndksjnj", "not overide")
            }

        })

        contrasenaId.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.i("begoskndksjnj", "not overide")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
              if(correoid.length() > 7 && contrasenaId.length() >5){
                  buttonInicio.isEnabled = true
                  buttonRegistro.isEnabled = true
              }else{
                  buttonInicio.isEnabled = false
                  buttonRegistro.isEnabled = false
              }
                Log.i("begoskndksjnj", "not overide")

            }

            override fun afterTextChanged(s: Editable?) {
               // buttonInicio.isEnabled = correoid.length() >= 8
                Log.i("begoskndksjnj", "not overide")
            }

        })

        session()




        //  FUNCION DE REGISTRO
        buttonRegistro.setOnClickListener {
            if(correoid.text.toString() != "" && contrasenaId.text.toString() != "") {
                val email = correoid.text.toString().trim()
                val pasword = contrasenaId.text.toString().trim()
                login(email, pasword)
            }
        }

        // LLAMA A INCIO DE SESION
        buttonInicio.setOnClickListener {
            if(correoid.text.toString() != "" && contrasenaId.text.toString() != "") {
                val email = correoid.text.toString().trim()
                val pasword = contrasenaId.text.toString().trim()
                inicio(email, pasword)
            }
        }
        
    }


    private fun inicio(email: String, pasword: String) {
        autentication!!.signInWithEmailAndPassword(email,pasword).addOnCompleteListener {
            if (it.isSuccessful){
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
                Toast.makeText(applicationContext, "SE HA REGISTRADO CORRECTAMENTE", Toast.LENGTH_LONG).show()
                aSegunda(email)
                finish()
            }else{
                Toast.makeText(applicationContext, "Ha fallado el Registro", Toast.LENGTH_LONG).show()
            }
            
        }
    }
    private fun aSegunda(email: String){
        val intent = Intent(this,SegundaActivity::class.java)
        intent.putExtra("email",email)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        outLayout.visibility = View.VISIBLE
    }

    private fun session(){
        val prefs: SharedPreferences = getSharedPreferences(("mipreferencia"), Context.MODE_PRIVATE)
        val correoSha:String?  = prefs.getString("correo",null)
        if(correoSha != null){
            outLayout.visibility = View.INVISIBLE
            aSegunda(correoSha)


        }
    }

    private fun configurarBotones(miBoton: Button, titulo: String) {
        miBoton.setBackgroundColor(Color.BLUE)
        miBoton.setTextColor(Color.WHITE)
      //  miBoton.shadowColor.red
        miBoton.text = titulo
       // miBoton.isEnabled = false
    }
    
    
    
    
}