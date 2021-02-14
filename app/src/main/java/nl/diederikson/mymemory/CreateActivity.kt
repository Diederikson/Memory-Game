package nl.diederikson.mymemory

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nl.diederikson.mymemory.models.BoardSize
import nl.diederikson.mymemory.models.MemoryGame
import nl.diederikson.mymemory.utils.EXTRA_BOARD_SIZE
import nl.diederikson.mymemory.utils.isPermissionGranted
import nl.diederikson.mymemory.utils.requestPermission

class CreateActivity : AppCompatActivity() {

    companion object{
        private const val PICK_PHOTO_CODE = 222 //Klaproos 222 (dezz doet er niet toe)
        private const val READ_EXTERNAL_PHOTOS_CODE = 3755 //Klaproos  (dezz doet er ook niet toe)
        private const val READ_PHOTOS_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE
        private const val TAG = "CreateActivity" // Voor de log message of the prime minister at r.113
    }
    //TC 1:59:30 na het aanmaken van de componenten in de design view maken we ze hier aan als variableen

    private lateinit var rvImagePicker : RecyclerView
    private lateinit var etGame: EditText
    private lateinit var btnSave : Button

    //en lateint: value is set in onCreate. Eigenlijk: de value is later gezet.

    private lateinit var boardSize: BoardSize
    private var numImagesRequired = -1
    private val chosenImageUris = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        rvImagePicker = findViewById(R.id.rvImagePicker)
        etGame = findViewById(R.id.etGameName)
        btnSave = findViewById(R.id.btnSave)
        //Hou je bek button werkt nu ff niet
        //supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        boardSize = intent.getSerializableExtra(EXTRA_BOARD_SIZE) as BoardSize
        numImagesRequired = boardSize.getNumPairs()
        supportActionBar?.title = "Choos pics (0 / $numImagesRequired)"
        //Questionmark operator: only reference the atrribute if suppActionbar is not null

        // Net als de memoryboard mainactivity recyclerview ook hier 2 corecomponents van recyclerview
        // te weten de adapter en de layout manager
        rvImagePicker.adapter = ImagePickerAdapter(this,chosenImageUris, boardSize, object:
                ImagePickerAdapter.ImageClickListener{
                override fun onPlaceholderClicked() {
                    if (isPermissionGranted(this@CreateActivity, READ_PHOTOS_PERMISSION)) {
                        launchIntentForPhotos()
                    } else {
                        requestPermission(
                            this@CreateActivity,
                            READ_PHOTOS_PERMISSION,
                            READ_EXTERNAL_PHOTOS_CODE
                        )
                    }
                }
        })
        rvImagePicker.setHasFixedSize(true)
        rvImagePicker.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }

    //TC2:30:31 AH momentje: We will get al callback onrequestPermission bladie bla
    //Wat daarmee bedoeld wordt: de requestpermission functie aanroep van r57 (die gedefinieerd is in de utils file
    //Aldaar wordt de Android corefunctie aangeroepen. Genereert een aanroep naar een specifieke functie )onRequestPermissionsResult
    //Die hieronder override wordt met onze eigen implementatie. We get a callback wil dus zeggen dat je een bepaalde aanroep naar een bepaalde
    //functie krijgt
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_EXTERNAL_PHOTOS_CODE){//Wel handig als het idd om onze aanroep gaat
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                launchIntentForPhotos()
            }else{
                Toast.makeText(this,"In order to ceate a custom game, you need to provide acces to your photo's",Toast.LENGTH_LONG).show()

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    //TC 2:22:30 onActivity result komt altijd hier terug dus overriden maarr
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // eerst requestcode checken. Is het inderdaad het picken van photos geweest waar we van terugkeren?
        // want dan weten we zeker dat we het resultaat verwerken van de juiste result.
        // En de indier pakt bij nader inzien de negatie zodat de gebruiker makkelijker gewaarschuwd kan worden
        // Als iets niet OK is

        if(requestCode != PICK_PHOTO_CODE || resultCode != Activity.RESULT_OK || data == null){
            Log.w(TAG, "Did not get data back from launched activity, user likely canceled flow.")
            return // Jammer, foutje bedankt
        }
        //Guys were in business! Er zijn twee mogelijkheden. De device support 1 foto selecteren of de device
        //support 2 fotos selecteren. Als 1 foto tegelijk ondersteund wordt is data.data aanwezig in de return.
        //bij multiple selection ondersteund is data.clipData ondersteund (TC2:24:19)
        val selectedUri = data.data
        val clipData = data.clipData
        if(clipData != null){
            Log.i(TAG,"clipData numImages ${clipData.itemCount}: $clipData")
            //[HG] van Hier gebleven. Zoek morgen op [HG} voor een snelle start. We zijn op TC 2:24:52
        }


    }

    private fun launchIntentForPhotos() {
         // Let ff op met android assistemt wat je kiest om het goed te krijgen.
        //TC 2:14:40
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        //TC2:16:52 Android geeft geen toestemming standaard om het filesysteem te benaderen.
        //Die toestemming wordt gegeven via de manifest file
        startActivityForResult(Intent.createChooser(intent, "Choose Pics"),PICK_PHOTO_CODE)
    }
}
