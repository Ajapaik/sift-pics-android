package ee.ajapaik.sorter.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import ee.ajapaik.sorter.R;
import ee.ajapaik.sorter.data.Album;
import ee.ajapaik.sorter.data.util.Status;
import ee.ajapaik.sorter.fragment.util.WebFragment;
import ee.ajapaik.sorter.util.Objects;
import ee.ajapaik.sorter.util.WebAction;

public class AlbumFragment extends WebFragment {
    private static final String TAG = "AlbumsFragment";
    private static final String KEY_ALBUM = "album";
    private static final String KEY_ALBUM_IDENTIFIER = "album_id";

    private Album m_album;

    public String getAlbumIdentifier() {
        Bundle arguments = getArguments();

        return (arguments != null) ? arguments.getString(KEY_ALBUM_IDENTIFIER) : null;
    }

    public void setAlbumIdentifier(String albumIdentifier) {
        if(albumIdentifier != null) {
            Bundle arguments = new Bundle();

            arguments.putString(KEY_ALBUM_IDENTIFIER, albumIdentifier);
            setArguments(arguments);
        } else {
            setArguments(null);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_albums, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null) {
            Album album = savedInstanceState.getParcelable(KEY_ALBUM);

            setAlbum(album);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelable(KEY_ALBUM, m_album);
    }

    @Override
    public void onStart() {
        super.onStart();
        onRefresh(false);
    }

    public Album getAlbum() {
        return m_album;
    }

    public void setAlbum(Album album) {
        if(!Objects.match(m_album, album)) {
            m_album = album;

            if(m_album != null) {

            } else {

            }
        }
    }

    protected void onRefresh(final boolean animated) {
        Context context = getActivity();

        if(m_album == null) {
            getProgressBar().setVisibility(View.VISIBLE);
        }

        getConnection().enqueue(context, (m_album != null) ? Album.createStateAction(context, m_album) : Album.createStateAction(context, getAlbumIdentifier()), new WebAction.ResultHandler<Album>() {
            @Override
            public void onActionResult(Status status, Album album) {
                if(m_album == null) {
                    getProgressBar().setVisibility(View.GONE);
                }

                if(album != null) {
                    setAlbum(album);
                } else if(m_album == null || animated) {
                    // TODO: Show error alert
                }
            }
        });
    }

    private ProgressBar getProgressBar() {
        return (ProgressBar)getView().findViewById(R.id.progress_bar);
    }
}
