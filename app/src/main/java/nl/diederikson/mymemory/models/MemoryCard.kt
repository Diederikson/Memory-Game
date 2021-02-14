package nl.diederikson.mymemory.models

data class MemoryCard(
        // verschil tussen var en val in Ktlin. Val is value, staat vast kan niet veranderen
        // var staat niet vast kan wel veranderen
        val identifier: Int,
        var isFaceUp: Boolean = false,
        var isMatched: Boolean = false

)