package com.example.segundo

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.graphics.red
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_segunda.*

private const val RECUEST_CAMARA = 1

class SegundaActivity : AppCompatActivity() {
    var foto: Bitmap? = null
    var foto1: Bitmap? = null
    var foto2: Bitmap? = null
    private var fotoboolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segunda)

        configurarBotones(btnFotos, "FOTOGRAFIAR")
        configurarBotones(btnGuardados,"PREVIOS")
        configurarBotones(btnGuardar,"GUARDAR DATOS")
        configurarBotones(btnSalir,"SALIR")

        editHistoria.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i("begoskndksjnj", "not overide")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(editHistoria.length() == 8 && editNombre.length() >4){
                    btnFotos.isEnabled = true
                    btnGuardar.isEnabled = true
                    btnGuardados.isEnabled = true
                    btnSalir.isEnabled = true
                }else{
                    btnFotos.isEnabled = false
                    btnGuardar.isEnabled = false
                    btnGuardados.isEnabled = false
                    btnSalir.isEnabled = true
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.i("begoskndksjnj", "not overide")
            }

        })

        editNombre.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i("begoskndksjnj", "not overide")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(editHistoria.length() == 8 && editNombre.length() >4){
                    btnFotos.isEnabled = true
                    btnGuardar.isEnabled = true
                    btnGuardados.isEnabled = true
                    btnSalir.isEnabled = true
                }else{
                    btnFotos.isEnabled = false
                    btnGuardar.isEnabled = false
                    btnGuardados.isEnabled = false
                    btnSalir.isEnabled = true
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.i("begoskndksjnj", "not overide")
            }

        })



        btnFotos.setOnClickListener {
            abreCamara_Click()
        }
    }

    //detectamos el click para abrir camara
    private fun abreCamara_Click(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                // PEDIMOS PERMIDOS
                val permisosCamara = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permisosCamara, RECUEST_CAMARA)
            }else
                abreCamara()

        }else{
            abreCamara()
        }
    }

    private fun abreCamara(){
        val value = ContentValues()
        value.put(MediaStore.Images.Media.TITLE, "Nueva imagen")
        val camaraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        camaraIntent.putExtra(MediaStore.EXTRA_OUTPUT,foto)
        startActivityForResult(camaraIntent, RECUEST_CAMARA)

    }

    private fun configurarBotones(miBoton: Button, titulo: String) {
        miBoton.setBackgroundColor(Color.BLUE)
       // miBoton.setTextColor(Color.WHITE)
        miBoton.shadowColor.red
        miBoton.setText(titulo)
        miBoton.isEnabled = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            RECUEST_CAMARA -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    abreCamara()
                else
                    Toast.makeText(applicationContext, "No puedes acceder a la camara", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (fotoboolean) {
            if (resultCode == Activity.RESULT_OK && requestCode == RECUEST_CAMARA) {
                foto1 = data?.getParcelableExtra<Bitmap>("data")
                imagenPrimera.setImageBitmap(foto1)
                fotoboolean = false
            }
        } else {
            if (resultCode == Activity.RESULT_OK && requestCode == RECUEST_CAMARA) {
                foto2 = data?.getParcelableExtra<Bitmap>("data")
                imagenSegunda.setImageBitmap(foto2)
                fotoboolean = true
            }
        }
    }


}