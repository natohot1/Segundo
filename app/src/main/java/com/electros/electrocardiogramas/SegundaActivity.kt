package com.electros.electrocardiogramas

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.electros.electrocardiogramas.databinding.ActivitySegundaBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*



private const val RECUEST_CAMARA = 1


class SegundaActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySegundaBinding

    var correo:String? = null
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
    val mifecha = sdf.format(Date())
    var filename:String = ""

    private val db = FirebaseFirestore.getInstance()



    private var fotoboolean = true
    private var btnFotoboolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySegundaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "ELECTROCARDIOGRAMAS"

        filename = UUID.randomUUID().toString()

        iniciarComponentes()

        binding.btnFotos.setOnClickListener {
            if(binding.editHistoria.text.length < 8){
                Toast.makeText(applicationContext, "H. CLINICA DEBE TENER 8 DIGITOS", Toast.LENGTH_LONG).show()
                return@setOnClickListener
                }
            if(binding.editNombre.text.length < 4){
                Toast.makeText(applicationContext, "REVISE NOMBRE", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            abreCamara_Click()
        }
        binding.btnGuardar.setOnClickListener {
            if(btnFotoboolean) {
                Toast.makeText(this, "Debes tomar las dos IMAGENES", Toast.LENGTH_LONG).show()
            }else{
                previoGuardar() { eventPost ->

                    if (eventPost.isSuccess) {
                        guardarDatosFirebase(eventPost.photoUrl, eventPost.photoUrl1)
                        startActivity(intent)
                    } else {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(
                            this,
                            "Error subiendo imágenes a Firebase Storage. Revisa tu conexión o reglas de Storage.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            }

        }
        binding.btnSalir.setOnClickListener {
            val prefs: SharedPreferences.Editor? = getSharedPreferences(("mipreferencia"), Context.MODE_PRIVATE).edit()
            prefs?.clear()
            prefs?.apply()

            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
            
            Toast.makeText(this, "HAS CERRADO SESIÓN", Toast.LENGTH_LONG).show()
            salirInicio()
            finish()
        }



        binding.btnGuardados.setOnClickListener {
            if (binding.editHistoria.text.length <8 ){
                Log.d("SegundaActivity","Historia tendra 8 digitos incluido 0 delante")
                Toast.makeText(applicationContext, "Historia tendra 8 digitos incluido 0 delante", Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(this, Tabla3::class.java)
                intent.putExtra("historia",binding.editHistoria.text.toString())
                startActivity(intent)
                binding.editHistoria.setText("")
            }
        }
    }

    private fun salirInicio(){
        val intent = Intent(this, SplashScreenActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Un archivo nuevo por foto (nombre único). createTempFile con el mismo prefijo
     * en algunos dispositivos ha llevado a leer/subir dos veces el mismo contenido.
     */
    private fun createUniqueImageFile(): File {
        val dir = File(filesDir, "ecg_captures").also { if (!it.exists()) it.mkdirs() }
        return File(dir, "ECG_${UUID.randomUUID()}.jpg").apply {
            if (exists()) delete()
            createNewFile()
        }
    }


    private fun iniciarComponentes() {
        val objetoInt: Intent=intent
        correo = objetoInt.getStringExtra("email")
        val prefs: SharedPreferences.Editor? = getSharedPreferences(("mipreferencia"), Context.MODE_PRIVATE).edit()
        prefs?.putString("correo",correo)
        prefs?.apply()

        configurarBotones(binding.btnFotos, "FOTOGRAFIAR")
        configurarBotones(binding.btnGuardados,"PREVIOS")
        configurarBotones(binding.btnGuardar,"GUARDAR DATOS")
        configurarBotones(binding.btnSalir,"CERRAR SESIÓN")
        binding.imagenPrimera.setImageResource(R.drawable.ecgnegro)
        binding.imagenSegunda.setImageResource(R.drawable.ecgnegro)
    }

    private fun previoGuardar(callback:(EvenPost)->Unit) {
        binding.progressBar.visibility = View.VISIBLE
        val evenPost = EvenPost()

        val file1 = captureFile1
        val file2 = captureFile2
        if (file2 == null || !file2.exists()) {
            evenPost.isSuccess = false
            binding.progressBar.visibility = View.INVISIBLE
            callback(evenPost)
            return
        }

        // La 1.ª foto se congela en memoria al cerrar la cámara: en algunos móviles la 2.ª captura
        // sobrescribe el primer fichero o la cámara sigue escribiendo en el URI anterior.
        val bytes1 = frozenPhoto1Jpeg
        if (bytes1 == null || bytes1.isEmpty()) {
            Toast.makeText(
                this,
                "Falta la primera imagen en memoria. Vuelve a tomar las dos fotos.",
                Toast.LENGTH_LONG
            ).show()
            evenPost.isSuccess = false
            binding.progressBar.visibility = View.INVISIBLE
            callback(evenPost)
            return
        }

        val bytes2 = frozenPhoto2Jpeg ?: file2.readBytes()
        if (bytes1.isEmpty() || bytes2.isEmpty()) {
            Toast.makeText(
                this,
                "Fichero de imagen vacío: vuelve a tomar las dos fotos.",
                Toast.LENGTH_LONG
            ).show()
            evenPost.isSuccess = false
            binding.progressBar.visibility = View.INVISIBLE
            callback(evenPost)
            return
        }
        val sha1 = sha256Short(bytes1)
        val sha2 = sha256Short(bytes2)
        Log.d(
            "SegundaActivity",
            "Subida: f1=frozen len=${bytes1.size} sha=$sha1 | f2=${file2.name} len=${bytes2.size} sha=$sha2 sameBytes=${bytes1.contentEquals(bytes2)}"
        )
        if (bytes1.contentEquals(bytes2)) {
            Toast.makeText(
                this,
                "Las dos fotos son idénticas en disco: vuelve a tomar la 2.ª imagen.",
                Toast.LENGTH_LONG
            ).show()
            evenPost.isSuccess = false
            binding.progressBar.visibility = View.INVISIBLE
            callback(evenPost)
            return
        }

        val storageRoot = FirebaseStorage.getInstance().reference.child("imagenes")
        val photoRef1 = storageRoot.child("$filename.jpg")
        val photoRef2 = storageRoot.child("${filename}_2.jpg")

        val meta = com.google.firebase.storage.StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()

        // putBytes desde File: no depende de cómo el ContentResolver resuelva el content://
        photoRef1.putBytes(bytes1, meta)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception ?: RuntimeException("Upload 1 failed")
                photoRef1.downloadUrl
            }
            .continueWithTask { url1Task ->
                val url1 = url1Task.result?.toString().orEmpty()
                evenPost.photoUrl = url1
                photoRef2.putBytes(bytes2, meta)
                    .continueWithTask { t2 ->
                        if (!t2.isSuccessful) throw t2.exception ?: RuntimeException("Upload 2 failed")
                        photoRef2.downloadUrl
                    }
            }
            .addOnSuccessListener { url2Uri ->
                val url2 = url2Uri?.toString().orEmpty()
                evenPost.photoUrl1 = url2
                evenPost.isSuccess =
                    evenPost.photoUrl.isNotBlank() && evenPost.photoUrl1.isNotBlank()
                Log.d("SegundaActivity", "OK storage: ${photoRef1.path} | ${photoRef2.path}")
                callback(evenPost)
            }
            .addOnFailureListener { e ->
                Log.e("SegundaActivity", "Error subiendo imagenes", e)
                evenPost.isSuccess = false
                binding.progressBar.visibility = View.INVISIBLE
                callback(evenPost)
            }
    }

    private fun sha256Short(data: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256").digest(data)
        return md.copyOfRange(0, 6).joinToString("") { "%02x".format(it) }
    }

    private fun guardarDatosFirebase(documentId:String, documentId1: String){


            val grupo2 = binding.editHistoria.text.toString()
            val grupo = "misHistorias"
            val user = User(binding.editNombre.text.toString(),mifecha,correo!!, filename,
                documentId,
                documentId1,
                grupo2

            )

            db.collection(grupo).document(filename).set(user).addOnCompleteListener {
                binding.editNombre.text.clear()
                binding.editHistoria.text.clear()
                binding.imagenPrimera.setImageResource(R.drawable.ecgnegro)
                binding.imagenSegunda.setImageResource(R.drawable.ecgnegro)
                tempImageUri = null
                tempImageUri2 = null
                captureFile1 = null
                captureFile2 = null
                frozenPhoto1Jpeg = null
                frozenPhoto2Jpeg = null
                fotoboolean = true
                btnFotoboolean = true
                binding.btnFotos.visibility = View.VISIBLE
                filename = UUID.randomUUID().toString()
                Toast.makeText(this, "Se ha guardado satisfactoriamente", Toast.LENGTH_LONG).show()
                binding.progressBar.visibility = View.INVISIBLE
                configurarBotones(binding.btnFotos, "FOTOGRAFIAR")
            }.addOnFailureListener {
                Toast.makeText(this, "No se pudo guardar", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.INVISIBLE
            }.addOnFailureListener {
                configurarBotones(binding.btnFotos, "FOTOGRAFIAR")
            }

    }

    private var tempImageUri: Uri? = null
    private var tempImageUri2: Uri? = null
    private var captureFile1: File? = null
    private var captureFile2: File? = null
    /** Copias inmutables al cerrar la cámara (evita que otra captura pise los ficheros antes de Guardar). */
    private var frozenPhoto1Jpeg: ByteArray? = null
    private var frozenPhoto2Jpeg: ByteArray? = null




    /** Dos contratos independientes para evitar que la cámara reutilice el URI de la 1.ª foto. */
    private val cameraPrimeraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                val f1 = captureFile1
                frozenPhoto1Jpeg = try {
                    f1?.takeIf { it.exists() && it.length() > 0 }?.readBytes()
                } catch (e: Exception) {
                    Log.e("SegundaActivity", "Leyendo foto 1", e)
                    null
                }
                tempImageUri?.let { revokeUriPermissionFromCameraApps(it) }
                binding.imagenPrimera.setImageURI(tempImageUri)
                fotoboolean = false
                btnFotoboolean = true
                configurarBotones(binding.btnFotos, "2º IMAGEN")
            }
        }

    private val cameraSegundaLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                val f2 = captureFile2
                frozenPhoto2Jpeg = try {
                    f2?.takeIf { it.exists() && it.length() > 0 }?.readBytes()
                } catch (e: Exception) {
                    Log.e("SegundaActivity", "Leyendo foto 2", e)
                    null
                }
                tempImageUri2?.let { revokeUriPermissionFromCameraApps(it) }
                binding.imagenSegunda.setImageURI(tempImageUri2)
                fotoboolean = true
                btnFotoboolean = false
                binding.btnFotos.visibility = View.INVISIBLE
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
                    captureFile1 = createUniqueImageFile()
                    tempImageUri =
                        FileProvider.getUriForFile(
                            this,
                            "${BuildConfig.APPLICATION_ID}.provider",
                            captureFile1!!
                        ).also { uri ->
                            grantUriPermissionForCapture(uri)
                        }
                    cameraPrimeraLauncher.launch(tempImageUri!!)

                }else{
                    captureFile2 = createUniqueImageFile()
                    tempImageUri2 =
                        FileProvider.getUriForFile(
                            this,
                            "${BuildConfig.APPLICATION_ID}.provider",
                            captureFile2!!
                        ).also { uri ->
                            grantUriPermissionForCapture(uri)
                        }
                    cameraSegundaLauncher.launch(tempImageUri2!!)
                }
        }
        else{
            Toast.makeText(this, "Camara no accesile", Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Otorga el URI solo a la app que realmente abrirá la cámara (evita que varias apps compartan
     * el mismo permiso y reutilicen el primer destino).
     */
    private fun grantUriPermissionForCapture(uri: Uri) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val cn: ComponentName = intent.resolveActivity(packageManager) ?: run {
            grantUriPermissionToAllCameraApps(uri)
            return
        }
        val flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        try {
            grantUriPermission(cn.packageName, uri, flags)
        } catch (e: SecurityException) {
            Log.w("SegundaActivity", "grantUri falló para ${cn.packageName}, probando lista completa", e)
            grantUriPermissionToAllCameraApps(uri)
        }
    }

    private fun grantUriPermissionToAllCameraApps(uri: Uri) {
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        val activities = packageManager.queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY)
        for (info in activities) {
            val pkg = info.activityInfo.packageName
            try {
                grantUriPermission(pkg, uri, flags)
            } catch (e: SecurityException) {
                Log.w("SegundaActivity", "No se pudo otorgar URI a $pkg", e)
            }
        }
    }

    /**
     * Quita lectura/escritura del URI para todas las apps (API 26+). En versiones anteriores no hay API equivalente.
     */
    private fun revokeUriPermissionFromCameraApps(uri: Uri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        try {
            revokeUriPermission(uri, flags)
        } catch (_: Exception) { }
    }

    private fun configurarBotones(miBoton: Button, titulo: String) {
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