package com.example.segundo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_registro.buttonRegistro
import kotlinx.android.synthetic.main.activity_registro.contrasenaId
import kotlinx.android.synthetic.main.activity_registro.contrasenaId2
import kotlinx.android.synthetic.main.activity_registro.correoid
import kotlinx.android.synthetic.main.activity_registro.outLayout2

class RegistroActivity : AppCompatActivity() {
    private var autentication : FirebaseAuth?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        title="PERPETUO SOCORRO"

        autentication = FirebaseAuth.getInstance()

        configurarBotones(buttonRegistro,"REGISTRARSE")

        correoid.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.i("begoskndksjnj", "not overide")
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(correoid.length() > 8 && contrasenaId.length() >5 && contrasenaId2.length() >5){
                    buttonRegistro.setBackgroundColor(Color.parseColor("#0c18f1"))
                    buttonRegistro.setTextColor(Color.parseColor("#ffffff"))
                    buttonRegistro.isEnabled = true
                }
                else{
                    buttonRegistro.setBackgroundColor(Color.parseColor("#babbc8"))
                    // miBoton.setBackgroundColor(Color.BLUE)
                    buttonRegistro.setTextColor(Color.parseColor("#dbddf5"))
                    buttonRegistro.isEnabled = false
                }
            }
            override fun afterTextChanged(s: Editable?) {
                Log.i("begoskndksjnj", "not overide")
            }
        })

        contrasenaId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.i("begoskndksjnj", "not overide")
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(contrasenaId.length() > 5 && correoid.length() >8 && contrasenaId2.length() >5){
                    buttonRegistro.setBackgroundColor(Color.parseColor("#0c18f1"))
                    buttonRegistro.setTextColor(Color.parseColor("#ffffff"))
                    buttonRegistro.isEnabled = true
                }else{
                    buttonRegistro.setBackgroundColor(Color.parseColor("#babbc8"))
                    buttonRegistro.setTextColor(Color.parseColor("#dbddf5"))
                    buttonRegistro.isEnabled = false
                }
                Log.i("begoskndksjnj", "not overide")
            }

            override fun afterTextChanged(s: Editable?) {
                Log.i("begoskndksjnj", "not overide")
            }
        })

        contrasenaId2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.i("begoskndksjnj", "not overide")
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(contrasenaId.length() > 5 && correoid.length() >8 && contrasenaId2.length() >5){
                    buttonRegistro.setBackgroundColor(Color.parseColor("#0c18f1"))
                    buttonRegistro.setTextColor(Color.parseColor("#ffffff"))
                    buttonRegistro.isEnabled = true
                }else{
                    buttonRegistro.setBackgroundColor(Color.parseColor("#babbc8"))
                    buttonRegistro.setTextColor(Color.parseColor("#dbddf5"))
                    buttonRegistro.isEnabled = false
                }
                Log.i("begoskndksjnj", "not overide")
            }

            override fun afterTextChanged(s: Editable?) {
                Log.i("begoskndksjnj", "not overide")
            }
        })



        //  FUNCION DE REGISTRO
        buttonRegistro.setOnClickListener {
            if (contrasenaId != contrasenaId2){
                Toast.makeText(applicationContext, "Las contraseñas no son iguales", Toast.LENGTH_SHORT).show()
            }else {
                val email = correoid.text.toString().trim()
                val pasword = contrasenaId.text.toString().trim()
                login(email, pasword)
            }

        }



    }

    private fun validar(): Boolean {
        if(contrasenaId.length() > 0 && correoid.length() >0){
            buttonRegistro.setBackgroundColor(Color.parseColor("#0c18f1"))
            buttonRegistro.setTextColor(Color.parseColor("#ffffff"))
        }else{
            buttonRegistro.setBackgroundColor(Color.parseColor("#babbc8"))

            buttonRegistro.setTextColor(Color.parseColor("#dbddf5"))
        }




        var esValido = true
        if(correoid.text.toString().contains("@")){
            esValido = true
        }
        if(correoid.text.toString() != "" && contrasenaId.text.toString() != ""){
            esValido = true
        }
        if(correoid.text.toString().length >= 8){
            esValido = false
            Toast.makeText(applicationContext, "Correo no valido", Toast.LENGTH_SHORT).show()
        }
        if(contrasenaId.text.toString().length >= 6){
            esValido = false
            Toast.makeText(applicationContext, "Contraseña no valida", Toast.LENGTH_SHORT).show()
        }

        return esValido
    }


    private fun inicio(email: String, pasword: String) {
        autentication!!.signInWithEmailAndPassword(email,pasword).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(applicationContext, "Has Iniciado sesion", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SegundaActivity::class.java)
                intent.putExtra("email",email)
                startActivity(intent)
                finish()
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
        val intent = Intent(this, SegundaActivity::class.java)
        intent.putExtra("email",email)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        outLayout2.visibility = View.VISIBLE
    }



    private fun configurarBotones(miBoton: Button, titulo: String) {
        miBoton.setBackgroundColor(Color.parseColor("#babbc8"))
        // miBoton.setBackgroundColor(Color.BLUE)
        miBoton.setTextColor(Color.parseColor("#dbddf5"))
        //  miBoton.shadowColor.red
        miBoton.text = titulo
        // miBoton.isEnabled = false
        miBoton.isEnabled = false
    }


}
