package nl.diederikson.mymemory

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import nl.diederikson.mymemory.models.BoardSize
import nl.diederikson.mymemory.models.MemoryCard
import kotlin.math.min

// 20210110 Hier gebleven; timecode 24:53. Abstract class gaat het over: dus methoden definieren;
// Maar zit nog met die <<>> haken. Die <> is de diamond/generic pattern, hebben we ooit al eens uitgezocht
// en die lege haakjes aan het einde is gewoon een arraynotatie. Het is van het type array.
// En dat klopt ook wel want je slaat de memorykaartjes hier op (mbv recyclerviewer
// Deez class is op de automaat gemaakt. Voor een herhaling van de uitleg zie de video TC22:51

//TC23:22 private val toevoegen in de header van de clas heeft als effect dat de variabelen automatisch
//beschikbaar zijn in de class (die hoef je dus niet over te pakken in een private var. Shortcut)
//TC23:46 ViewHolder is 1 memorycardje. Is lastig kom daar nog maar eens op terug
//TIL: Android heeft de parameter van de constructor aangepast naar BoardSize (was int)
class MemoryBoardAdapter(
        private val context: Context,
        private val boardSize: BoardSize,
        private val cards: List<MemoryCard>,
        private val cardClickListener : CardClickListener
) :
        RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {

    companion object {
        private const val MARGIN_SIZE =10
        //TAG is de label die we gebruiken om in de log message aan te geven waar het bericht vandaan kwam
        // Daar maken we bij afspraak de classname van. Zie ook de doc van de Log.i fucntie
        // Tijdens het runnen logcat aanklikken (rand onderaan) en niveau info kiezen en filteren op
        // MemoryBoardAdapter
        //
        private const val TAG = "MemoryBoardAdapter"
    }

    interface CardClickListener {
        fun onCardClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardWidth = parent.width / boardSize.getWidth() - (2 * MARGIN_SIZE)//twee kaarten op een rij met merge li en re eraf
        val cardHeigth = parent.height / boardSize.getHeight() - (2 * MARGIN_SIZE)// vier kaarten onder elkaar
        val cardSideLength = min(cardWidth,cardHeigth)
        val view = LayoutInflater.from(context).inflate(R.layout.memory_card,parent,false)
        val layoutParams = view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        //deze cast is nodig om bij de margins te kunnen komen (die zijn blijkbaar hogerop gedefinieeerd)
        //En je trekt de margin dus af van de cardsize, deze wordt kleiner, en je voegt te marge to aan
        // de leyout
        layoutParams.width = cardSideLength
        layoutParams.height = cardSideLength
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        return ViewHolder(view)
    }
    // Parent is de recyclerview, dus de layout waarin de kaartjes zitten. Dat is ook de maat om het oppervlak
    // eerlijk te verdelen  r.22. we nemen daarbij 1 maat: het minimum van de hoogte of de breedte r.24.
    // daardoor krijg je altijd vierkante kaartjes (video TC31:04)
    override fun getItemCount()= boardSize.numCards

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //Config van de clicklisteners
        private val imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)

        fun bind(position: Int) {
            val memoryCard = cards[position]
            imageButton.setImageResource(if(memoryCard.isFaceUp) memoryCard.identifier else R.drawable.ic_launcher_background)

            imageButton.alpha = if (memoryCard.isMatched) .4f else 1.0f //Float natuurlijk
            //Deze hele zeut: TC 01:14:20 +/-
            val colorStateList = if (memoryCard.isMatched) ContextCompat.getColorStateList(context, R.color.color_gray) else null
            ViewCompat.setBackgroundTintList(imageButton, colorStateList)

            imageButton.setOnClickListener{
                Log.i(TAG,"Clicked on position $position")
                cardClickListener.onCardClicked(position)
            }
        }
    }


}
