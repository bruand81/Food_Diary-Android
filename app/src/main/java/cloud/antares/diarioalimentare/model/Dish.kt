package cloud.antares.diarioalimentare.model

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey

open class Dish: RealmObject() {
    @PrimaryKey
    var _id: String = System.currentTimeMillis().toString()
    var name: String = ""

    @LinkingObjects("dishes")
    val mealForDish: RealmResults<Meal>? = null
}