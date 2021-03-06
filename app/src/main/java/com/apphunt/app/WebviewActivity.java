package com.apphunt.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.apphunt.app.constants.Constants;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nmp on 15-11-30.
 */
public class WebviewActivity extends AppCompatActivity {
    public static final String TAG = WebviewActivity.class.getSimpleName();

    @InjectView(R.id.webView)
    WebView webView;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.pB1)
    ProgressBar Pbar;

    private String url, title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        webView.getSettings().setJavaScriptEnabled(true);

        url = getIntent().getStringExtra(Constants.EXTRA_URL);
        toolbar.setSubtitle(url);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Activity activity = this;
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                if(progress < 100 && Pbar.getVisibility() == ProgressBar.GONE){
                    Pbar.setVisibility(ProgressBar.VISIBLE);
                }
                Pbar.setProgress(progress);
                if(progress == 100) {
                    Pbar.setVisibility(ProgressBar.GONE);
                }


            }

        });
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if(url.contains("bnc.lt")) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(i);
                    } else {
                        webView.loadUrl(url);
                    }

                    return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                title = view.getTitle();
                toolbar.setTitle(title);
                getSupportActionBar().setTitle(view.getTitle());
            }
        });

        webView.loadUrl(url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        } else if(item.getItemId() == R.id.action_share) {
            shareTextUrl();
        } else if(item.getItemId() == R.id.action_refresh) {
            webView.reload();
        }
        return super.onOptionsItemSelected(item);

    }

    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, title);
        share.putExtra(Intent.EXTRA_TEXT, url);

        startActivity(Intent.createChooser(share, "Share link with:"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_webview, menu);

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
}
