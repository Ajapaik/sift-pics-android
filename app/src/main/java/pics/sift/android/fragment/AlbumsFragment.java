package pics.sift.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import pics.sift.android.AlbumActivity;
import pics.sift.android.R;
import pics.sift.android.adapter.AlbumAdapter;
import pics.sift.android.data.Album;
import pics.sift.android.data.Feed;
import pics.sift.android.data.util.Status;
import pics.sift.android.fragment.util.WebFragment;
import pics.sift.android.util.Objects;
import pics.sift.android.util.WebAction;

public class AlbumsFragment extends WebFragment {
    private static final String TAG = "AlbumsFragment";
    private static final String KEY_FEED = "feed";

    private Feed m_feed;

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
        ListView listView;

        super.onActivityCreated(savedInstanceState);

        listView = getListView();
        listView.setEmptyView(getEmptyView());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Album album = (Album)parent.getItemAtPosition(position);

                AlbumActivity.start(getActivity(), album);
            }
        });

        if(savedInstanceState != null) {
            Feed feed = savedInstanceState.getParcelable(KEY_FEED);

            setFeed(feed);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_refresh) {
            onRefresh(true);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelable(KEY_FEED, m_feed);
    }

    @Override
    public void onStart() {
        super.onStart();
        onRefresh(false);
    }

    public Feed getFeed() {
        return m_feed;
    }

    public void setFeed(Feed feed) {
        if(!Objects.match(m_feed, feed)) {
            ListView listView = getListView();

            m_feed = feed;

            if(m_feed != null) {
                listView.setAdapter(new AlbumAdapter(listView.getContext(), m_feed.getAlbums()));
            } else {
                getEmptyView().setText(R.string.album_label_no_data);
                listView.setAdapter(null);
            }
        }
    }

    protected void onRefresh(final boolean animated) {
        Context context = getActivity();

        if(m_feed == null) {
            getProgressBar().setVisibility(View.VISIBLE);
        }

        getConnection().enqueue(context, Feed.createAction(context), new WebAction.ResultHandler<Feed>() {
            @Override
            public void onActionResult(Status status, Feed feed) {
                if(m_feed == null) {
                    getProgressBar().setVisibility(View.GONE);
                }

                if(feed != null) {
                    setFeed(feed);
                } else if(m_feed == null || animated) {
                    // TODO: Show error alert
                }
            }
        });
    }

    private TextView getEmptyView() {
        return (TextView)getView().findViewById(R.id.empty);
    }

    private ListView getListView() {
        return (ListView)getView().findViewById(R.id.list);
    }

    private ProgressBar getProgressBar() {
        return (ProgressBar)getView().findViewById(R.id.progress_bar);
    }
}