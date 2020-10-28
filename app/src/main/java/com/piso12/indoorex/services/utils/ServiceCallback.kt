package com.piso12.indoorex.services.utils

interface ServiceCallback<T> {

    fun onComplete(result: ServiceResult<T>)
}