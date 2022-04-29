package com.adyen.android.assignment.api.model

data class Result(
    val categories: List<Category>,
    val fsq_id: String="",
    val distance: Int,
    val geocodes: GeoCode,
    val location: Location,
    val name: String="",
    val timezone: String="",
)