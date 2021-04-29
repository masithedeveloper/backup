package com.money.randing.error

sealed class AppException : Exception() {

    object PermissionNotGrantedException : AppException()
    object ShouldRequestPermissionRationaleException : AppException()
}