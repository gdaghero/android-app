package com.piso12.indoorex.services.utils

class ServiceResult<T> {

    var result: T? = null
    var error: Throwable? = null
    var isSuccessful = false

    constructor(result: T) {
        this.result = result
        this.isSuccessful = true
    }

    constructor(error: Throwable) {
        this.error = error
        this.isSuccessful = false
    }
}
