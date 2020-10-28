package com.piso12.indoorex.helpers

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import java.lang.reflect.Type

class JsonParser<T> {

    fun parseAsList(body: String, listType: Type): List<T> {
        val json = JsonParser().parse(body).asJsonArray
        return Gson().fromJson(json, listType) as List<T>
    }

    fun parseAsObject(body: String, objectType: Type): T {
        val json = JsonParser().parse(body).asJsonObject
        return Gson().fromJson(json, objectType) as T
    }
}