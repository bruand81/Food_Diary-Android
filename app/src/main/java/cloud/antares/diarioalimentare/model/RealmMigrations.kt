package cloud.antares.diarioalimentare.model

import io.realm.DynamicRealm
import io.realm.RealmMigration
import io.realm.RealmObjectSchema
import io.realm.RealmSchema


class RealmMigrations: RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema : RealmSchema = realm.schema

        if (oldVersion < 1L) {
            val dishSchema: RealmObjectSchema? = schema.get("Dish")

            dishSchema?.addField("quantity", Double::class.java)

            schema["Dish"]!!.transform { obj -> obj.setDouble("quantity", -1.0) }
        }
    }

    override fun hashCode(): Int {
        return 37
    }

    override fun equals(other: Any?): Boolean {
        return other is RealmMigrations
    }
}
