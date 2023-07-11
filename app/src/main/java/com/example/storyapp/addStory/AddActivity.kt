package com.example.storyapp.addStory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityAddBinding
import com.example.storyapp.model.ViewModelFactory
import com.example.storyapp.strorypage.ListStoryActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding

    private lateinit var viewModelFactory: ViewModelFactory

    private lateinit var currentPhotoPath: String

    private var getFile: File? = null

    private val addSViewModel: AddViewModel by viewModels { viewModelFactory }

    companion object {
        private const val Request_Code = 10
        private val Required_Permission = arrayOf(Manifest.permission.CAMERA)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.title_add_story)
            setDisplayHomeAsUpEnabled(true)
        }

        viewModel()
        permission()
        addStory()
    }

    private fun viewModel() {
        viewModelFactory = ViewModelFactory.getInstance(this)
    }

    private fun permission() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this@AddActivity,
                Required_Permission,
                Request_Code
            )
        }
    }

    private fun allPermissionsGranted() = Required_Permission.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    //bagian set ADDSTORY

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addStory() {
        binding.apply {
            buttonCamera.setOnClickListener { takePhoto() }
            buttonOpenFile.setOnClickListener { startGallery() }
            buttonAdd.setOnClickListener { uploadStory() }
        }
    }

    private fun uploadStory() {
        Loading()
        addSViewModel.getSession().observe(this@AddActivity) {
            if (getFile != null) {
                val file = FileImage(getFile as File)
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                upload(
                    it.token,
                    imageMultipart,
                    binding.edAddDescription.text.toString()
                        .toRequestBody("text/plain".toMediaType())
                )
            } else {
                Toast.makeText(
                    this@AddActivity, getString(R.string.input_image),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun upload(token: String, imageMultipart: MultipartBody.Part, toRequestBody: RequestBody, ) {
        addSViewModel.Story(token, imageMultipart, toRequestBody)
        addSViewModel.upload.observe(this@AddActivity) {
            if (!it.error) {
                moveActivity()
            }
        }
        show()
    }

    private fun moveActivity() {
        val intent = Intent(this@AddActivity, ListStoryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun show() {
        addSViewModel.toast.observe(this@AddActivity) {
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(
                    this@AddActivity, toastText, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun Loading() {
        addSViewModel.Loading.observe(this@AddActivity) {
            binding.pbAdd.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"

        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val File = File(selectedImg, this)

            getFile = File
            binding.ivAddStory.setImageURI(selectedImg)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddActivity,
                "com.example.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath

            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val resultBitmap = BitmapFactory.decodeFile(getFile?.path)

            val exif = ExifInterface(getFile?.path!!)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }

            val rotatedBitmap =
                Bitmap.createBitmap(resultBitmap, 0, 0, resultBitmap.width, resultBitmap.height, matrix, true)

            exif.setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL.toString())
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> exif.setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_90.toString())
                ExifInterface.ORIENTATION_ROTATE_180 -> exif.setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_180.toString())
                ExifInterface.ORIENTATION_ROTATE_270 -> exif.setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_270.toString())
            }
            exif.saveAttributes()

            binding.ivAddStory.setImageBitmap(rotatedBitmap)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}



