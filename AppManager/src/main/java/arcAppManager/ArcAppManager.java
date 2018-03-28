package arcAppManager;

import android.app.Activity;
import android.content.Context;

import java.util.concurrent.TimeUnit;

/**
 * Created by Devbd on 3/26/18.
 */

public class ArcAppManager {
    private static final ArcAppManager ourInstance = new ArcAppManager();
    private Apps apps = null;

    public interface onPromoterNotifyListener {
        void appShowed(String pkgName);

        void appInstallClicked(boolean isClicked);

        void appCancelled();
    }

    public static ArcAppManager getInstance() {
        return ourInstance;
    }


    private ArcAppManager() {
    }

    public void showPromotedAds(Activity activity, onPromoterNotifyListener promoterNotifyListener) throws Exception {
        new ShowPromotAppDialog(activity, "", promoterNotifyListener);
    }

    public void showPromotedAds(Activity activity, String buttonName, onPromoterNotifyListener promoterNotifyListener) throws Exception {
        new ShowPromotAppDialog(activity, buttonName, promoterNotifyListener);
    }

    Apps getApps() {
        return apps;
    }

    void setApps(Apps apps) {
        this.apps = apps;
    }

    public void initiate(final Context context, HttpSyncAppManager.onHttpSyncNotifyListener onHttpSyncNotify, int refreshIntervalHour) {
        long lastSavedDuration = System.currentTimeMillis() -
                ArcAppManagerdb.getLongSetting(context, "refresh_time", 0);
        long diffInMin = TimeUnit.MILLISECONDS.toHours(lastSavedDuration);
        ArcLog.w("Time Diff clan: " + diffInMin + "");
        new HttpSyncAppManager(onHttpSyncNotify, (diffInMin >= refreshIntervalHour), context);
    }
}
