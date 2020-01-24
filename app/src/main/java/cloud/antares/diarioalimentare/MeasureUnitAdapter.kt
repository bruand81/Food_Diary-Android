package cloud.antares.diarioalimentare

import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.recyclerview.widget.RecyclerView
import cloud.antares.diarioalimentare.model.MeasureUnit
import io.realm.RealmResults
import kotlinx.android.synthetic.main.mesureunitrecyclerview_item_row.view.*


class MeasureUnitAdapter(private val measureUnits: RealmResults<MeasureUnit>, private val measureUnitHolderOnClickDelegate: MeasureUnitHolderOnClickDelegate): RecyclerView.Adapter<MeasureUnitAdapter.MeasureUnitHolder>() {

    class MeasureUnitHolder(v: View, private val measureUnitHolderOnClickDelegate: MeasureUnitHolderOnClickDelegate): RecyclerView.ViewHolder(v), View.OnClickListener, View.OnCreateContextMenuListener, View.OnLongClickListener {
        var view : View = v
        var measureUnit: MeasureUnit? = null

        init {
            v.setOnClickListener(this)
        }

        fun bindMeasureUnit(measureUnit: MeasureUnit) {
            this.measureUnit = measureUnit
            this.view.measureunitname.text = measureUnit.name
            this.view.isLongClickable = true
            this.view.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            measureUnitHolderOnClickDelegate.onClick(view, measureUnit)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu?.setHeaderTitle(R.string.measure_unit)
            menu?.add(0,view.id,0,R.string.delete)
            menu?.add(0,view.id,0,R.string.modify)
        }

        override fun onLongClick(v: View?): Boolean {
            val popup = PopupMenu(v?.context,v)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_contextual_menu_item -> {
                        println("Delete item ${this.measureUnit}")
                        return@setOnMenuItemClickListener measureUnitHolderOnClickDelegate.delete_measure_unit(v, measureUnit)
                    }
                    R.id.modify_contextual_menu_item -> {
                        println("Modify item ${this.measureUnit}")
                        return@setOnMenuItemClickListener measureUnitHolderOnClickDelegate.edit_measure_unit(v, measureUnit)
                    }
                    else -> true
                }
            }
            popup.inflate(R.menu.measure_unit_recycler_view_contex_menu)
            popup.gravity = Gravity.CENTER_HORIZONTAL

            popup.show()

            return measureUnitHolderOnClickDelegate.onLongClick(view, measureUnit)
        }

    }

    interface MeasureUnitHolderOnClickDelegate {
        fun onClick(view: View, measureUnit: MeasureUnit?)
        fun onLongClick(v: View?, measureUnit: MeasureUnit?): Boolean
        fun delete_measure_unit(v: View?,measureUnit: MeasureUnit?): Boolean
        fun edit_measure_unit(v: View?,measureUnit: MeasureUnit?): Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasureUnitHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mesureunitrecyclerview_item_row, parent, false)
        return MeasureUnitHolder(view, measureUnitHolderOnClickDelegate)
    }

    override fun getItemCount(): Int {
        val mucount = measureUnits.count()
        return mucount
    }

    override fun onBindViewHolder(holder: MeasureUnitHolder, position: Int) {
        val measureUnit = measureUnits[position]
        if (measureUnit!=null){
            holder.bindMeasureUnit(measureUnit)
        }
    }
}