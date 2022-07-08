package com.example.segundo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_segunda.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*



private const val RECUEST_CAMARA = 1
private lateinit var photoFile: File
private const val FILE_NAME = "photo.jpg"

class SegundaActivity : AppCompatActivity() {
    var foUri3:Uri? = null
    var foUri4:Uri? = null
    var fotoURL1:String = ""
    var fotoURL2:String = ""

    var ur1: String = ""
    var ur2: String? = ""

    var correo:String? = null
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
    val mifecha = sdf.format(Date())
    var filename:String = ""

    var mini:File? = null


    private val db = FirebaseFirestore.getInstance()



    private var fotoboolean = true
    private var btnFotoboolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segunda)
        title = "ELECTROCARDIOGRAMAS"

        filename = UUID.randomUUID().toString()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        iniciarComponentes()

        btnFotos.setOnClickListener {
            if(editHistoria.text.length < 8){
                Toast.makeText(applicationContext, "H. CLINICA DEBE TENER 8 DIGITOS", Toast.LENGTH_LONG).show()
                return@setOnClickListener
                }
            if(editNombre.text.length < 4){
                Toast.makeText(applicationContext, "REVISE NOMBRE", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            abreCamara_Click()
        }
        btnGuardar.setOnClickListener {
            if(btnFotoboolean) {
                Toast.makeText(this, "Debes tomar las dos IMAGENES", Toast.LENGTH_LONG).show()
            }else{
                previoGuardar() { eventPost ->

                    if (eventPost.isSuccess) {
                        guardarDatosFirebase(eventPost.photoUrl!!, eventPost.photoUrl1!!)
                        startActivity(intent)


                    }
                }

            }

        }
        btnSalir.setOnClickListener {
            val prefs: SharedPreferences.Editor? = getSharedPreferences(("mipreferencia"), Context.MODE_PRIVATE).edit()
            prefs?.clear()
            val apply = prefs?.apply()
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "HAS SALIDO DE ECG+", Toast.LENGTH_LONG).show()
            onBackPressed()
        }
        btnGuardados.setOnClickListener {
            if (editHistoria.text.length <8 ){
                Log.d("SegundaActivity","Historia tendra 8 digitos incluido 0 delante")
                Toast.makeText(applicationContext, "Historia tendra 8 digitos incluido 0 delante", Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(this,Tabla3::class.java)
                intent.putExtra("historia",editHistoria.text.toString())
                startActivity(intent)
                editHistoria.setText("")
            }
        }
    }

    private fun createImageFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("temp_image", "jpg", storageDir)
    }

    private fun createImageFile2(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("temp_image", "png", storageDir)
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
    }

    private fun previoGuardar(callback:(EvenPost)->Unit) {
        progressBar.visibility = View.VISIBLE
        val nombre1 = filename
        val evenPost = EvenPost()


        val grupo = editHistoria.text.toString()
        evenPost.documentId = FirebaseFirestore.getInstance().collection(grupo).document().id
        val storageRef = FirebaseStorage.getInstance().reference.child("imagenes/$nombre1")

        //primero verificamos si tempImageUri no esta vacio
        tempImageUri?.let { uri ->
            val photoRef = storageRef.child(nombre1)

           // val ref = FirebaseStorage.getInstance().getReference("/imagenes/$nombre1")
            photoRef.putFile(tempImageUri!!)
                .addOnSuccessListener {
                    it.storage.downloadUrl.addOnSuccessListener { dowloadURL ->
                        evenPost.isSuccess = true
                        evenPost.photoUrl = dowloadURL.toString()
                        callback(evenPost)
                    }
                    Log.d("SegundaActivity", "subio imagen: ${it.metadata?.path}")
                }
                .addOnFailureListener{
                    evenPost.isSuccess = false
                    callback(evenPost)
                }
        }


        val nombre2 = filename+"a"

        evenPost.documentId1 = FirebaseFirestore.getInstance().collection(grupo).document().id
        val storageRef1 = FirebaseStorage.getInstance().reference.child("imagenes/$nombre2")


        tempImageUri2?.let {uri1 ->
           // val ref2 = FirebaseStorage.getInstance().getReference("/imagenes/$nombre2")
           // val photoRef1 = storageRef1.child(evenPost.documentId1!!)
            val photoRef1 = storageRef1.child(nombre2)
            photoRef1.putFile(tempImageUri2!!)
                .addOnSuccessListener {
                    it.storage.downloadUrl.addOnSuccessListener { dowloadURL ->
                        evenPost.isSuccess = true
                        evenPost.photoUrl1 = dowloadURL.toString()
                        callback(evenPost)
                    }
                    Log.d("SegundaActivity", "subio imagen: ${it.metadata?.path}")
                }
                .addOnFailureListener {
                    evenPost.isSuccess = false
                    callback(evenPost)
                }

        }

    }

    private fun guardarDatosFirebase(documentId:String, documentId1: String){

            val grupo = editHistoria.text.toString()
            val user = User(editNombre.text.toString(),mifecha,correo!!, filename,
                documentId,
                documentId1
            )

            db.collection(grupo).document(mifecha).set(user).addOnCompleteListener {
                editNombre.text.clear()
                editHistoria.text.clear()
                imagenPrimera.setImageResource(R.drawable.ecgnegro)
                imagenSegunda.setImageResource(R.drawable.ecgnegro)
                Toast.makeText(this, "Se ha guardado satisfactoriamente", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.INVISIBLE
            }.addOnFailureListener {
                Toast.makeText(this, "No se pudo guardar", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.INVISIBLE
            }.addOnFailureListener {

            }

    }

    private var tempImageUri: Uri? = null
    private var tempImageUri2: Uri? = null
    private var tempImageFilePath = ""




    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()){ success ->
        if (success){
            if (fotoboolean){
                imagenPrimera.setImageURI(tempImageUri)
                fotoboolean = false
                btnFotoboolean = true
            }else{
                imagenSegunda.setImageURI(tempImageUri2)
                fotoboolean = true
                btnFotoboolean = false
            }
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
    val tomarFotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    private fun abreCamara(){
     //  val tomarFotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (tomarFotoIntent.resolveActivity(this.packageManager) != null) {

                if (fotoboolean) {
                    tempImageUri = FileProvider.getUriForFile(
                        this,
                        "com.example.segundo.provider",
                        createImageFile().also {
                            tempImageFilePath = it.absolutePath
                        })
                    cameraLauncher.launch(tempImageUri)

                }else{
                    tempImageUri2 = FileProvider.getUriForFile(
                        this,
                        "com.example.segundo.provider",
                        createImageFile().also {
                            tempImageFilePath = it.absolutePath
                        })
                    cameraLauncher.launch(tempImageUri2)
                }
        }
        else{
            Toast.makeText(this, "Camara no accesile", Toast.LENGTH_SHORT).show()
        }

    }

    private fun configurarBotones(miBoton: Button, titulo: String) {
        miBoton.setBackgroundColor(Color.BLUE)
        miBoton.setTextColor(Color.WHITE)
        miBoton.text = titulo
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray
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

 //   override fun onCreateOptionsMenu(menu: Menu?): Boolean {
 //       menuInflater.inflate(R.menu.menu_main, menu)
 //       return super.onCreateOptionsMenu(menu)
 //   }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.acerca ->{
                Toast.makeText(this,"Programa",Toast.LENGTH_LONG).show()
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Electrocardiogramas, es una solucion sensilla para consulta de documentos medicos u otra indole, sin necesidad de software complejo")
            }
        }
        return super.onOptionsItemSelected(item)
    }
}