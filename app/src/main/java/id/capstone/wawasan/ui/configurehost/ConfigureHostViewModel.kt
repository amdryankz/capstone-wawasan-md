package id.capstone.wawasan.ui.configurehost

import android.util.Log
import androidx.lifecycle.LiveData
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


    private val _isConfigured: MutableLiveData<Boolean> = MutableLiveData(false)
    val isConfigured: LiveData<Boolean> = _isConfigured

    private val _isConnected: MutableLiveData<Boolean> = MutableLiveData(false)
    val isConnected: LiveData<Boolean> = _isConnected

    fun validateInput(
        username: String,
        password: String,
        host: String,
        port: String,
        db: String
    ): String? {
        if (username.isEmpty()) {
            return "Username is required"
        }

        if (password.isEmpty()) {
            return "Password is required"
        }

        if (host.isEmpty()) {
            return "Host is required"
        }

        if (port.isEmpty()) {
            return "Port is required"
        }

        if (db.isEmpty()) {
            return "Database is required"
        }

        return null
    }

    fun connectToDatabase(
        username: String,
        password: String,
        host: String,
        port: String,
        database: String
    ) {
        val api = ApiConfig.getApiService().connectDb(username, password, host, port, database)
        api.enqueue(object : retrofit2.Callback<DbResponse> {
            override fun onResponse(call: Call<DbResponse>, response: Response<DbResponse>) {
                if (response.isSuccessful) {
                    _isConnected.value = true
                    saveConfiguration(username, password, host, port, database)
                } else {
                    _isConnected.value = false
                    // Handle error response
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DbResponse>, t: Throwable) {
                _isConnected.value = false
                // Handle network or API call failure
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun saveConfiguration(
        username: String,
        password: String,
        host: String,
        port: String,
        db: String
    ) {
        val database = FirebaseDatabase.getInstance()

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            val hostConfigRef = database.getReference("host_config/$uid")
            val hostConfig = HostConfiguration(username, password, host, port, db)
            hostConfigRef.setValue(hostConfig)
        }

        _isConfigured.value = true
    }

    companion object {
        private const val TAG = "ConfigureHostViewModel"
    }
}
