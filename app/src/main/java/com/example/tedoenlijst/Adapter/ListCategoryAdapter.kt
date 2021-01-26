package com.example.tedoenlijst.Adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.tedoenlijst.CategoryActivity
import com.example.tedoenlijst.DBHelper.DBHelper
import com.example.tedoenlijst.Model.Category
import com.example.tedoenlijst.R
import kotlinx.android.synthetic.main.list_category.view.*
import com.example.tedoenlijst.MainActivity
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.toolbar_add_category.*
import kotlinx.android.synthetic.main.toolbar_add_category.view.*

class ListCategoryAdapter(
    internal var activity: CategoryActivity,
    internal var lstCategory: List<Category>,
    val context: Context
):BaseAdapter() {

    internal lateinit var db: DBHelper
    internal lateinit var tempCategory: List<Category>
    internal var inflanter:LayoutInflater
    init {
        inflanter = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as
                LayoutInflater
    }

    override fun getCount(): Int {
        return lstCategory.size
    }

    override fun getItem(p0: Int): Any {
        return lstCategory[p0]
    }

    override fun getItemId(p0: Int): Long {
        return lstCategory[p0].id_category.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val rowView:View
        db = DBHelper(context)
        rowView = inflanter.inflate(R.layout.list_category, null)
        val trash = rowView.findViewById<ImageView>(R.id.trash)
        val edit = rowView.findViewById<ImageView>(R.id.edit)

        trash.setOnClickListener {
            if (lstCategory[position].jumlahTask != 0)
                Toast.makeText(context, "There are still tasks in this category", Toast.LENGTH_SHORT).show()
            else
                AskOptionDelete(lstCategory[position].id_category, lstCategory[position].name_category.toString())
        }

        edit.setOnClickListener {
            AskOptionEdit(lstCategory[position].id_category, lstCategory[position].name_category.toString())
        }

        rowView.txt_title.text = lstCategory[position].name_category.toString()
        if (lstCategory[position].jumlahTask != 0){
            rowView.txt_count.text = "Task: "+lstCategory[position].jumlahTask.toString()
        } else {
            rowView.txt_count.text = "No Task"
        }
        return rowView
    }

    private fun AskOptionEdit(idd: Int, name: String) {
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.toolbar_add_category, null)
        val mBuilder = androidx.appcompat.app.AlertDialog.Builder(context)
            .setView(mDialogView)
            .setTitle("Edit Category")

        val mAlertDialog = mBuilder.show()
        mDialogView.dialogNameEt.setText(name)
        mDialogView.dialogLoginBtn.setText("Update")

        mDialogView.dialogLoginBtn.setOnClickListener {
            if (mDialogView.dialogNameEt.text.toString() != "Default" && mDialogView.dialogNameEt.text.toString() != "Finished" && mDialogView.dialogNameEt.text.toString() != "All List" && mDialogView.dialogNameEt.text.toString() != "New List") {
                if (checkCategorryValid(mDialogView.dialogNameEt.text.toString())){
                    Toast.makeText(context, "This category name is already exist", Toast.LENGTH_SHORT).show()
                } else {
                    val category = Category(
                        idd,
                        mDialogView.dialogNameEt.text.toString()
                    )

                    db.updateCategory(category)
                    activity.refreshData()
                }
            } else {
                Toast.makeText(context, "Can't rename this category with this name", Toast.LENGTH_SHORT).show()
            }

            mAlertDialog.dismiss()
        }

        mDialogView.dialogCancelBtn.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun AskOptionDelete(idd: Int, name: String) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to Delete?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                val category = Category(idd, name)
                db.deleteCategory(category)
                activity.refreshData()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun checkCategorryValid(txt: String): Boolean {
        tempCategory = db.getCategory(txt)
        return tempCategory.size != 0
    }
}

