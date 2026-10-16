package com.fady.shielddnssocial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        Button enable = findViewById(R.id.enable);
        enable.setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));

        TextView info = findViewById(R.id.info);
        info.setText(
                "Enable only: ShieldDNS Instagram Search Blocker\n\n" +
                "This service reacts only to Instagram Search/Explore navigation labels. " +
                "It does not store messages, photos, captions, or searches.");
    }
}
