package com.fady.shielddnscore;

import android.app.*;
import android.content.*;
import android.net.VpnService;
import android.os.Bundle;
import android.widget.*;

public class MainActivity extends Activity {
    private TextView status;
    private TextView scheduleStatus;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.status);
        scheduleStatus = findViewById(R.id.scheduleStatus);

        findViewById(R.id.start).setOnClickListener(v -> requestVpnAndApply());

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

    private void refreshStatus() {
        scheduleStatus.setText(
                ScheduleManager.isActiveNow()
                        ? "Schedule now: PROTECTED (17:00–06:00)"
                        : "Schedule now: FREE (06:00–17:00)");

        status.setText(
                "ShieldDNS Core runs the family filter automatically from 5 PM to 6 AM after VPN permission is granted once.");
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
        refreshStatus();
    }
}
