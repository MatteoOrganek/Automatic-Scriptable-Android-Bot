package owres.org.owresassistantv2;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AutomaticService extends AccessibilityService {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // VARIABLES DECLARATION

    public static AutomaticService instance;
    private static String control_package = "";

    private static final int EVENT_TYPE_ACTION_WINDOW = 32;
    public static final String PACKAGE_NAME = "owres.org.owresassistant";
    public static final String ACCESSIBILITY_ID = PACKAGE_NAME + "/.AutomaticService";

    public static String currPackage;
    public static String lastPackage = "";
    public static AccessibilityNodeInfo testCurrSource;
    public static AccessibilityNodeInfo currSource;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // OVERRIDES


    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "AutomaticService Interrupted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceConnected() {
        instance = this;
        Log.d(MainActivity.TAG, "AutomaticService connected.");
        Toast.makeText(getApplicationContext(), "AutomaticService Connected.", Toast.LENGTH_SHORT).show();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.notificationTimeout = 100;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;

        this.setServiceInfo(info);
    }
}



