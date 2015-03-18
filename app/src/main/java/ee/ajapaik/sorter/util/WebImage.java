package ee.ajapaik.sorter.util;

import android.content.Context;

import java.io.InputStream;

public class WebImage extends WebOperation {
    public WebImage(Context context, String path) {
        super(context, path, null);
    }

    @Override
    protected void onFailure() {
    }

    @Override
    protected void onResponse(int statusCode, InputStream stream) {

    }
}
