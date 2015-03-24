package pics.sift.android.util;

public class Objects {
    public static boolean match(Object a, Object b) {
        if(a == b) {
            return true;
        }

        if(a == null || b == null) {
            return false;
        }

        return a.equals(b);
    }
}
