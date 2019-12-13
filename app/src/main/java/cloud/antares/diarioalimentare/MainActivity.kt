package cloud.antares.diarioalimentare

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.TimePicker
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
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            val editMealIntent: Intent = Intent(view.context, EditMealActivity::class.java)
            editMealIntent.putExtra("mealID", "none")
            startActivity(editMealIntent)
        }

    }

    override fun onResume() {
        super.onResume()
        // Open the realm for the UI thread.
        realm = Realm.getDefaultInstance()

        checkEmotions(realm)

        val meals: RealmResults<Meal> = realm.where<Meal>().findAll()

        layoutManager = LinearLayoutManager(this)
        mealRecyclerView.layoutManager = layoutManager

        adapter = MealAdapter(meals)
        mealRecyclerView.adapter = adapter
    }

    override fun onStop() {
        super.onStop()
        realm.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val settingsIntent: Intent = Intent(this.applicationContext, SettingsActivity::class.java)
                startActivity(settingsIntent)
                return true
            }
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
