package id.capstone.wawasan.retrofit

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class Responses(

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("success")
	val success: Boolean
) : Parcelable

@Parcelize
data class DataItem(

	@field:SerializedName("nama")
	val nama: String
) : Parcelable

@Parcelize
data class DbResponse(

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
) : Parcelable

data class HostConfiguration(
	val username: String = "",
	val password: String = "",
	val host: String = "",
	val port: String = "",
	val database: String = ""
) {
	constructor() : this("", "", "", "", "")
}