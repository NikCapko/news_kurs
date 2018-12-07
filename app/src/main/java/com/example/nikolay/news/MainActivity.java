package com.example.nikolay.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nikolay.news.request.BrowserActivity;
import com.example.nikolay.news.request.NewsApi;
import com.example.nikolay.news.request.NewsModel;
import com.example.nikolay.news.request.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements Adapter.OnItemClickListener {

    public static final String TAG = "MainActivity.TAG";
    public static final String URL = "MainActivity.URL";

    @BindView(R.id.rl_news)
    RecyclerView rlNews;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;

    Adapter adapter;

    NewsModel newsModel;

    Retrofit retrofit = Utils.getRetrofitClient();
    NewsApi newsApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        rlNews.setVisibility(View.GONE);
        progress.setVisibility(ProgressBar.VISIBLE);
        newsApi = retrofit.create(NewsApi.class);
        getNews();
    }

    private void getNews() {
        Call<NewsModel> call = newsApi.getNews("0");
        call.enqueue(new Callback<NewsModel>() {
            @Override
            public void onResponse(Call<NewsModel> call, Response<NewsModel> response) {
                hideProgress();
                newsModel = response.body();
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
        Intent intent = new Intent(this, BrowserActivity.class);
        intent.putExtra(URL, "");
        startActivity(intent);
    }
}
