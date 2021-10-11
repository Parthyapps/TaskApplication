package com.parthyapps.taskapplication.view

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.R.attr
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.parthyapps.taskapplication.R
import com.parthyapps.taskapplication.database.TaskRecord
import com.parthyapps.taskapplication.databinding.ActivityCreateTaskBinding
import com.terentiev.notes.utils.Constants.INTENT_OBJECT
import android.graphics.Bitmap

import android.R.attr.data
import java.io.ByteArrayOutputStream


class CreateTaskActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private var taskRecord: TaskRecord? = null

    private lateinit var binding: ActivityCreateTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.toolbar.setTitle(R.string.app_name)
        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val intent = intent
        if (intent != null && intent.hasExtra(INTENT_OBJECT)) {
            val task: TaskRecord =
                intent.getParcelableExtra(INTENT_OBJECT)!!
            this.taskRecord = task
            prePopulateData(task)
            binding.etTodoTitle.setSelection(binding.etTodoTitle.text.length)
        }
        binding.toolbar.toolbar.title =
            if (taskRecord != null) getString(R.string.editNote) else getString(R.string.createNote)

        binding.done.setOnClickListener {
            saveTodo()
        }
        binding.add.setOnClickListener {

            pickImage()
        }
    }

    private fun pickImage() {

        if (ActivityCompat.checkSelfPermission(
                this,
                READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            // val intent = Intent()
            // intent.type = "image/*"
            // intent.action = Intent.ACTION_GET_CONTENT
            // startActivityForResult(Intent.createChooser(intent, "Select Picture"), 123)

            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            )
            intent.type = "image/*"
            intent.putExtra("crop", "true")
            intent.putExtra("scale", true)
            intent.putExtra("aspectX", 16)
            intent.putExtra("aspectY", 9)
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_REQUEST_CODE
            )
        }
    }

    private fun prePopulateData(task: TaskRecord) {
        binding.etTodoTitle.setText(task.title)
        binding.etTodoContent.setText(task.content)
    }

    private fun saveTodo() {
        if (validateFields()) {
            val id = if (taskRecord != null) taskRecord?.id else null
            val todo = TaskRecord(
                id = id,
                title = binding.etTodoTitle.text.toString(),
                content = binding.etTodoContent.text.toString(),
                image = selectedImageUri.toString()
            )
            val intent = Intent()
            intent.putExtra(INTENT_OBJECT, todo)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE) {

            selectedImageUri = data!!.data!!
            binding.image.setImageURI(selectedImageUri)

            val theImage = data.extras!!.get("data") as Bitmap

            val stream = ByteArrayOutputStream()
            theImage.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray: ByteArray = stream.toByteArray()
         //   selectedImageUri = byteArray

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // pick image after request permission success
                    pickImage()
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        if (binding.etTodoTitle.text.isEmpty()) {
            binding.etTodoTitle.error =
                getString(R.string.pleaseEnterTitle)
            binding.etTodoTitle.requestFocus()
            return false
        }
        if (binding.etTodoContent.text.isEmpty()) {
            binding.etTodoContent.error =
                getString(R.string.pleaseEnterContent)
            binding.etTodoContent.requestFocus()
            return false
        }
        return true
    }

    companion object {
        const val PICK_IMAGE_REQUEST_CODE = 1000
        const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 1001
    }
}
