package pics.sift.android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import pics.sift.android.WebService;
import pics.sift.android.util.Objects;
import pics.sift.android.util.WebActivity;
import pics.sift.android.util.WebImage;

public class WebImageView extends ImageView {
    private static final String TAG = "WebImageView";

    private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";
    private static final String KEY_SRC = "src";

    private static final int INVALID_RESOURCE_ID = 0;

    private boolean m_attachedToWindow = false;
    private WebService.Connection m_connection;
    private WebImage m_image;
    private int m_placeholderResourceId = INVALID_RESOURCE_ID;
    private OnLoadListener m_loadListener;
    private Uri m_uri;

    public WebImageView(Context context) {
        super(context);
    }

    public WebImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if(attrs != null) {
            readAttributes(context, attrs);
        }
    }

    public WebImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if(attrs != null) {
            readAttributes(context, attrs);
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        if(!Objects.match(m_uri, uri)) {
            stopLoadingImage();

            m_uri = uri;

            if(m_attachedToWindow) {
                startLoadingImage();
            }

            if(m_placeholderResourceId != INVALID_RESOURCE_ID && (m_image == null || m_image.getDrawable() != null)) {
                setImageResource(m_placeholderResourceId);

                if(m_loadListener != null) {
                    m_loadListener.onImageUnloaded();
                }
            }
        }
    }

    public void setOnLoadListener(OnLoadListener loadListener) {
        m_loadListener = loadListener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        m_attachedToWindow = true;
        startLoadingImage();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopLoadingImage();
        m_attachedToWindow = false;
        super.onDetachedFromWindow();
    }

    private void startLoadingImage() {
        if(m_image == null && m_uri != null) {
            Context context = getContext();

            if(m_connection == null) {
                if(context instanceof WebActivity) {
                    m_connection = ((WebActivity)context).getConnection();
                }

                if(m_connection == null) {
                    Log.e(TAG, "Unable to get a web connection");
                    return;
                }
            }

            m_image = m_connection.enqueue(context, new WebImage(context, m_uri), new WebImage.ResultHandler() {
                @Override
                public void onImageResult(int status, Drawable drawable) {
                    if(m_attachedToWindow) {
                        if(drawable != null) {
                            setImageDrawable(drawable);

                            if(m_loadListener != null) {
                                m_loadListener.onImageLoaded();
                            }
                        } else {
                            if(m_loadListener != null) {
                                m_loadListener.onImageFailed();
                            }
                        }
                    }
                }
            });
        }
    }

    private void stopLoadingImage() {
        if(m_image != null) {
            m_connection.dequeue(getContext(), m_image);
            m_image = null;
        }
    }

    private void readAttributes(Context context, AttributeSet attrs) {
        m_placeholderResourceId = attrs.getAttributeResourceValue(NAMESPACE, KEY_SRC, INVALID_RESOURCE_ID);
    }

    public interface OnLoadListener {
        void onImageLoaded();
        void onImageUnloaded();
        void onImageFailed();
    }
}
