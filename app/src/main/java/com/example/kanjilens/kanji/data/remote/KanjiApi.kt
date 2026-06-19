package com.example.kanjilens.kanji.data.remote

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class KanjiResponse(
    val kanji: String,
    val meanings: List<String> = emptyList(),
    val kun_readings: List<String> = emptyList(),
    val on_readings: List<String> = emptyList(),
    val name_readings: List<String> = emptyList(),
    val stroke_count: Int? = null,
    val grade: Int? = null,
    val jlpt: Int? = null,
    val heisig_en: String? = null,
)

interface KanjiApiService {
    @GET("v1/kanji/{character}")
    fun getKanji(@Path("character") character: String): Call<KanjiResponse>
}

object KanjiApi {
    val service: KanjiApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://kanjiapi.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KanjiApiService::class.java)
    }
}
