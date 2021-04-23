package com.example.project_crud

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.idescout.sql.SqlScoutServer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_update.*
import kotlinx.android.synthetic.main.dialog_update.tvcancel
import kotlinx.android.synthetic.main.dialog_user.*
import kotlinx.android.synthetic.main.item_row.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        SqlScoutServer.create(this, getPackageName());
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupListOfDataIntoRecyclerView()
        btnR.setOnClickListener{
            addRecord()
            closeKeyboard()
            setupListOfDataIntoRecyclerView()
        }
    }

    private fun addRecord(){

        val name = TextNama.text.toString()
        val email = TextEmail.text.toString()
        val phone = TextPhone.text.toString()
        val address = TextAddress.text.toString()


        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        if (!name.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !address.isEmpty()){
            val status = databaseHandler.addEmployee(EmpModel(0, name, email, phone, address))
            if (status > -1){
                Toast.makeText( this, "Record Saved", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText( this,"Masukkan Data anda", Toast.LENGTH_SHORT).show()
        }
    }

    // method untuk mendapatkan jumlah record
    private fun getItemList(): ArrayList<EmpModel>{
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val empList: ArrayList<EmpModel> = databaseHandler.viewEmployee()
        return empList
    }


    // method untuk menampilkan emplist ke recycler view
    private fun  setupListOfDataIntoRecyclerView(){
        if (getItemList().size > 0){
            rv_item.visibility = View.VISIBLE
            TV_No_record.visibility = View.GONE

            rv_item.layoutManager = LinearLayoutManager(this)
            rv_item.adapter =  ItemAdapter(this,getItemList())
        }else{
            rv_item.visibility = View.GONE
            TV_No_record.visibility = View.VISIBLE
        }
    }

    //show more data
    fun moredata (empModelClass: EmpModel) {
        val moredatauser = Dialog(this, R.style.Theme_Dialog)

        moredatauser.setCancelable(true)
        moredatauser.setContentView(R.layout.dialog_user)

        moredatauser.data_name.setText(empModelClass.nama)
        moredatauser.data_email.setText(empModelClass.email)
        moredatauser.data_phone.setText(empModelClass.phone)
        moredatauser.data_address.setText(empModelClass.address)

        moredatauser.tvcancel.setOnClickListener {
            moredatauser.dismiss()
        }
        moredatauser.show()
    }


    // update data
    fun updateRecordDialog (empModelClass: EmpModel) {
        val updateDialog = Dialog(this, R.style.Theme_Dialog)

        updateDialog.setCancelable(true)
        updateDialog.setContentView(R.layout.dialog_update)

        updateDialog.et_updatename.setText(empModelClass.nama)
        updateDialog.et_updateemail.setText(empModelClass.email)
        updateDialog.et_updatephone.setText(empModelClass.phone)
        updateDialog.et_updateaddress.setText(empModelClass.address)

        updateDialog.tvupdate.setOnClickListener{
            val name = updateDialog.et_updatename.text.toString()
            val email = updateDialog.et_updateemail.text.toString()
            val phone = updateDialog.et_updatephone.text.toString()
            val address = updateDialog.et_updateaddress.text.toString()

            val databaseHandler : DatabaseHandler = DatabaseHandler(this)

            if (!name.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !address.isEmpty()) {
                val status = databaseHandler.updateEmployee (EmpModel(empModelClass.id, name, email, phone, address))
                if (status > -1) {
                    Toast.makeText(this, "Record Update", Toast.LENGTH_SHORT).show()
                    setupListOfDataIntoRecyclerView()
                    updateDialog.dismiss()
                    closeKeyboard()
                }
            } else {
                Toast.makeText(this, "Name or Email Can't Blank", Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(this, "Tombol Update", Toast.LENGTH_SHORT).show()
        }
        updateDialog.tvcancel.setOnClickListener{
            updateDialog.dismiss()
        }
        updateDialog.show()


    }


    // delete Data
    fun deleteRecordAlertDialog (empModelClass: EmpModel) {
        val builder = AlertDialog.Builder (this)

        builder.setTitle("Delete Record ")
        builder.setMessage("Are You Sure ?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        // Menampilkan tombol Yes
        builder.setPositiveButton("Yes") {dialog: DialogInterface?, which ->
            val databaseHandler : DatabaseHandler = DatabaseHandler(this)
            val status = databaseHandler.deleteEmployee(EmpModel(empModelClass.id, "", "", "", ""))

            if (status > -1) {
                Toast.makeText(this, "Record Delete Successfully", Toast.LENGTH_SHORT).show()
                setupListOfDataIntoRecyclerView()
            }
            dialog?.dismiss()
        }

        // Menampilkan tombol No
        builder.setNegativeButton("No") {dialog: DialogInterface?, which ->
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    // Method to Close Keyboard
    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}