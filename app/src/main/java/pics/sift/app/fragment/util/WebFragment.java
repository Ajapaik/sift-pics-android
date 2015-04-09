package pics.sift.app.fragment.util;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import pics.sift.app.WebService;
import pics.sift.app.util.Settings;
import pics.sift.app.util.WebActivity;

public abstract class WebFragment extends Fragment {
    protected ActionBar getActionBar() {
        return ((WebActivity)getActivity()).getSupportActionBar();
    }

    protected WebService.Connection getConnection() {
        return ((WebActivity)getActivity()).getConnection();
    }

    protected Settings getSettings() {
        return ((WebActivity)getActivity()).getSettings();
    }

    protected boolean checkPlayServices(boolean ui) {
        return ((WebActivity)getActivity()).checkPlayServices(ui);
    }

    protected void registerDevice(boolean ui) {
        ((WebActivity)getActivity()).registerDevice(ui);
    }

    protected void unregisterDevice() {
        ((WebActivity)getActivity()).unregisterDevice();
    }
}
