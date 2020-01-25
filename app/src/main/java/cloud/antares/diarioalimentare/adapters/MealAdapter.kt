package cloud.antares.diarioalimentare.adapters

import android.content.Intent
import android.view.*
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import cloud.antares.diarioalimentare.model.Meal
import kotlinx.android.synthetic.main.mealrecyclerwiew_item_row.view.*
import androidx.core.content.ContextCompat.startActivity
import cloud.antares.diarioalimentare.EditMealActivity
import cloud.antares.diarioalimentare.R
import io.realm.RealmResults


class MealAdapter(val meals: RealmResults<Meal>, private val delegate: MealHolderDelegate): RecyclerView.Adapter<MealAdapter.MealHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.mealrecyclerwiew_item_row, parent, false)

        return MealHolder(view, delegate)
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

    class MealHolder(v: View, private val delegate: MealHolderDelegate): RecyclerView.ViewHolder(v), View.OnClickListener, View.OnCreateContextMenuListener, View.OnLongClickListener {
        var view : View = v
        var meal : Meal? = null

        init {
            v.setOnClickListener(this)
        }

        fun bindMeal(meal: Meal) {
            this.meal = meal
            val emotionsForMeal = meal.emotionForMeals

            val emotion = emotionsForMeal?.firstOrNull()
            if (emotion != null) {
                this.view.mealEmoji.text = emotion.toShortString()
            }
            //val formatter = SimpleDateFormat("dd MMMM yyyy HH:mm", view.context.resources.configuration.locales[0])
            //val mealDate = formatter.format(meal.mealDate)
            this.view.dateTextView.text = meal.toString()
            val dishesString: String = meal.dishes.joinToString{
                it.toString()
            }
            this.view.mealDescription.text = dishesString
            this.view.isLongClickable = true
            this.view.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            delegate.editMeal(v, meal)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {}

        override fun onLongClick(v: View?): Boolean {
            val popup = PopupMenu(v?.context,v)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_contextual_menu_item -> {
                        println("Delete item ${this.meal}")
                        return@setOnMenuItemClickListener delegate.deleteMeal(v, meal)
                    }
                    R.id.modify_contextual_menu_item -> {
                        println("Modify item ${this.meal}")
                        return@setOnMenuItemClickListener delegate.editMeal(v, meal)
                    }
                    else -> true
                }
            }
            popup.inflate(R.menu.measure_unit_recycler_view_contex_menu)
            popup.gravity = Gravity.CENTER_HORIZONTAL

            popup.show()

            return true
        }

    }

    interface MealHolderDelegate {
        fun deleteMeal(v:View?, meal:Meal?): Boolean
        fun editMeal(v:View?, meal:Meal?): Boolean
    }
}