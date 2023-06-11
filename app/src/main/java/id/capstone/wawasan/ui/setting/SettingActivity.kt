package id.capstone.wawasan.ui.setting

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import id.capstone.wawasan.R
import id.capstone.wawasan.databinding.ActivitySettingBinding
import id.capstone.wawasan.ui.profile.ProfileFragment
import id.capstone.wawasan.ui.changepassword.ChangePasswordFragment
import id.capstone.wawasan.ui.configurehost.ConfigureHostActivity
import id.capstone.wawasan.ui.home.HomeActivity
import id.capstone.wawasan.ui.login.LoginActivity

class SettingActivity : AppCompatActivity(), ProfileFragment.ProfileUpdateListener {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val user = firebaseAuth.currentUser

        if (user != null) {
            binding.tvName.text = user.displayName
            binding.tvEmail.text = user.email

            if (user.photoUrl != null) {
                Picasso.get().load(user.photoUrl).into(binding.ivProfile)
            }
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        binding.btnProfile.setOnClickListener {
            val profileFragment = ProfileFragment()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, profileFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnChangepass.setOnClickListener {
            val changePasswordFragment = ChangePasswordFragment()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, changePasswordFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.backBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.btnConfigure.setOnClickListener {
            val intent = Intent(this, ConfigureHostActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onProfileUpdated(name: String?, photoUrl: Uri?) {
        if (!name.isNullOrEmpty()) {
            binding.tvName.text = name
        }

        if (photoUrl != null) {
            Picasso.get().load(photoUrl).into(binding.ivProfile)
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { dialog, _ ->
                // Logout
                firebaseAuth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finishAffinity()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}