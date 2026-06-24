package com.electros.electrocardiogramas

import android.annotation.SuppressLint
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
        configurarBotones(binding.buttonInicio,"INICIAR")


        binding.correoid.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.i("begoskndksjnj", "not overide")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.correoid.length() > 8 && binding.contrasenaId.length() >5){
                    binding.buttonInicio.setBackgroundColor(Color.parseColor("#0c18f1"))
                    binding.buttonInicio.setTextColor(Color.parseColor("#ffffff"))
                    binding.buttonInicio.isEnabled = true
                }
                else{
                    binding.buttonInicio.setBackgroundColor(Color.parseColor("#babbc8"))
                    binding.buttonInicio.setTextColor(Color.parseColor("#dbddf5"))
                    binding.buttonInicio.isEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                Log.i("begoskndksjnj", "not overide")
            }

        })

        binding.contrasenaId.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.i("begoskndksjnj", "not overide")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.correoid.length() > 8 && binding.contrasenaId.length() >5){
                    binding.buttonInicio.setBackgroundColor(Color.parseColor("#0c18f1"))
                    binding.buttonInicio.setTextColor(Color.parseColor("#ffffff"))
                    binding.buttonInicio.isEnabled = true
                }
                else{
                    binding.buttonInicio.setBackgroundColor(Color.parseColor("#babbc8"))
                    binding.buttonInicio.setTextColor(Color.parseColor("#dbddf5"))
                    binding.buttonInicio.isEnabled = false
                }

                Log.i("begoskndksjnj", "not overide")

            }

            override fun afterTextChanged(s: Editable?) {
                Log.i("begoskndksjnj", "not overide")
            }

        })

        session()

        binding.buttonInicio.setOnClickListener {
            if(binding.correoid.text.toString() != "" && binding.contrasenaId.text.toString() != "") {
                val email = binding.correoid.text.toString().trim()
                val pasword = binding.contrasenaId.text.toString().trim()
                inicio(email, pasword)
            }
        }

    }

    private fun validar(): Boolean {
        if(binding.contrasenaId.length() > 0 && binding.correoid.length() >0){
        }else{
        }

        var esValido = true
        if(binding.correoid.text.toString().contains("@")){
            esValido = true
        }
        if(binding.correoid.text.toString() != "" && binding.contrasenaId.text.toString() != ""){
            esValido = true
        }
        if(binding.correoid.text.toString().length >= 8){
            esValido = false
            Toast.makeText(applicationContext, "Correo no valido", Toast.LENGTH_SHORT).show()
        }
        if(binding.contrasenaId.text.toString().length >= 6){
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
        miBoton.setBackgroundColor(Color.parseColor("#babbc8"))
        miBoton.setTextColor(Color.parseColor("#dbddf5"))
        miBoton.text = titulo
        miBoton.isEnabled = false
    }
}
