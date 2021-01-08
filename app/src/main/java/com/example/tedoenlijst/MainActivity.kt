package com.example.tedoenlijst

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.toolbar_add_category.view.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var pos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val colours = arrayOf("Red", "Green", "Blue", "Yellow", "New List")
        val adapter = ArrayAdapter(
            this,
            R.layout.toolbar_spinner_item,
            colours
        )

        adapter.setDropDownViewResource(R.layout.toolbar_spinner_item_list)
        spinnerTollbar?.adapter = adapter
        spinnerTollbar?.onItemSelectedListener = this

        button1.setOnClickListener{
            addNewCategory()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var items : String = parent?.getItemAtPosition(position) as String
        Toast.makeText(applicationContext, "$items", Toast.LENGTH_SHORT).show()

        if (items == "New List"){
            addNewCategory()
            spinnerTollbar?.setSelection(pos)
        } else {
            pos = position
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(applicationContext, "Nothing Select", Toast.LENGTH_SHORT).show()
    }

    private fun addNewCategory() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.toolbar_add_category, null)

        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("Add List")

        val  mAlertDialog = mBuilder.show()

        mDialogView.dialogLoginBtn.setOnClickListener {
            mAlertDialog.dismiss()

            val name = mDialogView.dialogNameEt.text.toString()
            mainInfoTv.setText("Name: "+ name)
        }

        mDialogView.dialogCancelBtn.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }
}