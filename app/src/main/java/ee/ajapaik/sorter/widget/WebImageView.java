package ee.ajapaik.sorter.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class WebImageView extends ImageView {
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";
    private static final String KEY_SRC = "src";

    private static final int INVALID_RESOURCE_ID = 0;

    private int m_placeholderResourceId = INVALID_RESOURCE_ID;
    private String m_url;

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

    private void readAttributes(Context context, AttributeSet attrs) {
        m_placeholderResourceId = attrs.getAttributeResourceValue(NAMESPACE, KEY_SRC, INVALID_RESOURCE_ID);
    }

    public String getUrl() {
        return m_url;
    }

    public void setUrl(String url) {

    }
}
