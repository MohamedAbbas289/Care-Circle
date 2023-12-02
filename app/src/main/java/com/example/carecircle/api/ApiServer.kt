package com.example.carecircle.api

import com.example.carecircle.model.MyResponse
import com.example.carecircle.model.Sender
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiServer {

    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAAkxh8GvU:APA91bEYWSEcogqqhQFaVXIiP3NK1Qe2N4MCh384JGqGC4h5kwS50CApXrc21glgOHDHwNhJnck5G1EIao2UrgSYJ03Ni9yNVW6_bkhV-h0hsvCmBIu_nCab6Wukhl1aw_kkmIv1cPOi"
    )
    @POST("fcm/send")
    fun sendNotification(@Body sender: Sender): Call<MyResponse>

}