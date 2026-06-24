package com.electros.electrocardiogramas

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.electros.electrocardiogramas.databinding.ActivityRegistroBinding
import com.google.firebase.auth.FirebaseAuth


class RegistroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroBinding
    private var autentication : FirebaseAuth?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title="PERPETUO SOCORRO"

        autentication = FirebaseAuth.getInstance()

        configurarBotones(binding.buttonRegistro,"REGISTRARSE")

        binding.correoid.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.i("begoskndksjnj", "not overide")
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.correoid.length() > 8 && binding.contrasenaId.length() >5 && binding.contrasenaId2.length() >5){
                    binding.buttonRegistro.setBackgroundColor(Color.parseColor("#0c18f1"))
                    binding.buttonRegistro.setTextColor(Color.parseColor("#ffffff"))
                    binding.buttonRegistro.isEnabled = true
                }
                else{
                    binding.buttonRegistro.setBackgroundColor(Color.parseColor("#babbc8"))
                    binding.buttonRegistro.setTextColor(Color.parseColor("#dbddf5"))
                    binding.buttonRegistro.isEnabled = false
                }
            }
            override fun afterTextChanged(s: Editable?) {
                Log.i("begoskndksjnj", "not overide")
            }
        })

        binding.contrasenaId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.i("begoskndksjnj", "not overide")
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.contrasenaId.length() > 5 && binding.correoid.length() >8 && binding.contrasenaId2.length() >5){
                    binding.buttonRegistro.setBackgroundColor(Color.parseColor("#0c18f1"))
                    binding.buttonRegistro.setTextColor(Color.parseColor("#ffffff"))
                    binding.buttonRegistro.isEnabled = true
                }else{
                    binding.buttonRegistro.setBackgroundColor(Color.parseColor("#babbc8"))
                    binding.buttonRegistro.setTextColor(Color.parseColor("#dbddf5"))
                    binding.buttonRegistro.isEnabled = false
                }
                Log.i("begoskndksjnj", "not overide")
            }

            override fun afterTextChanged(s: Editable?) {
                Log.i("begoskndksjnj", "not overide")
            }
        })

        binding.contrasenaId2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.i("begoskndksjnj", "not overide")
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.contrasenaId.length() > 5 && binding.correoid.length() >8 && binding.contrasenaId2.length() >5){
                    binding.buttonRegistro.setBackgroundColor(Color.parseColor("#0c18f1"))
                    binding.buttonRegistro.setTextColor(Color.parseColor("#ffffff"))
                    binding.buttonRegistro.isEnabled = true
                }else{
                    binding.buttonRegistro.setBackgroundColor(Color.parseColor("#babbc8"))
                    binding.buttonRegistro.setTextColor(Color.parseColor("#dbddf5"))
                    binding.buttonRegistro.isEnabled = false
                }
                Log.i("begoskndksjnj", "not overide")
            }

            override fun afterTextChanged(s: Editable?) {
                Log.i("begoskndksjnj", "not overide")
            }
        })

        binding.buttonRegistro.setOnClickListener {
            val pass1 = binding.contrasenaId.text?.toString()?.trim().orEmpty()
            val pass2 = binding.contrasenaId2.text?.toString()?.trim().orEmpty()
            if (pass1 != pass2){
                Toast.makeText(applicationContext, "Las contraseñas no son iguales", Toast.LENGTH_SHORT).show()
            }else {
                val email = binding.correoid.text.toString().trim()
                val pasword = pass1
                login(email, pasword)
            }

        }
    }

    private fun validar(): Boolean {
        if(binding.contrasenaId.length() > 0 && binding.correoid.length() >0){
            binding.buttonRegistro.setBackgroundColor(Color.parseColor("#0c18f1"))
            binding.buttonRegistro.setTextColor(Color.parseColor("#ffffff"))
        }else{
            binding.buttonRegistro.setBackgroundColor(Color.parseColor("#babbc8"))
            binding.buttonRegistro.setTextColor(Color.parseColor("#dbddf5"))
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
        binding.outLayout2.visibility = View.VISIBLE
    }

    private fun configurarBotones(miBoton: Button, titulo: String) {
        miBoton.setBackgroundColor(Color.parseColor("#babbc8"))
        miBoton.setTextColor(Color.parseColor("#dbddf5"))
        miBoton.text = titulo
        miBoton.isEnabled = false
    }
}
