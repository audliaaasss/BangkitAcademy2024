package com.dicoding.exam.optionalexam3

// TODO
fun manipulateString(str: String, int: Int): String {
    val regex = Regex("\\d+")
    return if (regex.containsMatchIn(str)) {
        regex.replace(str) {
            val number = it.value.toInt()
            (number * int).toString()
        }
    } else {
        str + int
    }
}
