package pics.sift.app.data;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pics.sift.app.data.util.Model;
import pics.sift.app.util.Objects;
import pics.sift.app.util.WebAction;

public class Profile extends Model {
    private static final String KEY_IDENTIFIER = "id";
    private static final String KEY_LINK = "link";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_META = "meta";
    private static final String KEY_STATE = "state";
    private static final String KEY_STATS_PICS = "pics";
    private static final String KEY_STATS_RANK = "rank";
    private static final String KEY_STATS_TAGGED = "tagged";
    private static final String KEY_FAVORITES = "favorites";
    private static final String KEY_FAVORITES_ADD = "favorites+";
    private static final String KEY_FAVORITES_REMOVE = "favorites-";

    public static WebAction<Profile> createAction(Context context, Profile profile) {
        Map<String, String> parameters = new Hashtable<String, String>();

        if(profile != null && profile.getState() != null) {
            parameters.put("state", profile.getState());
        }

        return new Action(context, "/user/me/", parameters, profile);
    }

    public static WebAction<Profile> createFavoriteAction(Context context, Profile profile, Favorite favorite, boolean state) {
        Map<String, String> parameters = new Hashtable<String, String>();

        if(profile != null && profile.getState() != null) {
            parameters.put("state", profile.getState());
        }

        parameters.put("album", favorite.getAlbumIdentifier());
        parameters.put("photo", favorite.getPhotoIdentifier());

        if(profile != null && profile.getState() != null) {
            parameters.put("state", profile.getState());
        }

        return new Action(context, (state) ? "/user/favorite/add/" : "/user/favorite/remove/", parameters, profile);
    }


    public static Profile parse(String str) {
        return CREATOR.parse(str);
    }

    private Set<Favorite> m_favorites;
    private String m_message;
    private Meta m_meta;
    private Hyperlink m_link;
    private int m_pics;
    private int m_rank;
    private String m_state;
    private int m_tagged;

    public Profile() {
        m_favorites = new HashSet<Favorite>();
        m_pics = -1;
        m_tagged = -1;
    }

    public Profile(JsonObject attributes) {
        this(attributes, null);
    }

