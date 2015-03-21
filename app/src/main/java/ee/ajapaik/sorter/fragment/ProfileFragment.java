package ee.ajapaik.sorter.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import ee.ajapaik.sorter.AlbumActivity;
import ee.ajapaik.sorter.R;
import ee.ajapaik.sorter.adapter.FavoritesAdapter;
import ee.ajapaik.sorter.data.Hyperlink;
import ee.ajapaik.sorter.data.Profile;
import ee.ajapaik.sorter.data.util.Status;
import ee.ajapaik.sorter.fragment.util.WebFragment;
import ee.ajapaik.sorter.util.Favorite;
import ee.ajapaik.sorter.util.Objects;
import ee.ajapaik.sorter.util.Settings;
import ee.ajapaik.sorter.util.WebAction;

public class ProfileFragment extends WebFragment {
    private static final String TAG = "ProfileFragment";
    private static final String KEY_PROFILE = "profile";

    private Profile m_profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        ListView listView;

        super.onActivityCreated(savedInstanceState);

        listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Favorite favorite = (Favorite)parent.getItemAtPosition(position);

                AlbumActivity.start(getActivity(), favorite);
            }
        });

        if(savedInstanceState != null) {
            Profile profile = savedInstanceState.getParcelable(KEY_PROFILE);

            setProfile(profile);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelable(KEY_PROFILE, m_profile);
    }

    @Override
    public void onStart() {
        super.onStart();
        onRefresh(false);
    }

    public Profile getProfile() {
        return m_profile;
    }

    public void setProfile(Profile profile) {
        if(!Objects.match(m_profile, profile)) {
            Context context = getActivity();
            Settings settings = new Settings(context);
            ListView listView = getListView();

            m_profile = profile;

            if(m_profile != null) {
                View header = LayoutInflater.from(context).inflate(R.layout.list_profile_header, listView, false);
                List<Favorite> favorites = settings.getFavorites();
                Hyperlink link = m_profile.getLink();

                listView.setAdapter(new FavoritesAdapter(listView.getContext(), favorites));
                listView.addHeaderView(header, null, false);

                getTitleView().setText(Html.fromHtml(context.getResources().getQuantityString(R.plurals.profile_stats, m_profile.getTaggedCount(), m_profile.getTaggedCount(), m_profile.getPicturesCount())));

                if(m_profile.getMessage() != null) {
                    getSubtitleView().setText(m_profile.getMessage());
                }

                if(link != null) {
                    getLinkView().setText(link.toHtml());
                } else {
                    getLinkView().setVisibility(View.GONE);
                }

                if(favorites.size() == 0) {
                    getFavoritesView().setVisibility(View.GONE);
                }
            } else {
                listView.setAdapter(null);
            }
        }
    }

    protected void onRefresh(final boolean animated) {
        Context context = getActivity();

        if(m_profile == null) {
            getProgressBar().setVisibility(View.VISIBLE);
        }

        getConnection().enqueue(context, Profile.createAction(context), new WebAction.ResultHandler<Profile>() {
            @Override
            public void onActionResult(Status status, Profile profile) {
                if(m_profile == null) {
                    getProgressBar().setVisibility(View.GONE);
                }

                if(profile != null) {
                    setProfile(profile);
                } else if(m_profile == null || animated) {
                    // TODO: Show error alert
                }
            }
        });
    }

    private ListView getListView() {
        return (ListView)getView().findViewById(R.id.list);
    }

    private TextView getTitleView() {
        return (TextView)getView().findViewById(R.id.text_title);
    }

    private TextView getSubtitleView() {
        return (TextView)getView().findViewById(R.id.text_subtitle);
    }

    private TextView getFavoritesView() {
        return (TextView)getView().findViewById(R.id.text_favorites);
    }

    private TextView getLinkView() {
        return (TextView)getView().findViewById(R.id.text_link);
    }

    private ProgressBar getProgressBar() {
        return (ProgressBar)getView().findViewById(R.id.progress_bar);
    }
}
