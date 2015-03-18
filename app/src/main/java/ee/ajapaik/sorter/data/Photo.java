package ee.ajapaik.sorter.data;

import android.net.Uri;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

import ee.ajapaik.sorter.data.util.Model;
import ee.ajapaik.sorter.util.Objects;

public class Photo extends Model {
    private static final String KEY_IDENTIFIER = "id";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_SOURCE = "source";
    private static final String KEY_TAGS = "tags";

    public static Photo parse(String str) {
        return CREATOR.parse(str);
    }

    private String m_identifier;
    private Uri m_image;
    private String m_title;
    private String m_author;
    private Hyperlink m_source;
    private List<Tag> m_tags;

    public Photo(JsonObject attributes) {
        this(attributes, null);
    }

    public Photo(JsonObject attributes, Photo basePhoto) {
        m_identifier = readIdentifier(attributes, KEY_IDENTIFIER);
        m_image = readUri(attributes, KEY_IMAGE, (basePhoto != null) ? basePhoto.getImage() : null);
        m_title = readString(attributes, KEY_TITLE, (basePhoto != null) ? basePhoto.getTitle() : null);
        m_author = readString(attributes, KEY_AUTHOR, (basePhoto != null) ? basePhoto.getAuthor() : null);
        m_source = readHyperlink(attributes, KEY_SOURCE, (basePhoto != null) ? basePhoto.getSource() : null);
        m_tags = new ArrayList<Tag>();

        for(JsonElement tagElement : readArray(attributes, KEY_TAGS)) {
            if(tagElement.isJsonPrimitive()) {
                JsonPrimitive tagPrimitive = tagElement.getAsJsonPrimitive();
                String tagName = (tagPrimitive.isString()) ? tagPrimitive.getAsString() : null;

                if(tagName != null) {
                    Tag tag = Tag.parse(tagName);

                    if(tag != Tag.UNKNOWN) {
                        m_tags.add(tag);
                    }
                }
            }
        }

        if(m_identifier == null || m_image == null) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public JsonObject getAttributes() {
        JsonObject attributes = new JsonObject();

        write(attributes, KEY_IDENTIFIER, m_identifier);
        write(attributes, KEY_IMAGE, m_image);
        write(attributes, KEY_TITLE, m_title);
        write(attributes, KEY_AUTHOR, m_author);
        write(attributes, KEY_SOURCE, m_source);

        if(m_tags != null && m_tags.size() > 0) {
            JsonArray array = new JsonArray();

            for(Tag tag : m_tags) {
                array.add(new JsonPrimitive(tag.toString()));
            }

            attributes.add(KEY_TAGS, array);
        }

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

    public String getAuthor() {
        return m_author;
    }

    public Hyperlink getSource() {
        return m_source;
    }

    public List<Tag> getTags() {
        return m_tags;
    }

    @Override
    public boolean equals(Object obj) {
        Photo photo = (Photo)obj;

        if(photo == this) {
            return true;
        }

        if(photo == null ||
           !Objects.match(photo.getIdentifier(), m_identifier) ||
           !Objects.match(photo.getImage(), m_image) ||
           !Objects.match(photo.getTitle(), m_title) ||
           !Objects.match(photo.getAuthor(), m_author) ||
           !Objects.match(photo.getSource(), m_source) ||
           !Objects.match(photo.getTags(), m_tags)) {
            return false;
        }

        return true;
    }

    public enum Tag {
        INTERIOR_OR_EXTERIOR("interior_or_exterior"),
        PUBLIC_OR_PRIVATE("public_or_private"),
        URBAN_OR_RURAL("urban_or_rural"),
        LANDSCAPE_OR_PORTRAIT("landscape_or_portrait"),
        GROUND_OR_RAISED("ground_or_raised"),
        VIEW_OR_SOCIAL("view_or_social"),
        STAGED_OR_NATURAL("staged_or_natural"),
        ONE_OR_MANY("one_or_many"),
        WHOLE_OR_DETAIL("whole_or_detail"),
        UNKNOWN(null);

        public static Tag parse(String code) {
            if(code != null) {
                for(Tag tag : values()) {
                    if(Objects.match(tag.m_code, code)) {
                        return tag;
                    }
                }
            }

            return UNKNOWN;
        }

        private final String m_code;

        private Tag(final String code) {
            m_code = code;
        }

        @Override
        public String toString() {
            return m_code;
        }
    }

    public static final Model.Creator<Photo> CREATOR = new Model.Creator<Photo>() {
        @Override
        public Photo newInstance(JsonObject attributes) {
            return new Photo(attributes);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
