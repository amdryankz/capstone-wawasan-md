package id.capstone.wawasan.ui.supplierproduct

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.capstone.wawasan.retrofit.ApiConfig
import id.capstone.wawasan.retrofit.ProductResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupplierProductViewModel : ViewModel() {

    private val _data = MutableLiveData<ProductResponse>()
    val data: LiveData<ProductResponse> = _data

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    var supplierName: String = ""
        set(value) {
            field = value
            getProduct()
        }

    private fun getProduct() {
        _isLoading.value = true
        val api = ApiConfig.getApiService().getProduct(supplierName)
        api.enqueue(object : Callback<ProductResponse> {
            @SuppressLint("LongLogTag")
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                _isLoading.value = false
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

            @SuppressLint("LongLogTag")
            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "SupplierProductViewModel"
    }
}