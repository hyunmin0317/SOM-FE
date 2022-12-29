package com.smu.som

import java.io.Serializable

class Question(
    var id : Int? = null,
    var target : String? = null,
    var question : String? = null,
    var isAdult: String? = null,
    var category: String? = null
): Serializable