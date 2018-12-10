package com.example.nikolay.news.request;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsApi {

    @GET("get-news/{count}")
    Call<NewsModel> getNews(@Path("count") String count);

    @GET("add-view/{id1}")
    Call<Void> newsView(@Path("id1") String newsId);
}
