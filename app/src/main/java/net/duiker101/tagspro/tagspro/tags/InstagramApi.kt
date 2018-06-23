package net.duiker101.tagspro.tagspro.tags


import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//https://instagram.com/web/search/topsearch/?context=blended&query=%23travel
interface IInstagramApi {
    @GET("/web/search/topsearch/")
    fun search(
            @Query("context") context: String,
            @Query("query") query: String
    ): Observable<APITagSearchResponse>
}

object InstgramApi {
    private val service: IInstagramApi

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://instagram.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        service = retrofit.create(IInstagramApi::class.java)
    }

    fun search(text: String): Observable<APITagSearchResponse> {
        return service.search("blended", "#$text")
    }
}