package nl.xservices.plugins.threedeetouch;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutManager;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ThreeDeeTouch extends CordovaPlugin {

    private CallbackContext callbackContext;
    private Context applicationContext;
    private Activity activity;

    private static final boolean IS_NOUGAT_SDK_VERSION = Build.VERSION.SDK_INT >= 25;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        this.applicationContext = cordova.getActivity().getApplicationContext();
        this.activity = cordova.getActivity();

        if (action.equals("isAvailable")) {
            this.isAvailable();
        } else if (action.equals("configureQuickActions")) {
            this.configureQuickActions(args);
        }

        return true;
    }

    private boolean isAvailable() {
        JSONObject available = new JSONObject();

        try {
            available.put("isAvailable", IS_NOUGAT_SDK_VERSION);
            callbackContext.success(available);
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
        }

        return IS_NOUGAT_SDK_VERSION;
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void configureQuickActions(JSONArray args) throws JSONException {
        if (args.length() == 0) {
            callbackContext.error("No Quick Action options provided");
        } else if(IS_NOUGAT_SDK_VERSION) {
            ShortcutManager shortcutManager = activity.getSystemService(ShortcutManager.class);
            ArrayList<ShortcutInfo> shortcut = new ArrayList<ShortcutInfo>();
            JSONArray shortcutsArray = args.getJSONArray(0);
            Intent shortcutIntent;
            JSONObject shortcutObject;
            Icon icon;

            for (int i = 0, j = shortcutsArray.length(); i < j; i++) {
                shortcutObject = shortcutsArray.getJSONObject(i);
                icon = Icon.createWithResource(applicationContext, applicationContext.getResources().getIdentifier(shortcutObject.getString("iconType").toLowerCase(), "drawable", applicationContext.getPackageName()));
                // shortcutIntent = new Intent(activity, );
                // shortcutIntent.putExtra("type", shortcutObject.getString("type"));

                shortcut.add(new ShortcutInfo.Builder(applicationContext, "id" + i)
                    .setShortLabel(shortcutObject.getString("title"))
                    .setIcon(icon)
                    // .setIntent(shortcutIntent)
                    .build()
                );
            }

            shortcutManager.setDynamicShortcuts(shortcut);
            callbackContext.success("Shortcuts set");
        } else {
            callbackContext.error("Unavailable for this device");
        }
    }
}