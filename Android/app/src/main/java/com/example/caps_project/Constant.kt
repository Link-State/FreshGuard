package com.example.caps_project

object Constant {
    var Code2Name: HashMap<Int, String> = hashMapOf(
        1 to "사과",
        2 to "바나나",
        3 to "오렌지",
        4 to "피망",
        5 to "당근",
        6 to "오이",
        7 to "망고",
        8 to "감자",
        9 to "딸기",
        10 to "토마토"
    )

    var Name2Code: HashMap<String, Int> = hashMapOf(
        "사과" to 1,
        "바나나" to 2,
        "오렌지" to 3,
        "피망" to 4,
        "당근" to 5,
        "오이" to 6,
        "망고" to 7,
        "감자" to 8,
        "딸기" to 9,
        "토마토" to 10,
    )

    var Level2Name: HashMap<Int, String> = hashMapOf(
        1 to "나쁨",
        2 to "보통",
        3 to "좋음"
    )

    var Level2Emoji: HashMap<Int, String> = hashMapOf(
        1 to "🔴",
        2 to "🟡",
        3 to "🟢"
    )

    var Name2Emoji: HashMap<String, String> = hashMapOf(
        "나쁨" to "🔴",
        "보통" to "🟡",
        "좋음" to "🟢"
    )

    var Name2Level: HashMap<String, Int> = hashMapOf(
        "나쁨" to 1,
        "보통" to 2,
        "좋음" to 3
    )
}