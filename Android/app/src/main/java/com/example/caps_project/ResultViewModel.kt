package com.example.caps_project

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ResultViewModel : ViewModel() {
    // 등록할 아이템들의 체크 상태를 저장 (Key: 재료 이름, Value: 체크 여부)
    val itemsToRegister = MutableLiveData<MutableMap<String, Boolean>>(mutableMapOf())

    // 체크박스 상태 업데이트
    fun updateRegistration(name: String, isChecked: Boolean) {
        val currentMap = itemsToRegister.value ?: mutableMapOf()
        currentMap[name] = isChecked
        itemsToRegister.value = currentMap
    }

    // B7 Activity가 처음 데이터를 로드할 때 호출
    fun initializeItems(items: List<IngredientResult>) {
        val currentMap = items.associate { it.name to false }.toMutableMap()
        itemsToRegister.value = currentMap
    }
}