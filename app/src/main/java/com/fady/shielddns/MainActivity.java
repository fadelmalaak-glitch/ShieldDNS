package com.fady.shielddns;

import android.app.*;
import android.app.admin.DevicePolicyManager;
import android.content.*;
import android.net.VpnService;
import android.os.Bundle;
import android.widget.*;

public class MainActivity extends Activity {
    private TextView status;
    private TextView adminStatus;
    private DevicePolicyManager dpm;
    private ComponentName admin;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.status);
        adminStatus = findViewById(R.id.adminStatus);
        dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        admin = new ComponentName(this, ShieldDeviceAdminReceiver.class);

        findViewById(R.id.start).setOnClickListener(v -> {
            Intent p = VpnService.prepare(this);
            if (p != null) startActivityForResult(p, 7);
            else startFilter();
        });

        findViewById(R.id.stop).setOnClickListener(v -> {
            startService(new Intent(this, FilterVpnService.class).setAction("STOP"));
            status.setText("Protection is OFF");
        });

        findViewById(R.id.enableAdmin).setOnClickListener(v -> enableAdminProtection());

        refreshAdminStatus();
    }

    private void enableAdminProtection() {
        if (!dpm.isAdminActive(admin)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "ShieldDNS uses device administrator access to make accidental or impulsive removal harder. You remain in control and can disable it from Android settings.");
            startActivityForResult(intent, 8);
            return;
        }

        applyStrongUninstallBlockIfOwner();
        refreshAdminStatus();
    }

    private void applyStrongUninstallBlockIfOwner() {
        try {
            if (dpm.isDeviceOwnerApp(getPackageName())) {
                dpm.setUninstallBlocked(admin, getPackageName(), true);
                Toast.makeText(this, "Strong uninstall block enabled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,
                        "Device Admin enabled. Full uninstall blocking requires Device Owner mode.",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Could not apply strong uninstall block on this device.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void refreshAdminStatus() {
        if (dpm.isDeviceOwnerApp(getPackageName())) {
            adminStatus.setText("Removal protection: STRONG (Device Owner)");
        } else if (dpm.isAdminActive(admin)) {
            adminStatus.setText("Removal protection: ON (Device Admin)");
        } else {
            adminStatus.setText("Removal protection: OFF");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dpm != null) refreshAdminStatus();
    }

    @Override
    protected void onActivityResult(int r, int c, Intent d) {
        super.onActivityResult(r, c, d);
        if (r == 7 && c == RESULT_OK) startFilter();
        if (r == 8 && c == RESULT_OK) {
            applyStrongUninstallBlockIfOwner();
            refreshAdminStatus();
        }
    }

    private void startFilter() {
        startForegroundService(new Intent(this, FilterVpnService.class).setAction("START"));
        status.setText("Protection is ON — Family Filter");
    }
}
