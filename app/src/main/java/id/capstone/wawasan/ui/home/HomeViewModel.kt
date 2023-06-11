package id.capstone.wawasan.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.capstone.wawasan.retrofit.ApiConfig
import id.capstone.wawasan.retrofit.Responses
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    private val _data = MutableLiveData<Responses>()
    val data: LiveData<Responses> = _data

    companion object {
        private const val TAG = "HomeViewModel"
    }

    init {
        getData()
    }

    private fun getData() {
        val client = ApiConfig.getApiService().getData()
        client.enqueue(object : Callback<Responses> {
            override fun onResponse(
                call: Call<Responses>,
                response: Response<Responses>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _data.value = response.body()
                        Log.e(TAG, "success")
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Responses>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}