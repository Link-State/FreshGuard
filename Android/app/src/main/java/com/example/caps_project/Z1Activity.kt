package com.example.caps_project

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.caps_project.models.responses.ResponseDiscernmentIngredient
import com.example.caps_project.models.responses.ResponseLoadDiscernment
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class Z1Activity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var btnCamera: Button
    private lateinit var btnLoad: Button
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_photo_load)

        ImageSingleton.clear()

        imageView = findViewById(R.id.iv_profile)
        btnCamera = findViewById(R.id.btn_camera)
        btnLoad = findViewById(R.id.btn_load)
        btnNext = findViewById(R.id.btn_next)

        // 사진이 들어간 여부 체크용
        var hasImage = false

        // 카메라 버튼
        btnCamera.setOnClickListener {
            if (checkCameraPermission()) openCamera()
            else requestCameraPermission()
        }

        // 갤러리 버튼
        btnLoad.setOnClickListener {
            openGallery()
        }

        // 다음 버튼 → B6_LoadingActivity 이동
        btnNext.setOnClickListener {
            if (!hasImage && ImageSingleton.bitmap == null) {
                Toast.makeText(this, "사진을 먼저 넣어주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fileName = "target.jpg"
            val targetFile = File(cacheDir, fileName)

            try {
                targetFile.createNewFile()
                val stream = FileOutputStream(targetFile)

                ImageSingleton.bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.close()
            } catch (e: Exception) {
                Toast.makeText(this@Z1Activity, "파일문제, 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                finish()
            }

            val requestText = Session.userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val requestImage = targetFile.asRequestBody("image/jpg".toMediaTypeOrNull())
            val requestMultipart = MultipartBody.Part.createFormData("image", targetFile.name, requestImage)
            var retryCount = 0
            RetrofitBuilder.api.discernmentIngredient(requestText, requestMultipart).enqueue(object : Callback<ResponseDiscernmentIngredient> {
                override fun onResponse(call: Call<ResponseDiscernmentIngredient>, response: Response<ResponseDiscernmentIngredient>) {
                    if (response.isSuccessful) {
                        if (response.body()?.success == true) {
                            val intent = Intent(this@Z1Activity, B6_LoadingActivity::class.java)
                            intent.putExtra("session_id", response.body()?.session_id)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@Z1Activity, "다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseDiscernmentIngredient>, t: Throwable) {
                    retryCount += 1
                    if (retryCount <= RetrofitBuilder.retry) {
                        call.clone().enqueue(this)
                    }
                }
            })
        }
    }

    // 카메라 결과 처리
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data

            val imageBitmap = data?.extras?.get("data") as? Bitmap

            if (imageBitmap != null) {
                imageView.setImageBitmap(imageBitmap)
                ImageSingleton.bitmap = imageBitmap
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    // 갤러리 결과 처리
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val imageUri = data?.data

            if (imageUri != null) {
                imageView.setImageURI(imageUri)

                // Bitmap 으로 변환 후 저장
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                ImageSingleton.bitmap = bitmap
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            100
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
