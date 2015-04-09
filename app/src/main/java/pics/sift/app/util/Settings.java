package pics.sift.app.util;

import android.content.Context;
import android.content.SharedPreferences;

import pics.sift.app.data.Device;
import pics.sift.app.data.Favorite;
import pics.sift.app.data.Profile;
import pics.sift.app.data.Session;

public class Settings {
    @SuppressWarnings("unused")
    private static final String TAG = "Settings";
    private static final String SHARED_PREFS = "prefs";

    private static final String KEY_AUTHORIZATION = "authorization";
    private static final String KEY_PROFILE = "profile";
    private static final String KEY_REGISTRATION = "registration";
    private static final String KEY_SESSION = "session";

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

    public Registration getRegistration() {
        return Registration.parse(m_packageVersion, m_preferences.getString(KEY_REGISTRATION, null));
    }

    public void setRegistration(Registration registration) {
        SharedPreferences.Editor editor = m_preferences.edit();

        editor.putString(KEY_REGISTRATION, (registration != null) ? registration.toString() : null);
        editor.apply();
    }

    public void setRegistration(String token) {
        setRegistration(new Registration(m_packageVersion, token));
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
