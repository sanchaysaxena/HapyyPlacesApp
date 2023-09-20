package com.example.happyplaces

import android.Manifest
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.happyplaces.databinding.ActivityAddHpactivityBinding
import com.example.happyplaces.databinding.ActivityMainBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddHPActivity : AppCompatActivity(),View.OnClickListener {

    private var cal=Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    private lateinit var binding: ActivityAddHpactivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddHpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.tbAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbAddPlace.setNavigationOnClickListener {
            onBackPressed()
        }
        dateSetListener=DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.MONTH,month)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDateInView()
        }
        binding.etDate.setOnClickListener(this)
        binding.tvAddImage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.et_date->{
                DatePickerDialog(this@AddHPActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            R.id.tv_add_image->{
                val pictureDialog=AlertDialog.Builder(this)
                pictureDialog.setTitle("SELECT ACTION")
                val pictureDialogItems= arrayOf("Select photo from gallery","Select photo from camera")
                pictureDialog.setItems(pictureDialogItems){
                    _, which->
                    when(which){
                        0->choosePhotoFromGallery()
                        1-> Toast.makeText(this,"Camera selection coming soon",Toast.LENGTH_SHORT).show()
                    }
                }.show()
            }
        }
    }

    private fun choosePhotoFromGallery() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?)
            {
                if(report!!.areAllPermissionsGranted()){
                    Toast.makeText(this@AddHPActivity,"All permissions granted. Now you can select the image from gallery",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions:MutableList<PermissionRequest> , token: PermissionToken)
            {
                showRationalDialogPermissions()
            }
        }).onSameThread().check()
    }
    private fun showRationalDialogPermissions(){
        AlertDialog.Builder(this).setMessage("It looks like you have turned off permissions required")
            .setPositiveButton("Go to settings"){
                _,_->
                try {
                    val intent=Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri=Uri.fromParts("package",packageName,null)
                    intent.data=uri
                    startActivity(intent)
                }catch (e:ActivityNotFoundException){
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel"){ dialog,_->
                dialog.dismiss()
            }.show()
    }

    private fun updateDateInView(){
        val myFormat="dd.MM.yyyy"
        val sdf=SimpleDateFormat(myFormat, Locale.getDefault())
        binding.etDate.setText(sdf.format(cal.time).toString())
    }
}