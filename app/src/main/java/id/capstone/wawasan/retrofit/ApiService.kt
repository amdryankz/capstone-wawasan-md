package id.capstone.wawasan.retrofit

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("api/get-supplier-name")
    fun getSupplier() : Call<Responses>

    @GET("api/get-supplier-name/{supplierName}")
    fun getProduct(@Path("supplierName") supplierName: String) : Call<ProductResponse>

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