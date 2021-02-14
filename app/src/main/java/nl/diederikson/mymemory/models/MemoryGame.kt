package nl.diederikson.mymemory.models

import nl.diederikson.mymemory.utils.DEFAULT_ICONS


class MemoryGame (val boardSize: BoardSize){
    val cards: List<MemoryCard>
    var numPairsFound = 0
    //Int? is nullable int
    private var numCardFlips = 0
    private var indexOfSingleSelectedCard: Int? = null

    init{
        val chosenImages = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedImages  = (chosenImages + chosenImages).shuffled()
        //it is de iterator, de parameter die over de map heenloopt (moet toch
        cards = randomizedImages.map{MemoryCard(it)}// faceup en itmatsched in de formal params hebben een default, hoeven dus niet genoemd te worden
        //in de aanroep
    }

    fun flipCard(position: Int) :Boolean{
        numCardFlips++
        val card = cards[position]
        // Three cases/states en state transitions
        // 1. nog geen kaarten omgedraaid ==> flip over selected card
        // 2. 1 kaart omgedraaid ==> flip over selected card and check=> if match notyfy user and deactivate flipping
        // 3. 2 kkarten omgedraaid ==> draai 2 kaarten terug en flip over selected card en die is
        // te combimneren met 1, want kaart omdraaien doe je toch
        var foundMatch = false
        if (indexOfSingleSelectedCard == null) {
            //0 or 2 cards flipped over
            restoreCards()
            indexOfSingleSelectedCard = position
        }else{
            // 1 card previously flipped over, double excamationmark TC01:11:20
            foundMatch = checkForMatch(indexOfSingleSelectedCard!!, position)
            indexOfSingleSelectedCard = null //reset TC 01:11:38
        }
        card.isFaceUp = !card.isFaceUp
        return foundMatch
    }
//: is return type
    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        if (cards[position1].identifier != cards[position2].identifier){
            return false
        }
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound++
        return true
    }

    private fun restoreCards() {
        for (card in cards){
            if (!card.isMatched){
                card.isFaceUp = false
            }
        }
    }

    fun haveWonGame(): Boolean {
        return numPairsFound == boardSize.getNumPairs() // het is dus echt spel winnen alle combis zijn gevonden
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumMoves(): Int { // rounding down
        return numCardFlips / 2
    }
}
