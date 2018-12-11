package com.example.nikolay.news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nikolay.news.request.News;
import com.example.nikolay.news.request.NewsApi;
import com.example.nikolay.news.request.NewsModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements Adapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "MainActivity.TAG";
    public static final String LINK = "MainActivity.LINK";
    private static final String SAVED_TEXT = "MainActivity.SAVED_TEXT";

    String BASE_URL = "http://192.168.2.4/";

    @BindView(R.id.rl_news)
    RecyclerView rlNews;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;

    Adapter adapter;

    List<News> newsList;

    private int pageIndex;

    private SharedPreferences sPref;

    Retrofit retrofit;
    NewsApi newsApi;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        swipe.setOnRefreshListener(this);
        rlNews.setVisibility(View.GONE);
        progress.setVisibility(ProgressBar.VISIBLE);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor).build();
        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        sPref = getPreferences(MODE_PRIVATE);
        BASE_URL = sPref.getString(SAVED_TEXT, BASE_URL);

        newsApi = retrofit.create(NewsApi.class);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        newsList = new ArrayList<>();
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
            progress.setVisibility(ProgressBar.VISIBLE);
            progress.setVisibility(View.VISIBLE);
            layoutManager = new LinearLayoutManager(MainActivity.this);
            getNews();
        }, 0);
        swipe.setRefreshing(false);
    }

    private void getNews() {
        Call<NewsModel> call = newsApi.getNews(String.valueOf(pageIndex));
        call.enqueue(new Callback<NewsModel>() {
            @Override
            public void onResponse(Call<NewsModel> call, Response<NewsModel> response) {
                hideProgress();
                NewsModel newsModel = response.body();
                if (newsModel != null) {
                    newsList.addAll(newsModel.getData());
                    rlNews.setLayoutManager(layoutManager);
                    rlNews.setItemAnimator(new DefaultItemAnimator());
                    adapter = new Adapter(newsList, MainActivity.this);
                    adapter.setOnItemClickListener(MainActivity.this);
                    rlNews.setAdapter(adapter);
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Settings");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(BASE_URL);
        builder.setView(input);
        builder
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    BASE_URL = input.getText().toString();
                    sPref = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString(SAVED_TEXT, BASE_URL);
                    ed.apply();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.show();
        return false;
    }

    void hideProgress() {
        rlNews.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        progress.setVisibility(ProgressBar.GONE);
    }

    @Override
    public void onItemClick(int id) {
        Call<Void> call = newsApi.newsView(String.valueOf(newsList.get(id).getId()));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsList.get(id).getLink()));
                //intent.putExtra(LINK, newsList.get(id).getLink());
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public void loadMoreNews() {
        pageIndex += 1;
        Call<NewsModel> call = newsApi.getNews(String.valueOf(pageIndex));
        call.enqueue(new Callback<NewsModel>() {
            @Override
            public void onResponse(Call<NewsModel> call, Response<NewsModel> response) {
                NewsModel newsModel = response.body();
                if (newsModel != null) {
                    Parcelable recyclerViewState;
                    recyclerViewState = rlNews.getLayoutManager().onSaveInstanceState();
                    newsList.addAll(newsModel.getData());
                    rlNews.getAdapter().notifyDataSetChanged();
                    rlNews.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                }
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
