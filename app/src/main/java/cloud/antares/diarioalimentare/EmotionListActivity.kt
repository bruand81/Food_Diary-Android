package cloud.antares.diarioalimentare

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cloud.antares.diarioalimentare.adapters.EmotionListAdapter
import cloud.antares.diarioalimentare.model.Emotion
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.where

import kotlinx.android.synthetic.main.activity_emotion_list.*
import kotlinx.android.synthetic.main.content_emotion_list.*

class EmotionListActivity : AppCompatActivity(), EmotionListAdapter.EmotionHolderDelegate {
    private lateinit var realm: Realm
    private lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: EmotionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_list)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
            /*val emotionIntent: Intent = Intent(view.context, EditEmotionActivity::class.java)
            emotionIntent.putExtra("emotionID", "none")
            ContextCompat.startActivity(view.context, emotionIntent, null)*/
            editEmotion(view,null)
        }

        supportActionBar?.title = getString(R.string.emotion_list_view_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        Realm.init(this)

//        loadEmotion()
    }

    override fun onStop() {
        super.onStop()
        realm.close()
    }

    override fun onResume() {
        super.onResume()
        loadEmotion()
    }

    private fun loadEmotion() {
        // Open the realm for the UI thread.
        realm = Realm.getDefaultInstance()

        val emotions: RealmResults<Emotion> = realm.where<Emotion>().sort("name", Sort.ASCENDING).findAll()
        layoutManager = LinearLayoutManager(this)
        emotionRecyclerView.layoutManager = layoutManager

        adapter =
            EmotionListAdapter(emotions, this)

        emotionRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        realm.close()
    }

    override fun deleteEmotion(view: View?, emotion: Emotion?): Boolean {
        var isError = false
        try {
            // Open the realm for the UI thread.
            realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            emotion?.deleteFromRealm()
            realm.commitTransaction()
            loadEmotion()
        } catch (error: Throwable) {
            println(error)
            error.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(error)
            isError = true
        } finally {
            if(!realm.isClosed) {
                realm.close()
            }
        }
        return !isError
    }

    override fun editEmotion(view: View?, emotion: Emotion?): Boolean {
        val emotionIntent: Intent = Intent(view?.context, EditEmotionActivity::class.java)
        if (emotion != null) {
            emotionIntent.putExtra("emotionID", emotion._id)
        } else {
            emotionIntent.putExtra("emotionID","none")
        }
        startActivity(emotionIntent)
        return true
    }
}
