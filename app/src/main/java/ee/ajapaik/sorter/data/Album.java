package ee.ajapaik.sorter.data;

import android.content.Context;
import android.net.Uri;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import ee.ajapaik.sorter.data.util.Model;
import ee.ajapaik.sorter.util.Objects;
import ee.ajapaik.sorter.util.WebAction;

public class Album extends Model {
    private static final String KEY_IDENTIFIER = "id";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBTITLE = "subtitle";
    private static final String KEY_TAGGED = "tagged";
    private static final String KEY_STATE = "state";
    private static final String KEY_PHOTOS = "photos";
    private static final String KEY_PHOTOS_ADD = "photos+";
    private static final String KEY_PHOTOS_REMOVE = "photos-";

    public static WebAction<Album> createStateAction(Context context, Album album) {
        Map<String, String> parameters = new Hashtable<String, String>();

        parameters.put("id", album.getIdentifier());

        if(album.getState() != null) {
            parameters.put("state", album.getState());
        }

        return new Action(context, "/album/state/", parameters, album, album.getIdentifier());
    }

    public static WebAction<Album> createStateAction(Context context, String albumIdentifier) {
        Map<String, String> parameters = new Hashtable<String, String>();

        parameters.put("id", albumIdentifier);

        return new Action(context, "/album/state/", parameters, null, albumIdentifier);
    }

    public static WebAction<Album> createTagAction(Context context, Album album, String photoIdentifier, Photo.Tag tag, Photo.TagResult result) {
        Map<String, String> parameters = new Hashtable<String, String>();

        parameters.put("id", album.getIdentifier());
        parameters.put("photo", photoIdentifier);
        parameters.put("tag", tag.toString());
        parameters.put("value", Integer.toString(result.getCode()));

        if(album.getState() != null) {
            parameters.put("state", album.getState());
        }

        return new Action(context, "/album/tag/", parameters, album, album.getIdentifier());
    }

    public static Album parse(String str) {
        return CREATOR.parse(str);
    }

    private String m_identifier;
    private Uri m_image;
    private String m_title;
    private String m_subtitle;
    private boolean m_tagged;
    private String m_state;
    private List<Photo> m_photos;

    public Album(JsonObject attributes) {
        this(attributes, null, null);
    }

