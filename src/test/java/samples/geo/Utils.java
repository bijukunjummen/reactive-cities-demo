package samples.geo;

public abstract class Utils {

    public static void sleep(long sleepTimeInMillis) {
        try {
            Thread.sleep(sleepTimeInMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
