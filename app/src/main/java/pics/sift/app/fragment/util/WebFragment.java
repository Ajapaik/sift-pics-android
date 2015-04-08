package pics.sift.app.fragment.util;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import pics.sift.app.WebService;
import pics.sift.app.util.WebActivity;

public abstract class WebFragment extends Fragment {
    protected ActionBar getActionBar() {
        return ((WebActivity)getActivity()).getSupportActionBar();
    }

    protected WebService.Connection getConnection() {
        return ((WebActivity)getActivity()).getConnection();
    }
}
