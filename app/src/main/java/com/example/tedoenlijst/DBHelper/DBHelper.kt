package com.example.tedoenlijst.DBHelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.tedoenlijst.Model.Category
import java.util.ArrayList

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VER) {
    companion object{
        private val DATABASE_VER = 1;
        private val DATABASE_NAME = "Tedoenlijst.db"
        private val TABLE_NAME = "Category"
        private val COL_ID_CATEGORY = "id_category"
        private val COL_NAME_CATEGORY = "name_category"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY = ("CREATE TABLE $TABLE_NAME($COL_ID_CATEGORY INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, $COL_NAME_CATEGORY TEXT)")
        db!!.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db!!)
    }

    val allCategory:List<Category> get(){
        val listCategory = ArrayList<Category>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
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
        db.insert(TABLE_NAME,null, values)
        db.close()
    }

    fun updateCategory(category: Category):Int{
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_NAME_CATEGORY, category.name_category)
        return db.update(TABLE_NAME, values, "$COL_ID_CATEGORY=?",
            arrayOf(category.id_category.toString()))
    }

    fun deleteCategory(category: Category){
        val db = this.writableDatabase
        db.delete(TABLE_NAME,"$COL_ID_CATEGORY=?", arrayOf(category.id_category.toString()))
        db.close()
    }
}
