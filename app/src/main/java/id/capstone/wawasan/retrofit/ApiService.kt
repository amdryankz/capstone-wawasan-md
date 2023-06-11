package id.capstone.wawasan.retrofit

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("api/get-supplier-name")
    fun getData(): Call<Responses>

    @FormUrlEncoded
    @POST("api/connect-to-database")
    fun connectDb(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("host") host: String,
        @Field("port") port: String,
        @Field("database") database: String
    ) : Call<DbResponse>
}