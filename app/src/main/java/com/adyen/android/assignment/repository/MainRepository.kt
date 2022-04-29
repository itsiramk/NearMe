package com.adyen.android.assignment.repository

import com.adyen.android.assignment.api.PlacesService
import com.adyen.android.assignment.api.model.ResponseWrapper
import retrofit2.Response
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiService: PlacesService) {
    suspend fun getVenueRecommendations(query: Map<String, String>): Response<ResponseWrapper> {
        return apiService.getVenueRecommendations(query)
    }
}
