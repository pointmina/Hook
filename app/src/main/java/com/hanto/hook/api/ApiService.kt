package com.hanto.hook.api

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

// 서버의 어떤 api랑 통신해서 어떤 응답을 어떤 식으로 가져올 건지 대해서 청사진을 그림
interface ApiService {
    //============== Hook ==============//
    @GET("/api/hook/9") // TEST 용도 -- dummy data 9개 버전
    suspend fun findMyHooks(): Response<ApiResponse>
    // 200: ok, 200: none.
    @POST("/api/hook/12")
    suspend fun createHook(@Body requestData: HookRequest): ApiResponse
    // 201: created, 400: Bad Request
    @PUT("/api/hook/12")
    suspend fun updateHook() : ApiResponse
    // 200: ok, 400: Bad Request, 403: Forbidden, 404: Not Found by ID
    @DELETE("/api/hook/7")
    suspend fun deleteHook() : ApiResponse
    // 200: ok, 404: Not Found by ID

    //============== TAG ==============//
    @GET("/api/tag/random")
    suspend fun getRandomTag() : ApiResponse

    @GET("/api/tag/all")
    suspend fun findMyTags() : ApiResponse

    /*@GET("/api/tag?name=학교")
    suspend fun getTagByName(@Header) : ApiResponse*/

    @POST("/api/tag/")
    suspend fun createTag() : ApiResponse

    @PUT("/api/tag/1")
    suspend fun updateTagName() : ApiResponse

    @DELETE("/api/tag/4")
    suspend fun deleteTag() : ApiResponse

    // ============== USER ==============//
    fun provideApiService(retrofit: Retrofit): ApiService{
        return retrofit.create(ApiService::class.java)
    }

}
