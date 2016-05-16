package net.sabamiso.android.androidsimplesignage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import net.sabamiso.android.util.Config;

public class SignageActivity extends Activity {
    RelativeLayout layout;
    WebView webView;
    OverlayView overlayView;

    Config config;

    Handler handler = new Handler();

    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);

        Config.init(this);
        config = Config.getInstance();

        layout = new RelativeLayout(this);
        setContentView(layout);

        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.setInitialScale(100);
        webView.setWebViewClient(new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        layout.addView(webView, new RelativeLayout.LayoutParams(MP, MP));

        overlayView = new OverlayView(this);
        layout.addView(overlayView, new RelativeLayout.LayoutParams(MP, MP));

    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();

        webView.onResume();
        webView.resumeTimers();

        String url = config.getString("url", "http://www.example.com/");
        webView.loadUrl(url);

        boolean enable_reload = config.getBoolean("enable_reload", false);
        if (enable_reload) {
            scheduleReload();
        }

        scheduleCheckVolumekey();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(reloadHandler);
        handler.removeCallbacks(checkVolumekeyHandler);

        webView.pauseTimers();
        webView.onPause();

        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        hideSystemUI();
    }

    private void hideSystemUI() {
        // see also...https://developer.android.com/training/system-ui/immersive.html
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    Runnable reloadHandler = new Runnable() {
        @Override
        public void run() {
            doReload();
            scheduleReload();
        }
    };

    protected void doReload() {
        webView.clearCache(true);
        webView.reload();
    }

    protected void scheduleReload() {
        int reload_interval = config.getInt("reload_interval", 180) * 1000;
        handler.postDelayed(reloadHandler, reload_interval);
    }

    /////////////////////////////////////////////////////////////////////////////////////

    boolean press_volume_down;
    boolean press_volume_up;
    boolean press_press_volume_up_down;
    long press_volume_up_down_time = 0;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        long diff;

        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    press_volume_up = true;
                    return true;
                }
                else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    press_volume_down = true;
                    return true;
                }
                break;
            case KeyEvent.ACTION_UP:
                diff = System.currentTimeMillis() - press_volume_up_down_time;
                if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    press_volume_up = false;
                    press_volume_down = false;
                    return true;
                }
                else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    press_volume_up = false;
                    press_volume_down = false;

                    return true;
                }
                break;
            default:
        }

        return super.dispatchKeyEvent(event);
    }

    void checkVolumekeyStatus() {
        if (press_volume_up == true && press_volume_down == true) {
            if (press_press_volume_up_down == false) {
                press_press_volume_up_down = true;
                press_volume_up_down_time = System.currentTimeMillis();
            } else {
                long diff = System.currentTimeMillis() - press_volume_up_down_time;
                if (diff > 3000) {
                    press_volume_up = false;
                    press_volume_down = false;
                    press_press_volume_up_down = false;

                    startSettingsActivity();
                }
            }
        }
        else{
            press_press_volume_up_down = false;
        }
    }

    void scheduleCheckVolumekey() {
        handler.postDelayed(checkVolumekeyHandler, 200);
    }

    Runnable checkVolumekeyHandler = new Runnable() {
        @Override
        public void run() {
            checkVolumekeyStatus();
            scheduleCheckVolumekey();
        }
    };
}
