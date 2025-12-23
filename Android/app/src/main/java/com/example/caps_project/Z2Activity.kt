package com.example.caps_project

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream

class Z2Activity : AppCompatActivity() {

    private lateinit var btnChange: Button
    private lateinit var btnDelete: Button
    private lateinit var imageView: ImageView

    private val GALLERY_REQUEST = 2001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_photo_update) // ✅ XML 이름에 맞게 설정

        btnChange = findViewById(R.id.btn_change)
        btnDelete = findViewById(R.id.btn_delete)

        // 이미지 표시용 ImageView 동적 추가
        imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                600
            )
            setPadding(0, 30, 0, 0)
            setBackgroundColor(android.graphics.Color.LTGRAY) // 삭제 시 기본 배경색
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        // 버튼들이 들어있는 LinearLayout 찾아서 ImageView 추가
        val parentLayout = findViewById<LinearLayout>(R.id.rootLayout)
        parentLayout.addView(imageView)

        // 🖼️ 사진 변경 (갤러리에서 선택)
        btnChange.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY_REQUEST)
        }

        // 🗑️ 사진 삭제
        btnDelete.setOnClickListener {
            imageView.setImageDrawable(null)
            imageView.setBackgroundColor(android.graphics.Color.LTGRAY)
            Toast.makeText(this, "사진이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 갤러리 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                val inputStream: InputStream? = contentResolver.openInputStream(selectedImageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                imageView.setImageBitmap(bitmap)
                imageView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                inputStream?.close()
                Toast.makeText(this, "사진이 변경되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}