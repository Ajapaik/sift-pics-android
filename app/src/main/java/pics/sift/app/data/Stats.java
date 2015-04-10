package pics.sift.app.data;

import com.google.gson.JsonObject;

import pics.sift.app.data.util.Model;

public class Stats extends Model {
    private static final String KEY_USERS = "users";
    private static final String KEY_DECISIONS = "decisions";
    private static final String KEY_TAGGED = "tagged";
    private static final String KEY_RANK = "rank";

    public static Stats parse(String str) {
        return CREATOR.parse(str);
    }

    private int m_users;
    private int m_decisions;
    private int m_tagged;
    private int m_rank;

    public Stats() {
        m_users = 0;
        m_decisions = 0;
        m_tagged = 0;
        m_rank = 0;
    }

    public Stats(JsonObject attributes) {
        m_users = readInteger(attributes, KEY_USERS);
        m_decisions = readInteger(attributes, KEY_DECISIONS);
        m_tagged = readInteger(attributes, KEY_TAGGED);
        m_rank = readInteger(attributes, KEY_RANK);
    }

    @Override
    public JsonObject getAttributes() {
        JsonObject attributes = new JsonObject();

        attributes.addProperty(KEY_USERS, m_users);
        attributes.addProperty(KEY_DECISIONS, m_decisions);
        attributes.addProperty(KEY_TAGGED, m_tagged);
        attributes.addProperty(KEY_RANK, m_rank);

        return attributes;
    }

    public int getUsersCount() {
        return m_users;
    }

    public int getDecisionsCount() {
        return m_decisions;
    }

    public int getTaggedCount() {
        return m_tagged;
    }

    public int getRank() {
        return m_rank;
    }

    public boolean empty() {
        return (m_users == 0 && m_decisions == 0 && m_tagged == 0 && m_rank == 0) ? true : false;
    }

    @Override
    public boolean equals(Object obj) {
        Stats stats = (Stats)obj;

        if(stats == this) {
            return true;
        }

        if(stats == null ||
           stats.getUsersCount() != m_users ||
           stats.getDecisionsCount() != m_decisions ||
           stats.getTaggedCount() != m_tagged ||
           stats.getRank() != m_rank) {
            return false;
        }

        return true;
    }

    public static final Model.Creator<Stats> CREATOR = new Model.Creator<Stats>() {
        @Override
        public Stats newInstance(JsonObject attributes) {
            return new Stats(attributes);
        }

        @Override
        public Stats[] newArray(int size) {
            return new Stats[size];
        }
    };
}
