package cloud.antares.diarioalimentare.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import java.util.*

open class Meal: RealmObject() {
    @PrimaryKey
    var _id: String = System.currentTimeMillis().toString()
    var name: String = ""
    var mealDate: Date = Date()
    var dishes: RealmList<Dish> = RealmList()

    @LinkingObjects("meals")
    val emotionForMeals: RealmResults<Emotion>? = null
}