    public Profile(JsonObject attributes, Profile baseProfile) {
        JsonElement element = attributes.get(KEY_FAVORITES);
        JsonObject meta = readObject(attributes, KEY_META);

        m_link = readHyperlink(attributes, KEY_LINK);
        m_message = readString(attributes, KEY_MESSAGE);
        m_pics = readInteger(attributes, KEY_STATS_PICS);
        m_rank = readInteger(attributes, KEY_STATS_RANK);
        m_state = readString(attributes, KEY_STATE);
        m_tagged = readInteger(attributes, KEY_STATS_TAGGED);
        m_favorites = new HashSet<Favorite>();

        if(meta != null) {
            m_meta = new Meta(meta);
        }

        if(element != null && element.isJsonArray()) {
            for(JsonElement favoriteElement : element.getAsJsonArray()) {
                if(favoriteElement.isJsonObject()) {
                    try {
                        m_favorites.add(new Favorite(favoriteElement.getAsJsonObject()));
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if(baseProfile != null) {
            Set<Favorite> favorites = baseProfile.m_favorites;

            if(favorites != null && favorites.size() > 0) {
                for(Favorite favorite : favorites) {
                    m_favorites.add(favorite);
                }
            }
        }

        for(JsonElement favoriteToRemoveElement : readArray(attributes, KEY_FAVORITES_REMOVE)) {
            if(favoriteToRemoveElement.isJsonPrimitive()) {
                JsonPrimitive favoritePrimitive = favoriteToRemoveElement.getAsJsonPrimitive();
                Favorite favorite = null;

                if(favoritePrimitive.isString()) {
                    favorite = getFavorite(favoritePrimitive.getAsString());
                } else if(favoritePrimitive.isNumber()) {
                    favorite = getFavorite(favoritePrimitive.toString());
                }

                if(favorite != null) {
                    m_favorites.remove(favorite);
                }
            }
        }

        for(JsonElement favoriteToAddElement : readArray(attributes, KEY_FAVORITES_ADD)) {
            List<Favorite> localFavorites = getLocalFavorites();

            if(favoriteToAddElement.isJsonObject()) {
                try {
                    JsonObject favoriteObject = favoriteToAddElement.getAsJsonObject();
                    Favorite oldFavorite = getFavorite(readIdentifier(favoriteObject, KEY_IDENTIFIER));
                    Favorite newFavorite = new Favorite(favoriteObject, oldFavorite);

                    if(oldFavorite == null) {
                        for(Favorite localFavorite : localFavorites) {
                            if(localFavorite.matches(newFavorite)) {
                                oldFavorite = localFavorite;
                                break;
                            }
                        }
                    }

                    if(oldFavorite == null) {
                        m_favorites.add(newFavorite);
                    } else if(!Objects.match(oldFavorite, newFavorite)) {
                        m_favorites.remove(oldFavorite);
                        m_favorites.add(newFavorite);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected Profile(Profile baseProfile, Set<Favorite> favorites) {
        m_link = baseProfile.getLink();
        m_message = baseProfile.getMessage();
        m_meta = baseProfile.getMeta();
        m_state = baseProfile.getState();
        m_pics = baseProfile.getPicturesCount();
        m_rank = baseProfile.getRank();
        m_tagged = baseProfile.getTaggedCount();
        m_favorites = favorites;
    }

    public boolean isObsolete() {
        return (m_tagged < 0 || m_pics < 0) ? true : false;
    }

    @Override
    public JsonObject getAttributes() {
        JsonObject attributes = new JsonObject();

        write(attributes, KEY_LINK, m_link);
        write(attributes, KEY_MESSAGE, m_message);
        write(attributes, KEY_STATE, m_state);
        write(attributes, KEY_STATS_PICS, m_pics);
        write(attributes, KEY_STATS_RANK, m_rank);
        write(attributes, KEY_STATS_TAGGED, m_tagged);

        if(m_meta != null) {
            attributes.add(KEY_META, m_meta.getAttributes());
        }

        if(m_favorites != null && m_favorites.size() > 0) {
            JsonArray array = new JsonArray();

            for(Favorite favorite : m_favorites) {
                array.add(favorite.getAttributes());
            }

            attributes.add(KEY_FAVORITES, array);
        }

        return attributes;
    }

    public List<Favorite> getFavorites() {
        List<Favorite> favorites = new ArrayList<Favorite>();

        for(Favorite favorite : m_favorites) {
            if(!favorite.isObsolete()) {
                favorites.add(favorite);
            }
        }

        Collections.sort(favorites, new Favorite.FavoriteComparator());

        return favorites;
    }

    public List<Favorite> getLocalFavorites() {
        List<Favorite> favorites = new ArrayList<Favorite>();

        for(Favorite favorite : m_favorites) {
            if(favorite.getIdentifier() == null) {
                favorites.add(favorite);
            }
        }

        return favorites;
    }

    public Favorite getFavorite(String identifier) {
        if(identifier != null && m_favorites != null) {
            for(Favorite favorite : m_favorites) {
                String identifier_ = favorite.getIdentifier();

                if(identifier_ != null && identifier_.equals(identifier)) {
                    return favorite;
                }
            }
        }

        return null;
    }

    public Favorite getFavorite(String albumIdentifier, String photoIdentifier) {
        if(photoIdentifier != null && m_favorites != null) {
            for(Favorite favorite : m_favorites) {
                if(favorite.getPhotoIdentifier().equals(photoIdentifier) &&
                   (albumIdentifier == null || favorite.getAlbumIdentifier().equals(albumIdentifier))) {
                    return favorite;
                }
            }
        }

        return null;
    }

    public Hyperlink getLink() {
        return m_link;
    }

    public String getMessage() {
        return m_message;
    }

    public Meta getMeta() {
        return m_meta;
    }

    public int getPicturesCount() {
        return m_pics;
    }

    public String getState() {
        return m_state;
    }

    public int getRank() {
        return m_rank;
    }

    public int getTaggedCount() {
        return m_tagged;
    }

    public Profile profileByAddingFavorite(Favorite favorite) {
        Set<Favorite> copy;

        for(Favorite favorite_ : m_favorites) {
            if(favorite_.matches(favorite)) {
                if(!favorite.equals(favorite)) {
                    copy = new HashSet<Favorite>(m_favorites);
                    copy.remove(favorite_);
                    copy.add(favorite);

                    return new Profile(this, copy);
                } else {
                    return this;
                }
            }
        }

        copy = new HashSet<Favorite>(m_favorites);
        copy.add(favorite);

        return new Profile(this, copy);
    }

    public Profile profileByRemovingFavorite(Favorite favorite) {
        for(Favorite favorite_ : m_favorites) {
            if(!favorite_.isObsolete() && favorite_.matches(favorite)) {
                Set<Favorite> copy = new HashSet<Favorite>(m_favorites);

                copy.remove(favorite_);
                copy.add(favorite.getObsolete());

                return new Profile(this, copy);
            }
        }

        return this;
    }

    @Override
    public boolean equals(Object obj) {
        Profile profile = (Profile)obj;

        if(profile == this) {
            return true;
        }

        if(profile == null ||
           profile.getPicturesCount() != m_pics ||
           profile.getTaggedCount() != m_tagged ||
           profile.getRank() != m_rank ||
           !Objects.match(profile.getLink(), m_link) ||
           !Objects.match(profile.getMessage(), m_message) ||
           !Objects.match(profile.getMeta(), m_meta) ||
           !Objects.match(profile.getState(), m_state) ||
           !Objects.match(profile.m_favorites, m_favorites)) {
            return false;
        }

        return true;
    }

    private static class Action extends WebAction<Profile> {
        private Profile m_baseProfile;

        public Action(Context context, String path, Map<String, String> parameters, Profile baseProfile) {
            super(context, path, parameters, CREATOR);
            m_baseProfile = baseProfile;
        }

        @Override
        public String getUniqueId() {
            return (m_baseProfile != null && m_baseProfile.getState() != null) ? getUrl() + "/" + m_baseProfile.getState() : null;
        }

        @Override
        protected Profile parseObject(JsonObject attributes) {
            return new Profile(attributes, m_baseProfile);
        }
    }

    public static final Model.Creator<Profile> CREATOR = new Model.Creator<Profile>() {
        @Override
        public Profile newInstance(JsonObject attributes) {
            return new Profile(attributes);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
}
