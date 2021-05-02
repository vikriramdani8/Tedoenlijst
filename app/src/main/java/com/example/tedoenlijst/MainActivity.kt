package com.example.tedoenlijst

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.tedoenlijst.Adapter.ListTaskAdapter
import com.example.tedoenlijst.DBHelper.DBHelper
import com.example.tedoenlijst.Model.Category
import com.example.tedoenlijst.Model.Task
import com.example.tedoenlijst.Receiver.MyAlarmReceiver
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.toolbar_add_category.view.*
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import android.view.Menu as Menu1
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

open class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener{
    var pos = 0
    var idxArr = 0

    internal lateinit var db: DBHelper
    internal var lstCategory:List<Category> = ArrayList<Category>()
    internal var tempCategory:List<Category> = ArrayList<Category>()
    internal var lstTask:List<Task> = ArrayList<Task>()
    internal var deleteMode = false
    private var listView: ListView ? = null

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRestart() {
        refreshDataCategory()
        refreshDataTask()

        super.onRestart()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        db = DBHelper(this)
        refreshDataCategory()
        refreshDataTask()

        list_task_all.setDivider(null)
        btn_add.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java).apply {}
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu1): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if (id == R.id.setting_action) {
            val intent = Intent(this, CategoryActivity::class.java).apply {}
            startActivity(intent)
            return true
        } else if (id == R.id.delete_action)  {
            deleteMode = !deleteMode

            if (deleteMode)
                item.setTitle("Normal Mode")
            else
                item.setTitle("Delete Mode")

            refreshDataTask()
        }

        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var items : String = parent?.getItemAtPosition(position) as String

        if (items == "New List"){
            addNewCategory()
            spinnerTollbar?.setSelection(pos)
        } else {
            pos = position
            if (items == "Default"){
                idxArr = 0
            } else if (items == "All List") {
                idxArr = -100
            } else if (items == "Finished") {
                idxArr = -99
            }  else {
                idxArr = pos-2
            }

            refreshDataTask()
        }
    }

    fun append(arr: Array<String>, element: String): Array<String> {
        val list: MutableList<String> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    private fun addNewCategory() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.toolbar_add_category, null)

        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("New List")

        val  mAlertDialog = mBuilder.show()

        mDialogView.dialogLoginBtn.setOnClickListener {
            if (mDialogView.dialogNameEt.text.toString() != "Default" && mDialogView.dialogNameEt.text.toString() != "Finished" && mDialogView.dialogNameEt.text.toString() != "All List" && mDialogView.dialogNameEt.text.toString() != "New List"){
                if (checkCategorryValid(mDialogView.dialogNameEt.text.toString())){
                    Toast.makeText(this, "This category name is already exist", Toast.LENGTH_SHORT).show()
                } else {
                    val category = Category(
                        0,
                        mDialogView.dialogNameEt.text.toString()
                    )

                    db.addCategory(category)
                    refreshDataCategory()
                }
            } else {
                Toast.makeText(this, "Can't rename this category with this name", Toast.LENGTH_SHORT).show()
            }

            mAlertDialog.dismiss()
        }

        mDialogView.dialogCancelBtn.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun checkCategorryValid(txt: String): Boolean {
        tempCategory = db.getCategory(txt)
        return tempCategory.size != 0
    }

    fun refreshDataCategory(){
        lstCategory = db.allCategory
        val temp_array: Array<Category> = lstCategory.toTypedArray()
        var listCate = arrayOf("All List", "Default")
        temp_array.forEach {
            listCate = append(
                listCate,
                it.name_category.toString()
            )
        }

        listCate = append(listCate, "Finished")
        listCate = append(listCate, "New List")
        val adapter2 = ArrayAdapter(
            this,
            R.layout.toolbar_spinner_item,
            listCate
        )

        adapter2.setDropDownViewResource(R.layout.toolbar_spinner_item_list)
        spinnerTollbar?.adapter = adapter2
        spinnerTollbar?.onItemSelectedListener = this
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshDataTask(){
        lstTask = db.allTask

        val listTaskOverdue = ArrayList<Task>()
        val listTaskToday = ArrayList<Task>()
        val listTaskTomorrow = ArrayList<Task>()
        val listTaskThisWeek = ArrayList<Task>()
        val listTaskNextWeek = ArrayList<Task>()
        val listTaskLater = ArrayList<Task>()
        val listTaskNoDate = ArrayList<Task>()
        val listTaskAll = ArrayList<Task>()

        listTaskOverdue.add(Task("Overdue"))
        listTaskToday.add(Task("Today"))
        listTaskTomorrow.add(Task("Tomorrow"))
        listTaskThisWeek.add(Task("This Week"))
        listTaskNextWeek.add(Task("Next Week"))
        listTaskLater.add(Task("Later"))
        listTaskNoDate.add(Task("No Date"))

        for (tsk in lstTask){
            if (checkValidTask(tsk.id_category) || (tsk.pemisah != "" && tsk.pemisah != null))
                if (checkValidTaskActive(tsk.task_active)){
                    if (tsk.date_task != null && tsk.date_task.toString() != ""){
                        val formatter = DateTimeFormatter.ofPattern("dd/M/yyyy")

                        var temp = tsk.date_task.toString().split("/")
                        val dateNow = LocalDate.of(temp[2].toInt(), temp[1].toInt(), temp[0].toInt())
                        val formatterDateNow = dateNow.format(formatter)

                        val current = LocalDate.now()
                        val formattedToday = current.format(formatter)
                        val dow = DayOfWeek.valueOf(current.dayOfWeek.toString())

                        val addDateWeek = 6 - dow.value
                        val thisWeek = current.plusDays(addDateWeek.toLong())
                        val nextWeek = current.plusDays(14)

                        val tomorrow = current.plusDays(1)
                        val formattedTomorrow = tomorrow.format(formatter)

                        if (dateNow.isBefore(current))
                            listTaskOverdue.add(tsk)
                        else if (formattedToday == tsk.date_task.toString())
                            listTaskToday.add(tsk)
                        else if (formattedTomorrow == tsk.date_task.toString())
                            listTaskTomorrow.add(tsk)
                        else if (dateNow.isBefore(thisWeek) && dateNow.isAfter(tomorrow))
                            listTaskThisWeek.add(tsk)
                        else if (dateNow.isAfter(thisWeek) && dateNow.isBefore(nextWeek))
                            listTaskNextWeek.add(tsk)
                        else
                            listTaskLater.add(tsk)
                    } else {
                        listTaskNoDate.add(tsk)
                    }
                }

        }

        if (listTaskOverdue.size > 1)
            listTaskAll.addAll(listTaskOverdue)

        if (listTaskToday.size > 1)
            listTaskAll.addAll(listTaskToday)

        if (listTaskTomorrow.size > 1)
            listTaskAll.addAll(listTaskTomorrow)

        if (listTaskThisWeek.size > 1)
            listTaskAll.addAll(listTaskThisWeek)

        if (listTaskNextWeek.size > 1)
            listTaskAll.addAll(listTaskNextWeek)

        if (listTaskLater.size > 1)
            listTaskAll.addAll(listTaskLater)

        if (listTaskNoDate.size > 1)
            listTaskAll.addAll(listTaskNoDate)

        if (listTaskAll.size == 0)
            nothing.showww()
        else
            nothing.hideee()

        val adapterAll = ListTaskAdapter(this@MainActivity, listTaskAll, this, deleteMode)
        listView = findViewById(R.id.list_task_all)
        listView?.adapter = adapterAll
        listView?.choiceMode = ListView.CHOICE_MODE_MULTIPLE
    }

    fun View.hideee() {
        visibility = View.GONE
    }

    fun View.showww() {
        visibility = View.VISIBLE
    }

    fun checkValidTaskActive(id: Int): Boolean {
        if (idxArr == -99)
            return id == 0

        return id == 1
    }

    fun checkValidTask(id: Int): Boolean {
        if (pos == 0 || idxArr == -99){
            return true
        } else if (pos == 1){
            return id == 0
        } else {
            return id == lstCategory[idxArr].id_category
        }

        return false
    }
}

