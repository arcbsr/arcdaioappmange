package arcAppManager;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Devbd on 3/26/18.
 */

public class HttpSyncAppManager extends AsyncTask<Void, Void, Object> {
    private onHttpSyncNotifyListener onHttpSyncNotify = null;
    public final static String KEY_SAVED_DATA = "arc_app_manager_db";
    private final static String ARC_ROOT_URL = "http://79.143.190.131/arcappmanager/api/app-details?pkg_name=";
    private boolean refreshData = true;
    Context context = null;
    public static final String ARC_ERROR_CONNECTION = "Error in connection";

    public HttpSyncAppManager(onHttpSyncNotifyListener onHttpSyncNotify, boolean refreshData, Context context) {
        this.onHttpSyncNotify = onHttpSyncNotify;
        this.refreshData = refreshData;
        this.context = context;
        execute();
    }

    public interface onHttpSyncNotifyListener {
        void onPreConnection();

        void onPostConnection(Object object, boolean isSuccess);

        void onDoInBackground(String pkgName);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (onHttpSyncNotify != null)
            onHttpSyncNotify.onPreConnection();
    }

    @Override
    protected void onPostExecute(Object apps) {
        super.onPostExecute(apps);
        if (apps == null) {
            if (onHttpSyncNotify != null)
                onHttpSyncNotify.onPostConnection(setDataForError(), false);
            return;
        }
        onHttpSyncNotify.onPostConnection(apps, true);
    }

    private class resultModule {
        String result = "";
        int responseCode = 200;
    }

    @Override
    protected Object doInBackground(Void... voids) {
        Apps response = null;
        resultModule result = new resultModule();
        try {
            if (refreshData) {
                result = getHttpResponse();
                ArcLog.w("data load from server");
            } else {
                ArcLog.w("data load from local||server");
                result.result = ArcAppManagerdb.getStringValue(context, KEY_SAVED_DATA, "");
                if (result.result.length() == 0) {
                    result = getHttpResponse();
                }
            }
            ArcLog.w(result.result);
            Gson gson = new Gson();
            response = gson.fromJson(result.result, Apps.class);
            ArcAppManagerdb.setStringValue(context, KEY_SAVED_DATA, result.result);
            ArcAppManager.getInstance().setApps(response);
        } catch (Exception e) {
            ArcLog.w(e.getMessage());
            return null;
        }
        if (result.responseCode != 200) {
            return null;

        }
        if (refreshData) {
            ArcAppManagerdb.setSetting(context, "refresh_time", System.currentTimeMillis());
            ArcLog.w("set interval for server");
        }
        return response;
    }

    private String setDataForError() {
        ArcAppManager.getInstance().setApps(null);
        ArcLog.w(ARC_ERROR_CONNECTION);
        return ARC_ERROR_CONNECTION;
    }

    private resultModule getHttpResponse() {
        resultModule result = new resultModule();
        if (onHttpSyncNotify != null) {
            try {
                onHttpSyncNotify.onDoInBackground(context.getApplicationContext().getPackageName());
                ArcLog.w(ARC_ROOT_URL + context.getApplicationContext().getPackageName());
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(ARC_ROOT_URL + context.getApplicationContext().getPackageName())
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .build();
                okhttp3.Response responsebody = client.newCall(request).execute();
                result.responseCode = responsebody.code();
                //Log.w("response code", responseCode + "");
                //Log.w("result ", responsebody.body().string());
                //result = new String(new MCrypt().decrypt(responsebody.body().string()));
                result.result = responsebody.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
