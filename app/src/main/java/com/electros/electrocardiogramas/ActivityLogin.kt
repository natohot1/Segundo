package com.electros.electrocardiogramas

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.electros.electrocardiogramas.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class ActivityLogin : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var autentication : FirebaseAuth?=null


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title="PERPETUO SOCORRO"

        autentication = FirebaseAuth.getInstance()

        configurarBotones(binding.buttonLogin, "LOGIN")
        configurarBotones(binding.buttonNuevoLogin, "ELIMINAR REGISTRO")

        binding.buttonLogin.setOnClickListener {
            val email = binding.correoid.text.toString().trim()
            val pass = binding.contrasenaId.text.toString().trim()
            if (validar()) {
                inicio(email, pass)
            }
        }

        binding.buttonNuevoLogin.setOnClickListener {
            val email = binding.correoid.text.toString()
            if (email.isNotEmpty()) {
                val intent = Intent(this, EliminarActivity::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Por favor, introduce un correo", Toast.LENGTH_SHORT).show()
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validarBotones()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.correoid.addTextChangedListener(textWatcher)
        binding.contrasenaId.addTextChangedListener(textWatcher)

        session()

    }

    private fun validarBotones() {
        val email = binding.correoid.text.toString()
        val pass = binding.contrasenaId.text.toString()

        // Reglas para Login
        binding.buttonLogin.isEnabled = email.contains("@") && email.length > 8 && pass.length >= 6

        // Reglas para Eliminar
        binding.buttonNuevoLogin.isEnabled = email.length > 8
    }

    private fun validar(): Boolean {
        var esValido = true
        val email = binding.correoid.text.toString()
        val pass = binding.contrasenaId.text.toString()

        if (!email.contains("@") || email.length <= 8) {
            esValido = false
            Toast.makeText(applicationContext, "Correo no valido (debe contener @ y > 8 caracteres)", Toast.LENGTH_SHORT).show()
        } else if (pass.length < 6) {
            esValido = false
            Toast.makeText(applicationContext, "Contraseña no valida (mínimo 6 caracteres)", Toast.LENGTH_SHORT).show()
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
        binding.outLayout.visibility = View.VISIBLE
    }

    private fun session(){
        val prefs: SharedPreferences = getSharedPreferences(("mipreferencia"), Context.MODE_PRIVATE)
        val correoSha:String?  = prefs.getString("correo",null)
        if(correoSha != null){
            binding.outLayout.visibility = View.INVISIBLE
            aSegunda(correoSha)
        }
    }

    private fun configurarBotones(miBoton: Button, titulo: String) {
        miBoton.text = titulo
        miBoton.isEnabled = false
    }
}
