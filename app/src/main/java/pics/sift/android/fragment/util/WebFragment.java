package pics.sift.android.fragment.util;

import android.support.v4.app.Fragment;

import pics.sift.android.WebService;
import pics.sift.android.util.WebActivity;

public abstract class WebFragment extends Fragment {
    protected WebService.Connection getConnection() {
        return ((WebActivity)getActivity()).getConnection();
    }
}
