package cloud.antares.diarioalimentare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cloud.antares.diarioalimentare.model.Emotion
import io.realm.RealmResults
import kotlinx.android.synthetic.main.emotionrecyclerview_item_row.view.*

class EmotionListAdapter(val emotions: RealmResults<Emotion>): RecyclerView.Adapter<EmotionListAdapter.EmotionHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmotionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.emotionrecyclerview_item_row, parent, false)
        return EmotionHolder(view)
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

    class EmotionHolder(v: View): RecyclerView.ViewHolder(v), View.OnClickListener {
        var view : View = v
        var emotion : Emotion? = null

        init {
            v.setOnClickListener(this)
        }

        fun bind(emotion: Emotion) {
            this.emotion = emotion
            this.view.emotionEmoticon.text = emotion.emotion
            this.view.emotionName.text = emotion.name
        }

        override fun onClick(v: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}