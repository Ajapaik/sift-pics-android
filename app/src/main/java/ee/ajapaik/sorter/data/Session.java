package ee.ajapaik.sorter.data;

import com.google.gson.JsonObject;

import java.util.Date;

import ee.ajapaik.sorter.data.util.Model;
import ee.ajapaik.sorter.util.Objects;

public class Session extends Model {
    private static final String KEY_EXPIRES = "expires.abs";
    private static final String KEY_SESSION_LENGTH = "expires";
    private static final String KEY_SESSION = "session";
    private static final String KEY_USER = "user";

    private static final int DEFAULT_SESSION_LENGTH_IN_SECONDS = 60;

    public static Session parse(String str) {
        return CREATOR.parse(str);
    }

    private long m_expires;
    private String m_token;
    private String m_user;

    public Session(JsonObject attributes) {
        long expires = readLong(attributes, KEY_EXPIRES);

        m_token = readString(attributes, KEY_SESSION);
        m_user = readIdentifier(attributes, KEY_USER);

        if(expires == 0) {
            expires = readInteger(attributes, KEY_SESSION_LENGTH);
            m_expires = new Date().getTime() + ((expires > 0) ? expires : DEFAULT_SESSION_LENGTH_IN_SECONDS) * 1000;
        } else {
            m_expires = expires;
        }

        if(m_user == null || m_token == null) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public JsonObject getAttributes() {
        JsonObject attributes = new JsonObject();

        if(m_token != null) {
            attributes.addProperty(KEY_SESSION, m_token);
        }

        if(m_user != null) {
            attributes.addProperty(KEY_USER, m_user);
        }

        attributes.addProperty(KEY_EXPIRES, m_expires);

        return attributes;
    }

    public long getExpires() {
        return m_expires;
    }

    public String getToken() {
        return m_token;
    }

    public String getUser() {
        return m_user;
    }

    public boolean isExpired() {
        return (new Date().getTime() > m_expires) ? true : false;
    }

    @Override
    public boolean equals(Object obj) {
        Session session = (Session)obj;

        if(session == this) {
            return true;
        }

        if(session == null ||
           session.getExpires() != m_expires ||
           !Objects.match(session.getToken(), m_token) ||
           !Objects.match(session.getUser(), m_user)) {
            return false;
        }

        return true;
    }

    public static final Model.Creator<Session> CREATOR = new Model.Creator<Session>() {
        @Override
        public Session newInstance(JsonObject attributes) {
            return new Session(attributes);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };
}
