package com.example.caps_project

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.caps_project.databinding.ActivityDetailBinding
import com.example.caps_project.models.requests.RequestAddIngredient
import com.example.caps_project.models.requests.RequestDeleteIngredient
import com.example.caps_project.models.requests.RequestLoadHistory
import com.example.caps_project.models.responses.HistoryList
import com.example.caps_project.models.responses.ResponseAddIngredient
import com.example.caps_project.models.responses.ResponseDeleteIngredient
import com.example.caps_project.models.responses.ResponseLoadHistory
import com.example.caps_project.models.responses.ResponseModifyIngredient
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailBinding
    var originIngredient:Int? = null
    var originLevel:Int? = null
    var originExpire:String? = null
    var originImage:String? = null
    private var ingredient_id = -1

    private var isChangedImage = false

    private var touchedDeleteBtn: Int = 0
    private var isEditing: Boolean = false

    private lateinit var imgFood: ImageView

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val imageUri = data?.data

            if (imageUri != null) {
                imgFood.setImageURI(imageUri)

                // Bitmap 으로 변환 후 저장
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                ImageSingleton.bitmap = bitmap
                isChangedImage = true
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isChangedImage = false
        touchedDeleteBtn = 0
        isEditing = false

        val etName = findViewById<EditText>(R.id.et_name)
        val etExpiry = findViewById<EditText>(R.id.et_expiry)
        val etFreshness = findViewById<EditText>(R.id.et_freshness)
        val etConsume = findViewById<EditText>(R.id.et_consume)
        val btnDelete = findViewById<TextView>(R.id.btn_delete_ingredient)
        val btnSave = findViewById<Button>(R.id.btn_save)
        val btnCancel = findViewById<Button>(R.id.btn_cancel)
        imgFood = findViewById<ImageView>(R.id.img_food)

        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd (E)")

        // B2에서 전달된 데이터 표시
        val mode:Int = intent.getIntExtra("MODE", 1)
        if (mode == 1) {
            btnDelete.visibility = View.GONE
            etName.setText("")
            etExpiry.setText(currentDate.format(formatter))
            etFreshness.setText("")
            etConsume.setText("")
            Glide.with(this).clear(binding.imgFood)
            btnSave.text = "저장"
        }
        else if (mode == 2) {
            btnDelete.visibility = View.VISIBLE
            ingredient_id = intent.getIntExtra("ingredient_id", -1)
            originIngredient = Constant.Name2Code.get(intent.getStringExtra("name"))
            originExpire = intent.getStringExtra("expire")
            originLevel = Constant.Name2Level.get(intent.getStringExtra("freshness"))
            originImage = intent.getStringExtra("image")

            etName.setText(intent.getStringExtra("name") ?: "")
            etExpiry.setText(intent.getStringExtra("date") ?: "")
            etFreshness.setText(intent.getStringExtra("freshness") ?: "")
            etConsume.setText(intent.getStringExtra("expire") ?: "")
            Glide.with(this).load(intent.getStringExtra("image")).into(binding.imgFood)
            btnSave.text = "수정"

            btnDelete.setOnClickListener {
                if (isEditing) return@setOnClickListener

                touchedDeleteBtn += 1

                if (touchedDeleteBtn == 1) {
                    Toast.makeText(this@DetailActivity, "식재료를 삭제하기 위해서는 삭제 버튼을 2번 더 눌러주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (touchedDeleteBtn == 2) {
                    Toast.makeText(this@DetailActivity, "삭제 버튼을 한번 더 누르면 해당 식재료가 삭제됩니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (touchedDeleteBtn > 3) {
                    return@setOnClickListener
                }

                isEditing = true
                // 식재료 삭제 요청
                val input = RequestDeleteIngredient(ingredient_id)
                var retryCount = 0
                RetrofitBuilder.api.deleteIngredient(input).enqueue(object : Callback<ResponseDeleteIngredient> {
                    override fun onResponse(call: Call<ResponseDeleteIngredient>, response: Response<ResponseDeleteIngredient>) {
                        if (response.isSuccessful) {
                            if (response.body()?.success == true) {
                                Toast.makeText(this@DetailActivity, "식재료가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@DetailActivity, "삭제에 실패하였습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@DetailActivity, "삭제에 실패하였습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }
                        isEditing = false
                    }

                    override fun onFailure(call: Call<ResponseDeleteIngredient>, t: Throwable) {
                        retryCount += 1
                        if (retryCount <= RetrofitBuilder.retry) {
                            call.clone().enqueue(this)
                        } else {
                            Toast.makeText(this@DetailActivity, "삭제에 실패하였습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                            isEditing = false
                        }
                    }
                })
            }
        }

        // (18) 이미지 클릭 시 갤러리에서 사진 변경
        imgFood.setOnClickListener {
            openGallery()
        }

        // (20) 저장 버튼 클릭
        btnSave.setOnClickListener {
            if (isEditing) return@setOnClickListener

//            Toast.makeText(this, "식재료 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show()
            val changeName = Constant.Name2Code.get(etName.text.toString().trim())
            val changeExpire = etConsume.text.toString().trim()
            val changeLevel = Constant.Name2Level.get(etFreshness.text.toString().trim())
            var hasExpire = false

            if (changeName == null) {
                Toast.makeText(this, "식재료명은 사과, 바나나, 오렌지, 피망, 당근, 오이, 망고, 감자, 딸기, 토마토 중 하나를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (changeLevel == null) {
                Toast.makeText(this, "신선도는 좋음, 보통, 나쁨 중 하나를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (changeExpire.length > 0 && changeExpire.length < 10) {
                Toast.makeText(this, "소비기한은 2025.01.01 형식으로 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (changeExpire.length >= 10) hasExpire = true

            var serversideFormattedExpireDate:String? = null
            if (hasExpire) {
                val year = changeExpire.subSequence(0, 4).toString().toIntOrNull()
                val month = changeExpire.subSequence(5, 7).toString().toIntOrNull()
                var day = changeExpire.subSequence(8, 10).toString().toIntOrNull()

                if ( year == null || month == null || day == null) {
                    Toast.makeText(this, "소비기한은 2025.01.01 형식으로 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (month < 0 || month > 12) {
                    Toast.makeText(this, "1-12월 사이로 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (day < 0) {
                    Toast.makeText(this, "1-31일 사이로 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                var isOrdinaryYear = false
                if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) isOrdinaryYear = true

                if (isOrdinaryYear && month == 2 && day > 29) {
                    day = 29
                }

                if (!isOrdinaryYear && month == 2 && day > 28) {
                    day = 28
                }

                if ((month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) && day > 31) {
                    day = 31
                }

                if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30) {
                    day = 30
                }

                val serversideFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                serversideFormattedExpireDate = LocalDate.of(year, month, day).format(serversideFormatter)
            }

            var requestMultipart: MultipartBody.Part? = null
            if(isChangedImage) {
                val fileName = "target.jpg"
                val targetFile = File(cacheDir, fileName)

                try {
                    targetFile.createNewFile()
                    val stream = FileOutputStream(targetFile)

                    ImageSingleton.bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    stream.close()
                } catch (e: Exception) {
                    Toast.makeText(this, "파일문제, 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    finish()
                }

                val requestImage = targetFile.asRequestBody("image/jpg".toMediaTypeOrNull())
                requestMultipart = MultipartBody.Part.createFormData("image", targetFile.name, requestImage)
            }

            isEditing = true

            if (mode == 1) {
                val uid = Session.userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val lv = changeLevel.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val ingre_num = changeName.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                var expd: RequestBody? = null
                if (serversideFormattedExpireDate != null) expd = serversideFormattedExpireDate.toRequestBody("text/plain".toMediaTypeOrNull())
                val dis_id = null
                var retryCount = 0
                RetrofitBuilder.api.addIngredient(uid, lv, ingre_num, expd, dis_id, requestMultipart).enqueue(object : Callback<ResponseAddIngredient> {
                    override fun onResponse(call: Call<ResponseAddIngredient>, response: Response<ResponseAddIngredient>) {
                        if (response.isSuccessful) {
                            if (response.body()?.success == true) {
//                                response.body()?.ingredient_id
                                Toast.makeText(this@DetailActivity, "성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@DetailActivity, "저장에 실패했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@DetailActivity, "저장에 실패했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }
                        isEditing = false
                    }

                    override fun onFailure(call: Call<ResponseAddIngredient>, t: Throwable) {
                        retryCount += 1
                        if (retryCount <= RetrofitBuilder.retry) {
                            call.clone().enqueue(this)
                        } else {
                            Toast.makeText(this@DetailActivity, "저장에 실패했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                            isEditing = false
                        }
                    }
                })
            }

            if (mode == 2) {
                val ingre_id = ingredient_id.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                var lv: RequestBody? = null
                var ingre_num: RequestBody? = null
                var expd: RequestBody? = null

                if (changeLevel != originLevel) {
                    lv = changeLevel.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                }

                if (changeName != originIngredient) {
                    ingre_num = changeName.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                }

                if (changeExpire != originExpire && serversideFormattedExpireDate != null) {
                    expd = serversideFormattedExpireDate.toRequestBody("text/plain".toMediaTypeOrNull())
                }

                var retryCount = 0
                RetrofitBuilder.api.modifyIngredient(ingre_id, lv, ingre_num, expd, requestMultipart).enqueue(object : Callback<ResponseModifyIngredient> {
                    override fun onResponse(call: Call<ResponseModifyIngredient>, response: Response<ResponseModifyIngredient>) {
                        if (response.isSuccessful) {
                            if (response.body()?.success == true) {
                                Toast.makeText(this@DetailActivity, "성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@DetailActivity, "수정에 실패하였습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@DetailActivity, "수정에 실패하였습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }
                        isEditing = false
                    }

                    override fun onFailure(call: Call<ResponseModifyIngredient>, t: Throwable) {
                        retryCount += 1
                        if (retryCount <= RetrofitBuilder.retry) {
                            call.clone().enqueue(this)
                        } else {
                            Toast.makeText(this@DetailActivity, "수정에 실패하였습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                            isEditing = false
                        }
                    }
                })
            }
        }

        // (21) 취소 버튼 클릭
        btnCancel.setOnClickListener {
//            Toast.makeText(this, "수정이 취소되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

}
