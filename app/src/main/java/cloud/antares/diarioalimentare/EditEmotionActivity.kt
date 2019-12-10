package cloud.antares.diarioalimentare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import cloud.antares.diarioalimentare.model.Emotion
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit_emotion.*

class EditEmotionActivity : AppCompatActivity() {
    private lateinit var realm: Realm
    private lateinit var emotion: Emotion
    private var isEdit: Boolean = false

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
            isEdit = true
        }
//        else {
//            deleteEmotionButton.isEnabled=false
//            deleteEmotionButton.isClickable = false
//            deleteEmotionButton.isVisible = false
//
//        }
//
//        deleteEmotionButton.setOnClickListener {
//            finish()
//        }
//
        emotion_ok_button.setOnClickListener {
            saveEmotion()
            finish()
        }

        emotion_cancel_button.setOnClickListener { finish() }
//
//        edit_emotion_toolbar.inflateMenu(R.menu.emotion_bottom_menu)
//        edit_emotion_toolbar.setOnMenuItemClickListener { onOptionsItemSelected(it) }
    }

    override fun onStop() {
        super.onStop()
        realm.close()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.emotion_bottom_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        invalidateOptionsMenu()
        if (isEdit) {
            menu?.findItem(R.id.edit_emotion_toolbar_delete)?.isEnabled = true
            menu?.findItem(R.id.edit_emotion_toolbar_delete)?.isVisible = true
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.edit_emotion_toolbar_ok -> {
                saveEmotion()
                finish()
                return true
            }
            R.id.edit_emotion_toolbar_cancel -> {
                finish()
                return true
            }
            R.id.edit_emotion_toolbar_delete -> {
//                if (isEdit) {
//                    realm.beginTransaction()
//                    emotion.deleteFromRealm()
//                    realm.commitTransaction()
//                }
                buildDeleteAlertDialog().show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun buildDeleteAlertDialog(): AlertDialog.Builder {
        val builder = AlertDialog.Builder(this)
        if (!isEdit) {
            finish()
        }
        builder.setTitle(R.string.delete_emotion_dialog_title)
        builder.setMessage(R.string.confirm_emotion_deletion_message)
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            if (isEdit) {
                realm.beginTransaction()
                emotion.deleteFromRealm()
                realm.commitTransaction()
                finish()
            }
        }
        builder.setIcon(R.drawable.ic_alert_warning)

        builder.setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel()}
        return builder
    }

    private fun saveEmotion() {
        var emoticon:String = emoticonForEmotionEditText.text.toString()
        var name:String = nameForEmotionEditText.text.toString()

        realm.beginTransaction()
        emotion.emoticon = emoticon
        emotion.name = name
        realm.copyToRealmOrUpdate(emotion)
        realm.commitTransaction()
    }
}
