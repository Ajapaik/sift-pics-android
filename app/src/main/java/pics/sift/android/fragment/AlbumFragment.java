package pics.sift.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import pics.sift.android.BuildConfig;
import pics.sift.android.R;
import pics.sift.android.data.Album;
import pics.sift.android.data.Favorite;
import pics.sift.android.data.Hyperlink;
import pics.sift.android.data.Photo;
import pics.sift.android.data.Profile;
import pics.sift.android.data.util.Status;
import pics.sift.android.fragment.util.WebFragment;
import pics.sift.android.util.Objects;
import pics.sift.android.util.Settings;
import pics.sift.android.util.WebAction;
import pics.sift.android.util.WebImage;
import pics.sift.android.widget.WebImageView;
import pics.sift.android.widget.util.OnSwipeTouchListener;

public class AlbumFragment extends WebFragment {
    private static final String TAG = "AlbumsFragment";
    private static final String KEY_ALBUM = "album";
    private static final String KEY_IMMERSIVE_MODE = "immersive_mode";
    private static final String KEY_SELECTED_PHOTO = "selected_photo";
    private static final String KEY_SELECTED_PHOTO_LOADED = "selected_photo_loaded";
    private static final String KEY_SELECTED_TAG = "selected_tag";
    private static final String KEY_SELECTED_INFO = "selected_info";
    private static final String KEY_ALBUM_IDENTIFIER = "album_id";
    private static final String KEY_PHOTO_IDENTIFIER = "photo_id";

    private static final int THUMBNAIL_SIZE = 400;

    private Album m_album;
    private Profile m_profile;
    private boolean m_immersiveMode;
    private String m_selectedPhoto;
    private boolean m_selectedPhotoLoaded;
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
        m_profile = m_settings.getProfile();

        if(m_profile == null) {
            m_profile = new Profile();
        }

        getImageView().setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeLeft() {
                onNextPhoto();
            }

            @Override
            public void onSwipeRight() {
                onPrevPhoto();
            }

