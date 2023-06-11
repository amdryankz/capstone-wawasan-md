package id.capstone.wawasan.ui.home

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import id.capstone.wawasan.adapter.DataAdapter
import id.capstone.wawasan.databinding.ActivityHomeBinding
import id.capstone.wawasan.retrofit.DataItem
import id.capstone.wawasan.retrofit.HostConfiguration
import id.capstone.wawasan.ui.profile.ProfileFragment
import id.capstone.wawasan.ui.setting.SettingActivity
import id.capstone.wawasan.ui.configurehost.ConfigureHostViewModel

class HomeActivity : AppCompatActivity(), ProfileFragment.ProfileUpdateListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val homeViewModel by viewModels<HomeViewModel>()
    private val configureHostViewModel by viewModels<ConfigureHostViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val user = firebaseAuth.currentUser
        val uid = user?.uid

        if (user != null) {
            binding.textName.text = user.displayName

            if (user.photoUrl != null) {
                Picasso.get().load(user.photoUrl).into(binding.ivProfile)
            }
        } else {
            binding.textName.text = "Anonymous"
        }

        val database = FirebaseDatabase.getInstance()
        val hostConfigRef = database.getReference("host_config/$uid")

        hostConfigRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val hostConfig = dataSnapshot.getValue(HostConfiguration::class.java)

                // Gunakan konfigurasi host yang diperoleh
                if (hostConfig != null) {
                    val username = hostConfig.username
                    val password = hostConfig.password
                    val host = hostConfig.host
                    val port = hostConfig.port
                    val database = hostConfig.database

                    // Gunakan nilai-nilai konfigurasi host untuk melakukan operasi yang diperlukan, misalnya koneksi ke database.
                    configureHostViewModel.connectToDatabase(
                        username,
                        password,
                        host,
                        port,
                        database
                    )
                    getListData(null)
                    // ...
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Penanganan kesalahan saat membaca konfigurasi host dari Firebase
            }
        })

        binding.icSetting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                getListData(newText)
                return true
            }

        })

        val layoutManager = LinearLayoutManager(this)
        binding.rv.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rv.addItemDecoration(itemDecoration)

    }

    private fun getListData(filter: String?) {
        homeViewModel.data.observe(this) { dataResponse ->
            if (dataResponse != null) {
                val dataItems = dataResponse.data
                val filteredDataItems =
                    filterDataItems(dataItems, filter) // Metode untuk memfilter data
                val adapter = DataAdapter(filteredDataItems)
                binding.rv.adapter = adapter
            }
        }
    }

    private fun filterDataItems(dataItems: List<DataItem>, filter: String?): List<DataItem> {
        // Logika untuk memfilter daftar data berdasarkan teks yang dimasukkan
        if (filter.isNullOrEmpty()) {
            return dataItems // Kembalikan daftar data asli jika filter kosong atau null
        }

        val filteredList = mutableListOf<DataItem>()
        val query = filter.toLowerCase()

        for (item in dataItems) {
            // Tambahkan item ke daftar hasil filter jika memenuhi kriteria pencarian
            if (item.nama.toLowerCase().contains(query)) {
                filteredList.add(item)
            }
        }

        return filteredList
    }


    override fun onProfileUpdated(name: String?, photoUrl: Uri?) {
        refreshProfile()
    }

    private fun refreshProfile() {
        val user = firebaseAuth.currentUser

        if (user != null) {
            binding.textName.text = user.displayName

            if (user.photoUrl != null) {
                Picasso.get().load(user.photoUrl).into(binding.ivProfile)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshProfile()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("finishAffinity()"))
    override fun onBackPressed() {
        finishAffinity() // Keluar dari aplikasi
    }
}