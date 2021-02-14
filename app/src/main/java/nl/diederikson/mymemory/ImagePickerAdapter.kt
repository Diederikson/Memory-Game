package nl.diederikson.mymemory

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import nl.diederikson.mymemory.models.BoardSize
import kotlin.math.min

class ImagePickerAdapter(
        private val context : Context,
        private val imageUris: List<Uri>,
        private val boardSize: BoardSize,
        private val imageClickListener: ImageClickListener
) : RecyclerView.Adapter<ImagePickerAdapter.ViewHolder>(){

    interface ImageClickListener {
        fun onPlaceholderClicked()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_image,parent,false)
        val cardWidth = parent.width / boardSize.getWidth()
        val cardHeight = parent.height / boardSize.getHeight()
        val cardSidelength = min(cardWidth,cardHeight)
        val layoutParams = view.findViewById<ImageView>(R.id.ivCustomimage).layoutParams
        layoutParams.width = cardSidelength
        layoutParams.height = cardSidelength
        return ViewHolder(view)
    }

    override fun getItemCount() = boardSize.getNumPairs()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < imageUris.size){
            holder.bind(imageUris[position])
        }else{
            holder.bind()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val ivCustomImage = itemView.findViewById<ImageView>(R.id.ivCustomimage)

        fun bind(uri:Uri) {
            ivCustomImage.setImageURI(uri)
            ivCustomImage.setOnClickListener(null)
        }

        fun bind() {
            ivCustomImage.setOnClickListener{
                //lanch intent for User to seletc Photo's
                imageClickListener.onPlaceholderClicked()
            }
        }

    }
}
