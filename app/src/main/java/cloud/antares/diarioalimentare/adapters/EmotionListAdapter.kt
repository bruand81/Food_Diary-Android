package cloud.antares.diarioalimentare.adapters

import android.content.Intent
import android.view.*
import android.widget.PopupMenu
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import cloud.antares.diarioalimentare.EditEmotionActivity
import cloud.antares.diarioalimentare.R
import cloud.antares.diarioalimentare.model.Emotion
import io.realm.RealmResults
import kotlinx.android.synthetic.main.emotionrecyclerview_item_row.view.*

class EmotionListAdapter(val emotions: RealmResults<Emotion>, private val delegate: EmotionHolderDelegate): RecyclerView.Adapter<EmotionListAdapter.EmotionHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmotionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.emotionrecyclerview_item_row, parent, false)
        return EmotionHolder(view, delegate)
    }

    override fun getItemCount(): Int {
        return emotions.size
    }

    override fun onBindViewHolder(holder: EmotionHolder, position: Int) {
        val emotion = emotions[position]
        if(emotion != null){
            holder.bind(emotion)
        }
    }

    class EmotionHolder(v: View, private val delegate: EmotionHolderDelegate): RecyclerView.ViewHolder(v), View.OnClickListener, View.OnCreateContextMenuListener, View.OnLongClickListener {
        var view : View = v
        var emotion : Emotion? = null

        init {
            v.setOnClickListener(this)
            view.isLongClickable = true
            view.setOnLongClickListener(this)
        }

        fun bind(emotion: Emotion) {
            this.emotion = emotion
            this.view.emotionEmoticon.text = emotion.emoticon
            this.view.emotionName.text = emotion.name
        }

        override fun onClick(v: View?) {
//            val emotionIntent: Intent = Intent(view.context, EditEmotionActivity::class.java)
////            val emotionId:String = if (emotion != null) emotion!!._id else "none"
////            emotionIntent.putExtra("emotionID", emotionId)
////            startActivity(view.context, emotionIntent, null)
            delegate.editEmotion(view, emotion)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {}

        override fun onLongClick(v: View?): Boolean {
            val popup = PopupMenu(v?.context,v)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_contextual_menu_item -> {
                        println("Delete item ${this.emotion}")
                        return@setOnMenuItemClickListener delegate.deleteEmotion(v, emotion)
                    }
                    R.id.modify_contextual_menu_item -> {
                        println("Modify item ${this.emotion}")
                        return@setOnMenuItemClickListener delegate.editEmotion(v, emotion)
                    }
                    else -> true
                }
            }
            popup.inflate(R.menu.measure_unit_recycler_view_contex_menu)
            popup.gravity = Gravity.CENTER_HORIZONTAL

            popup.show()

            return true
        }

    }
    interface EmotionHolderDelegate {
        fun deleteEmotion(view:View?, emotion:Emotion?): Boolean
        fun editEmotion(view:View?, emotion:Emotion?): Boolean
    }
}