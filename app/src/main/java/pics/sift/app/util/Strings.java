package pics.sift.app.util;

public class Strings {
    public static String toBase16(byte b) {
        return new String( new char[] { (char)fromBase16((b >> 4) & 0x0F), (char)fromBase16((b >> 0) & 0x0F) });
    }

    private static int fromBase16(int value) {
        if(value >= 0 && value <= 9) {
            return '0' + value;
        } else if(value >= 10 && value <= 15) {
            return 'A' + value - 10;
        }

        return '0';
    }
}
