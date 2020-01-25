package cloud.antares.diarioalimentare

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cloud.antares.diarioalimentare.adapters.MealAdapter
import cloud.antares.diarioalimentare.model.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), MealAdapter.MealHolderDelegate {
    private lateinit var realm: Realm
    private lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: MealAdapter
    lateinit var mAdView : AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Realm.init(this)

        val configuration: RealmConfiguration = RealmConfiguration.Builder().name("food_diary.realm").schemaVersion(3).migration(
            RealmMigrations()
        ).build()
        Realm.setDefaultConfiguration(configuration)
        realm = Realm.getDefaultInstance()
        checkEmotions(realm)
        checkMeasureUnit(realm)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        MobileAds.initialize(this, getString(R.string.admob_app_id))
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

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
        checkMeasureUnit(realm)

        initMealrecyclerView()
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
            R.id.action_settings -> true
            R.id.showEmotions -> {
                val emotionIntent: Intent = Intent(this.applicationContext, EmotionListActivity::class.java)
                startActivity(emotionIntent)
                return true
            }
            R.id.measure_unit -> {
                val measureUnitIntent = Intent(this.applicationContext, MeasureUnitManagement::class.java)
                startActivity(measureUnitIntent)
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

    private fun checkMeasureUnit(realm: Realm) {
        println("checkMeasureUnit")
        if(realm.where<MeasureUnit>().count() < 1) {
            val measures: List<MeasureUnit> = listOf(
                MeasureUnit(getString(R.string.NN_MU)),
                MeasureUnit(getString(R.string.PZ_MU)),
                MeasureUnit(getString(R.string.ML_MU)),
                MeasureUnit(getString(R.string.GR_MU))
            )
            realm.beginTransaction()
            realm.insert(measures)
            realm.commitTransaction()
        }

        val dishes = realm.where<Dish>().isEmpty("measureUnitForDishes").findAll()
        val mu = realm.where<MeasureUnit>().equalTo("name",getString(R.string.NN_MU)).findFirst()

        realm.beginTransaction()
        mu?.dishes?.addAll(dishes)
        realm.commitTransaction()
    }

    private fun initMealrecyclerView(){
        realm = Realm.getDefaultInstance()
        val meals: RealmResults<Meal> = realm.where<Meal>().findAll()

        layoutManager = LinearLayoutManager(this)
        mealRecyclerView.layoutManager = layoutManager

        adapter = MealAdapter(meals, this)
        mealRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

    }

    override fun deleteMeal(v: View?, meal: Meal?): Boolean {
        try {
            realm.beginTransaction()
            meal?.deleteFromRealm()
            realm.commitTransaction()
        } catch (error: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(error)
            return false
        } finally {
            initMealrecyclerView()
        }
        return true
    }

    override fun editMeal(v: View?, meal: Meal?): Boolean {
        val editMealIntent: Intent = Intent(v?.context, EditMealActivity::class.java)
        val mealId:String = if (meal != null) meal!!._id else "none"
        editMealIntent.putExtra("mealID", mealId)
        startActivity(editMealIntent)
        return true
    }

}
