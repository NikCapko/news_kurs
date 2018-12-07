package com.example.nikolay.news.request;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.example.nikolay.news.MainActivity;
import com.example.nikolay.news.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrowserActivity extends AppCompatActivity {

    @BindView(R.id.wv_browser)
    WebView wvBrowser;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        ButterKnife.bind(this);

        wvBrowser.getSettings().setJavaScriptEnabled(true);

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            String url = arguments.get(MainActivity.URL).toString();
            wvBrowser.loadUrl(url);
        }
    }
}
