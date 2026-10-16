package com.fady.shielddnssocial;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String PREFS = "shielddns_social_prefs";
    private static final String CONSENT = "accessibility_consent_v1";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        TextView info = findViewById(R.id.info);
        info.setText(
                "ShieldDNS Social blocks only Instagram Search/Explore navigation.\n\n" +
                "It does not read or store messages, photos, captions, contacts, passwords, " +
                "payment information, or search history.\n\n" +
                "Android Accessibility access is required only so the app can detect when " +
                "you tap Instagram Search/Explore and immediately navigate back.");

        Button enable = findViewById(R.id.enable);
        enable.setOnClickListener(v -> showDisclosure());

        Button privacy = findViewById(R.id.privacy);
        privacy.setOnClickListener(v -> showPrivacyNotice());
    }

    private void showDisclosure() {
        new AlertDialog.Builder(this)
                .setTitle("Accessibility access disclosure")
                .setMessage(
                        "ShieldDNS Social uses Android Accessibility only inside Instagram. " +
                        "It detects taps on Search/Explore so those screens can be blocked.\n\n" +
                        "The service is restricted to the Instagram app package. It does not " +
                        "collect, save, transmit, or share your screen content, messages, photos, " +
                        "captions, passwords, financial information, contacts, or search history.\n\n" +
                        "Do you agree to enable this limited Accessibility service?")
                .setNegativeButton("Not now", null)
                .setPositiveButton("I agree", (d, which) -> {
                    getSharedPreferences(PREFS, MODE_PRIVATE)
                            .edit()
                            .putBoolean(CONSENT, true)
                            .apply();
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                })
                .show();
    }

    private void showPrivacyNotice() {
        new AlertDialog.Builder(this)
                .setTitle("Privacy notice")
                .setMessage(
                        "Data collection: none.\n\n" +
                        "Data storage: none.\n\n" +
                        "Data sharing: none.\n\n" +
                        "Accessibility scope: Instagram only, for blocking Search/Explore navigation.")
                .setPositiveButton("OK", null)
                .show();
    }
}
