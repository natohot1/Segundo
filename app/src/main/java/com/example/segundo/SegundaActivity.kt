package com.example.segundo

import Paciente
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_segunda.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

private const val RECUEST_CAMARA = 1

class SegundaActivity : AppCompatActivity() {

    var foto: Bitmap? = null
    var foto1: Bitmap? = null
    var foto2: Bitmap? = null
    var foUri3:Uri? = null
    var foUri4:Uri? = null

    var datos: Bundle? = null
    var correo:String? = null

    private var fotoboolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segunda)

        val objetoInt: Intent=intent

        correo = objetoInt.getStringExtra("email")

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
        btnGuardar.setOnClickListener {
            guardarDatosFirebas()
        }
    }

    private fun guardarDatosFirebas() {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/imagenes/$filename")
        ref.putFile(foUri3!!)
            .addOnSuccessListener {
                Log.d("SegundaActivity","subio imagen: ${it.metadata?.path}")
            }
        val filename2 = UUID.randomUUID().toString()
        val ref2 = FirebaseStorage.getInstance().getReference("/imagenes/$filename2")
        ref2.putFile(foUri4!!)
            .addOnSuccessListener {
                Log.d("SegundaActivity","subio imagen: ${it.metadata?.path}")
            }
        val sdf = SimpleDateFormat("dd/M/yyyy hh.mm.ss")
        val mifecha = sdf.format(Date())


        val grupo = editHistoria.text.toString()
        val database = FirebaseDatabase.getInstance().reference
        val paciente = Paciente(correo!!,mifecha,filename,ref.toString(),ref2.toString(),editNombre.text.toString())
        database.child(grupo).setValue(paciente).addOnCompleteListener {
            editNombre.text.clear()
            editHistoria.text.clear()
            Toast.makeText(this,"Guardado", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this,"No se pudo guardar", Toast.LENGTH_SHORT).show()
        }








    }

    private fun guardarfotoStorege(miUri:Uri) {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/imagenes/$filename")
        ref.putFile(miUri!!)
            .addOnSuccessListener {
                Log.d("SegundaActivity","subio imagen: ${it.metadata?.path}")
            }

    }

    val getAction = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(fotoboolean) {
            val bitmap = it?.data?.extras?.get("data") as Bitmap
            foUri3 = getImageUriFromBitmap(this,bitmap)
            imagenPrimera.setImageURI(foUri3)
            fotoboolean = false
        }else{
            val bitmap = it?.data?.extras?.get("data") as Bitmap
            foUri4 = getImageUriFromBitmap(this,bitmap)
            imagenSegunda.setImageURI(foUri4)
            fotoboolean = true
        }
    }

    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
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
       // val value = ContentValues()
       // value.put(MediaStore.Images.Media.TITLE, "Nueva imagen")
        val camaraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
       // camaraIntent.putExtra(MediaStore.EXTRA_OUTPUT,foto)
      //  startActivityForResult(camaraIntent, RECUEST_CAMARA)
        getAction.launch(camaraIntent)

    }

    private fun configurarBotones(miBoton: Button, titulo: String) {
        miBoton.setBackgroundColor(Color.BLUE)
       // miBoton.setTextColor(Color.WHITE)
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
}