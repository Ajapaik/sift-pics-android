package pics.sift.android.data;

import android.net.Uri;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Comparator;
import java.util.Date;

import pics.sift.android.data.util.Model;
import pics.sift.android.util.Dates;
import pics.sift.android.util.Objects;

public class Favorite extends Model {
    private static final String KEY_IDENTIFIER = "id";
    private static final String KEY_ALBUM_IDENTIFIER = "album_id";
    private static final String KEY_PHOTO_IDENTIFIER = "photo_id";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_DATE = "date";
    private static final String KEY_TITLE = "title";

    public static Favorite parse(String str) {
        return CREATOR.parse(str);
    }

    private String m_identifier;
    private String m_albumIdentifier;
    private String m_photoIdentifier;
    private Uri m_image;
    private Date m_date;
    private String m_title;

    public Favorite(Album album, Photo photo) {
        m_albumIdentifier = album.getIdentifier();
        m_photoIdentifier = photo.getIdentifier();
        m_image = photo.getImage();
        m_date = new Date();
        m_title = photo.getTitle();
    }

    public Favorite(JsonObject attributes) {
        this(attributes, null);
    }

    public Favorite(JsonObject attributes, Favorite baseFavorite) {
        JsonPrimitive primitive;

        m_identifier = readIdentifier(attributes, KEY_IDENTIFIER);
        m_albumIdentifier = readIdentifier(attributes, KEY_ALBUM_IDENTIFIER, (baseFavorite != null) ? baseFavorite.getAlbumIdentifier() : null);
        m_photoIdentifier = readIdentifier(attributes, KEY_PHOTO_IDENTIFIER, (baseFavorite != null) ? baseFavorite.getPhotoIdentifier() : null);
        m_image = readUri(attributes, KEY_IMAGE, (baseFavorite != null) ? baseFavorite.getImage() : null);
        m_date = Dates.parse(readString(attributes, KEY_DATE));
        m_title = readString(attributes, KEY_TITLE, (baseFavorite != null) ? baseFavorite.getTitle() : null);

        if(m_date == null && baseFavorite != null) {
            m_date = baseFavorite.getDate();
        }

        if(m_albumIdentifier == null || m_photoIdentifier == null) {
            throw new IllegalArgumentException();
        }
    }

    protected Favorite(Favorite favorite) {
        m_identifier = favorite.getIdentifier();
        m_albumIdentifier = favorite.getAlbumIdentifier();
        m_photoIdentifier = favorite.getPhotoIdentifier();
        m_image = favorite.getImage();
        m_date = favorite.getDate();
        m_title = favorite.getTitle();
    }

    public JsonObject getAttributes() {
        JsonObject attributes = new JsonObject();

        write(attributes, KEY_IDENTIFIER, m_identifier);
        write(attributes, KEY_IMAGE, m_image);
        write(attributes, KEY_ALBUM_IDENTIFIER, m_albumIdentifier);
        write(attributes, KEY_PHOTO_IDENTIFIER, m_photoIdentifier);
        write(attributes, KEY_DATE, Dates.toString(m_date));
        write(attributes, KEY_TITLE, m_title);

        return attributes;
    }

    public String getAlbumIdentifier() {
        return m_albumIdentifier;
    }

    public String getPhotoIdentifier() {
        return m_photoIdentifier;
    }

    public String getIdentifier() {
        return m_identifier;
    }

    public Uri getImage() {
        return m_image;
    }

    public Uri getThumbnail(int preferredDimension) {
        return Photo.resolve(m_image, preferredDimension);
    }

    public Date getDate() {
        return m_date;
    }

    public String getTitle() {
        return m_title;
    }

    public boolean isObsolete() {
        return (m_image == null) ? true : false;
    }

    public Favorite getObsolete() {
        if(m_image != null) {
            Favorite obsolete = new Favorite(this);

            obsolete.m_image = null;
            obsolete.m_title = null;

            return obsolete;
        }

        return this;
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
           !Objects.match(favorite.getIdentifier(), m_identifier) ||
           !Objects.match(favorite.getAlbumIdentifier(), m_albumIdentifier) ||
           !Objects.match(favorite.getPhotoIdentifier(), m_photoIdentifier) ||
           !Objects.match(favorite.getImage(), m_image) ||
           !Objects.match(favorite.getTitle(), m_title) ||
           !Objects.match(favorite.getDate(), m_date)) {
            return false;
        }

        return true;
    }

    public static class FavoriteComparator implements Comparator<Favorite> {
        public int compare(Favorite a, Favorite b) {
            long aL = a.m_date.getTime();
            long bL = b.m_date.getTime();

            if(aL > bL) {
                return -1;
            }

            if(aL < bL) {
                return 1;
            }

            return 0;
        }
    }

    public static final Model.Creator<Favorite> CREATOR = new Model.Creator<Favorite>() {
        @Override
        public Favorite newInstance(JsonObject attributes) {
            return new Favorite(attributes);
        }

        @Override
        public Favorite[] newArray(int size) {
            return new Favorite[size];
        }
    };
}
