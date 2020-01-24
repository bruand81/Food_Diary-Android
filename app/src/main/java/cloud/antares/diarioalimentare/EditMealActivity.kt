package cloud.antares.diarioalimentare

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cloud.antares.diarioalimentare.model.Dish
import cloud.antares.diarioalimentare.model.Emotion
import cloud.antares.diarioalimentare.model.Meal
import cloud.antares.diarioalimentare.model.MeasureUnit
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit_meal.*
import java.text.SimpleDateFormat
import java.util.*


class EditMealActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, DishAdapter.DishHolderOnClickDelegate {
    private lateinit var realm: Realm
    private lateinit var meal: Meal
    private lateinit var dateFormatter: SimpleDateFormat
    private lateinit var timeFormatter: SimpleDateFormat
    private lateinit var emotionMap: Map<String, Emotion>
    private lateinit var measureUnitMap: Map<String, MeasureUnit>
    private lateinit var measureUnitList: List<String>
    private lateinit var emotionList: List<String>
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var dishAdapter: DishAdapter
    private var dishes: MutableSet<Dish> = mutableSetOf<Dish>()
    private var emotion: Emotion? = null
    private var selectedDish: Dish? = null
    private var isEdit:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_meal)
        Realm.init(this)

        realm = Realm.getDefaultInstance()
        val mealID:String = (intent.extras?.getString("mealID", "none"))?:"none"

        meal = realm.where<Meal>().equalTo("_id", mealID).findFirst() ?: Meal()

        if(meal._id == mealID){
            isEdit = true
        }

//        if(mealID.equals("none")){
//            deleteMealButton.isEnabled = false
//            deleteMealButton.isClickable = false
//            deleteMealButton.isVisible = false
//        }

        dateFormatter = SimpleDateFormat("dd MMMM yyyy", applicationContext.resources.configuration.locales[0])
        timeFormatter = SimpleDateFormat("HH:mm", applicationContext.resources.configuration.locales[0])

        addDishButton.setOnClickListener { v ->
            builAlertDialog(v,null).show()
//            val builder = AlertDialog.Builder(v.context)
//            builder.setTitle(R.string.add_dish_dialog_title)
//            val inputText: AutoCompleteTextView = AutoCompleteTextView(v.context)
//            val dishesList: List<Dish> = realm.where<Dish>().findAll().toList()
//            val adapter: ArrayAdapter<Dish> = ArrayAdapter<Dish>(v.context, android.R.layout.simple_dropdown_item_1line, dishesList)
//            inputText.setAdapter(adapter)
//            inputText.setOnItemClickListener { adapterView, _, position, _ ->
//                selectedDish = adapterView.adapter.getItem(position) as Dish
//                Toast.makeText(v.context, selectedDish?.name, Toast.LENGTH_LONG).show()
//            }
//
//            builder.setPositiveButton(android.R.string.ok) { _, _ ->
//                val newDish: Dish?
//                if(selectedDish != null && selectedDish?.name == inputText.text.toString()){
//                    newDish = selectedDish
//                } else {
//                    newDish = Dish()
//                    newDish.name = inputText.text.toString()
//                }
//                dishes.add(newDish!!)
//            }
//
//            builder.setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel()}
//
//            builder.show()

        }
