package pics.sift.app.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;

public class Registration {
    private static final String KEY_CODE = "code";
    private static final String KEY_VERSION = "version";

    public static Registration parse(int packageVersion, String str) {
        if(str != null) {
            try {
                JsonElement element = new JsonParser().parse(new JsonReader(new StringReader(str)));

                if(element.isJsonObject()) {
                    return new Registration(packageVersion, element.getAsJsonObject());
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private int m_packageVersion;
    private String m_code;
    private int m_version;

    public Registration(int packageVersion, JsonObject attributes) {
        JsonPrimitive primitive;

        m_packageVersion = packageVersion;
        m_code = ((primitive = attributes.getAsJsonPrimitive(KEY_CODE)) != null && primitive.isString()) ? primitive.getAsString() : "";
        m_version = ((primitive = attributes.getAsJsonPrimitive(KEY_VERSION)) != null && primitive.isNumber()) ? primitive.getAsInt() : Integer.MIN_VALUE;
    }

    public Registration(int packageVersion, String code) {
        this(packageVersion, code, packageVersion);
    }

    protected Registration(int packageVersion, String code, int version) {
        m_packageVersion = packageVersion;
        m_code = (code != null) ? code : "";
        m_version = version;
    }

    public JsonObject getAttributes() {
        JsonObject attributes = new JsonObject();

        attributes.addProperty(KEY_CODE, m_code);
        attributes.addProperty(KEY_VERSION, m_version);

        return attributes;
    }

    public String getCode() {
        return m_code;
    }

    public int getVersion() {
        return m_version;
    }

    public boolean isExpired() {
        return (m_version != m_packageVersion) ? true : false;
    }

    public boolean isValid() {
        return (m_code != null && m_code.length() > 0) ? true : false;
    }

    public Registration toExpired() {
        return new Registration(m_packageVersion, m_code, Integer.MIN_VALUE);
    }

    @Override
    public String toString() {
        return getAttributes().toString();
    }

    @Override
    public boolean equals(Object obj) {
        Registration registration = (Registration)obj;

        if(registration == this) {
            return true;
        }

        if(registration == null ||
           !Objects.match(m_code, registration.getCode()) ||
           m_version != registration.getVersion()) {
            return false;
        }

        return true;
    }
}
