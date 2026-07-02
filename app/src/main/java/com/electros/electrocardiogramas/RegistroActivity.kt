package com.electros.electrocardiogramas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

        configurarBotones(binding.buttonRegistro, "REGISTRARSE")

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validarBotones()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.correoid.addTextChangedListener(textWatcher)
        binding.contrasenaId.addTextChangedListener(textWatcher)
        binding.contrasenaId2.addTextChangedListener(textWatcher)

        binding.buttonRegistro.setOnClickListener {
            val email = binding.correoid.text.toString().trim()
            val pass = binding.contrasenaId.text.toString().trim()
            val pass2 = binding.contrasenaId2.text.toString().trim()

            if (validar()) {
                if (pass == pass2) {
                    login(email, pass)
                } else {
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validarBotones() {
        val email = binding.correoid.text.toString()
        val pass = binding.contrasenaId.text.toString()
        val pass2 = binding.contrasenaId2.text.toString()

        // Habilitar botón si los campos son válidos y coinciden
        binding.buttonRegistro.isEnabled = email.contains("@") && email.length > 8 &&
                pass.length >= 6 && pass == pass2
    }

    private fun validar(): Boolean {
        var esValido = true
        val email = binding.correoid.text.toString()
        val pass = binding.contrasenaId.text.toString()

        if (!email.contains("@") || email.length <= 8) {
            esValido = false
            Toast.makeText(applicationContext, "Correo no válido (debe contener @ y > 8 caracteres)", Toast.LENGTH_SHORT).show()
        } else if (pass.length < 6) {
            esValido = false
            Toast.makeText(applicationContext, "Contraseña no válida (mínimo 6 caracteres)", Toast.LENGTH_SHORT).show()
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
        miBoton.text = titulo
        miBoton.isEnabled = false
    }
}
