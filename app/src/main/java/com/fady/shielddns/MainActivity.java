package com.fady.shielddnsv5;

import android.app.*;
import android.app.admin.DevicePolicyManager;
import android.content.*;
import android.net.VpnService;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.*;

public class MainActivity extends Activity {
    private TextView status;
    private TextView adminStatus;
    private TextView scheduleStatus;

    private DevicePolicyManager dpm;
    private ComponentName admin;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        setContentView(R.layout.activity_main);

        status = findViewById(R.id.status);
        adminStatus = findViewById(R.id.adminStatus);
        scheduleStatus = findViewById(R.id.scheduleStatus);

        dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        admin = new ComponentName(this, ShieldDeviceAdminReceiver.class);

        findViewById(R.id.start).setOnClickListener(v -> requestVpnAndApply());

        findViewById(R.id.enableAdmin).setOnClickListener(v -> enableAdminProtection());

        findViewById(R.id.accessibility).setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));

        ScheduleManager.apply(this);
        refreshStatus();
    }

    private void requestVpnAndApply() {
        Intent p = VpnService.prepare(this);

        if (p != null) {
            startActivityForResult(p, 7);
        } else {
            ScheduleManager.apply(this);
            Toast.makeText(this, "VPN permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void enableAdminProtection() {
        if (!dpm.isAdminActive(admin)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

            intent.putExtra(
                    DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    admin);

            intent.putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "ShieldDNS uses Device Administrator as a basic removal barrier. Android still keeps a user recovery path.");

            startActivityForResult(intent, 8);
        } else {
            Toast.makeText(this, "Removal protection is already enabled", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshStatus() {
        scheduleStatus.setText(
                ScheduleManager.isActiveNow()
                        ? "Schedule now: PROTECTED (17:00–06:00)"
                        : "Schedule now: FREE (06:00–17:00)");

        adminStatus.setText(
                dpm.isAdminActive(admin)
                        ? "Removal protection: ON (Device Admin)"
                        : "Removal protection: OFF");

        status.setText(
                "The filter starts automatically during the protected window after VPN permission has been granted once.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScheduleManager.apply(this);
        refreshStatus();
    }

    @Override
    protected void onActivityResult(int r, int c, Intent d) {
        super.onActivityResult(r, c, d);

        if (r == 7 && c == RESULT_OK) {
            ScheduleManager.apply(this);
        }

        if (r == 8 && c == RESULT_OK) {
            ScheduleManager.apply(this);
        }

        refreshStatus();
    }
}
