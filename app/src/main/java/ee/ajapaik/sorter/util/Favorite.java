package ee.ajapaik.sorter.util;

import android.net.Uri;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ee.ajapaik.sorter.data.Album;
import ee.ajapaik.sorter.data.Photo;

public class Favorite {
    private static final String KEY_ALBUM_IDENTIFIER = "album_id";
    private static final String KEY_PHOTO_IDENTIFIER = "photo_id";
    private static final String KEY_IMAGE = "url";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBTITLE = "subtitle";

    public static Favorite parse(String str) {
        if(str != null) {
            try {
                JsonElement element = new JsonParser().parse(new JsonReader(new StringReader(str)));

                if(element.isJsonObject()) {
                    return new Favorite(element.getAsJsonObject());
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static List<Favorite> parseAll(String str) {
        List<Favorite> list = new ArrayList<Favorite>();

        if(str != null) {
            try {
                JsonElement element = new JsonParser().parse(new JsonReader(new StringReader(str)));

                if(element.isJsonArray()) {
                    for(JsonElement attributes : element.getAsJsonArray()) {
                        if(attributes.isJsonObject()) {
                            list.add(new Favorite(attributes.getAsJsonObject()));
                        }
                    }
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    public static String getAttributes(List<Favorite> list) {
        JsonArray array = new JsonArray();

        if(list != null) {
            for(Favorite favorite : list) {
                array.add(favorite.getAttributes());
            }
        }

        return array.toString();
    }

    private String m_albumIdentifier;
    private String m_photoIdentifier;
    private Uri m_image;
    private Date m_timestamp;
    private String m_title;
    private String m_subtitle;

    public Favorite(Album album, Photo photo) {
        m_albumIdentifier = album.getIdentifier();
        m_photoIdentifier = photo.getIdentifier();
        m_image = photo.getImage();
        m_title = album.getTitle();
        m_subtitle = photo.getTitle();
        m_timestamp = new Date();
    }

    public Favorite(JsonObject attributes) {
        JsonPrimitive primitive;

        m_albumIdentifier = ((primitive = attributes.getAsJsonPrimitive(KEY_ALBUM_IDENTIFIER)) != null && primitive.isString()) ? primitive.getAsString() : null;
        m_photoIdentifier = ((primitive = attributes.getAsJsonPrimitive(KEY_PHOTO_IDENTIFIER)) != null && primitive.isString()) ? primitive.getAsString() : null;
        m_image = ((primitive = attributes.getAsJsonPrimitive(KEY_IMAGE)) != null && primitive.isString()) ? Uri.parse(primitive.getAsString()) : null;
        m_title = ((primitive = attributes.getAsJsonPrimitive(KEY_TITLE)) != null && primitive.isString()) ? primitive.getAsString() : null;
        m_subtitle = ((primitive = attributes.getAsJsonPrimitive(KEY_SUBTITLE)) != null && primitive.isString()) ? primitive.getAsString() : null;
        m_timestamp = Dates.parse(((primitive = attributes.getAsJsonPrimitive(KEY_TIMESTAMP)) != null && primitive.isString()) ? primitive.getAsString() : null);
    }

    public JsonObject getAttributes() {
        JsonObject attributes = new JsonObject();

        if(m_albumIdentifier != null) {
            attributes.addProperty(KEY_ALBUM_IDENTIFIER, m_albumIdentifier);
        }

        if(m_photoIdentifier != null) {
            attributes.addProperty(KEY_PHOTO_IDENTIFIER, m_photoIdentifier);
        }

        if(m_image != null) {
            attributes.addProperty(KEY_IMAGE, m_image.toString());
        }

        if(m_title != null) {
            attributes.addProperty(KEY_TITLE, m_title);
        }

        if(m_subtitle != null) {
            attributes.addProperty(KEY_SUBTITLE, m_subtitle);
        }

        if(m_timestamp != null) {
            attributes.addProperty(KEY_TIMESTAMP, Dates.toString(m_timestamp));
        }

        return attributes;
    }

    public String getAlbumIdentifier() {
        return m_albumIdentifier;
    }

    public String getPhotoIdentifier() {
        return m_photoIdentifier;
    }

    public Uri getImage() {
        return m_image;
    }

    public Uri getThumbnail(int preferredDimension) {
        return Photo.resolve(m_image, preferredDimension);
    }

    public String getTitle() {
        return m_title;
    }

    public String getSubtitle() {
        return m_subtitle;
    }

    public Date getTimestamp() {
        return m_timestamp;
    }

    public boolean matches(Favorite favorite) {
        if(favorite == this) {
            return true;
        }

        if(favorite == null ||
           !Objects.match(favorite.getAlbumIdentifier(), m_albumIdentifier) ||
           !Objects.match(favorite.getPhotoIdentifier(), m_photoIdentifier)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        Favorite favorite = (Favorite)obj;

        if(favorite == this) {
            return true;
        }

        if(favorite == null ||
           !Objects.match(favorite.getAlbumIdentifier(), m_albumIdentifier) ||
           !Objects.match(favorite.getPhotoIdentifier(), m_photoIdentifier) ||
           !Objects.match(favorite.getImage(), m_image) ||
           !Objects.match(favorite.getTimestamp(), m_timestamp) ||
           !Objects.match(favorite.getTitle(), m_title) ||
           !Objects.match(favorite.getTitle(), m_subtitle)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return getAttributes().toString();
    }
}
