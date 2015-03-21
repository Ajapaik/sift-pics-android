package ee.ajapaik.sorter.data;

import android.content.Context;

import com.google.gson.JsonObject;

import ee.ajapaik.sorter.data.util.Model;
import ee.ajapaik.sorter.util.Objects;
import ee.ajapaik.sorter.util.WebAction;

public class Profile extends Model {
    private static final String KEY_LINK = "link";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_STATS_PICS = "pics";
    private static final String KEY_STATS_TAGGED = "tagged";

    public static WebAction<Profile> createAction(Context context) {
        return new WebAction<Profile>(context, "/user/me/", null, CREATOR);
    }

    public static Profile parse(String str) {
        return CREATOR.parse(str);
    }

    private String m_message;
    private Hyperlink m_link;
    private int m_pics;
    private int m_tagged;

    public Profile(JsonObject attributes) {
        m_link = readHyperlink(attributes, KEY_LINK);
        m_message = readString(attributes, KEY_MESSAGE);
        m_pics = readInteger(attributes, KEY_STATS_PICS);
        m_tagged = readInteger(attributes, KEY_STATS_TAGGED);
    }

    @Override
    public JsonObject getAttributes() {
        JsonObject attributes = new JsonObject();

        write(attributes, KEY_LINK, m_link);
        write(attributes, KEY_MESSAGE, m_message);
        write(attributes, KEY_STATS_PICS, m_pics);
        write(attributes, KEY_STATS_TAGGED, m_tagged);

        return attributes;
    }

    public Hyperlink getLink() {
        return m_link;
    }

    public String getMessage() {
        return m_message;
    }

    public int getPicturesCount() {
        return m_pics;
    }

    public int getTaggedCount() {
        return m_tagged;
    }

    @Override
    public boolean equals(Object obj) {
        Profile profile = (Profile)obj;

        if(profile == this) {
            return true;
        }

        if(profile == null ||
           !Objects.match(profile.getLink(), m_link) ||
           !Objects.match(profile.getMessage(), m_message) ||
           profile.getPicturesCount() != m_pics ||
           profile.getTaggedCount() != m_tagged) {
            return false;
        }

        return true;
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
