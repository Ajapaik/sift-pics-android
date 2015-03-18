package ee.ajapaik.sorter.data;

import android.net.Uri;

import com.google.gson.JsonObject;

import ee.ajapaik.sorter.data.util.Model;
import ee.ajapaik.sorter.util.Objects;

public class Album extends Model {
    private static final String KEY_IDENTIFIER = "id";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBTITLE = "subtitle";
    private static final String KEY_TAGGED = "tagged";

    public static Album parse(String str) {
        return CREATOR.parse(str);
    }

    private String m_identifier;
    private Uri m_image;
    private String m_title;
    private String m_subtitle;
    private boolean m_tagged;

    public Album(JsonObject attributes) {
        m_identifier = readIdentifier(attributes, KEY_IDENTIFIER);
        m_image = readUri(attributes, KEY_IMAGE);
        m_title = readString(attributes, KEY_TITLE);
        m_subtitle = readString(attributes, KEY_SUBTITLE);
        m_tagged = (readInteger(attributes, KEY_TAGGED) == 1) ? true : false;

        if(m_identifier == null) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public JsonObject getAttributes() {
        JsonObject attributes = new JsonObject();

        write(attributes, KEY_IDENTIFIER, m_identifier);
        write(attributes, KEY_IMAGE, m_image);
        write(attributes, KEY_TITLE, m_title);
        write(attributes, KEY_SUBTITLE, m_subtitle);
        write(attributes, KEY_TAGGED, (m_tagged) ? 1 : 0);

        return attributes;
    }

    public String getIdentifier() {
        return m_identifier;
    }

    public Uri getImage() {
        return m_image;
    }

    public String getTitle() {
        return m_title;
    }

    public String getSubtitle() {
        return m_subtitle;
    }

    public boolean isTagged() {
        return m_tagged;
    }

    @Override
    public boolean equals(Object obj) {
        Album album = (Album)obj;

        if(album == this) {
            return true;
        }

        if(album == null ||
           !Objects.match(album.getIdentifier(), m_identifier) ||
           !Objects.match(album.getImage(), m_image) ||
           !Objects.match(album.getTitle(), m_title) ||
           !Objects.match(album.getSubtitle(), m_subtitle) ||
           album.isTagged() != m_tagged) {
            return false;
        }

        return true;
    }

    public static final Model.Creator<Album> CREATOR = new Model.Creator<Album>() {
        @Override
        public Album newInstance(JsonObject attributes) {
            return new Album(attributes);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
