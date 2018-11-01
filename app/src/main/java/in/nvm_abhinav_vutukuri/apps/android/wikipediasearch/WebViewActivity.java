package in.nvm_abhinav_vutukuri.apps.android.wikipediasearch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import static in.nvm_abhinav_vutukuri.apps.android.wikipediasearch.Utils.hasInternetAccess;
import static in.nvm_abhinav_vutukuri.apps.android.wikipediasearch.Utils.showAlertDialog;

public class WebViewActivity extends AppCompatActivity
{
    private ProgressBar webViewProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webViewProgressBar = findViewById(R.id.webViewProgressBar);
        WebView webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
                webViewProgressBar.setVisibility(View.VISIBLE);
                setTitle("Loading....");
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                webViewProgressBar.setVisibility(View.GONE);
                setTitle(view.getTitle());
            }
        });

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT))
        {
            String url = intent.getStringExtra(Intent.EXTRA_TEXT);
            webView.loadUrl(url);
        }
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();

        try
        {
            if (!hasInternetAccess())
            {
                showAlertDialog(this, R.string.error_no_internet_acsess, R.string.msg_check_network);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
