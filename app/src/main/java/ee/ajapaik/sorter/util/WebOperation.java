package ee.ajapaik.sorter.util;

import android.os.Parcelable;

public class WebOperation {
    public String getUniqueId() {
        return null;
    }

    public interface ResultHandler {
        public void onResult(int error, Parcelable data);
    }
}
