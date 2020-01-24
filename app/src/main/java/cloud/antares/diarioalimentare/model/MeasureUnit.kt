package cloud.antares.diarioalimentare.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class MeasureUnit(): RealmObject() {
    @PrimaryKey
    var _id: String = UUID.randomUUID().toString()
    var name: String = ""
    var dishes: RealmList<Dish> = RealmList()

    constructor(name: String) : this(){
        this.name = name
    }

    override fun toString(): String {
        return name
    }
}