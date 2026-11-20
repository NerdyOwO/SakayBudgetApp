package com.sakaybudget.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewFeature;

public class MainActivity extends Activity {

    private static final String HOST = "appassets.androidplatform.net";
    private WebView web;
    private FrameLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        web = new WebView(this);
        web.setBackgroundColor(Color.TRANSPARENT);

        // Keep the page out from under the status bar, navigation bar and keyboard on every Android version.
        root = new FrameLayout(this);
        root.setBackgroundColor(getColor(R.color.window_bg));
        root.addView(web, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
                return insets;
            }
        });
        setContentView(root);

        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);      // keeps your budget and history on the device
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
            // The app has its own dark themes, so stop WebView from darkening twice.
            WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, false);
        }
        web.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // Serves app/src/main/assets over https://appassets.androidplatform.net/assets/
        final WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        web.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return loader.shouldInterceptRequest(request.getUrl());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (HOST.equals(uri.getHost())) return false;
                startActivity(new Intent(Intent.ACTION_VIEW, uri)); // open outside links in the browser
                return true;
            }
        });

        web.addJavascriptInterface(new Bridge(), "Android");

        if (savedInstanceState == null) {
            web.loadUrl("https://" + HOST + "/assets/index.html");
        } else {
            web.restoreState(savedInstanceState);
        }
    }

    /** Lets the page colour the status bar and navigation bar to match its theme. */
    private class Bridge {
        @JavascriptInterface
        public void setBars(final String hex, final boolean darkIcons) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int color = Color.parseColor(hex);
                        Window w = getWindow();
                        w.setStatusBarColor(color);
                        w.setNavigationBarColor(color);
                        root.setBackgroundColor(color);
                        View d = w.getDecorView();
                        int flags = d.getSystemUiVisibility();
                        int light = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                        d.setSystemUiVisibility(darkIcons ? (flags | light) : (flags & ~light));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) {
            web.goBack();   // the page keeps a history entry per tab
        } else {
            super.onBackPressed();
        }
    }
}
