package nl.mprog.ghost;

/**
 * Created by woofw_000 on 29/09/2015.
 *
 * Code source: http://www.myandroidsolutions.com/2013/04/27/android-get-application-context/
 */
import android.app.Application;
import android.content.Context;

public class ApplicationContextProvider extends Application {

    /**
     * Keeps a reference of the application context
     */
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();

    }

    /**
     * Returns the application context
     *
     * @return application context
     */
    public static Context getContext() {
        return sContext;
    }

}