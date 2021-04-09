package com.example.tedoenlijst

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.tedoenlijst.DBHelper.DBHelper
import kotlinx.android.synthetic.main.activity_add_task.*
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.activity_category.backButton
import kotlinx.android.synthetic.main.activity_category.toolbar
import com.example.tedoenlijst.Model.Category
import com.example.tedoenlijst.Model.Task
import com.example.tedoenlijst.Receiver.MyAlarmReceiver
import io.karn.notify.Notify
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row_layout.*
import kotlinx.android.synthetic.main.toolbar_add_category.view.*
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    internal lateinit var db: DBHelper
    internal var lstCategory:List<Category> = ArrayList<Category>()
    internal var spinValue = "Default"
    internal var spinRepeat = 0
    internal var taskDate = ""
    internal var id_category = -1

    internal var c = Calendar.getInstance()

    private val CHANNEL_ID = "CHANNEL_ID_01"

    fun View.toggleVisibility() {
        if (visibility == View.VISIBLE) {
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        if (intent.extras != null){
            var bundle = intent.extras
            if (bundle != null) {
                id_category = bundle.getInt("id_task")
                Toast.makeText(this, id_category.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        setSupportActionBar(toolbar)
        txt_date.setFocusable(false);
        txt_date.setClickable(true);

        txt_time.setFocusable(false);
        txt_time.setClickable(true);

        db = DBHelper(this)
        refreshData()
        iniSpinnerRepeat()

        backButton.setOnClickListener {
            onBackPressed()
        }

        ic_addCategory.setOnClickListener {
            addNewCategory()
        }

        val dy = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        var mL=  arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        var mS = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec");
        ic_removeDate.toggleVisibility()
        txt_time.toggleVisibility()
        ic_removeTime.toggleVisibility()
        ic_times.toggleVisibility()

        txt_date.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                c.set(Calendar.YEAR, mYear)
                c.set(Calendar.MONTH, mMonth)
                c.set(Calendar.DAY_OF_MONTH, mDay)

                taskDate = ""+mDay+"/"+(mMonth+1)+"/"+mYear
                txt_date.setText(""+ dy[c.get(Calendar.DAY_OF_WEEK)-1] +", " + mL[mMonth] + " " + mDay + ", " + mYear)
                ic_removeDate.toggleVisibility()
                txt_time.toggleVisibility()
                ic_times.toggleVisibility()
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

            dpd.show()
        }

        txt_time.setOnClickListener {
            val timeSetLis = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                c.set(Calendar.HOUR_OF_DAY, hour)
                c.set(Calendar.MINUTE, minute)
                c.set(Calendar.SECOND, 0)
                txt_time.setText(SimpleDateFormat("HH:mm").format(c.time))
                ic_removeTime.toggleVisibility()
            }
            TimePickerDialog(this, timeSetLis, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }

        ic_removeDate.setOnClickListener {
            txt_date.setText("")
            txt_time.toggleVisibility()
            ic_removeDate.toggleVisibility()
            ic_times.toggleVisibility()

            if(txt_time.text != null && txt_time.text.toString() != "")
                ic_removeTime.toggleVisibility()
        }

        createNoticficationChannel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_save, menu)
        return true
    }

    fun append(arr: Array<String>, element: String): Array<String> {
        val list: MutableList<String> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }

    fun refreshData(){
        lstCategory = db.allCategory
        val temp_array: Array<Category> = lstCategory.toTypedArray()
        var listCate = arrayOf("Default")
        temp_array.forEach {
            listCate = append(
                listCate,
                it.name_category.toString()
            )
        }

        val adapter2 = ArrayAdapter(
            this,
            R.layout.add_spinner_item,
            listCate
        )

        adapter2.setDropDownViewResource(R.layout.toolbar_spinner_item_list)
        spinnerAdd?.adapter = adapter2
        spinnerAdd?.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent?.getId() == R.id.spinnerAdd)
            spinValue = parent?.getItemAtPosition(position) as String
//        else if (parent?.getId() == R.id.spinnerRepeat)
//            Toast.makeText(this, parent?.getItemAtPosition(position) as String, Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        println("Tis")
    }

    private fun addNewCategory() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.toolbar_add_category, null)

        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("New List")

        val  mAlertDialog = mBuilder.show()

        mDialogView.dialogLoginBtn.setOnClickListener {
            val category = Category(
                0,
                mDialogView.dialogNameEt.text.toString()
            )

            db.addCategory(category)
            refreshData()
            mAlertDialog.dismiss()
        }

        mDialogView.dialogCancelBtn.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if (id == R.id.add_list_action) {

            if (txt_nameTask.text.toString() == "" || txt_time.text.toString() == "" || taskDate == ""){
                Toast.makeText(this, "Field tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                addTask()
            }

            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun addTask() {
        lstCategory = db.getCategory(spinValue.toString())
        val temp_array: Array<Category> = lstCategory.toTypedArray()
        var idCate = 0

        if (spinValue != "Default")
            idCate = temp_array[0].id_category

        val task = Task(
            0, idCate, txt_nameTask.text.toString(), taskDate, txt_time.text.toString(), 0, 1
        )

        setAlarm(c, "Waktunya "+txt_nameTask.text.toString())
        db.addTask(task)
        onBackPressed()
    }

    fun iniSpinnerRepeat(){
        var lstRepeat = arrayOf("No Repeat", "Once A day", "Once a Week", "Once a month")
        val adapter = ArrayAdapter(
            this,
            R.layout.add_spinner_item,
            lstRepeat
        )

        adapter.setDropDownViewResource(R.layout.toolbar_spinner_item_list)
        spinnerRepeat?.adapter = adapter
        spinnerRepeat?.onItemSelectedListener = this
    }

    private fun createNoticficationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notif Title"
            val descriptionText = "Notif Desc"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setAlarm(now: Calendar, message: String){
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss")

        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendarList = ArrayList<Calendar>()

        calendarList.add(now)
        val text_timer = StringBuilder()
        for (calendar in calendarList){
            calendar.add(Calendar.SECOND, 0)
            val requestCode = Random().nextInt()
            val intent = Intent(this, MyAlarmReceiver::class.java)
            intent.putExtra("message", message)

            val pi = PendingIntent.getBroadcast(this, requestCode, intent, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pi)
            else
                am.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pi)

            text_timer.append(simpleDateFormat.format(calendar.timeInMillis)).append("\n")
        }
    }
}