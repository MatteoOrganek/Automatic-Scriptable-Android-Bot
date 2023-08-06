package owres.org.owresassistantv2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //https://api.telegram.org/bot5938702067:AAEw3R4tzLNIr0XKbJwgN6vQclmRHdbnjvk/sendMessage?chat_id=6173266911&text=hello,%20world!
    //https://api.telegram.org/bot5938702067:AAEw3R4tzLNIr0XKbJwgN6vQclmRHdbnjvk/getUpdates?offset=0

    static Context INSTANCE;
    static Boolean SNACK_HIDDEN = false;
    static String CHANNEL_ID = "27";
    static String TAG = "_OWRES_DEBUG";
    static final String ACCESSIBILITY_SERVICE_PATH = "owres.org.owresassistantv2/owres.org.owresassistant.AutomaticService";


    static String LAST_MESSAGE_ID = "";
    static String MESSAGE = "";
    static String CURRENT_MESSAGE_ID = "";
    String BOT_ID = "5938702067:AAEw3R4tzLNIr0XKbJwgN6vQclmRHdbnjvk";
    String CHAT_ID = "6173266911";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        INSTANCE = MainActivity.this;


        Tools.checkPermission(android.Manifest.permission.WAKE_LOCK, 101);
        Tools.checkPermission(android.Manifest.permission.RECEIVE_BOOT_COMPLETED, 102);
        Tools.checkPermission(Manifest.permission.SCHEDULE_EXACT_ALARM, 103);


        Intent i = new Intent(this, ForegroundService.class);
        this.startForegroundService(i);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume(){
        super.onResume();

        INSTANCE = MainActivity.this;

    }


    static void checkForUpdates(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(MainActivity.INSTANCE);
        String url = "https://api.telegram.org/bot5938702067:AAEw3R4tzLNIr0XKbJwgN6vQclmRHdbnjvk/getUpdates?offset=-1";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Display the first 500 characters of the response string.
                    if (response.contains("message")) {
                        CURRENT_MESSAGE_ID = response.split("message_id\":")[1].split(",")[0];
                        Tools.logDebug("Checking for messages...");
                        if (!CURRENT_MESSAGE_ID.equals(LAST_MESSAGE_ID)) {

                            // New message!
                            LAST_MESSAGE_ID = CURRENT_MESSAGE_ID;
                            MESSAGE = response.split("text\":\"")[1].split("\"")[0];
                            Tools.logDebug("New message no." + CURRENT_MESSAGE_ID + " | Content: " + MESSAGE);
                            switch (MESSAGE) {
                                case "Marco":
                                    MainActivity.writeMessage("Polo!");
                            }


                        }
                    }

                }, error -> Tools.logDebug("Unable to  check messages."));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    static void writeMessage(String message){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(MainActivity.INSTANCE);
        String url = "https://api.telegram.org/bot5938702067:AAEw3R4tzLNIr0XKbJwgN6vQclmRHdbnjvk/sendMessage?chat_id=6173266911&text=" + message;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Display the first 500 characters of the response string.
                    Tools.logDebug("Post Successful: " + message);

                }, error -> Tools.logDebug("That didn't work!"));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }



}