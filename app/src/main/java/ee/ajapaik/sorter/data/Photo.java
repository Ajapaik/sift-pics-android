package ee.ajapaik.sorter.data;

import android.net.Uri;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

import ee.ajapaik.sorter.R;
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

    public static Uri resolve(Uri uri) {
        return resolve(uri, 400);
    }

    public static Uri resolve(Uri uri, int preferredDimension) {
        if(uri != null) {
            String str = uri.toString();

            str = str.replace("[DIM]", Integer.toString(preferredDimension));

            return Uri.parse(str);
        }

        return uri;
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

        // FIXME: "tag" is just a temporary workaround for a server-side bug
        for(JsonElement tagElement : readArray(attributes, (attributes.has("tag")) ? "tag" : KEY_TAGS)) {
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

    public boolean hasTags() {
        return (m_tags != null && m_tags.size() > 0) ? true : false;
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

    public Uri getThumbnail(int preferredDimension) {
        return resolve(m_image, preferredDimension);
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
        INTERIOR_OR_EXTERIOR("interior_or_exterior", 0x01, R.string.photo_tag_interior, R.drawable.ic_local_hotel_white_48dp, R.string.photo_tag_exterior, R.drawable.ic_nature_people_white_48dp),
        PUBLIC_OR_PRIVATE("public_or_private", 0x02, R.string.photo_tag_public, R.drawable.ic_public_white_48dp, R.string.photo_tag_private, R.drawable.ic_vpn_lock_white_48dp),
        URBAN_OR_RURAL("urban_or_rural", 0x04, R.string.photo_tag_urban, R.drawable.ic_location_city_white_48dp, R.string.photo_tag_rural, R.drawable.ic_nature_white_48dp),
        GROUND_OR_RAISED("ground_or_raised", 0x08, R.string.photo_tag_ground, R.drawable.ic_nature_people_white_48dp, R.string.photo_tag_raised, R.drawable.ic_filter_drama_white_48dp),
        VIEW_OR_SOCIAL("view_or_social", 0x10, R.string.photo_tag_view, R.drawable.ic_landscape_white_48dp, R.string.photo_tag_social, R.drawable.ic_accessibility_white_48dp),
        STAGED_OR_NATURAL("staged_or_natural", 0x20, R.string.photo_tag_staged, R.drawable.ic_portrait_white_48dp, R.string.photo_tag_natural, R.drawable.ic_directions_walk_white_48dp),
        ONE_OR_MANY("one_or_many", 0x40, R.string.photo_tag_one, R.drawable.ic_person_white_48dp, R.string.photo_tag_many, R.drawable.ic_group_add_white_48dp),
        WHOLE_OR_DETAIL("whole_or_detail", 0x80, R.string.photo_tag_whole, R.drawable.ic_location_city_white_48dp, R.string.photo_tag_detail, R.drawable.ic_local_florist_white_48dp),
        UNKNOWN(null, R.string.photo_tag_na, 0x00, R.drawable.ic_cancel_white_48dp, R.string.photo_tag_na, R.drawable.ic_cancel_white_48dp);

        public static Tag parse(String code) {
            return parse(code, UNKNOWN);
        }

        public static Tag parse(String code, Tag defaultValue) {
            if(code != null) {
                for(Tag tag : values()) {
                    if(Objects.match(tag.m_code, code)) {
                        return tag;
                    }
                }
            }

            return defaultValue;
        }

        private final String m_code;
        private final int m_mask;
        private final int m_leftTitleResourceId;
        private final int m_leftImageResourceId;
        private final int m_rightTitleResourceId;
        private final int m_rightImageResourceId;

        private Tag(final String code, final int mask, final int leftTitleResourceId, final int leftImageResourceId, final int rightTitleResourceId, final int rightImageResourceId) {
            m_code = code;
            m_mask = mask;
            m_leftTitleResourceId = leftTitleResourceId;
            m_leftImageResourceId = leftImageResourceId;
            m_rightTitleResourceId = rightTitleResourceId;
            m_rightImageResourceId = rightImageResourceId;
        }

        public int getMask() {
            return m_mask;
        }

        public int getLeftSubtractMask() {
            if(this == INTERIOR_OR_EXTERIOR) {
                return GROUND_OR_RAISED.getMask() | URBAN_OR_RURAL.getMask();
            }

            if(this == VIEW_OR_SOCIAL) {
                return ONE_OR_MANY.getMask() | STAGED_OR_NATURAL.getMask();
            }

            return 0x00;
        }

        public int getLeftTitleResourceId() {
            return m_leftTitleResourceId;
        }

        public int getLeftImageResourceId() {
            return m_leftImageResourceId;
        }

        public int getRightSubtractMask() {
            if(this == VIEW_OR_SOCIAL) {
                return WHOLE_OR_DETAIL.getMask();
            }

            return 0x00;
        }

        public int getRightTitleResourceId() {
            return m_rightTitleResourceId;
        }

        public int getRightImageResourceId() {
            return m_rightImageResourceId;
        }

        @Override
        public String toString() {
            return m_code;
        }
    }

    public enum TagResult {
        LEFT(-1),
        RIGHT(1),
        NOT_APPLICABLE(0);

        private final int m_code;

        private TagResult(final int code) {
            m_code = code;
        }

        public int getCode() {
            return m_code;
        }

        @Override
        public String toString() {
            return Integer.toString(m_code);
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
