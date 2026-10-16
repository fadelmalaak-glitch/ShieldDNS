package com.fady.shielddnssocial;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import java.util.Locale;

public class InstagramSearchBlockerService extends AccessibilityService {

    private static final String PREFS = "shielddns_social_prefs";
    private static final String CONSENT = "accessibility_consent_v1";
    private long lastBlockAt = 0L;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        boolean consent = getSharedPreferences(PREFS, MODE_PRIVATE)
                .getBoolean(CONSENT, false);

        if (!consent) {
            Toast.makeText(this,
                    "Open ShieldDNS Social and review the disclosure first",
                    Toast.LENGTH_LONG).show();
            disableSelf();
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null || event.getPackageName() == null) return;
        if (!"com.instagram.android".contentEquals(event.getPackageName())) return;
        if (event.getEventType() != AccessibilityEvent.TYPE_VIEW_CLICKED) return;

        AccessibilityNodeInfo node = event.getSource();
        if (node == null) return;

        String label = labelOf(node);
        if (isSearchOrExplore(label)) {
            long now = System.currentTimeMillis();
            if (now - lastBlockAt < 800) return;
            lastBlockAt = now;

            performGlobalAction(GLOBAL_ACTION_BACK);
            Toast.makeText(this,
                    "Instagram Search/Explore blocked",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String labelOf(AccessibilityNodeInfo node) {
        StringBuilder s = new StringBuilder();

        if (node.getText() != null) {
            s.append(node.getText()).append(' ');
        }
        if (node.getContentDescription() != null) {
            s.append(node.getContentDescription()).append(' ');
        }

        AccessibilityNodeInfo parent = node.getParent();
        if (parent != null) {
            if (parent.getText() != null) {
                s.append(parent.getText()).append(' ');
            }
            if (parent.getContentDescription() != null) {
                s.append(parent.getContentDescription()).append(' ');
            }
        }

        return s.toString().trim().toLowerCase(Locale.ROOT);
    }

    private boolean isSearchOrExplore(String s) {
        if (s == null || s.isEmpty()) return false;

        return s.equals("search")
                || s.equals("explore")
                || s.equals("search explore")
                || s.equals("search and explore")
                || s.equals("بحث")
                || s.equals("استكشاف")
                || s.equals("بحث واستكشاف")
                || s.startsWith("search ")
                || s.startsWith("explore ")
                || s.endsWith(" search")
                || s.endsWith(" explore");
    }

    @Override
    public void onInterrupt() {}
}
