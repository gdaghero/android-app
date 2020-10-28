package com.piso12.indoorex.dtos

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SearchResultDto : Serializable {

    @SerializedName("type")
    lateinit var objType: String
    @SerializedName("object")
    lateinit var obj: Any
}