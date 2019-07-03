package cloud.antares.diarioalimentare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cloud.antares.diarioalimentare.model.Emotion
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit_emotion.*

class EditEmotionActivity : AppCompatActivity() {
    private lateinit var realm: Realm
    private lateinit var emotion: Emotion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_emotion)

        Realm.init(this)

        realm = Realm.getDefaultInstance()
        
        val emotionID:String = (intent.extras?.getString("emotionID", "none"))?:"none"
        emotion = realm.where<Emotion>().equalTo("_id", emotionID).findFirst() ?: Emotion()
        if(emotion._id == emotionID){
            emoticonForEmotionEditText.setText(emotion.emoticon)
            nameForEmotionEditText.setText(emotion.name)
        }

        emotion_ok_button.setOnClickListener {
            var emoticon:String = emoticonForEmotionEditText.text.toString()
            var name:String = nameForEmotionEditText.text.toString()

            realm.beginTransaction()
            emotion.emoticon = emoticon
            emotion.name = name
            realm.copyToRealmOrUpdate(emotion)
            realm.commitTransaction()

            finish()
        }

        emotion_cancel_button.setOnClickListener { finish() }
    }

    override fun onStop() {
        super.onStop()
        realm.close()
    }
}
