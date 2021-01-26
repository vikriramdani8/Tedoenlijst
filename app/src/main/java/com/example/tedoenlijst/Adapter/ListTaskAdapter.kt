package com.example.tedoenlijst.Adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import com.example.tedoenlijst.AddTaskActivity
import com.example.tedoenlijst.DBHelper.DBHelper
import com.example.tedoenlijst.MainActivity
import com.example.tedoenlijst.Model.Task
import com.example.tedoenlijst.R
import kotlinx.android.synthetic.main.list_task.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ListTaskAdapter(
    internal var activity: MainActivity,
    internal var lstTask: List<Task>,
    val context: Context,
    val deleteMode: Boolean
): BaseAdapter() {

    internal lateinit var db: DBHelper
    internal var inflanter: LayoutInflater
    init {
        inflanter = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as
                LayoutInflater
    }

    fun View.toggleVisibility() {
        if (visibility == View.VISIBLE) {
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE
        }
    }

    fun View.gone(animate: Boolean = true) {
        hide(View.GONE, animate)
    }

    private fun View.hide(hidingStrategy: Int, animate: Boolean = true) {
        if (animate) {
            animate().alpha(0f).setDuration(300).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    visibility = hidingStrategy
                }
            })
        } else {
            visibility = hidingStrategy
        }
    }

    override fun getCount(): Int {
        return lstTask.size
    }

    override fun getItem(p0: Int): Any {
        return lstTask[p0]
    }

    override fun getItemId(p0: Int): Long {
        return lstTask[p0].id_task.toLong()
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView:View
        db = DBHelper(context)
        rowView = inflanter.inflate(R.layout.list_task, null)
        var lstBody = rowView.findViewById<LinearLayout>(R.id.list_task_item_body)
        var del = rowView.findViewById<ImageView>(R.id.delete_task)
        if (deleteMode)
            del.toggleVisibility()

        rowView.setOnClickListener {
            if (lstTask[position].pemisah.toString() == "" || lstTask[position].pemisah == null){
                val bundle = Bundle()
                bundle.putInt("id_task", lstTask[position].id_task)

                val intent = Intent(context, AddTaskActivity::class.java).apply {
                    putExtras(bundle)
                }

                context.startActivity(intent)
            }
        }

        if (lstTask[position].pemisah.toString() == "" || lstTask[position].pemisah == null) {
            lstBody.toggleVisibility()
            val cb = rowView.findViewById<CheckBox>(R.id.cb_task)
            val delTask = rowView.findViewById<ImageView>(R.id.delete_task)
            if (lstTask[position].task_active == 0)
                cb!!.isChecked = true
            var lst = rowView.findViewById<LinearLayout>(R.id.list_task_item)

            delTask.setOnClickListener {
                val tk = Task(lstTask[position].id_task)
                db.deleteTask(tk)
                activity.refreshDataTask()

                lst.gone()
                val handler = Handler()
                val runnable = Runnable {
                    db.deleteTask(tk)
                    activity.refreshDataTask()
                }

                handler.postDelayed(runnable, 300)
            }

            cb.setOnCheckedChangeListener { buttonView, isChecked ->
                var check = 0;
                if (!isChecked)
                    check = 1

                var task = Task(
                    lstTask[position].id_task,
                    check
                )

                lst.gone()
                val handler = Handler()
                val runnable = Runnable {
                    db.updateTaskActive(task)
                    activity.refreshDataTask()
                }

                handler.postDelayed(runnable, 300)
            }

            if (lstTask[position].date_task.toString() == "" || lstTask[position].date_task == null) {
                rowView.txt_dateTask.toggleVisibility()
            } else {
                var temp = lstTask[position].date_task.toString().split("/")
                val dy = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                var mS = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec");

                val c = Calendar.getInstance()
                c.set(Calendar.YEAR, temp[2].toInt())
                c.set(Calendar.MONTH, temp[1].toInt())
                c.set(Calendar.DAY_OF_MONTH, temp[0].toInt())

                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd/M/yyyy")
                val formattedToday = current.format(formatter)

                val tomorrow = current.plusDays(1)
                val formattedTomorrow = tomorrow.format(formatter)

                val yesterday = current.minusDays(1)
                val formattedYesterday = yesterday.format(formatter)

                var dte = ""
                if (formattedToday == lstTask[position].date_task.toString())
                    dte = "Today"
                else if (formattedTomorrow == lstTask[position].date_task.toString())
                    dte = "Tomorrow"
                else if (formattedYesterday == lstTask[position].date_task.toString())
                    dte = "Yesterday"
                else
                    dte = dy[c.get(Calendar.DAY_OF_WEEK)]+", "+ mS[c.get(Calendar.MONTH)-1] +" "+c.get(Calendar.DAY_OF_MONTH)

                rowView.txt_dateTask.text = dte+", "+lstTask[position].time_task.toString()
            }

            rowView.txt_nameTask.text = lstTask[position].name_task.toString()
        } else {
            rowView.pemisah.toggleVisibility()
            rowView.pemisah.text = lstTask[position].pemisah.toString()
            if (lstTask[position].pemisah.toString() == "Overdue")
                rowView.pemisah.setTextColor(Color.parseColor("#b2102f"))
            else if (lstTask[position].pemisah.toString() == "No Date")
                rowView.pemisah.setTextColor(Color.parseColor("#607D8B"))
            else
                rowView.pemisah.setTextColor(Color.parseColor("#00695C"))
        }

        return rowView
    }

}