            @Override
            public void onSingleTap() {
                setImmersiveMode(!m_immersiveMode);
            }
        });
        getImageView().setOnLoadListener(new WebImageView.OnLoadListener() {
            @Override
            public void onImageLoaded() {
                invalidatePhotoActions(true);
            }

            @Override
            public void onImageUnloaded() {
                invalidatePhotoActions(false);
            }

            @Override
            public void onImageFailed() {
            }
        });

        getSubtitleView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_album != null) {
                    Photo photo = m_album.getPhoto(m_selectedPhoto);

                    if(photo != null) {
                        Hyperlink link = photo.getSource();

                        if(link != null && link.getURL() != null) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);

                            intent.setData(link.getURL());
                            startActivity(intent);
                        }
                    }
                }
            }
        });

        getPrevButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrevPhoto();
            }
        });
        getPrevButton().setVisibility(View.GONE);

        getNextButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextPhoto();
            }
        });
        getNextButton().setVisibility(View.GONE);

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

            m_immersiveMode = savedInstanceState.getBoolean(KEY_IMMERSIVE_MODE);
            m_selectedPhoto = savedInstanceState.getString(KEY_SELECTED_PHOTO);
            m_selectedPhotoLoaded = savedInstanceState.getBoolean(KEY_SELECTED_PHOTO_LOADED);
            m_selectedTag = Photo.Tag.parse(savedInstanceState.getString(KEY_SELECTED_TAG), null);
            m_selectedInfo = savedInstanceState.getBoolean(KEY_SELECTED_INFO, false);

            setDetailsShown(m_selectedInfo);
            setAlbum(album);
        } else {
            m_selectedPhoto = getPhotoIdentifier();

            setDetailsShown(m_selectedInfo);
        }

        invalidatePhotoActions(m_selectedPhotoLoaded);
        setImmersiveMode(m_immersiveMode);

        // Re-syncronize unsynchronized favorites
        for(Favorite favorite : m_profile.getLocalFavorites()) {
            if(BuildConfig.DEBUG) {
                Log.d(TAG, (favorite.isObsolete()) ? "Re-deleting favorite for " + favorite.getPhotoIdentifier() : "Re-adding favorite for " + favorite.getPhotoIdentifier());
            }

            // Remote synchronization
            getConnection().enqueue(getActivity(), Profile.createFavoriteAction(getActivity(), m_profile, favorite, !favorite.isObsolete()), new WebAction.ResultHandler<Profile>() {
                @Override
                public void onActionResult(Status status, Profile profile) {
                    if(profile != null) {
                        m_settings.setProfile(profile);
                        m_profile = profile;
                    }
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelable(KEY_ALBUM, m_album);
        savedInstanceState.putBoolean(KEY_IMMERSIVE_MODE, m_immersiveMode);
        savedInstanceState.putBoolean(KEY_SELECTED_INFO, m_selectedInfo);
        savedInstanceState.putString(KEY_SELECTED_PHOTO, m_selectedPhoto);
        savedInstanceState.putBoolean(KEY_SELECTED_PHOTO_LOADED, m_selectedPhotoLoaded);
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
                Photo photo = (m_selectedPhoto != null) ? m_album.getPhoto(m_selectedPhoto) : null;

                getActionBar().setTitle(m_album.getTitle());

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

            // Local synchronization
            m_profile = (flag) ? m_settings.addFavorite(favorite, m_profile) : m_settings.removeFavorite(favorite, m_profile);

            // Remote synchronization
            getConnection().enqueue(getActivity(), Profile.createFavoriteAction(getActivity(), m_profile, favorite, flag), new WebAction.ResultHandler<Profile>() {
                @Override
                public void onActionResult(Status status, Profile profile) {
                    if(profile != null) {
                        m_settings.setProfile(profile);
                        m_profile = profile;
                    }
                }
            });

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

    protected void setImmersiveMode(boolean flag) {
        m_immersiveMode = flag;

        if(m_immersiveMode) {
            getOverlayImageLayout().setVisibility(View.INVISIBLE);
            getOverlayDetailsLayout().setVisibility(View.INVISIBLE);
        } else {
            getOverlayImageLayout().setVisibility(View.VISIBLE);
            getOverlayDetailsLayout().setVisibility(View.VISIBLE);
        }
    }

    protected void onRefresh(final boolean animated) {
        Context context = getActivity();

        if(m_album == null) {
            getProgressBar().setVisibility(View.VISIBLE);
            getMainLayout().setVisibility(View.GONE);
        }

        // Load favorites if the app is launched for the first time or the profile is too old.
        if(m_profile == null || m_profile.isObsolete()) {
            getConnection().enqueue(context, Profile.createAction(context, m_profile), new WebAction.ResultHandler<Profile>() {
                @Override
                public void onActionResult(Status status, Profile profile) {
                    if(profile != null) {
                        m_settings.setProfile(profile);
                        m_profile = profile;

                        onRefresh(animated);
                    } else if(animated) {
                        // TODO: Show error alert
                    }
                }
            });
            return;
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

        getImageView().setImageURI(photo.getThumbnail(THUMBNAIL_SIZE));
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
        preloadNextPhoto();
    }

    private void invalidatePhotoActions(boolean flag) {
        m_selectedPhotoLoaded = flag;

        getLeftActionButton().setEnabled(m_selectedPhotoLoaded);
        getRightActionButton().setEnabled(m_selectedPhotoLoaded);
        getOtherActionButton().setEnabled(m_selectedPhotoLoaded);
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

    private void preloadNextPhoto() {
        Photo photo = m_album.getNextPhoto(m_selectedPhoto);

        if(photo != null) {
            Context context = getActivity();

            getConnection().enqueue(context, new WebImage(context, photo.getThumbnail(THUMBNAIL_SIZE).toString()), null);
        }
    }

    private boolean isFavorite(String photoIdentifier) {
        Favorite favorite;

        return (m_profile != null && (favorite = m_profile.getFavorite(null, photoIdentifier)) != null && !favorite.isObsolete()) ? true : false;
    }

    private View getMainLayout() {
        return getView().findViewById(R.id.layout_main);
    }

    private View getOverlayImageLayout() {
        return getView().findViewById(R.id.layout_image);
    }

    private View getOverlayDetailsLayout() {
        return getView().findViewById(R.id.layout_details);
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

    private WebImageView getImageView() {
        return (WebImageView)getView().findViewById(R.id.image);
    }

    private TextView getTitleView() {
        return (TextView)getView().findViewById(R.id.text_title);
    }

    private Button getSubtitleView() {
        return (Button)getView().findViewById(R.id.button_subtitle);
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
