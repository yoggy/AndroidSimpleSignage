package net.sabamiso.android.androidsimplesignage;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        long diff;

        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    return true;
                }
                else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    return true;
                }
                break;
            case KeyEvent.ACTION_UP:
                if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    return true;
                }
                else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    return true;
                }
                break;
            default:
        }

        return super.dispatchKeyEvent(event);
    }
}