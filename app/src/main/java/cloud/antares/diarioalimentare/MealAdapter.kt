package cloud.antares.diarioalimentare

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cloud.antares.diarioalimentare.model.Meal
import kotlinx.android.synthetic.main.mealrecyclerwiew_item_row.view.*
import java.text.SimpleDateFormat
import android.os.Build
import io.realm.RealmResults
import java.util.*


class MealAdapter(val meals: RealmResults<Meal>): RecyclerView.Adapter<MealAdapter.MealHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.mealrecyclerwiew_item_row, parent, false)

        return MealHolder(view)
    }

    override fun getItemCount(): Int {
        return meals.count()
    }

    override fun onBindViewHolder(holder: MealHolder, position: Int) {
        val meal = meals[position]
        if (meal != null) {
            holder.bindMeal(meal)
        }
    }

    class MealHolder(v: View): RecyclerView.ViewHolder(v), View.OnClickListener {
        var view : View = v
        var meal : Meal? = null

        init {
            v.setOnClickListener(this)
        }

        fun bindMeal(meal: Meal) {
            this.meal = meal
            this.view.mealEmoji.text = meal.emotionForMeals?.first().toString()
            val formatter = SimpleDateFormat("dd MMMM yyyy HH:mm", view.context.resources.configuration.locales[0])
            val mealDate = formatter.format(meal.mealDate)
            this.view.dateTextView.text = meal.name + " - " + mealDate
        }
        override fun onClick(v: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}