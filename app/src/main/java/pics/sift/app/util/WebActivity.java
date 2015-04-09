package pics.sift.app.util;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import pics.sift.app.WebService;
import pics.sift.app.data.Device;
import pics.sift.app.data.util.Status;

public class WebActivity extends ActionBarActivity {
    private static final String TAG = "WebActivity";

    private WebService.Connection m_connection = new WebService.Connection();
    protected GoogleCloudMessaging m_gcm;
    protected Settings m_settings;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9001;
    private static final String SENDER_ID = "645607518692";

    public WebService.Connection getConnection() {
        return m_connection;
    }

    public Settings getSettings() {
        return m_settings;
    }

    public boolean checkPlayServices(boolean ui) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(resultCode == ConnectionResult.SUCCESS) {
            return true;
        }

        if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
            if(ui) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
        } else {
            Log.i(TAG, "GCM is not supported for this device.");
        }

        return false;
    }

    public void registerDevice(boolean ui) {
        Registration registration = m_settings.getRegistration();

        if(registration == null || registration.isExpired() || ui) {
            registerInBackground();
        } else if(!ui) {
            sendRegistrationIdToBackend(registration);
        }
    }

    public void unregisterDevice() {
        final Registration registration = m_settings.getRegistration();

        if(registration != null && registration.isValid()) {
            getConnection().enqueue(this, Device.createUnregistrationAction(this, registration), new WebAction.ResultHandler<Device>() {
                @Override
                public void onActionResult(Status status, Device device) {
                    if(status == Status.NONE) {
                        Registration registration_ = m_settings.getRegistration();

                        if(Objects.match(registration_, registration)) {
                            m_settings.setRegistration(null);
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_settings = new Settings(this);
        m_gcm = GoogleCloudMessaging.getInstance(this);
    }

    protected void onDestroy() {
        m_connection.dequeueAll(this);

        super.onDestroy();
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    Registration registration;


                    if(m_gcm == null) {
                        m_gcm = GoogleCloudMessaging.getInstance(WebActivity.this);
                    }

                    registration = m_settings.createRegistration(m_gcm.register(SENDER_ID));
                    Log.i(TAG, "Device registered, registration ID=" + registration.getCode());
                    sendRegistrationIdToBackend(registration);
                }
                catch(IOException ex) {
                    Log.e(TAG, "register", ex);
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {

            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend(final Registration registration) {
        if(registration != null && registration.isValid()) {
            m_settings.setRegistration(registration.toExpired());

            getConnection().enqueue(this, Device.createRegistrationAction(this, registration), new WebAction.ResultHandler<Device>() {
                @Override
                public void onActionResult(Status status, Device device) {
                    if(status == Status.NONE) {
                        m_settings.setRegistration(registration);
                    }
                }
            });
        }
    }
}
