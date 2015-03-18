package ee.ajapaik.sorter.util;

import android.content.Context;

import ee.ajapaik.sorter.data.util.Status;

public class WebAction<T> extends WebOperation {
    public void start(Context context, ResultHandler<T> handler) {

    }

    public void stop() {

    }

    public Status getStatus() {
        return Status.NONE;
    }

    public T getObject() {
        return null;
    }

    public interface ResultHandler<T> {
        void onActionResult(Status status, T data);
    }
}
