package id.capstone.wawasan.ui.changepassword

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordViewModel : ViewModel() {

    fun validateInput(currentPassword: String, newPassword: String, confirmPassword: String): String? {
        if (newPassword.isEmpty() || confirmPassword.isEmpty() || currentPassword.isEmpty()) {
            return "Mohon isi semua kolom"
        }

        if (newPassword != confirmPassword) {
            return "Konfirmasi password tidak cocok"
        }

        return null
    }

    fun changePassword(currentPassword: String, newPassword: String, callback: (Boolean, String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser

        val credential = EmailAuthProvider.getCredential(user?.email ?: "", currentPassword)
        user?.reauthenticate(credential)?.addOnSuccessListener {
            user.updatePassword(newPassword).addOnSuccessListener {
                callback(true, "Password berhasil diubah")
            }.addOnFailureListener { exception ->
                callback(false, exception.message ?: "Terjadi kesalahan saat mengubah password")
            }
        }?.addOnFailureListener { exception ->
            callback(false, exception.message ?: "Terjadi kesalahan saat melakukan authentikasi")
        }
    }


}