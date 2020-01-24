package cloud.antares.diarioalimentare.model

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import java.util.*
import cloud.antares.diarioalimentare.utilities.*

open class Dish: RealmObject() {
    @PrimaryKey
    var _id: String = UUID.randomUUID().toString()
    var name: String = ""
    var quantity: Double = -1.0

    @LinkingObjects("dishes")
    val mealForDish: RealmResults<Meal>? = null

    @LinkingObjects("dishes")
    val measureUnitForDishes: RealmResults<MeasureUnit>? = null

    override fun toString(): String {
        var quantityString = ""

        if (quantity>0){
            quantityString = " (${formatDecimals(quantity)} ${measureUnitForDishes?.firstOrNull().toString()})"
        }
        return "${name}${quantityString}"
    }
}