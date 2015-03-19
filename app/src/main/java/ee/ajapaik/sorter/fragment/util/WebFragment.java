package ee.ajapaik.sorter.fragment.util;

import android.support.v4.app.Fragment;

import ee.ajapaik.sorter.WebService;
import ee.ajapaik.sorter.util.WebActivity;

public abstract class WebFragment extends Fragment {
    protected WebService.Connection getConnection() {
        return ((WebActivity)getActivity()).getConnection();
    }
}
