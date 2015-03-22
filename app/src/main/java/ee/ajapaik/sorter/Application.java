package ee.ajapaik.sorter;

import ee.ajapaik.sorter.util.WebImage;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        WebImage.invalidate(this);
    }
}
