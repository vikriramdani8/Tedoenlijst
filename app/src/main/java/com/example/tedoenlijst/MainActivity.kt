package com.example.tedoenlijst

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import com.example.tedoenlijst.Adapter.ListCategoryAdapter
import com.example.tedoenlijst.DBHelper.DBHelper
import com.example.tedoenlijst.Model.Category
import kotlinx.android.synthetic.main.toolbar_add_category.view.*

open class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var pos = 0

    internal lateinit var db: DBHelper
    internal var lstCategory:List<Category> = ArrayList<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        list_book.setDivider(null);

        db = DBHelper(this)
        refreshData()

//        tambah_data.setOnClickListener {
//            val category = Category(
//                0,
//                category_name.text.toString()
//            )
//
//            db.addCategory(category)
//            refreshData()
//        }
//
//        update_data.setOnClickListener {
//            val category = Category(
//                Integer.parseInt(update_data.text.toString().substringAfterLast(":")),
//                category_name.text.toString()
//            )
//
//            db.updateCategory(category)
//            refreshData()
//        }
//
//        hapus_data.setOnClickListener {
//            val category = Category(
//                Integer.parseInt(hapus_data.text.toString().substringAfterLast(":")),
//                category_name.text.toString()
//            )
//
//            db.deleteCategory(category)
//            refreshData()
//        }

        btn_add.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java).apply {}
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if (id == R.id.setting_action) {
            val intent = Intent(this, CategoryActivity::class.java).apply {}
            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var items : String = parent?.getItemAtPosition(position) as String

        if (items == "New List"){
            addNewCategory()
            spinnerTollbar?.setSelection(pos)
        } else {
            pos = position
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

    fun refreshData(){
        lstCategory = db.allCategory
        val adapter = ListCategoryAdapter(this@MainActivity, lstCategory, update_data,
            hapus_data, category_name, this)
        list_book.adapter = adapter

        val temp_array: Array<Category> = lstCategory.toTypedArray()
        var listCate = arrayOf("Default")
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
}

