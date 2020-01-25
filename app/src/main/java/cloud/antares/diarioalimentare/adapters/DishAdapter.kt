package cloud.antares.diarioalimentare.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import cloud.antares.diarioalimentare.R
import cloud.antares.diarioalimentare.model.Dish
import kotlinx.android.synthetic.main.dishrecyclerview_item_row.view.*

class DishAdapter(private val dishes: List<Dish>, private val dishHolderOnClickDelegate: DishHolderOnClickDelegate): RecyclerView.Adapter<DishAdapter.DishHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dishrecyclerview_item_row, parent, false)
        return DishHolder(
            view,
            dishHolderOnClickDelegate
        )
    }

    override fun getItemCount(): Int {
        return dishes.count()
    }

    override fun onBindViewHolder(holder: DishHolder, position: Int) {
        val dish = dishes[position]
        holder.bindDish(dish)
    }

    class DishHolder(v: View, private val dishHolderOnClickDelegate: DishHolderOnClickDelegate): RecyclerView.ViewHolder(v), View.OnClickListener, View.OnLongClickListener{
        var view : View = v
        var dish: Dish? = null

        init {
            v.setOnClickListener(this)
        }

        fun bindDish(dish: Dish) {
            this.dish = dish
            this.view.dishName.text = dish.name
            this.view.deleteDishButton.setOnClickListener {
                dishHolderOnClickDelegate.deleteDish(this.view, dish)
            }
            this.view.isLongClickable = true
            this.view.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            dishHolderOnClickDelegate.onClick(view, dish)
        }

        override fun onLongClick(v: View?): Boolean {
            val popup = PopupMenu(v?.context,v)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_contextual_menu_item -> {
                        println("Delete item ${this.dish}")
                        return@setOnMenuItemClickListener dishHolderOnClickDelegate.deleteDish(v, dish)
                    }
                    R.id.modify_contextual_menu_item -> {
                        println("Modify item ${this.dish}")
                        return@setOnMenuItemClickListener dishHolderOnClickDelegate.editDish(v, dish)
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

    interface DishHolderOnClickDelegate {
        fun onClick(view: View, dish: Dish?)
        fun deleteDish(view: View?, dish: Dish?): Boolean
        fun editDish(view: View?, dish: Dish?): Boolean
    }
}