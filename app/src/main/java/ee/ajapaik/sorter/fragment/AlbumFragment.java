package ee.ajapaik.sorter.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import ee.ajapaik.sorter.R;
import ee.ajapaik.sorter.data.Album;
import ee.ajapaik.sorter.data.Photo;
import ee.ajapaik.sorter.data.util.Status;
import ee.ajapaik.sorter.fragment.util.WebFragment;
import ee.ajapaik.sorter.util.Favorite;
import ee.ajapaik.sorter.util.Objects;
import ee.ajapaik.sorter.util.Settings;
import ee.ajapaik.sorter.util.WebAction;

public class AlbumFragment extends WebFragment {
    private static final String TAG = "AlbumsFragment";
    private static final String KEY_ALBUM = "album";
    private static final String KEY_SELECTED_PHOTO = "selected_photo";
    private static final String KEY_SELECTED_TAG = "selected_tag";
    private static final String KEY_SELECTED_INFO = "selected_info";
    private static final String KEY_ALBUM_IDENTIFIER = "album_id";
    private static final String KEY_PHOTO_IDENTIFIER = "photo_id";

    private Album m_album;
    private List<Favorite> m_favorites;
    private String m_selectedPhoto;
    private Photo.Tag m_selectedTag;
    private boolean m_selectedInfo;
    private Settings m_settings;

    public String getAlbumIdentifier() {
        Bundle arguments = getArguments();

        return (arguments != null) ? arguments.getString(KEY_ALBUM_IDENTIFIER) : null;
    }

    public void setAlbumIdentifier(String albumIdentifier) {
        Bundle arguments = getArguments();

        if(arguments == null) {
            arguments = new Bundle();
        }

        if(albumIdentifier != null) {
            arguments.putString(KEY_ALBUM_IDENTIFIER, albumIdentifier);
        } else {
            arguments.remove(KEY_ALBUM_IDENTIFIER);
        }

        setArguments(arguments);
    }

    public String getPhotoIdentifier() {
        Bundle arguments = getArguments();

        return (arguments != null) ? arguments.getString(KEY_PHOTO_IDENTIFIER) : null;
    }

