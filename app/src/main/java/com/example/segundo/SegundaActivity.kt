package com.example.segundo

import Paciente
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.LauncherActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.rpc.context.AttributeContext
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_segunda.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

private const val RECUEST_CAMARA = 1
private lateinit var photoFile: File
private const val FILE_NAME = "photo.jpg"

class SegundaActivity : AppCompatActivity() {


    var foUri3:Uri? = null
    var foUri4:Uri? = null

    var correo:String? = null

    private val db = FirebaseFirestore.getInstance()

    private var fotoboolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segunda)

        iniciarComponentes()

        btnFotos.setOnClickListener {
            if(editHistoria.text.toString() != "" && editNombre.text.toString() != ""){
            abreCamara_Click()}
        }
        btnGuardar.setOnClickListener {
            if(editHistoria.text.toString() != "" && editNombre.text.toString() != ""){
            guardarDatosFirebas()}
        }
        btnSalir.setOnClickListener {
            val prefs: SharedPreferences.Editor? = getSharedPreferences(("mipreferencia"), Context.MODE_PRIVATE).edit()
            prefs?.clear()
            val apply = prefs?.apply()
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
        btnGuardados.setOnClickListener {
            if (editHistoria.text.toString().length <7 || editHistoria.text.toString().length >9){
                Log.d("SegundaActivity","Historia tendra 8 digitos incluido 0 delante")
                Toast.makeText(applicationContext, "Historia tendra 8 digitos incluido 0 delante", Toast.LENGTH_LONG).show()
            }else{
                val intent = Intent(this,Tabla3::class.java)
                intent.putExtra("historia",editHistoria.text.toString())
                startActivity(intent)
            }
        }
    }

    private fun iniciarComponentes() {
        val objetoInt: Intent=intent
        correo = objetoInt.getStringExtra("email")
        val prefs: SharedPreferences.Editor? = getSharedPreferences(("mipreferencia"), Context.MODE_PRIVATE).edit()
        prefs?.putString("correo",correo)
        prefs?.apply()

        configurarBotones(btnFotos, "FOTOGRAFIAR")
        configurarBotones(btnGuardados,"PREVIOS")
        configurarBotones(btnGuardar,"GUARDAR DATOS")
        configurarBotones(btnSalir,"SALIR")
        imagenPrimera.setImageResource(R.drawable.ecgnegro)
        imagenSegunda.setImageResource(R.drawable.ecgnegro)

        editHistoria.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i("begoskndksjnj", "not overide")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(editHistoria.length() == 8 ){
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
    }

    private fun guardarDatosFirebas() {
        progressBar.visibility = View.VISIBLE
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
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
        val mifecha = sdf.format(Date())


        val grupo = editHistoria.text.toString()
        val paciente = Paciente(correo!!,mifecha,ref.toString(),ref2.toString(),editNombre.text.toString())
        db.collection(grupo).document(mifecha).set(paciente).addOnCompleteListener {
            editNombre.text.clear()
            editHistoria.text.clear()
            imagenPrimera.setImageResource(R.drawable.ecg)
            imagenSegunda.setImageResource(R.drawable.ecg)
            Toast.makeText(this,"Guardado", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.INVISIBLE
        }.addOnFailureListener {
            Toast.makeText(this,"No se pudo guardar", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.INVISIBLE
        }

    }


    val getAction = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        if(fotoboolean) {
               val tomarImagen = BitmapFactory.decodeFile(photoFile.absolutePath)
            foUri3 = getImageUriFromBitmap(this,tomarImagen)
            imagenPrimera.setImageBitmap(tomarImagen)

            fotoboolean = false
        }else{
            val tomarImagen = BitmapFactory.decodeFile(photoFile.absolutePath)
            foUri4 = getImageUriFromBitmap(this,tomarImagen)
            imagenSegunda.setImageBitmap(tomarImagen)

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
        val tomarFotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile(FILE_NAME)
        val fileProvider = FileProvider.getUriForFile(this, "com.example.fileprovider", photoFile)
        tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        if (tomarFotoIntent.resolveActivity(this.packageManager) != null) {
            getAction.launch(tomarFotoIntent)
        }else{
            Toast.makeText(this, "Camara no accesile", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, "jpg",storageDirectory)
    }

    private fun configurarBotones(miBoton: Button, titulo: String) {
        miBoton.setBackgroundColor(Color.BLUE)
        miBoton.setTextColor(Color.WHITE)
        miBoton.text = titulo
       // miBoton.isEnabled = false
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