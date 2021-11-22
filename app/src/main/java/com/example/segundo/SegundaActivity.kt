package com.example.segundo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.core.graphics.red
import kotlinx.android.synthetic.main.activity_segunda.*

private const val RECUEST_CODE = 42
class SegundaActivity : AppCompatActivity() {
    private var fotoboolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segunda)

        configurarBotones(btnFotos, "FOTOGRAFIAR")
        configurarBotones(btnGuardados,"PREVIOS")

        btnFotos.setOnClickListener {
            if (fotoboolean) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                    startActivityForResult(takePictureIntent, RECUEST_CODE)
                } else {
                    Toast.makeText(this, "No se puede abrir camara", Toast.LENGTH_SHORT).show()
                }
                fotoboolean = false
            }else{
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                    startActivityForResult(takePictureIntent, RECUEST_CODE)
                } else {
                    Toast.makeText(this, "No se puede abrir camara", Toast.LENGTH_SHORT).show()
                }
                fotoboolean = true
            }
        }
    }

    private fun configurarBotones(miBoton: Button, titulo: String) {
        miBoton.setBackgroundColor(Color.BLUE)
        miBoton.setTextColor(Color.WHITE)
        miBoton.shadowColor.red
        miBoton.setText(titulo)
        miBoton.isEnabled = false


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (fotoboolean) {
            if (requestCode == RECUEST_CODE && resultCode == Activity.RESULT_OK) {
                val takenImage = data?.extras?.get("data") as Bitmap
                img1.setImageBitmap(takenImage)
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }else{
            if (requestCode == RECUEST_CODE && resultCode == Activity.RESULT_OK) {
                val takenImage = data?.extras?.get("data") as Bitmap
                img2.setImageBitmap(takenImage)
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

}