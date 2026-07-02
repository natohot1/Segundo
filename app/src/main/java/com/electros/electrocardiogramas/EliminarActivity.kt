package com.electros.electrocardiogramas

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.electros.electrocardiogramas.databinding.ActivityEliminarBinding
import com.google.firebase.auth.FirebaseAuth

class EliminarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEliminarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEliminarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("email") ?: "No identificado"
        binding.textEmailMostrado.text = email

        binding.buttonConfirmarEliminar.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null && user.email == email) {
                user.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Usuario eliminado correctamente", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Error al eliminar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "No se puede eliminar: el usuario no ha iniciado sesión o no coincide", Toast.LENGTH_LONG).show()
            }
        }

        binding.buttonCancelar.setOnClickListener {
            finish()
        }
    }
}
