package pics.sift.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

import pics.sift.app.data.Device;
import pics.sift.app.data.Favorite;
import pics.sift.app.data.Profile;
import pics.sift.app.data.Session;

public class Settings {
    @SuppressWarnings("unused")
    private static final String TAG = "Settings";
    private static final String SHARED_PREFS = "prefs";

    private static final String KEY_AUTHORIZATION = "authorization";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_PROFILE = "profile";
    private static final String KEY_REGISTRATION = "registration";
    private static final String KEY_SESSION = "session";

    public static void updateLocale(Context context, String language) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration cfg = resources.getConfiguration();

        cfg.locale = (language != null) ? new Locale(language) : Locale.getDefault();
        resources.updateConfiguration(cfg, dm);
    }

    private int m_packageVersion;
    private SharedPreferences m_preferences;

    public Settings(Context context) {
        m_packageVersion = Device.getPackageVersion(context);
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

    public String getLanguage() {
        return m_preferences.getString(KEY_LANGUAGE, null);
    }

    public void setLanguage(String language) {
        SharedPreferences.Editor editor = m_preferences.edit();

        editor.putString(KEY_LANGUAGE, language);
        editor.apply();
    }

    public Profile getProfile() {
        return Profile.parse(m_preferences.getString(KEY_PROFILE, null));
    }

    public void setProfile(Profile profile) {
        SharedPreferences.Editor editor = m_preferences.edit();

        editor.putString(KEY_PROFILE, (profile != null) ? profile.toString() : null);
        editor.apply();
    }

    public Profile addFavorite(Favorite favorite) {
        return addFavorite(favorite, getProfile());
    }

    public Profile addFavorite(Favorite favorite, Profile oldProfile) {
        Profile newProfile = oldProfile.profileByAddingFavorite(favorite);

        if(newProfile != oldProfile) {
            setProfile(newProfile);
        }

        return newProfile;
    }

    public Profile removeFavorite(Favorite favorite) {
        return removeFavorite(favorite, getProfile());
    }

    public Profile removeFavorite(Favorite favorite, Profile oldProfile) {
        Profile newProfile = oldProfile.profileByRemovingFavorite(favorite);

        if(newProfile != oldProfile) {
            setProfile(newProfile);
        }

        return newProfile;
    }

    public Registration createRegistration(String token) {
        return new Registration(m_packageVersion, token);
    }

    public Registration getRegistration() {
        return Registration.parse(m_packageVersion, m_preferences.getString(KEY_REGISTRATION, null));
    }

    public void setRegistration(Registration registration) {
        SharedPreferences.Editor editor = m_preferences.edit();

        if(registration == null) {
            registration = new Registration(m_packageVersion, (String)null);
        }

        editor.putString(KEY_REGISTRATION, registration.toString());
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
