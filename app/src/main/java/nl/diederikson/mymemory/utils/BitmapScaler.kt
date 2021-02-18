package nl.diederikson.mymemory.utils

import android.graphics.Bitmap

object BitmapScaler { // en dat object is een soort static modifier?!
    //Dit is gewoon een papiertje om de Android API heen
    //Scale and maintain aspect ration given a desired width
    //BitmapScaler.scaleToFitWidth(bitmap, 100)
    fun scaleToFitWidth(b: Bitmap, width: Int): Bitmap{
        val factor = width / b.width.toFloat()
        return Bitmap.createScaledBitmap(b, width, (b.height * factor).toInt(), true)
    }

    // Scale and maintain aspectration given a desired height
    fun scaleToFitHeight(b: Bitmap, height:Int): Bitmap{
        val factor = height / b.height.toFloat()
        return Bitmap.createScaledBitmap(b, (b.width * factor).toInt(), height, true)
    }


}
