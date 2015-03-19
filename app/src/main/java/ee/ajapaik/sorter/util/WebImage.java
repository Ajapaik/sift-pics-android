package ee.ajapaik.sorter.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ee.ajapaik.sorter.BuildConfig;

public class WebImage extends WebOperation {
    private static final String TAG = "WebImage";

    private static final long MAX_CACHE_AGE = 24 * 60 * 60; // 24H
    private static final long MAX_CACHE_SIZE = 10 * 1024 * 1024; // 10MB

    private static final String CACHE_PREFIX = "img_";
    private static final int BUFFER_SIZE = 32000;

    public static void invalidate(Context context) {
        List<File> cache = new ArrayList<File>();
        long timestamp = new Date().getTime();
        File dir = getCacheDir(context);
        File[] files = dir.listFiles();
        long totalSize = 0;

        if(files != null) {
            for(File file : files) {
                if(file.getName().startsWith(CACHE_PREFIX)) {
                    if(Math.abs(file.lastModified() - timestamp) > MAX_CACHE_AGE) {
                        file.delete();
                    } else {
                        cache.add(file);
                    }
                }
            }
        }

        Collections.sort(cache, new FileComparator());

        for(File file : cache) {
            long fileSize = file.length();

            if(totalSize + fileSize > MAX_CACHE_SIZE) {
                file.delete();
            } else {
                totalSize += fileSize;
            }
        }
    }

    public static void clear(Context context) {
        File dir = getCacheDir(context);
        File[] files = dir.listFiles();

        if(files != null) {
            for(File file : files) {
                if(file.getName().startsWith(CACHE_PREFIX)) {
                    file.delete();
                }
            }
        }
    }

    private static File getCacheDir(Context context) {
        return context.getFilesDir();
    }

    private Drawable m_drawable;
    private boolean m_cache;
    private String m_path;
    private boolean m_render;
    private int m_status = HTTP_STATUS_NOT_FOUND;

    public WebImage(Context context, Uri uri) {
        this(context, uri.toString());
    }

    public WebImage(Context context, String url) {
        this(context, url, true, true);
    }

    public WebImage(Context context, String url, boolean cache, boolean render) {
        super(context, url, null);

        m_cache = cache;
        m_path = CACHE_PREFIX + SHA1.encode(url);
        m_render = render;
    }

    public int getStatus() {
        return m_status;
    }

    public Drawable getDrawable() {
        return m_drawable;
    }

    public String getPath() {
        return m_path;
    }

    @Override
    public String getUniqueId() {
        return getPath();
    }

    @Override
    protected void onFailure() {
        m_status = HTTP_STATUS_INTERNAL_SERVER_ERROR;
    }

    @Override
    protected void onResponse(int statusCode, InputStream is) {
        if(BuildConfig.DEBUG) {
            Log.d(TAG, "statusCode=" + statusCode + ", stream=" + ((is != null) ? "YES" : "NONE"));
        }

        if(isCancelled()) {
            m_status = HTTP_STATUS_NO_CONTENT;
        } else if(statusCode == HTTP_STATUS_OK) {
            if(is != null) {
                try {
                    FileOutputStream os = m_context.openFileOutput(m_path, Context.MODE_PRIVATE);
                    byte data[] = new byte[BUFFER_SIZE];
                    int count;

                    while((count = is.read(data, 0, BUFFER_SIZE)) != -1 && !isCancelled()) {
                        os.write(data, 0, count);
                    }

                    os.flush();
                    os.close();
                    is.close();

                    m_status = HTTP_STATUS_OK;

                    if(isCancelled()) {
                        Log.d(TAG, "Deleted cancelled download (" + m_path + ")");

                        m_status = HTTP_STATUS_NO_CONTENT;
                        m_context.deleteFile(m_path);
                    }
                } catch(FileNotFoundException e) {
                    Log.e(TAG, "", e);
                    m_status = HTTP_STATUS_NOT_FOUND;
                } catch(IOException e) {
                    Log.d(TAG, "", e);
                    m_status = HTTP_STATUS_INTERNAL_SERVER_ERROR;
                }
            } else {
                m_status = HTTP_STATUS_NO_CONTENT;
            }
        } else {
            m_status = statusCode;
        }
    }

    @Override
    public boolean performRequest(String baseURL, Map<String, String> extraParameters) {
        boolean result = false;

        if(m_cache) {
            File file = new File(getCacheDir(m_context), m_path);

            if(file.exists()) {
                file.setLastModified(new Date().getTime());
                m_status = HTTP_STATUS_OK;
                result = true;
            }
        }

        if(!result) {
            result = super.performRequest(baseURL, extraParameters);
        }

        if(m_render && m_status == HTTP_STATUS_OK) {
            File file = new File(getCacheDir(m_context), m_path);

            m_drawable = Drawable.createFromPath(file.getAbsolutePath());

            if(m_drawable == null) {
                m_status = HTTP_STATUS_INTERNAL_SERVER_ERROR;
            }
        }

        return result;
    }

    private static class FileComparator implements Comparator<File> {
        public int compare(File a, File b) {
            long aL = a.lastModified();
            long bL = b.lastModified();

            if(aL > bL) {
                return -1;
            }

            if(aL < bL) {
                return 1;
            }

            return 0;
        }
    }

    public interface ResultHandler {
        void onImageResult(int status, Drawable drawable);
    }
}
