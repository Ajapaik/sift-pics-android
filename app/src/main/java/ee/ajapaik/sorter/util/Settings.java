package ee.ajapaik.sorter.util;

import android.content.Context;
import android.content.SharedPreferences;

import ee.ajapaik.sorter.data.Session;

public class Settings {
    @SuppressWarnings("unused")
    private static final String TAG = "Settings";
    private static final String SHARED_PREFS = "prefs";

    private static String KEY_AUTHORIZATION = "authorization";
    private static String KEY_SESSION = "session";

    private SharedPreferences m_preferences;

    public Settings(Context context) {
        m_preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }

    public Authorization getAuthorization() {
        return Authorization.parse(m_preferences.getString(KEY_AUTHORIZATION, null));
    }

    public void setAuthorization(Authorization authorization) {
        SharedPreferences.Editor editor = m_preferences.edit();

        editor.putString(KEY_AUTHORIZATION, (authorization != null) ? authorization.toString() : null);
        editor.apply();
    }

    public Session getSession() {
        return Session.parse(m_preferences.getString(KEY_SESSION, null));
    }

    public void setSession(Session session) {
        SharedPreferences.Editor editor = m_preferences.edit();

        editor.putString(KEY_SESSION, (session != null) ? session.toString() : null);
        editor.apply();
    }
}
