package pics.sift.app.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;
import java.util.Map;

import pics.sift.app.data.util.Model;
import pics.sift.app.util.Objects;

public class Meta extends Model {
    private static final String KEY_ALBUMS = "albums";

    public static Meta parse(String str) {
        return CREATOR.parse(str);
    }

    private Map<String, String> m_albums;

    public Meta(JsonObject attributes) {
        JsonObject albums = readObject(attributes, KEY_ALBUMS);

        m_albums = new HashMap<String, String>();

        if(albums != null) {
            for(Map.Entry<String, JsonElement> entry : albums.entrySet()) {
                if(entry.getValue().isJsonPrimitive()) {
                    JsonPrimitive primitive = entry.getValue().getAsJsonPrimitive();

                    if(primitive.isString()) {
                        m_albums.put(entry.getKey(), primitive.getAsString());
                    }
                }
            }
        }
    }

    @Override
    public JsonObject getAttributes() {
        JsonObject attributes = new JsonObject();

        if(m_albums.size() > 0) {
            JsonObject albums = new JsonObject();

            for(Map.Entry<String, String> entry : m_albums.entrySet()) {
                albums.addProperty(entry.getKey(), entry.getValue());
            }

            attributes.add(KEY_ALBUMS, albums);
        }

        return attributes;
    }

    public String getAlbumTitle(String albumId) {
        return (albumId != null) ? m_albums.get(albumId) : null;
    }

    @Override
    public boolean equals(Object obj) {
        Meta meta = (Meta)obj;

        if(meta == this) {
            return true;
        }

        if(meta == null ||
           !Objects.match(meta.m_albums, m_albums)) {
            return false;
        }

        return true;
    }

    public static final Model.Creator<Meta> CREATOR = new Model.Creator<Meta>() {
        @Override
        public Meta newInstance(JsonObject attributes) {
            return new Meta(attributes);
        }

        @Override
        public Meta[] newArray(int size) {
            return new Meta[size];
        }
    };
}
