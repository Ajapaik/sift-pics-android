package ee.ajapaik.sorter;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ee.ajapaik.sorter.data.Session;
import ee.ajapaik.sorter.util.Settings;
import ee.ajapaik.sorter.util.WebAction;
import ee.ajapaik.sorter.util.WebImage;
import ee.ajapaik.sorter.util.WebOperation;

public class WebService extends Service {
    private static final String TAG = "WebService";

    private static final String API_URL = "http://staging.ajapaik.ee/cat/v1/";
    private static final int MAX_CONNECTIONS = 4;

    private class Task {
        private List<WeakReference<ResultHandler>> m_handlers;
        private WebOperation m_operation;

        public Task(WebOperation operation) {
            m_handlers = new ArrayList<WeakReference<ResultHandler>>();
            m_operation = operation;
        }

        public void addHandler(ResultHandler handler) {
            m_handlers.add(new WeakReference<ResultHandler>(handler));
        }
    }

    private final IBinder m_binder = new LocalBinder();
    private final Handler m_handler = new Handler(Looper.getMainLooper());
    private ExecutorService m_actionQueue = Executors.newFixedThreadPool(1);
    private ExecutorService m_imageQueue = Executors.newFixedThreadPool(MAX_CONNECTIONS - 1);
    private List<Task> m_tasks = new ArrayList<Task>();
    private Session m_session = null;
    private Settings m_settings;

    public WebService() {
    }

    public void enqueueOperation(WebOperation operation, ResultHandler handler) {

    }

    public void dequeueOperation(WebOperation operation) {

    }

    @Override
    public void onCreate() {
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "onCreate");
        }

        super.onCreate();

        m_settings = new Settings(this);
        m_session = m_settings.getSession();

        if(m_session != null && m_session.isExpired()) {
            if(BuildConfig.DEBUG) {
                Log.d(TAG, "session was cleared, because it was out of date");
            }

            m_settings.setSession(null);
            m_session = null;
        }
    }

    @Override
    public void onDestroy() {
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "onDestroy");
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return m_binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    public static class Connection implements ServiceConnection {
        private List<QueueItem> m_queue = new ArrayList<QueueItem>();
        private boolean m_connecting = false;
        private LocalBinder m_binder;

        private interface QueueItem {
            WebOperation getOperation();
            void start();
            void stop();
        }

        private class ActionItem<T> implements QueueItem, WebService.ResultHandler {
            private Context m_context;
            private WebAction<T> m_action;
            private WebAction.ResultHandler<T> m_handler;

            public ActionItem(Context context, WebAction<T> action, WebAction.ResultHandler<T> handler) {
                m_context = context;
                m_action = action;
                m_handler = handler;
            }

            public WebOperation getOperation() {
                return m_action;
            }

            public void start() {
                m_binder.add(m_action, this);
            }

            public void stop() {
                //m_binder.remove(m_action);
                m_handler = null;
            }

            public void onResult(WebOperation operation) {
                m_queue.remove(this);

                if(m_handler != null) {
                    WebAction<T> action = (WebAction<T>)operation;

                    m_handler.onActionResult(action.getStatus(), action.getObject());
                }

                if(m_queue.size() == 0) {
                    disconnect(m_context);
                }
            }
        }

        public <T> WebAction<T> open(Context context, WebAction<T> action, WebAction.ResultHandler<T> handler) {
            String uniqueId = action.getUniqueId();
            ActionItem<T> actionItem;

            if(uniqueId != null) {
                for(QueueItem item : m_queue) {
                    String uniqueId_ = item.getOperation().getUniqueId();

                    if(uniqueId_ != null && uniqueId_.equals(uniqueId)) {
                        try {
                            action = (WebAction<T>)item.getOperation();

                            return action;
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            actionItem = new ActionItem<T>(context, action, handler);
            m_queue.add(actionItem);

            if(m_binder != null) {
                actionItem.start();
            } else {
                connect(context);
            }

            return action;
        }

        public WebImage open(Context context, WebImage image) {
            return image;
        }

        public void close(Context context, WebOperation operation) {
            for(int i = 0, c = m_queue.size(); i < c; i++) {
                QueueItem item = m_queue.get(i);

                if(item.getOperation() == operation) {
                    item.stop();
                    m_queue.remove(i);
                    break;
                }
            }

            if(m_queue.size() == 0) {
                disconnect(context);
            }
        }

        public void closeAll(Context context) {
            for(QueueItem item : m_queue) {
                item.stop();
            }

            m_queue.clear();
            disconnect(context);
        }

        private void connect(Context context) {
            if(!m_connecting) {
                m_connecting = true;
                context.bindService(new Intent(context, WebService.class), this, Context.BIND_AUTO_CREATE);
            }
        }

        private void disconnect(Context context) {
            if(m_connecting) {
                m_connecting = false;
                context.unbindService(this);
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if(BuildConfig.DEBUG) {
                Log.d(TAG, "onServiceConnected");
            }

            m_binder = (LocalBinder)service;

            if(m_binder != null) {
                for(QueueItem item : m_queue) {
                    item.start();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if(BuildConfig.DEBUG) {
                Log.d(TAG, "onServiceDisconnected");
            }

            m_binder = null;
        }
    }

    protected interface ResultHandler {
        void onResult(WebOperation operation);
    }

    protected class LocalBinder extends Binder {
        public void add(WebOperation operation, ResultHandler handler) {
            enqueueOperation(operation, handler);
        }

        public void remove(WebOperation operation) {
            dequeueOperation(operation);
        }
    }
}
