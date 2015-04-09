package pics.sift.app.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings;

import com.google.gson.JsonObject;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import pics.sift.app.data.util.Model;
import pics.sift.app.util.Registration;
import pics.sift.app.util.SHA1;
import pics.sift.app.util.WebAction;

public class Device extends Model {
    private static final String SHARED_PREFS = "prefs";
    private static final String KEY_UNIQUE_ID = "user.id";
    private static final String VALUE_GCM = "gcm";

    public static WebAction<Device> createRegistrationAction(Context context, Registration registration) {
        Map<String, String> parameters = new Hashtable<String, String>();

        parameters.put("id", SHA1.encode(getUniqueIdentifier(context)));
        parameters.put("type", VALUE_GCM);
        parameters.put("token", registration.getCode());

        return new WebAction(context, "/user/device/register/", parameters, CREATOR);
    }

    public static WebAction<Device> createUnregistrationAction(Context context, Registration registration) {
        Map<String, String> parameters = new Hashtable<String, String>();

        parameters.put("id", SHA1.encode(getUniqueIdentifier(context)));
        parameters.put("type", VALUE_GCM);
        parameters.put("token", registration.getCode());

        return new WebAction(context, "/user/device/unregister/", parameters, CREATOR);
    }

    public static int getPackageVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            return packageInfo.versionCode;
        }
        catch(NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static String getUniqueIdentifier(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        if(androidId == null || androidId.length() == 0) {
            SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            if((androidId = preferences.getString(KEY_UNIQUE_ID, "")).length() == 0) {
                SharedPreferences.Editor editor = preferences.edit();

                androidId = UUID.randomUUID().toString();
                editor.putString(KEY_UNIQUE_ID, androidId);
                editor.apply();
            }
        }

        return androidId;
    }

    public Device(JsonObject attributes) { }

    @Override
    public JsonObject getAttributes() {
        JsonObject attributes = new JsonObject();

        return attributes;
    }

    @Override
    public boolean equals(Object obj) {
        Device device = (Device)obj;

        if(device == this) {
            return true;
        }

        if(device == null) {
            return false;
        }

        return true;
    }

    public static final Model.Creator<Device> CREATOR = new Model.Creator<Device>() {
        @Override
        public Device newInstance(JsonObject attributes) {
            return new Device(attributes);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };
}
