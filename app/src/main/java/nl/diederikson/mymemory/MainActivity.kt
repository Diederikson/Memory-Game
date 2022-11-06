package nl.diederikson.mymemory

import android.animation.ArgbEvaluator
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import nl.diederikson.mymemory.models.BoardSize
import nl.diederikson.mymemory.models.MemoryCard
import nl.diederikson.mymemory.models.MemoryGame
import nl.diederikson.mymemory.utils.DEFAULT_ICONS
import nl.diederikson.mymemory.utils.EXTRA_BOARD_SIZE
import nl.diederikson.mymemory.utils.EXTRA_GAME_NAME

class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "MainActivity"
        private const val CREATE_REQUEST_CODE = 3755 // [2ndpass Wat is de tweede param van startactivityforresult)

    }

    private lateinit var clRoot: ConstraintLayout
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView

    private val db = Firebase.firestore
    private var gameName: String? = null
    private lateinit var memoryGame: MemoryGame
    private lateinit var adapter: MemoryBoardAdapter
    private var boardSize : BoardSize = BoardSize.HARD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clRoot = findViewById(R.id.clRoot) //deez nog een keer laten inwerken TC1:18:52
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)

        //Hack to start cardpicking directly for programming purpose
        //val intent = Intent(this, CreateActivity::class.java)
        //intent.putExtra(EXTRA_BOARD_SIZE, BoardSize.MEDIUM)
        //startActivity(intent)

        setUpBoard()// alle zaken uitgekopierd naar een nieuwe private function
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu) // menu is de menu want je moet er wel bij kunnen
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){ //soort select neem ik dan maar aan [2ndpass]
            R.id.mi_refresh -> {
                if(memoryGame.getNumMoves()>0 && !memoryGame.haveWonGame()){
                    showAlertDialog("Quit the current game?",null, View.OnClickListener {
                        setUpBoard()
                    } )
                }else{
                    setUpBoard()
                }
                return true
            }
            R.id.mi_new_size -> {
                showNewSizeDialog()
                return true
            }
            R.id.mi_custom -> {
                showCreationDialog()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CREATE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val customGameName = data?.getStringExtra(EXTRA_GAME_NAME)//retrieving the game
            if (customGameName == null){
               Log.e(TAG,"Got null custom game from CreateActivity")
               return
            }
            downloadGame(customGameName)
            //Hier gebleven!
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun downloadGame(gameName: String) {
        TODO("Not implemented")
    }

    private fun showCreationDialog() {
        // Hoen groot bord wordet. Want zoveel kaarten hebbie nodig. Deez dialoog lijkt erg op
        // showsizedialog. Vnadaar dat we de code daarvan kopeiren en aanpassen
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size,null )
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        //Die boardsizeview moet mee als lifeline teerug
        showAlertDialog("Create your own memory board",boardSizeView, View.OnClickListener {
            //set a new value for the board size in de desgin layout hebben we easy op default gezet
            //Met checked : TC1:44:50
            val desiredBoardsize: BoardSize   = when (radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            //setUpBoard() niet setup maar navigate to new screen (Activity)
            val intent = Intent(this,CreateActivity::class.java)
            //Dan startactivity of startactivityfor result dat laatste is als je iets terug wilt bekomen
            intent.putExtra(EXTRA_BOARD_SIZE,desiredBoardsize)
            //TC01:49:01 deez constante wordt in meerdere files gereft vandaar in de aparte constant file
            startActivityForResult(intent,CREATE_REQUEST_CODE) //[2ndpass]]
        })

    }

    private fun showNewSizeDialog() {
        // Een view is dus een stukje scherm met functionaliteit (TC1:37:11)
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size,null )
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        //Juiste radiobutton ff voorzetten voor je het menu laat zien (TC 1:40:33)
        when (boardSize){
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD -> radioGroupSize.check(R.id.rbHard)
        }
        //Die boardsizeview moet mee als lifeline teerug
        showAlertDialog("Choose the board size",boardSizeView, View.OnClickListener {
            //set a new value for the board size
            boardSize = when (radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            setUpBoard()
        })
    }

    private fun showAlertDialog(title: String, view: View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setView(view)
                .setNegativeButton("Cancel", null) //null is: doe niets igv cancel
                .setPositiveButton("OK") {_,_-> // Syntax om aan te geven dat je een deel van de signatuue weglaat TC1.33.05
                    positiveClickListener.onClick(null )
                }.show()
    }

    private fun setUpBoard() {
        when (boardSize){
            BoardSize.EASY -> {
                tvNumMoves.text = "Easy: 4 x 2 "
                tvNumPairs.text = "0 / 4"
            }
            BoardSize.MEDIUM ->{
                tvNumMoves.text = "Medium: 6 x 3 "
                tvNumPairs.text = "0 / 9"
            }
            BoardSize.HARD ->{
                tvNumMoves.text = "Hard: 6 x 4 "
                tvNumPairs.text = "0 / 12"
            }
        }
        tvNumPairs.setTextColor(ContextCompat.getColor(this,R.color.color_progress_none))
        memoryGame = MemoryGame(boardSize)



        // De recyleview bestaat uit 2 onderdelen de layoutmanager, die de componenten op hun plek zet
        // en de adapter, die de layout verbindt met data logiga (de brain uit de studio 7 app)
        // sethasfixedsize is nog een optimization method. we werken hier met fixed size kennelijk

        // omdat numpieces het totaalaantal elementen aangeeft en je weet dat de span 2 is, heb je
        // vervolgens ook het aantal rijen te pakken (namelijk 4)

        // Adapter even als voorbeelf (geldt ook voor momorygame. Deze twee lokale variabelen worden
        // omgezet van lokale method variabelen naar properties van de Main activity class, zodat ze
        // Beschikbaar komen voor andere methodes en niet alleen onCreate  [2ndpass] TC 01:02:29 ev
        adapter = MemoryBoardAdapter(this, boardSize, memoryGame.cards, object: MemoryBoardAdapter.CardClickListener{
            override fun onCardClicked(position: Int) {
                updateGameWithFlip(position)
            }

        })
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true) //TC 22:17  er worden altijd een vast aantal kaarten getoont=> optimalisatie
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth() ) //TC 21:14
    }
    private fun updateGameWithFlip(position: Int) {
        //Net als Brain bij de Jeugdjournaal app wordt de afhandeling van de click gedelegeerd aan
        //de klasse memorygame. hier moeten ook de bad states worden afgevangen.
        if(memoryGame.haveWonGame()){
            Snackbar.make(clRoot, "You allready won!", Snackbar.LENGTH_LONG).show()
            return // begrijp ff niet goed wat haveongame betekend. Zal wel bedoelen kaart match.
            //in elk geval doe je niks dat snap ik
            //En de massage to the user
            //clRoot haak je aan in het design scherm (TC1:18:38)
        }
        if(memoryGame.isCardFaceUp(position)){
            //kaart is gedraaid, magge niet terug omdraaien
            Snackbar.make(clRoot, "DON'T touch that Card!", Snackbar.LENGTH_SHORT).show()
            return
            // en een dikke vette ongemakkelijke foutmelding

        }
        // Flip den cards al dan niet met match
        if (memoryGame.flipCard(position)){ //foudamatch in memorygame is true als er een identieke kaard gevonden wordt [2ndpass[
            Log.i(TAG, "Founda match! Numpairs found: ${memoryGame.numPairsFound}")
            val color = ArgbEvaluator().evaluate(
                    memoryGame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                    ContextCompat.getColor(this,R.color.color_progress_none),
                    ContextCompat.getColor(this,R.color.color_progress_full)
            ) as Int
            tvNumPairs.setTextColor(color)
            tvNumPairs.text = "Pairs: ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
            if (memoryGame.haveWonGame()){
                Snackbar.make(clRoot, "Congrats, you win!",Snackbar.LENGTH_LONG).show()
            }
        }
        tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }
}