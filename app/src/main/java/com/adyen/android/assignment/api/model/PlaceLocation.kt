package com.adyen.android.assignment.api.model

data class PlaceLocation(
    val address: String="",
    val country: String="",
    val locality: String="",
    val cross_street: String="",
    val formatted_address: String="",
    val neighbourhood: List<String>,
    val postcode: String="",
    val region: String="",
)
