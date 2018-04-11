package arcAppManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Devbd on 3/26/18.
 */

public class ArcAppManager {
    private static final ArcAppManager ourInstance = new ArcAppManager();
    private Apps apps = null;
    private String message="nothing";

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

    public String showPromotedAds(Activity activity, onPromoterNotifyListener promoterNotifyListener) throws Exception {
        if (isDisable() || isPromotingDisable()) {
            return "Promoting Disable, please contact with admin.";
        }
        if (!checkAppsDataValidity()) {
            return "Error, please try again later.";
        }
        new ShowPromotAppDialog(activity, "", promoterNotifyListener);
        return "Ads Showed";
    }

    public boolean showPromotedAds(Activity activity, String buttonName, onPromoterNotifyListener promoterNotifyListener) throws Exception {
        if (apps == null || apps.getResponse() == null) {
            return false;
        }
        new ShowPromotAppDialog(activity, buttonName, promoterNotifyListener);
        return true;
    }

    Apps getApps() {
        return apps;
    }

    void setApps(Apps apps) {
        this.apps = apps;
    }
    void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        StringBuilder builder = new StringBuilder();
        builder.append("Publisher :" + (isDisable() ? "Disable" : "Enable") + "\n");
        builder.append("Promoting :" + (isPromotingDisable() ? "Disable" : "Enable") + "\n");
        builder.append("Ads :" + (isAdsDisable() ? "Disable" : "Enable"));
        return builder.toString();
    }

    public void initiate(final Context context, HttpSyncAppManager.onHttpSyncNotifyListener onHttpSyncNotify, int refreshIntervalHour) {
        long lastSavedDuration = System.currentTimeMillis() -
                ArcAppManagerdb.getLongSetting(context, "refresh_time", 0);
        long diffInMin = TimeUnit.MILLISECONDS.toHours(lastSavedDuration);
        ArcLog.w("Time Diff  : " + diffInMin + "");
        new HttpSyncAppManager(onHttpSyncNotify, (diffInMin >= refreshIntervalHour), context);
    }

    public String getVersion() {
        if (checkAppsDataValidity()) {
            return apps.getResponse().get(0).getAppVersion();
        }
        return "";
    }

    public String getShortDescrition() {
        if (checkAppsDataValidity()) {
            return apps.getResponse().get(0).getShortDescription();
        }
        return "";
    }

    public String getDescription() {
        if (checkAppsDataValidity()) {
            return apps.getResponse().get(0).getDescription();
        }
        return "";
    }

    public String getLink() {
        if (checkAppsDataValidity()) {
            return apps.getResponse().get(0).getLinks();
        }
        return "";
    }

    public String getAppData() {
        if (checkAppsDataValidity()) {
            return apps.getResponse().get(0).getAppData();
        }
        return "";
    }

    public boolean isUpdateMajor(String currentVersion) {
        if (checkAppsDataValidity()) {
            if (!currentVersion.equals(getVersion())) {
                return apps.getResponse().get(0).getIsMajorUpdate();
            }
        }
        return false;
    }

    public boolean isVersionChanged(String currentVersion) {
        if (checkAppsDataValidity()) {
            currentVersion.equals(getVersion());
        }
        return false;
    }

    public boolean isDisable() {
        if (!checkAppsDataValidity()) {
            return true;
        }
        return apps.getResponse().get(0).getStatus() == 0;

    }

    public boolean isAdsDisable() {
        if (!checkAppsDataValidity()) {
            return true;
        }
        return apps.getResponse().get(0).getStatusAds() == 0;

    }

    public boolean isPromotingDisable() {
        if (!checkAppsDataValidity()) {
            return false;
        }
        return apps.getResponse().get(0).getIsPromoteApps() == 0;

    }

    public List<PromotedAppsInfo> getpromotedApps() {
        if (checkAppsDataValidity()) {
            return apps.getResponse().get(0).getPromotedAppsInfo();
        }
        return null;
    }

    public void showLog(boolean isLogShow) {
        ArcLog.IS_LOG = isLogShow;
    }

    private boolean checkAppsDataValidity() {
        return (apps != null && apps.getResponse() != null && apps.getResponse().size() > 0);
    }
//    public void initiateSplash(int refreshIntervalHour, Activity activity, int ResultReq) {
//        long lastSavedDuration = System.currentTimeMillis() -
//                ArcAppManagerdb.getLongSetting(activity, "refresh_time", 0);
//        long diffInMin = TimeUnit.MILLISECONDS.toHours(lastSavedDuration);
//        ArcLog.w("Time Diff  : " + diffInMin + ">> Splash, pkg from: " + activity.getApplicationContext().getPackageName());
//        Intent intent = new Intent();
//        intent.setClassName(activity, "appmanager.arcadio.com.arcappmanager.AboutActivity");
//        intent.putExtra("refreshIntervalHour", refreshIntervalHour);
//        intent.putExtra("pkg", activity.getApplicationContext().getPackageName());
//        activity.startActivityForResult(intent, ResultReq);
//    }
}
