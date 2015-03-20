package ee.ajapaik.sorter.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private static final String KEY_SELECTED_FAVORITE = "selected_favorite";
    private static final String KEY_SELECTED_PHOTO = "selected_photo";
    private static final String KEY_SELECTED_TAG = "selected_tag";
    private static final String KEY_SELECTED_INFO = "selected_info";
    private static final String KEY_ALBUM_IDENTIFIER = "album_id";
    private static final String KEY_PHOTO_IDENTIFIER = "photo_id";

    private Album m_album;
    private boolean m_selectedFavorite;
    private String m_selectedPhoto;
    private int m_selectedTag;
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
                setFavorite(!m_selectedFavorite);
            }
        });

        getLeftActionButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        getRightActionButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        getOtherActionButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if(savedInstanceState != null) {
            Album album = savedInstanceState.getParcelable(KEY_ALBUM);

            m_selectedFavorite = savedInstanceState.getBoolean(KEY_SELECTED_FAVORITE);
            m_selectedPhoto = savedInstanceState.getString(KEY_SELECTED_PHOTO);
            m_selectedTag = savedInstanceState.getInt(KEY_SELECTED_TAG, 0);
            m_selectedInfo = savedInstanceState.getBoolean(KEY_SELECTED_INFO, false);

            setAlbum(album);
        } else {
            setDetailsShown(m_selectedInfo);
        }

        setFavorite(m_selectedFavorite);
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelable(KEY_ALBUM, m_album);
        savedInstanceState.putBoolean(KEY_SELECTED_FAVORITE, m_selectedFavorite);
        savedInstanceState.putBoolean(KEY_SELECTED_INFO, m_selectedInfo);
        savedInstanceState.putString(KEY_SELECTED_PHOTO, m_selectedPhoto);
        savedInstanceState.putInt(KEY_SELECTED_TAG, m_selectedTag);
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
                invalidatePhoto(photo);
            } else {
                // TODO: Exit dialog
            }
        }
    }

    protected void setFavorite(boolean flag) {
        Photo photo;

        m_selectedFavorite = flag;

        if(m_selectedFavorite) {
            getToggleFavoriteButton().setImageResource(R.drawable.ic_favorite_white_36dp);
        } else {
            getToggleFavoriteButton().setImageResource(R.drawable.ic_favorite_outline_white_36dp);
        }

        if(m_album != null && (photo = m_album.getPhoto(m_selectedPhoto)) != null) {
            Favorite favorite = new Favorite(m_album, photo);

            if(m_selectedFavorite) {
                m_settings.addFavorite(favorite);
            } else {
                m_settings.removeFavorite(favorite);
            }
        }
    }

    protected void setDetailsShown(boolean flag) {
        m_selectedInfo = flag;

        if(m_selectedInfo) {
            getToggleDetailsButton().setImageResource(R.drawable.ic_highlight_remove_white_36dp);
            getInfoLayout().setVisibility(View.VISIBLE);
            getActionsLayout().setVisibility(View.GONE);
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

        invalidatePhotoTag(photo, m_selectedTag);
    }

    private void invalidatePhotoTag(Photo photo, int selectedTag) {
        m_selectedTag = selectedTag;

        if(photo.hasTags()) {
            List<Photo.Tag> tags = photo.getTags();
            Photo.Tag tag;

            getToggleDetailsButton().setVisibility(View.VISIBLE);

            if(!m_selectedInfo) {
                setDetailsShown(m_selectedInfo);
            }

            if(m_selectedTag < 0 || m_selectedTag >= tags.size()) {
                m_selectedTag = 0;
            }

            if((tag = tags.get(m_selectedTag)) != null) {
                getLeftActionButton().setImageResource(tag.getLeftImageResourceId());
                getRightActionButton().setImageResource(tag.getRightImageResourceId());
            }
        } else {
            getToggleDetailsButton().setVisibility(View.GONE);
            getInfoLayout().setVisibility(View.VISIBLE);
            getActionsLayout().setVisibility(View.GONE);
        }
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

    private ImageButton getLeftActionButton() {
        return (ImageButton)getView().findViewById(R.id.button_action_left);
    }

    private ImageButton getRightActionButton() {
        return (ImageButton)getView().findViewById(R.id.button_action_right);
    }

    private ImageButton getOtherActionButton() {
        return (ImageButton)getView().findViewById(R.id.button_action_other);
    }
}
