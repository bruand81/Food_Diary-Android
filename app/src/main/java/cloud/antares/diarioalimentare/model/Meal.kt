package cloud.antares.diarioalimentare.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

open class Meal: RealmObject() {
    @PrimaryKey
    var _id: String = UUID.randomUUID().toString()
    var name: String = ""
    var mealDate: Date = Date()
    var dishes: RealmList<Dish> = RealmList()

    @LinkingObjects("meals")
    val emotionForMeals: RealmResults<Emotion>? = null

    override fun toString(): String {
        val formatter = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
        return "$name - ${formatter.format(mealDate)}"
    }
}