    public void setPhotoIdentifier(String photoIdentifier) {
        Bundle arguments = getArguments();

        if(arguments == null) {
            arguments = new Bundle();
        }

        if(photoIdentifier != null) {
            arguments.putString(KEY_PHOTO_IDENTIFIER, photoIdentifier);
        } else {
            arguments.remove(KEY_PHOTO_IDENTIFIER);
        }

        setArguments(arguments);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_album, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        m_settings = new Settings(getActivity());
        m_favorites = m_settings.getFavorites();

        getPrevButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrevPhoto();
            }
        });

        getNextButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextPhoto();
            }
        });

        getToggleDetailsButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDetailsShown(!m_selectedInfo);
            }
        });

        getToggleFavoriteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavorite(!isFavorite(m_selectedPhoto));
            }
        });

        getLeftActionButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTag(Photo.TagResult.LEFT);
            }
        });

        getRightActionButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTag(Photo.TagResult.RIGHT);
            }
        });

        getOtherActionButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTag(Photo.TagResult.NOT_APPLICABLE);
            }
        });

        if(savedInstanceState != null) {
            Album album = savedInstanceState.getParcelable(KEY_ALBUM);

            m_selectedPhoto = savedInstanceState.getString(KEY_SELECTED_PHOTO);
            m_selectedTag = Photo.Tag.parse(savedInstanceState.getString(KEY_SELECTED_TAG), null);
            m_selectedInfo = savedInstanceState.getBoolean(KEY_SELECTED_INFO, false);

            setDetailsShown(m_selectedInfo);
            setAlbum(album);
        } else {
            m_selectedPhoto = getPhotoIdentifier();

            setDetailsShown(m_selectedInfo);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelable(KEY_ALBUM, m_album);
        savedInstanceState.putBoolean(KEY_SELECTED_INFO, m_selectedInfo);
        savedInstanceState.putString(KEY_SELECTED_PHOTO, m_selectedPhoto);
        savedInstanceState.putString(KEY_SELECTED_TAG, (m_selectedTag != null) ? m_selectedTag.toString() : null);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(m_album == null) {
            onRefresh(false);
        }
    }

    public Album getAlbum() {
        return m_album;
    }

    public void setAlbum(Album album) {
        if(!Objects.match(m_album, album)) {
            m_album = album;

            if(m_album != null) {
                Photo photo =  (m_selectedPhoto != null) ? m_album.getPhoto(m_selectedPhoto) : null;

                if(photo == null) {
                    photo = m_album.getFirstPhoto();
                    m_selectedPhoto = (photo != null) ? photo.getIdentifier() : null;
                }

                if(photo != null) {
                    invalidatePhoto(photo);
                }
            } else {

            }
        }
    }

    protected void onPrevPhoto() {
        if(m_album != null) {
            Photo photo = m_album.getPrevPhoto(m_selectedPhoto);

            if(photo == null) {
                photo = m_album.getLastPhoto();
            }

            if(photo != null) {
                m_selectedTag = null;
                invalidatePhoto(photo);
            } else {
                // TODO: Exit dialog
            }
        }
    }

    protected void onNextPhoto() {
        if(m_album != null) {
            Photo photo = m_album.getNextPhoto(m_selectedPhoto);

            if(photo == null) {
                photo = m_album.getFirstPhoto();
            }

            if(photo != null) {
                m_selectedTag = null;
                invalidatePhoto(photo);
            } else {
                // TODO: Exit dialog
            }
        }
    }

    protected void setFavorite(boolean flag) {
        Photo photo;

        if(m_album != null && (photo = m_album.getPhoto(m_selectedPhoto)) != null) {
            Favorite favorite = new Favorite(m_album, photo);

            if(flag) {
                m_settings.addFavorite(favorite, m_favorites);
            } else {
                m_settings.removeFavorite(favorite, m_favorites);
            }

            invalidatePhotoFavorite(photo, flag);
        }
    }

    protected void setDetailsShown(boolean flag) {
        m_selectedInfo = flag;

        if(m_selectedInfo) {
            getToggleDetailsButton().setImageResource(R.drawable.ic_highlight_remove_white_36dp);
            getInfoLayout().setVisibility(View.VISIBLE);
            getActionsLayout().setVisibility(View.INVISIBLE);
        } else {
            getToggleDetailsButton().setImageResource(R.drawable.ic_info_outline_white_36dp);
            getInfoLayout().setVisibility(View.GONE);
            getActionsLayout().setVisibility(View.VISIBLE);
        }
    }

    protected void onRefresh(final boolean animated) {
        Context context = getActivity();

        if(m_album == null) {
            getProgressBar().setVisibility(View.VISIBLE);
            getMainLayout().setVisibility(View.GONE);
        }

        getConnection().enqueue(context, (m_album != null) ? Album.createStateAction(context, m_album) : Album.createStateAction(context, getAlbumIdentifier()), new WebAction.ResultHandler<Album>() {
            @Override
            public void onActionResult(Status status, Album album) {
                if(m_album == null) {
                    getProgressBar().setVisibility(View.GONE);
                    getMainLayout().setVisibility(View.VISIBLE);
                }

                if(album != null) {
                    setAlbum(album);
                } else if(m_album == null || animated) {
                    // TODO: Show error alert
                }
            }
        });
    }

    protected void onTag(Photo.TagResult result) {
        Photo photo;

        if(m_album != null && (photo = m_album.getPhoto(m_selectedPhoto)) != null && photo.hasTags()) {
            List<Photo.Tag> tags = photo.getTags();
            Context context = getActivity();
            int index;

            if(m_selectedTag == null || tags.indexOf(m_selectedTag) == -1) {
                m_selectedTag = tags.get(0);
            }

            getConnection().enqueue(context, Album.createTagAction(context, m_album, photo.getIdentifier(), m_selectedTag, result), new WebAction.ResultHandler<Album>() {
                @Override
                public void onActionResult(Status status, Album album) {
                    if(album != null) {
                        setAlbum(album);
                    }
                }
            });

            index = tags.indexOf(m_selectedTag);

            if(index >= 0 && index + 1 < tags.size()) {
                invalidatePhotoTag(photo, tags.get(index + 1));
            } else {
                onNextPhoto();
            }
        }
    }

    private void invalidatePhoto(Photo photo) {
        m_selectedPhoto = photo.getIdentifier();

        getImageView().setImageURI(photo.getImage());
        getTitleView().setText(photo.getTitle());

        if(photo.getSource() != null) {
            getSubtitleView().setText(photo.getSource().toHtml());
        } else if(photo.getAuthor() != null) {
            getSubtitleView().setText(photo.getAuthor());
        } else if(photo.getAuthor() != null) {
            getSubtitleView().setText("");
        }

        invalidatePhotoFavorite(photo, isFavorite(photo.getIdentifier()));
        invalidatePhotoTag(photo, m_selectedTag);
    }

    private void invalidatePhotoFavorite(Photo photo, boolean flag) {
        if(flag) {
            getToggleFavoriteButton().setImageResource(R.drawable.ic_favorite_white_36dp);
        } else {
            getToggleFavoriteButton().setImageResource(R.drawable.ic_favorite_outline_white_36dp);
        }
    }

    private void invalidatePhotoTag(Photo photo, Photo.Tag selectedTag) {
        m_selectedTag = selectedTag;

        if(photo.hasTags()) {
            Resources resources = getActivity().getResources();
            List<Photo.Tag> tags = photo.getTags();

            getToggleDetailsButton().setVisibility(View.VISIBLE);

            if(!m_selectedInfo) {
                setDetailsShown(m_selectedInfo);
            }

            if(m_selectedTag == null || tags.indexOf(m_selectedTag) == -1) {
                m_selectedTag = tags.get(0);
            }

            getLeftActionButton().setCompoundDrawablesWithIntrinsicBounds(0, m_selectedTag.getLeftImageResourceId(), 0, 0);
            getLeftActionButton().setText(resources.getString(m_selectedTag.getLeftTitleResourceId()));
            getRightActionButton().setCompoundDrawablesWithIntrinsicBounds(0, m_selectedTag.getRightImageResourceId(), 0, 0);
            getRightActionButton().setText(resources.getString(m_selectedTag.getRightTitleResourceId()));
        } else {
            getToggleDetailsButton().setVisibility(View.GONE);
            getInfoLayout().setVisibility(View.VISIBLE);
            getActionsLayout().setVisibility(View.GONE);
        }
    }

    private boolean isFavorite(String photoIdentifier) {
        for(Favorite favorite : m_favorites) {
            if(Objects.match(photoIdentifier, favorite.getPhotoIdentifier())) {
                return true;
            }
        }

        return false;
    }

    private View getMainLayout() {
        return getView().findViewById(R.id.layout_main);
    }

    private View getActionsLayout() {
        return getView().findViewById(R.id.layout_details_actions);
    }

    private View getInfoLayout() {
        return getView().findViewById(R.id.layout_details_info);
    }

    private ProgressBar getProgressBar() {
        return (ProgressBar)getView().findViewById(R.id.progress_bar);
    }

    private ImageView getImageView() {
        return (ImageView)getView().findViewById(R.id.image);
    }

    private TextView getTitleView() {
        return (TextView)getView().findViewById(R.id.text_title);
    }

    private TextView getSubtitleView() {
        return (TextView)getView().findViewById(R.id.text_subtitle);
    }

    private ImageButton getPrevButton() {
        return (ImageButton)getView().findViewById(R.id.button_prev);
    }

    private ImageButton getNextButton() {
        return (ImageButton)getView().findViewById(R.id.button_next);
    }

    private ImageButton getToggleDetailsButton() {
        return (ImageButton)getView().findViewById(R.id.button_details);
    }

    private ImageButton getToggleFavoriteButton() {
        return (ImageButton)getView().findViewById(R.id.button_favorite);
    }

    private Button getLeftActionButton() {
        return (Button)getView().findViewById(R.id.button_action_left);
    }

    private Button getRightActionButton() {
        return (Button)getView().findViewById(R.id.button_action_right);
    }

    private Button getOtherActionButton() {
        return (Button)getView().findViewById(R.id.button_action_other);
    }
}
