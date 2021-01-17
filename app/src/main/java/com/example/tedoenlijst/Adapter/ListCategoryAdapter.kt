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

class ListCategoryAdapter(
    internal var activity: Activity,
    internal var lstCategory: List<Category>,
    internal var btn_update: Button,
    internal var btn_delete: Button,
    internal var edt_name: EditText,
    val context: Context
):BaseAdapter() {

    internal lateinit var db: DBHelper
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

        trash.setOnClickListener {
            AskOption(lstCategory[position].id_category, lstCategory[position].name_category.toString())
        }

        rowView.txt_title.text = lstCategory[position].name_category.toString()
        rowView.setOnClickListener {
            edt_name.setText(rowView.txt_title.text.toString())
            btn_delete.setText("Hapus ID:" + lstCategory[position].id_category)
            btn_update.setText("Update ID:" + lstCategory[position].id_category)
        }
        return rowView
    }

    private fun AskOption(idd: Int, name: String) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to Delete?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                val category = Category(idd, name)
                db.deleteCategory(category)
                activity.recreate()
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
}

