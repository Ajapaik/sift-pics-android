package pics.sift.android.util;

import android.support.v7.app.ActionBarActivity;

import pics.sift.android.WebService;

public class WebActivity extends ActionBarActivity {
    private WebService.Connection m_connection = new WebService.Connection();

    public WebService.Connection getConnection() {
        return m_connection;
    }

    protected void onDestroy() {
        m_connection.dequeueAll(this);

        super.onDestroy();
    }
}
