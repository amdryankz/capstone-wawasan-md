package id.capstone.wawasan.ui.configurehost

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import id.capstone.wawasan.retrofit.ApiConfig
import id.capstone.wawasan.retrofit.DbResponse
import id.capstone.wawasan.retrofit.HostConfiguration
import retrofit2.Call
import retrofit2.Response

class ConfigureHostViewModel : ViewModel() {

    val dbResponseLiveData: MutableLiveData<DbResponse?> = MutableLiveData()
    var isConfigured: Boolean = false
    var isConnected: Boolean = false

    fun saveConfiguration(username: String, password: String, host: String, port: String, db: String) {
        // Simpan konfigurasi ke Firebase atau penyimpanan lokal lainnya
        val database = FirebaseDatabase.getInstance()

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            val hostConfigRef = database.getReference("host_config/$uid")
            val hostConfig = HostConfiguration(username, password, host, port, db)
            hostConfigRef.setValue(hostConfig) // hostConfig adalah objek HostConfiguration yang ingin Anda simpan
        }

        // Setelah konfigurasi disimpan, atur isConfigured menjadi true
        isConfigured = true
    }

    fun connectToDatabase(
        username: String,
        password: String,
        host: String,
        port: String,
        database: String
    ): Boolean {
        val api = ApiConfig.getApiService().connectDb(username, password, host, port, database)
        api.enqueue(object : retrofit2.Callback<DbResponse> {
            override fun onResponse(call: Call<DbResponse>, response: Response<DbResponse>) {
                if (response.isSuccessful) {
                    val dbResponse = response.body()
                    dbResponseLiveData.value = dbResponse
                    isConnected = true
                } else {
                    // Handle error response
                    Log.e(TAG, "onFailure: ${response.message()}")
                    isConnected = false
                }
            }

            override fun onFailure(call: Call<DbResponse>, t: Throwable) {
                // Handle network or API call failure
                Log.e(TAG, "onFailure: ${t.message}")
                isConnected = false
            }
        })

        return true
    }

    companion object {
        private const val TAG = "ConfigureViewModel"
    }

}