package cloud.antares.diarioalimentare

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cloud.antares.diarioalimentare.model.Meal
import kotlinx.android.synthetic.main.mealrecyclerwiew_item_row.view.*
import java.text.SimpleDateFormat
import android.os.Build
import androidx.core.content.ContextCompat.startActivity
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_edit_emotion.view.*
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
            this.view.mealEmoji.text = meal.emotionForMeals?.first()?.toShortString()
            //val formatter = SimpleDateFormat("dd MMMM yyyy HH:mm", view.context.resources.configuration.locales[0])
            //val mealDate = formatter.format(meal.mealDate)
            this.view.dateTextView.text = meal.toString()
            val dishesString: String = meal.dishes.joinToString{
                it.toString()
            }
            this.view.mealDescription.text = dishesString
        }
        override fun onClick(v: View?) {
            val editMealIntent: Intent = Intent(view.context, EditMealActivity::class.java)
            val mealId:String = if (meal != null) meal!!._id else "none"
            editMealIntent.putExtra("mealID", mealId)
            startActivity(view.context, editMealIntent, null)
        }

    }
}