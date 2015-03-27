package pics.sift.android.fragment.util;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import pics.sift.android.WebService;
import pics.sift.android.util.WebActivity;

public abstract class WebFragment extends Fragment {
    protected ActionBar getActionBar() {
        return ((WebActivity)getActivity()).getSupportActionBar();
    }

    protected WebService.Connection getConnection() {
        return ((WebActivity)getActivity()).getConnection();
    }
}
