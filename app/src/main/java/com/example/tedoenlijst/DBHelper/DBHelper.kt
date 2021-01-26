package com.example.tedoenlijst.DBHelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.tedoenlijst.Model.Category
import com.example.tedoenlijst.Model.Task
import java.util.ArrayList

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VER) {
    companion object{
        private val DATABASE_VER = 2;
        private val DATABASE_NAME = "Tedoenlijst.db"

        private val TABLE_NAME_CATEGORY = "Category"
        private val COL_ID_CATEGORY = "id_category"
        private val COL_NAME_CATEGORY = "name_category"

        private val TABLE_NAME_TASK = "Task"
        private val COL_ID_TASK = "id_task"
        private val COL_NAME_TASK = "name_task"
        private val COL_DATE_TASK = "date_task"
        private val COL_TIME_TASK = "time_task"
        private val COL_REPEAT = "repeat"
        private val COL_ACTIVE = "task_active"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY = ("CREATE TABLE $TABLE_NAME_CATEGORY($COL_ID_CATEGORY INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, $COL_NAME_CATEGORY TEXT)")
        db!!.execSQL(CREATE_TABLE_QUERY)

        val CREATE_TABLE_QUERY2 = ("CREATE TABLE $TABLE_NAME_TASK($COL_ID_TASK INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, $COL_ID_CATEGORY INT, $COL_NAME_TASK TEXT, $COL_DATE_TASK TEXT, $COL_TIME_TASK TEXT, $COL_REPEAT INT, $COL_ACTIVE INT)")
        db!!.execSQL(CREATE_TABLE_QUERY2)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_CATEGORY")
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_TASK")
        onCreate(db!!)
    }

    val allCategory:List<Category> get(){
        val listCategory = ArrayList<Category>()
        val selectQuery = "SELECT * FROM $TABLE_NAME_CATEGORY"
        val db = this.writableDatabase
        val crusor = db.rawQuery(selectQuery, null)
        if(crusor.moveToFirst()){
            do {
                val category = Category()
                category.id_category = crusor.getInt(crusor.getColumnIndex(COL_ID_CATEGORY))
                category.name_category = crusor.getString(crusor.getColumnIndex(COL_NAME_CATEGORY))
                listCategory.add(category)
            } while (crusor.moveToNext())
        }
        return listCategory
    }

    val allCategoryWithTask:List<Category> get(){
        val listCategory = ArrayList<Category>()
        val selectQuery = "SELECT Category.id_category, Category.name_category, COUNT(Task.id_task) AS jumlah FROM Category LEFT JOIN Task ON Task.id_category=Category.id_category GROUP BY Category.name_category"
        val db = this.writableDatabase
        val crusor = db.rawQuery(selectQuery, null)
        if(crusor.moveToFirst()){
            do {
                val category = Category()
                category.id_category = crusor.getInt(crusor.getColumnIndex(COL_ID_CATEGORY))
                category.name_category = crusor.getString(crusor.getColumnIndex(COL_NAME_CATEGORY))
                category.jumlahTask = crusor.getInt(crusor.getColumnIndex("jumlah"))
                listCategory.add(category)
            } while (crusor.moveToNext())
        }
        return listCategory
    }

    fun getCategory(name_cate: String):List<Category> {
        var listCategory = ArrayList<Category>()
        val selectQuery = "SELECT * FROM $TABLE_NAME_CATEGORY WHERE $COL_NAME_CATEGORY='"+name_cate+"'"
        val db = this.writableDatabase
        val crusor = db.rawQuery(selectQuery, null)
        if(crusor.moveToFirst()){
            do {
                val category = Category()
                category.id_category = crusor.getInt(crusor.getColumnIndex(COL_ID_CATEGORY))
                category.name_category = crusor.getString(crusor.getColumnIndex(COL_NAME_CATEGORY))
                listCategory.add(category)
            } while (crusor.moveToNext())
        }

        return listCategory
    }

    fun addCategory(category: Category){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_NAME_CATEGORY, category.name_category)
        db.insert(TABLE_NAME_CATEGORY,null, values)
        db.close()
    }

    fun updateCategory(category: Category):Int{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_NAME_CATEGORY, category.name_category)
        return db.update(TABLE_NAME_CATEGORY, values, "$COL_ID_CATEGORY=?",
            arrayOf(category.id_category.toString()))
    }

    fun deleteCategory(category: Category){
        val db = this.writableDatabase
        db.delete(TABLE_NAME_CATEGORY,"$COL_ID_CATEGORY=?", arrayOf(category.id_category.toString()))
        db.close()
    }

    val allTask:List<Task> get(){
        val listTask = ArrayList<Task>()
        val selectQuery = "SELECT * FROM $TABLE_NAME_TASK"
        val db = this.writableDatabase
        val crusor = db.rawQuery(selectQuery, null)
        if(crusor.moveToFirst()){
            do {
                val task = Task()
                task.id_task = crusor.getInt(crusor.getColumnIndex(COL_ID_TASK))
                task.id_category = crusor.getInt(crusor.getColumnIndex(COL_ID_CATEGORY))
                task.name_task = crusor.getString(crusor.getColumnIndex(COL_NAME_TASK))
                task.date_task = crusor.getString(crusor.getColumnIndex(COL_DATE_TASK))
                task.time_task = crusor.getString(crusor.getColumnIndex(COL_TIME_TASK))
                task.repeat = crusor.getInt(crusor.getColumnIndex(COL_REPEAT))
                task.task_active = crusor.getInt(crusor.getColumnIndex(COL_ACTIVE))
                listTask.add(task)
            } while (crusor.moveToNext())
        }
        return listTask
    }

    fun addTask(task: Task){
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COL_ID_CATEGORY, task.id_category)
        values.put(COL_NAME_TASK, task.name_task)
        values.put(COL_DATE_TASK, task.date_task)
        values.put(COL_TIME_TASK, task.time_task)
        values.put(COL_REPEAT, task.repeat)
        values.put(COL_ACTIVE, task.task_active)

        db.insert(TABLE_NAME_TASK,null, values)
        db.close()
    }

    fun deleteTask(task: Task){
        val db = this.writableDatabase
        db.delete(TABLE_NAME_TASK,"$COL_ID_TASK=?", arrayOf(task.id_task.toString()))
        db.close()
    }

    fun updateTask(task: Task):Int{
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COL_NAME_TASK, task.name_task)
        values.put(COL_DATE_TASK, task.date_task)
        values.put(COL_TIME_TASK, task.time_task)
        values.put(COL_REPEAT, task.repeat)
        values.put(COL_ID_CATEGORY, task.id_category)
        values.put(COL_ACTIVE, task.task_active)

        return db.update(
            TABLE_NAME_TASK, values, "$COL_ID_TASK=?",
            arrayOf(task.id_task.toString()))
    }

    fun updateTaskActive(task: Task):Int{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ACTIVE, task.task_active)
        return db.update(TABLE_NAME_TASK, values, "$COL_ID_TASK=?",
            arrayOf(task.id_task.toString()))
    }
}
