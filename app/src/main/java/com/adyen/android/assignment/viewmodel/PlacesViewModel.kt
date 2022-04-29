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
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val placesLiveData = MutableLiveData<List<Result>>()
    val usersLoadError = MutableLiveData<String?>()
    val loading = MutableLiveData<Boolean>()

    fun fetchVenueRecommendations(query: Map<String, String>) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.getVenueRecommendations(query)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    placesLiveData.value = response.body()?.results
                    usersLoadError.value = null
                    loading.value = false
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }
        usersLoadError.value = ""
        loading.value = false
    }

    private fun onError(message: String) {
        usersLoadError.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }


/*
    fun fetchVenueRecommendations(query:Map<String,String>) {

        CoroutineScope(Dispatchers.IO).launch {
            val response = mainRepository.getVenueRecommendations(query)
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        //Do something with response e.g show to the UI.
                        placesLiveData.postValue(response.body())
                    } else {
                        //toast("Error: ${response.code()}")
                    }
                } catch (e: HttpException) {
                  //  toast("Exception ${e.message}")
                } catch (e: Throwable) {
                 //   toast("Ooops: Something else went wrong")
                }
            }
        }

     }
*/


/*
    fun getUserInfoByUsername(query:Map<String,String>) {

        viewModelScope.launch {
            try {
                if (true) {
                    val response = mainRepository.getVenueRecommendations(query)
                    placesLiveData.postValue(response.body())
                } else {
                  //  picsData.postValue(Resource.Error(getApplication<MyApplication>().getString(R.string.no_internet_connection)))
                }
            } catch (t: Throwable) {
                when (t) {
                  */
/*  is IOException -> picsData.postValue(
                        Resource.Error(
                            getApplication<MyApplication>().getString(
                                R.string.network_failure
                            )
                        )
                    )
                    else -> picsData.postValue(
                        Resource.Error(
                            getApplication<MyApplication>().getString(
                                R.string.conversion_error
                            )
                        )
                    )*//*

                }
            }
        }
    }
*/

}