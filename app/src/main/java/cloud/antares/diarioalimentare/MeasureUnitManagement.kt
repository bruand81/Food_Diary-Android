package cloud.antares.diarioalimentare

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.LinearLayout
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cloud.antares.diarioalimentare.model.Dish
import cloud.antares.diarioalimentare.model.MeasureUnit
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit_meal.*

import kotlinx.android.synthetic.main.activity_measure_unit_management.*
import kotlinx.android.synthetic.main.content_measure_unit_management.*

class MeasureUnitManagement : AppCompatActivity(), MeasureUnitAdapter.MeasureUnitHolderOnClickDelegate {
    private lateinit var realm: Realm
    private lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: MeasureUnitAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure_unit_management)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            buildAddOrEditDialog(view, null).show()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        initMeasureUnitrecyclerView()
    }

    override fun onStop() {
        super.onStop()
        realm.close()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_measure_unit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.done_button_measure_unit_menu_item -> {
                finish()
                return true
            }
            R.id.cancel_button_measure_unit_menu_item -> {
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(view: View, measureUnit: MeasureUnit?) {
        println("Click")
    }

    override fun onLongClick(v: View?, measureUnit: MeasureUnit?): Boolean {
        println("Long Click")
        return true
    }

    override fun delete_measure_unit(v: View?, measureUnit: MeasureUnit?): Boolean {
        if (measureUnit != null) {
            // Open the realm for the UI thread.
            realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            measureUnit.deleteFromRealm()
            realm.commitTransaction()
        }
        initMeasureUnitrecyclerView()
        return true
    }

    override fun edit_measure_unit(v: View?, measureUnit: MeasureUnit?): Boolean {
//        val curView = v ?: this
        buildAddOrEditDialog(v, measureUnit).show()
        return true
    }

    private fun buildAddOrEditDialog(v: View?, measureUnit: MeasureUnit?): AlertDialog.Builder {
        val builder = AlertDialog.Builder(v?.context)
        val layout = LinearLayout(v?.context)
        layout.orientation = LinearLayout.VERTICAL
        val inputText = EditText(v?.context)
        inputText.setHint(R.string.add_measure_unit_edit_name)
        if(measureUnit == null){
            builder.setTitle(R.string.add_measure_unit_dialog)
        } else {
            builder.setTitle(R.string.edit_measure_unit_dialog)
            inputText.setText(measureUnit.name)
        }
        layout.addView(inputText)
        builder.setView(layout)
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            val curMU = measureUnit ?: MeasureUnit()
            realm.beginTransaction()
            curMU.name = inputText.text.toString()
            realm.insertOrUpdate(curMU)
            realm.commitTransaction()
            initMeasureUnitrecyclerView()
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel()}

        return builder
    }

    private fun initMeasureUnitrecyclerView() {
        hideKeyboard(this)
        currentFocus?.clearFocus()
        // Open the realm for the UI thread.
        realm = Realm.getDefaultInstance()

        val measureUnits = realm.where<MeasureUnit>().findAll()

        layoutManager = LinearLayoutManager(this)
        measureUnitRecyclerView.layoutManager = layoutManager

        adapter = MeasureUnitAdapter(measureUnits, this)
        val dividerItemDecoration = DividerItemDecoration(measureUnitRecyclerView.context, layoutManager.orientation)

        measureUnitRecyclerView.addItemDecoration(dividerItemDecoration)
        measureUnitRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        registerForContextMenu(measureUnitRecyclerView)
    }

    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
