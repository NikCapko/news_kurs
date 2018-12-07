package com.example.nikolay.news.request;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {

    @GET("get-news")
    Call<NewsModel> getNews(@Query("count") String count);

    @GET("news-view")
    Call<Void> newsView(@Query("id1") String newsId);
}