//        deleteMealButton.setOnClickListener { v ->
//            buildDeleteAlertDialog(v, meal).show()
//        }
    }

    override fun onResume() {
        super.onResume()

        val mealId:String = meal._id

        meal = realm.where<Meal>().equalTo("_id",mealId).findFirst() ?: Meal()

        if (isEdit) {
            mealNameEditText.setText(meal.name)
            meal.dishes.forEach { dishes.add(it) }
        }

        val emotions: RealmResults<Emotion> = realm.where<Emotion>().findAll()
        emotionMap = emotions.map {  formatEmotionForSpinner(it) to it }.toMap()
//        val emotionList: List<String> =  emotions.map { emotion -> emotion.emoticon + " " + emotion.name }
        emotionList = emotionMap.keys.toList()
        val emotionArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, emotionList)
        emotionSpinner.adapter = emotionArrayAdapter
        if(meal.emotionForMeals?.first() != null){
            emotionSpinner.setSelection(emotionArrayAdapter.getPosition(formatEmotionForSpinner(meal.emotionForMeals!!.first()!!)))
        }
        emotionSpinner.onItemSelectedListener = this

        setTimeAndDateButton()

        ok_button.setOnClickListener {
            saveMeal()
            finish()
        }

        cancel_button.setOnClickListener { finish() }

        initDishRecyclerView()
    }

    private fun initDishRecyclerView() {
        hideKeyboard(this)
        currentFocus?.clearFocus()
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = null
        recyclerView.layoutManager = layoutManager

        dishAdapter = DishAdapter(dishes.toList(), this)
        recyclerView.adapter = null
        recyclerView.adapter = dishAdapter

        dishAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.meal_edit_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        invalidateOptionsMenu()
        if (isEdit) {
            menu?.findItem(R.id.edit_meal_delete_menuitem)?.isEnabled = true
            menu?.findItem(R.id.edit_meal_delete_menuitem)?.isVisible = true
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.edit_meal_ok_menuitem -> {
                saveMeal()
                finish()
                return true
            }
            R.id.edit_meal_cancel_menuitem -> {
                finish()
                return true
            }
            R.id.edit_meal_delete_menuitem -> {
                buildDeleteAlertDialog().show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()
        realm.close()
    }

    override fun onClick(view: View, dish: Dish?) {
        builAlertDialog(view, dish).show()
    }

    private fun builAlertDialog(view: View, dish: Dish?): AlertDialog.Builder {
        val builder = AlertDialog.Builder(view.context)
        val layout = LinearLayout(view.context)
        layout.orientation = LinearLayout.VERTICAL
        builder.setTitle(R.string.add_dish_dialog_title)
        val inputText: AutoCompleteTextView = AutoCompleteTextView(view.context)
        val quantityInputText = EditText(view.context)
        val measureUnitSpinner = Spinner(view.context)
        val dishesList: List<Dish> = realm.where<Dish>().findAll().toList()
        val adapter: ArrayAdapter<Dish> = ArrayAdapter<Dish>(view.context, android.R.layout.simple_dropdown_item_1line, dishesList)
        var isEdit = false

        quantityInputText.inputType = InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL
        quantityInputText.setHint(R.string.add_dish_quantity)
        inputText.setHint(R.string.add_dish_name)

        val measureUnits: RealmResults<MeasureUnit> = realm.where<MeasureUnit>().findAll()
        val measureUnitMap = measureUnits.map { it.name to it }.toMap()
//        val emotionList: List<String> =  emotions.map { emotion -> emotion.emoticon + " " + emotion.name }
        measureUnitList = measureUnitMap.keys.toList()
        val measureUnitArrayAdapter: ArrayAdapter<String> = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item, measureUnitList)
        measureUnitSpinner.adapter = measureUnitArrayAdapter

        if(dish != null) {
            selectedDish = dish
            isEdit = true

            inputText.setText(dish.name)
            quantityInputText.setText("${dish.quantity}")
            measureUnitSpinner.setSelection(measureUnitArrayAdapter.getPosition(dish.measureUnitForDishes?.first()?.name),true)

            builder.setNeutralButton(R.string.delete_dish_button) { _, _ ->
                if(selectedDish != null && selectedDish?.name == inputText.text.toString()){
                    dishes.remove(selectedDish!!)
                    realm.beginTransaction()
                    realm.where<Dish>().equalTo("_id",selectedDish!!._id).findAll().deleteAllFromRealm()
                    realm.commitTransaction()
                    Toast.makeText(view.context,R.string.dish_deleted,Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(view.context,R.string.dish_deleting_error,Toast.LENGTH_SHORT).show()
                }
                initDishRecyclerView()
            }
        }

        inputText.setAdapter(adapter)
        inputText.setOnItemClickListener { adapterView, _, position, _ ->
            selectedDish = adapterView.adapter.getItem(position) as Dish
            Toast.makeText(applicationContext, selectedDish?.name, Toast.LENGTH_LONG).show()
        }
        layout.addView(inputText)
        layout.addView(quantityInputText)
        layout.addView(measureUnitSpinner)
        builder.setView(layout)
        //builder.setView(quantityInputText)

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            realm.beginTransaction()
            val newDish: Dish?
            //if(selectedDish != null && selectedDish?.name == inputText.text.toString()){

            if(selectedDish != null){
                newDish = selectedDish
            } else {
                newDish = Dish()
            }
            if (newDish != null) {
                newDish.name = inputText.text.toString()
                newDish.quantity = quantityInputText.text.toString().toDouble()
            }
            dishes.add(newDish!!)

            val selectedMeasureUnit = measureUnitMap.get(measureUnitSpinner.selectedItem as String)
            if(selectedMeasureUnit!= null){
                if(isEdit) {
                    if(!selectedMeasureUnit.equals(newDish.measureUnitForDishes?.first())){
                        selectedDish?.measureUnitForDishes?.first()?.dishes?.remove(newDish)
                        selectedMeasureUnit.dishes.add(newDish)
                    }
                } else {
                    selectedMeasureUnit.dishes.add(newDish)
                }
            }
            Toast.makeText(view.context,R.string.dish_added,Toast.LENGTH_SHORT).show()
            realm.commitTransaction()
            initDishRecyclerView()
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel()}

        return builder
    }

    private fun buildDeleteAlertDialog(): AlertDialog.Builder {
        val builder = AlertDialog.Builder(this)
        if (!isEdit) {
            finish()
        }
        builder.setTitle(R.string.delete_meal_dialog_title)
        builder.setMessage(R.string.confirm_meal_deletion_message)
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            if (isEdit) {
                    realm.beginTransaction()
                    meal.deleteFromRealm()
                    realm.commitTransaction()
                    finish()
            }
        }
        builder.setIcon(R.drawable.ic_alert_warning)

        builder.setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel()}
        return builder
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        emotion = null
        ok_button.isEnabled = false
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        emotion = emotionMap.get(emotionList[pos])
        ok_button.isEnabled = (emotion!= null)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val c = Calendar.getInstance()
        c.time = meal.mealDate
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)
        realm.beginTransaction()
        meal.mealDate = c.time
        realm.commitTransaction()
        setTimeAndDateButton()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        val c = Calendar.getInstance()
        c.time = meal.mealDate
        c.set(year, month, day)
        realm.beginTransaction()
        meal.mealDate = c.time
        realm.commitTransaction()
        setTimeAndDateButton()
    }

    fun showDatePickerDialog(v: View) {
        hideKeyboard(this)
        val c = Calendar.getInstance()
        c.time = meal.mealDate

        val datePickerDialog = DatePickerDialog(v.context, this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.show()
    }

    fun showTimePickerDialog(v: View) {
        hideKeyboard(this)
        val c = Calendar.getInstance()
        c.time = meal.mealDate

        val timePickerDialog = TimePickerDialog(v.context, this, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)
        timePickerDialog.show()
    }

    fun formatEmotionForSpinner(emotion: Emotion): String = emotion.emoticon + " " + emotion.name

    private fun setTimeAndDateButton() {
        val mealDate = dateFormatter.format(meal.mealDate)
        val mealTime = timeFormatter.format(meal.mealDate)
        dateButton.text = mealDate
        timeButton.text = mealTime
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

    private fun hideKeyboard(context: Context, view: View?) {
        if (view != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun saveMeal(){
        if(emotion == null){
            Toast.makeText(applicationContext,R.string.error_adding_meal, Toast.LENGTH_SHORT).show()
        } else {
            realm.beginTransaction()
            meal.name = mealNameEditText.text.toString()
            dishes.forEach { dish ->
                realm.copyToRealmOrUpdate(dish)
                meal.dishes.add(dish) }
            meal = realm.copyToRealmOrUpdate(meal)
            emotion!!.meals.add(meal)
            realm.commitTransaction()
            Toast.makeText(applicationContext,R.string.meal_added_succesfully, Toast.LENGTH_SHORT).show()
        }
    }
}
