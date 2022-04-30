package com.adyen.android.assignment.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        onError(error)
    }
    val placesLiveData = MutableLiveData<List<Result>>()
    val usersLoadError = MutableLiveData<String?>()
    val loading = MutableLiveData<Boolean>()
    private val error = "Unable to reach server !!";

    fun fetchVenueRecommendations(query: Map<String, String>) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.getVenueRecommendations(query)
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        placesLiveData.postValue(response.body()?.results)
                        usersLoadError.postValue("")
                        loading.postValue(false)
                    } else {
                        onError(error)
                    }
                } catch (e: Exception) {
                    onError(error)
                }
            }
        }
    }

    private fun onError(message: String) {
        usersLoadError.postValue(message)
        loading.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}