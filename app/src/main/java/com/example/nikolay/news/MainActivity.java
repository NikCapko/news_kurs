package com.example.nikolay.news;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nikolay.news.request.BrowserActivity;
import com.example.nikolay.news.request.News;
import com.example.nikolay.news.request.NewsApi;
import com.example.nikolay.news.request.NewsModel;
import com.example.nikolay.news.request.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements Adapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "MainActivity.TAG";
    public static final String URL = "MainActivity.URL";

    @BindView(R.id.rl_news)
    RecyclerView rlNews;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;

    Adapter adapter;

    List<News> newsList;

    private int pageIndex;

    Retrofit retrofit;
    NewsApi newsApi;
    private boolean isLoading;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        swipe.setOnRefreshListener(this);
        rlNews.setVisibility(View.GONE);
        progress.setVisibility(ProgressBar.VISIBLE);
        retrofit = Utils.getRetrofitClient();
        newsApi = retrofit.create(NewsApi.class);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        newsList = new ArrayList<>();
        rlNews.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItems = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading) {
                    if ((visibleItemCount + firstVisibleItems) >= totalItemCount) {
                        isLoading = true;
                        loadMoreNews();
                    }
                }
            }
        });
        pageIndex = 0;
        getNews();
    }

    @Override
    public void onRefresh() {
        swipe.setRefreshing(true);
        new Handler().postDelayed(() -> {
            pageIndex = 0;
            rlNews.setVisibility(View.GONE);
            progress.setVisibility(ProgressBar.VISIBLE);
            newsList = new ArrayList<>();
            layoutManager = new LinearLayoutManager(MainActivity.this);
            getNews();
        }, 4000);
        swipe.setRefreshing(false);
    }

    private void getNews() {
        Call<NewsModel> call = newsApi.getNews(String.valueOf(pageIndex));
        call.enqueue(new Callback<NewsModel>() {
            @Override
            public void onResponse(Call<NewsModel> call, Response<NewsModel> response) {
                hideProgress();
                NewsModel newsModel = response.body();
                newsList.addAll(newsModel.getData());
                rlNews.setLayoutManager(layoutManager);
                rlNews.setItemAnimator(new DefaultItemAnimator());
                adapter = new Adapter(newsModel.getData(), MainActivity.this);
                adapter.setOnItemClickListener(MainActivity.this);
                rlNews.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<NewsModel> call, Throwable t) {
                hideProgress();
                Log.d(TAG, t.getMessage());
                Toast toast = Toast.makeText(MainActivity.this, R.string.error_net, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    void hideProgress() {
        rlNews.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        progress.setVisibility(ProgressBar.GONE);
    }

    @Override
    public void onItemClick(int id) {
        newsApi.newsView(String.valueOf(id));
        Intent intent = new Intent(this, BrowserActivity.class);
        intent.putExtra(URL, newsList.get(id).getLink());
        startActivity(intent);
    }

    public void loadMoreNews() {
        isLoading = false;
        Call<NewsModel> call = newsApi.getNews(String.valueOf(pageIndex++));
        call.enqueue(new Callback<NewsModel>() {
            @Override
            public void onResponse(Call<NewsModel> call, Response<NewsModel> response) {
                NewsModel newsModel = response.body();
                newsList.addAll(newsModel.getData());
                adapter.notifyDataSetChanged();
                rlNews.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<NewsModel> call, Throwable t) {
                hideProgress();
                Log.d(TAG, t.getMessage());
                Toast toast = Toast.makeText(MainActivity.this, R.string.error_net, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }
}
