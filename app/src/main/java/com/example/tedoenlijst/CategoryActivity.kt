package com.example.tedoenlijst

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import com.example.tedoenlijst.Adapter.ListCategoryAdapter
import com.example.tedoenlijst.DBHelper.DBHelper
import com.example.tedoenlijst.Model.Category
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.activity_category.toolbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_add_category.view.*

class CategoryActivity : AppCompatActivity(),  AdapterView.OnItemSelectedListener {

    internal lateinit var db: DBHelper
    internal var lstCategory:List<Category> = ArrayList<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        setSupportActionBar(toolbar)

        task_list.setDivider(null)

        db = DBHelper(this)
        refreshData()

        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if (id == R.id.add_list_action) {
            addNewCategory()
            return true
        }

        return super.onOptionsItemSelected(item)

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

    fun refreshData(){
        lstCategory = db.allCategory
        val adapter = ListCategoryAdapter(this@CategoryActivity, lstCategory, update_data2,
            hapus_data2, category_name2, this)
        task_list.adapter = adapter
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}