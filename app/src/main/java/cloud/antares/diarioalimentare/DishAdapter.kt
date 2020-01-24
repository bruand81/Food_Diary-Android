package cloud.antares.diarioalimentare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cloud.antares.diarioalimentare.model.Dish
import kotlinx.android.synthetic.main.dishrecyclerview_item_row.view.*

class DishAdapter(private val dishes: List<Dish>, private val dishHolderOnClickDelegate: DishHolderOnClickDelegate): RecyclerView.Adapter<DishAdapter.DishHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dishrecyclerview_item_row, parent, false)
        return DishHolder(view, dishHolderOnClickDelegate)
    }

    override fun getItemCount(): Int {
        return dishes.count()
    }

    override fun onBindViewHolder(holder: DishHolder, position: Int) {
        val dish = dishes[position]
        holder.bindDish(dish)
    }

    class DishHolder(v: View, private val dishHolderOnClickDelegate: DishHolderOnClickDelegate): RecyclerView.ViewHolder(v), View.OnClickListener{
        var view : View = v
        var dish: Dish? = null

        init {
            v.setOnClickListener(this)
        }

        fun bindDish(dish: Dish) {
            this.dish = dish
            this.view.dishName.text = dish.name
        }

        override fun onClick(v: View?) {
            dishHolderOnClickDelegate.onClick(view, dish)
        }

    }

    interface DishHolderOnClickDelegate {
        fun onClick(view: View, dish: Dish?)
    }
}