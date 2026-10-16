package com.fady.shielddnscore;

import android.app.*;
import android.content.*;
import android.net.VpnService;
import android.os.Build;
import java.util.Calendar;

public final class ScheduleManager {
    private ScheduleManager() {}

    public static boolean isActiveNow() {
        int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return h >= 17 || h < 6;
    }

    public static void apply(Context context) {
        Context app = context.getApplicationContext();

        if (isActiveNow()) {
            // Automatic start works after VPN consent has been granted once.
            if (VpnService.prepare(app) == null) {
                Intent i = new Intent(app, FilterVpnService.class).setAction("START");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    app.startForegroundService(i);
                } else {
                    app.startService(i);
                }
            }
        } else {
            app.startService(new Intent(app, FilterVpnService.class).setAction("STOP"));
        }

        scheduleNextBoundary(app);
    }

    public static void scheduleNextBoundary(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Calendar now = Calendar.getInstance();
        Calendar next = (Calendar) now.clone();

        if (isActiveNow()) {
            next.set(Calendar.HOUR_OF_DAY, 6);
            next.set(Calendar.MINUTE, 0);
            next.set(Calendar.SECOND, 0);
            next.set(Calendar.MILLISECOND, 0);
            if (!next.after(now)) next.add(Calendar.DAY_OF_YEAR, 1);
        } else {
            next.set(Calendar.HOUR_OF_DAY, 17);
            next.set(Calendar.MINUTE, 0);
            next.set(Calendar.SECOND, 0);
            next.set(Calendar.MILLISECOND, 0);
            if (!next.after(now)) next.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent i = new Intent(context, ScheduleReceiver.class)
                .setAction("com.fady.shielddns.APPLY_SCHEDULE");
        PendingIntent pi = PendingIntent.getBroadcast(
                context, 5001, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && am.canScheduleExactAlarms()) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.getTimeInMillis(), pi);
        } else {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.getTimeInMillis(), pi);
        }
    }
}