    public Album(JsonObject attributes, Album baseAlbum, String baseIdentifier) {
        JsonElement element = attributes.get(KEY_PHOTOS);

        m_identifier = readIdentifier(attributes, KEY_IDENTIFIER);
        m_image = readUri(attributes, KEY_IMAGE, (baseAlbum != null) ? baseAlbum.getImage() : null);
        m_title = readString(attributes, KEY_TITLE, (baseAlbum != null) ? baseAlbum.getTitle() : null);
        m_subtitle = readString(attributes, KEY_SUBTITLE, (baseAlbum != null) ? baseAlbum.getSubtitle() : null);
        m_tagged = readBoolean(attributes, KEY_TAGGED, (baseAlbum != null) ? baseAlbum.isTagged() : false);
        m_state = readString(attributes, KEY_STATE, (baseAlbum != null) ? baseAlbum.getState() : null);
        m_photos = new ArrayList<Photo>();

        if(m_identifier == null && baseIdentifier != null) {
            m_identifier = baseIdentifier;
        }

        if(element != null) {
            if(element.isJsonArray()) {
                for(JsonElement photoElement : element.getAsJsonArray()) {
                    if(photoElement.isJsonObject()) {
                        try {
                            m_photos.add(new Photo(photoElement.getAsJsonObject()));
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if(!element.isJsonNull() && baseAlbum != null) {
                List<Photo> photos = baseAlbum.getPhotos();

                if(photos != null && photos.size() > 0) {
                    for(Photo photo : photos) {
                        m_photos.add(photo);
                    }
                }
            }
        }

        for(JsonElement photoToRemoveElement : readArray(attributes, KEY_PHOTOS_REMOVE)) {
            if(photoToRemoveElement.isJsonPrimitive()) {
                JsonPrimitive photoPrimitive = photoToRemoveElement.getAsJsonPrimitive();
                Photo photo = null;

                if(photoPrimitive.isString()) {
                    photo = getPhoto(photoPrimitive.getAsString());
                } else if(photoPrimitive.isNumber()) {
                    photo = getPhoto(photoPrimitive.toString());
                }

                if(photo != null) {
                    m_photos.remove(photo);
                }
            }
        }

        for(JsonElement photoToAddElement : readArray(attributes, KEY_PHOTOS_ADD)) {
            if(photoToAddElement.isJsonObject()) {
                try {
                    JsonObject photoObject = photoToAddElement.getAsJsonObject();
                    Photo oldPhoto = getPhoto(readIdentifier(photoObject, KEY_IDENTIFIER));
                    Photo newPhoto = new Photo(photoObject, oldPhoto);

                    if(oldPhoto == null) {
                        m_photos.add(newPhoto);
                    } else if(!Objects.match(oldPhoto, newPhoto)) {
                        m_photos.set(m_photos.indexOf(oldPhoto), newPhoto);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

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

        if(m_photos != null && m_photos.size() > 0) {
            JsonArray array = new JsonArray();

            for(Photo photo : m_photos) {
                array.add(new JsonPrimitive(photo.toString()));
            }

            attributes.add(KEY_PHOTOS, array);
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

    public String getSubtitle() {
        return m_subtitle;
    }

    public boolean isTagged() {
        return m_tagged;
    }

    public String getState() {
        return m_state;
    }

    public Photo getFirstPhoto() {
        if(m_photos != null) {
            for(Photo photo : m_photos) {
                if(photo.hasTags()) {
                    return photo;
                }
            }

            if(m_photos.size() > 0) {
                return m_photos.get(0);
            }
        }

        return null;
    }

    public Photo getLastPhoto() {
        if(m_photos != null) {
            for(int i = m_photos.size() - 1; i >= 0; i--) {
                Photo photo = m_photos.get(i);

                if(photo.hasTags()) {
                    return photo;
                }
            }

            if(m_photos.size() > 0) {
                return m_photos.get(m_photos.size() - 1);
            }
        }

        return null;
    }

    public Photo getPrevPhoto(String identifier) {
        if(identifier != null && m_photos != null) {
            for(int i = 0, c = m_photos.size(); i < c; i++) {
                Photo photo = m_photos.get(i);

                if(photo.getIdentifier().equals(identifier)) {
                    return (i > 0) ? m_photos.get(i - 1) : null;
                }
            }
        }

        return null;
    }

    public Photo getNextPhoto(String identifier) {
        if(identifier != null && m_photos != null) {
            for(int i = 0, c = m_photos.size(); i < c; i++) {
                Photo photo = m_photos.get(i);

                if(photo.getIdentifier().equals(identifier)) {
                    return (i + 1 < c) ? m_photos.get(i + 1) : null;
                }
            }
        }

        return null;
    }

    public Photo getPhoto(String identifier) {
        if(identifier != null && m_photos != null) {
            for(Photo photo : m_photos) {
                if(photo.getIdentifier().equals(identifier)) {
                    return photo;
                }
            }
        }

        return null;
    }

    public List<Photo> getPhotos() {
        return m_photos;
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
           !Objects.match(album.getState(), m_state) ||
           !Objects.match(album.getPhotos(), m_photos) ||
           album.isTagged() != m_tagged) {
            return false;
        }

        return true;
    }

    private static class Action extends WebAction<Album> {
        private Album m_baseAlbum;
        private String m_baseIdentifier;

        public Action(Context context, String path, Map<String, String> parameters, Album baseAlbum, String baseIdentifier) {
            super(context, path, parameters, CREATOR);
            m_baseAlbum = baseAlbum;
            m_baseIdentifier = baseIdentifier;
        }

        @Override
        public String getUniqueId() {
            return getUrl() + m_baseIdentifier + "/" + ((m_baseAlbum != null && m_baseAlbum.getState() != null) ? m_baseAlbum.getState() : "");
        }

        @Override
        protected Album parseObject(JsonObject attributes) {
            return new Album(attributes, m_baseAlbum, m_baseIdentifier);
        }
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
