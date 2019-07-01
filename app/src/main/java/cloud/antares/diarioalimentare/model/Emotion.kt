package cloud.antares.diarioalimentare.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Emotion(): RealmObject() {
    @PrimaryKey
    var _id: String = UUID.randomUUID().toString()
    var name: String = ""
    var emoticon: String = ""

    var meals: RealmList<Meal> = RealmList()

    constructor(name: String, emotion: String) : this() {
        this.name = name
        this.emoticon = emotion
    }
}