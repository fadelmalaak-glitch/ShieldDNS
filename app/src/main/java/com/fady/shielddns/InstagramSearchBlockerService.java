package com.fady.shielddns;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import java.util.Locale;

public class InstagramSearchBlockerService extends AccessibilityService {
    private long lastBlock = 0L;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null || event.getPackageName() == null) return;
        if (!"com.instagram.android".contentEquals(event.getPackageName())) return;

        int type = event.getEventType();

        if (type == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            AccessibilityNodeInfo src = event.getSource();
            if (looksLikeSearchOrExplore(src)) blockNow();
        } else if (type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            AccessibilityNodeInfo root = getRootInActiveWindow();
            if (containsSearchField(root, 0)) blockNow();
        }
    }

    private void blockNow() {
        long now = System.currentTimeMillis();
        if (now - lastBlock < 900) return;
        lastBlock = now;

        performGlobalAction(GLOBAL_ACTION_BACK);
        Toast.makeText(this,
                "Instagram Search is blocked by ShieldDNS",
                Toast.LENGTH_SHORT).show();
    }

    private boolean looksLikeSearchOrExplore(AccessibilityNodeInfo n) {
        if (n == null) return false;

        StringBuilder sb = new StringBuilder();
        if (n.getText() != null) sb.append(n.getText()).append(' ');
        if (n.getContentDescription() != null) sb.append(n.getContentDescription()).append(' ');

        String s = sb.toString().toLowerCase(Locale.ROOT).trim();

        return s.equals("search")
                || s.equals("explore")
                || s.contains("search and explore")
                || s.equals("بحث")
                || s.equals("استكشاف")
                || s.contains("بحث واستكشاف");
    }

    private boolean containsSearchField(AccessibilityNodeInfo n, int depth) {
        if (n == null || depth > 10) return false;

        CharSequence cls = n.getClassName();

        StringBuilder sb = new StringBuilder();
        if (n.getText() != null) sb.append(n.getText()).append(' ');
        if (n.getContentDescription() != null) sb.append(n.getContentDescription()).append(' ');
        if (n.getHintText() != null) sb.append(n.getHintText()).append(' ');

        String s = sb.toString().toLowerCase(Locale.ROOT);

        if (cls != null && cls.toString().contains("EditText")) {
            if (s.contains("search") || s.contains("بحث")) return true;
        }

        for (int i = 0; i < n.getChildCount(); i++) {
            AccessibilityNodeInfo child = n.getChild(i);
            if (containsSearchField(child, depth + 1)) return true;
        }

        return false;
    }

    @Override
    public void onInterrupt() {}
}
