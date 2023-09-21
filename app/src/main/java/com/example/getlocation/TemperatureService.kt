package com.example.getlocation

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TemperatureService {

    @GET("/albums")
    suspend fun getTemperature() : Response<Temperature>

    @GET("/albums")
    suspend fun getSortedAlbums(@Query("userId") userId : Int) : Response<Temperature>

    @GET("/albums/{id}")
    suspend fun getAlbum(@Path(value = "id")albumId:Int) : Response<Temperature>

}