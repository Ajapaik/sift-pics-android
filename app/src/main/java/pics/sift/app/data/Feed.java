package pics.sift.app.data;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pics.sift.app.data.util.Model;
import pics.sift.app.util.Objects;
import pics.sift.app.util.WebAction;

public class Feed extends Model {
    private static final String API_PATH = "/albums/";
    private static final String KEY_ALBUMS = "albums";
    private static final String KEY_STATS = "stats";

    public static WebAction<Feed> createAction(Context context) {
        return new Action(context, API_PATH, null);
    }

    public static Feed parse(String str) {
        return CREATOR.parse(str);
    }

    private List<Album> m_albums;
    private Stats m_stats;

    public Feed(JsonObject attributes) {
        JsonObject stats = readObject(attributes, KEY_STATS);

        m_albums = new ArrayList<Album>();
        m_stats = (stats != null) ? new Stats(stats) : new Stats();

        for(JsonElement tagElement : readArray(attributes, KEY_ALBUMS)) {
            if(tagElement.isJsonObject()) {
                try {
                    m_albums.add(new Album(tagElement.getAsJsonObject()));
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public JsonObject getAttributes() {
        JsonObject attributes = new JsonObject();

        if(m_albums.size() > 0) {
            JsonArray array = new JsonArray();

            for(Album album : m_albums) {
                array.add(album.getAttributes());
            }

            attributes.add(KEY_ALBUMS, array);
        }

        if(m_stats != null && !m_stats.empty()) {
            attributes.add(KEY_STATS, m_stats.getAttributes());
        }

        return attributes;
    }

    public List<Album> getAlbums() {
        return m_albums;
    }

    public Stats getStats() {
        return m_stats;
    }

    @Override
    public boolean equals(Object obj) {
        Feed feed = (Feed)obj;

        if(feed == this) {
            return true;
        }

        if(feed == null ||
           !Objects.match(feed.getAlbums(), m_albums) ||
           !Objects.match(feed.getStats(), m_stats)) {
            return false;
        }

        return true;
    }

    private static class Action extends WebAction<Feed> {
        public Action(Context context, String path, Map<String, String> parameters) {
            super(context, path, parameters, CREATOR);
        }

        @Override
        public String getUniqueId() {
            return API_PATH;
        }
    }

    public static final Model.Creator<Feed> CREATOR = new Model.Creator<Feed>() {
        @Override
        public Feed newInstance(JsonObject attributes) {
            return new Feed(attributes);
        }

        @Override
        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };
}
