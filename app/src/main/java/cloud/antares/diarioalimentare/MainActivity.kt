package cloud.antares.diarioalimentare

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import cloud.antares.diarioalimentare.model.Emotion
import cloud.antares.diarioalimentare.model.Meal
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.createObject
import io.realm.kotlin.where

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var realm: Realm
    private lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: MealAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Realm.init(this)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // Open the realm for the UI thread.
        realm = Realm.getDefaultInstance()

        checkEmotions(realm)

        val meals: RealmResults<Meal> = realm.where<Meal>().findAll()

        layoutManager = LinearLayoutManager(this)
        mealRecyclerView.layoutManager = layoutManager

        adapter = MealAdapter(meals)
        mealRecyclerView.adapter = adapter

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.showEmotions -> {
                val emotionIntent: Intent = Intent(this.applicationContext, EmotionListActivity::class.java)
                startActivity(emotionIntent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkEmotions(realm: Realm) {
        println("checkEmotions")
        if(realm.where<Emotion>().count() < 1) {
            println("insert emotions")

            val emotions: List<Emotion> = listOf(
                Emotion(getString(R.string.happy_emotion_default),getString(R.string.happy_emotion_emoticon_default)),
                Emotion(getString(R.string.angry_emotion_default),getString(R.string.angry_emotion_emoticon_default)),
                Emotion(getString(R.string.annoyed_emotion_default),getString(R.string.annoyed_emotion_emoticon_default)),
                Emotion(getString(R.string.sick_emotion_default),getString(R.string.sick_emotion_emoticon_default)),
                Emotion(getString(R.string.disgust_emotion_default),getString(R.string.disgust_emotion_emoticon_default))
            )
            realm.beginTransaction()
            realm.insert(emotions)
            realm.commitTransaction()
        }
    }

}
