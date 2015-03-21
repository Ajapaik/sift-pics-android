package ee.ajapaik.sorter.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

import ee.ajapaik.sorter.data.Session;

public class Settings {
    @SuppressWarnings("unused")
    private static final String TAG = "Settings";
    private static final String SHARED_PREFS = "prefs";

    private static String KEY_AUTHORIZATION = "authorization";
    private static String KEY_FAVORITES = "favorites";
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

    public List<Favorite> getFavorites() {
        return Favorite.parseAll(m_preferences.getString(KEY_FAVORITES, null));
    }

    public void setFavorites(List<Favorite> favorites) {
        SharedPreferences.Editor editor = m_preferences.edit();

        editor.putString(KEY_FAVORITES, (favorites != null) ? Favorite.getAttributes(favorites) : null);
        editor.apply();
    }

    public void addFavorite(Favorite favorite) {
        addFavorite(favorite, getFavorites());
    }

    public void addFavorite(Favorite favorite, List<Favorite> favorites) {
        for(int i = 0, c = favorites.size(); i < c; i++) {
            Favorite favorite_ = favorites.get(i);

            if(favorite_.matches(favorite)) {
                if(!favorite.equals(favorite)) {
                    favorites.set(i, favorite);
                    setFavorites(favorites);
                    return;
                }
            }
        }

        favorites.add(favorite);
        setFavorites(favorites);
    }

    public void removeFavorite(Favorite favorite) {
        removeFavorite(favorite, getFavorites());
    }

    public void removeFavorite(Favorite favorite, List<Favorite> favorites) {
        for(int i = 0, c = favorites.size(); i < c; i++) {
            Favorite favorite_ = favorites.get(i);

            if(favorite_.matches(favorite)) {
                favorites.remove(i);
                setFavorites(favorites);
                return;
            }
        }
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
