package owres.org.owresassistantv2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

class Tools {

    static void logDebug(String message) {
        Log.d("_OWRES_DEBUG", "@ - " + message);
    }


    static void showAToast(String st) {
        Toast toast = new Toast(MainActivity.INSTANCE);
        try {
            toast.getView().isShown();     // true if visible
            toast.setText(st);
        } catch (Exception e) {         // invisible if exception
            toast = Toast.makeText(MainActivity.INSTANCE, st, Toast.LENGTH_SHORT);

        }
        toast.show();  //finally display it
    }


    // Add INSTANCE in MainActivity and reference it to all activities onCreate and onResume
    @SuppressLint({"ResourceAsColor", "WrongConstant"})
    static void showASnack(String string, int STATUS) {
        Activity activity = (Activity) MainActivity.INSTANCE;
        if (activity != null) {
            Window window = activity.getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (!MainActivity.SNACK_HIDDEN) {
                String colorbg = "#000000";
                String colortxt = "#FFFFFF";
                switch (STATUS) {
                    case 0:
                        // Basic
                        break;
                    case 1:
                        colortxt = "#000000";
                        colorbg = "#00b400"; // Green
                        break;
                    case 2:
                        colortxt = "#000000";
                        colorbg = "#8A0B0B"; // Red
                        window.setStatusBarColor(Color.parseColor(colorbg));
                        break;
                }
                Snackbar snackbar = Snackbar.make(((Activity) MainActivity.INSTANCE).getWindow().getDecorView().findViewById(android.R.id.content), string.toUpperCase(Locale.ROOT), Snackbar.LENGTH_LONG);

                snackbar.setBackgroundTint(Color.parseColor(colorbg));
                snackbar.setTextColor(Color.parseColor(colortxt));
                View view = snackbar.getView();

                TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                tv.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setTextAlignment(Gravity.CENTER_HORIZONTAL);

                (snackbar.getView()).getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                snackbar.show();
            }
        }
    }

    static void writeToFile(String data, String path, Boolean append) {

        if (!data.equals("")) {
            try {
                FileWriter out = new FileWriter(path, append);
                out.append(data);
                out.close();
            } catch (IOException e) {
                Tools.logDebug(e.getMessage());
            }
        }

    }

    static String readFromFile(String path) throws IOException {
        final InputStream inputStream = new FileInputStream(new File(path));
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        final StringBuilder stringBuilder = new StringBuilder();

        boolean done = false;

        while (!done) {
            final String line = reader.readLine();
            done = (line == null);

            if (line != null) {
                stringBuilder.append(line);
            }
        }

        reader.close();
        inputStream.close();


        return stringBuilder.toString();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    static void createNotification(String title, String content) {


        Context mContext = MainActivity.INSTANCE;

        boolean NOTIFICATION_PRESENT = false;
        NotificationManager mNotificationManager = (NotificationManager) MainActivity.INSTANCE.getSystemService(Service.NOTIFICATION_SERVICE) ;
        StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();

        for (StatusBarNotification notification : notifications) {
            Tools.logDebug(notification.getNotification().getChannelId());
            if (notification.getNotification().getChannelId().equals(MainActivity.CHANNEL_ID)) {
                NOTIFICATION_PRESENT = true;
            }
        }

        if (!NOTIFICATION_PRESENT) {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.getApplicationContext(), MainActivity.CHANNEL_ID);

            Intent ii = new Intent(mContext.getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 27, ii, 0);

            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            bigText.setBigContentTitle(title);

            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setContentTitle(title);
            mBuilder.setContentText(content);
            mBuilder.setPriority(Notification.PRIORITY_MAX);
            mBuilder.setStyle(bigText);


            String channelId = MainActivity.CHANNEL_ID;
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);

            mNotificationManager.notify(0, mBuilder.build());
        }
    }

    static String printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        return elapsedDays + " Days / " + String.format("%02d", elapsedHours)+":"+String.format("%02d", elapsedMinutes)+":"+String.format("%02d", elapsedSeconds);
    }

    static boolean isNowBetweenTwoHours(int from, int to) {

        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int t = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);
        return to > from && t >= from && t <= to || to < from && (t >= from || t <= to);
    }

    static void storeData(String s, String s1){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.INSTANCE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(s, s1);
        editor.apply();
    }

    static String retrieveData(String s){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.INSTANCE);
        String name = preferences.getString(s, "");

        if (!name.equalsIgnoreCase("")) {
            return name;
        } else {
            return "";
        }
    }

    static void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (MainActivity.INSTANCE != null){
            if (ContextCompat.checkSelfPermission(MainActivity.INSTANCE, permission) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions((Activity) MainActivity.INSTANCE, new String[] { permission }, requestCode);
            }
            else {
                Tools.logDebug(permission + " Granted");
            }
        }
    }

    // Get Notification Bar height
    public static int getNotificationBarHeight(){
        Rect notificationBarRect = new Rect();
        Window window = ((Activity)MainActivity.INSTANCE).getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(notificationBarRect);
        int statusBarHeight = notificationBarRect.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        return contentViewTop - statusBarHeight;
    }

    // Send user to overlay permission settings
    static void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + ((Activity)MainActivity.INSTANCE).getApplicationContext().getPackageName()));
        ((Activity)MainActivity.INSTANCE).startActivityForResult(intent, 67);
    }

    // Check if accessibility service is enabled
    static boolean isAccessibilityServiceEnabled() {
        int accessibilityEnabled = 0;
        //Declaring packagname/servicename
        try {
            //Check if Accessibility is ON
            accessibilityEnabled = Settings.Secure.getInt(((Activity)MainActivity.INSTANCE).getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);

        } catch (Settings.SettingNotFoundException e) {
            //Accessibility not ON
            Toast.makeText(((Activity)MainActivity.INSTANCE).getApplicationContext(), "Please turn on Accessibility", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            ((Activity)MainActivity.INSTANCE).startActivity(intent);
        }

        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled==1) {

            //Get all of the Accessibility Services
            String settingValue = Settings.Secure.getString(((Activity)MainActivity.INSTANCE).getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

            if (settingValue != null) {
                splitter.setString(settingValue);

                while (splitter.hasNext()) {
                    String accessibilityService = splitter.next();
                    if (accessibilityService.equalsIgnoreCase(MainActivity.ACCESSIBILITY_SERVICE_PATH)){
                        return true;
                    }
                }
            }

        }
        return false;
    }
    static boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) (MainActivity.INSTANCE).getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static int getBatteryPercentage() {

        Context context = MainActivity.INSTANCE;
        if (Build.VERSION.SDK_INT >= 21) {

            BatteryManager bm = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        } else {

            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, iFilter);

            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

            double batteryPct = level / (double) scale;

            return (int) (batteryPct * 100);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Boolean isDeviceCharging(){
        Context context = MainActivity.INSTANCE;
        BatteryManager myBatteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);

        return  myBatteryManager.isCharging();

    }
}

