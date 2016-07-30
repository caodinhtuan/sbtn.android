package onworld.sbtn.tasks;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;

import onworld.sbtn.callbacks.DataDetailLoadedListener;
import onworld.sbtn.volley.Requestor;
import onworld.sbtn.volley.VolleySingleton;

/**
 * Created by linhnguyen on 12/26/15.
 */
public class TaskLoadDataDetail extends AsyncTask<Void, Void, String> {
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private DataDetailLoadedListener dataDetailLoadedListener;
    private String url;

    public TaskLoadDataDetail(DataDetailLoadedListener dataDetailLoadedListener, String url) {
        this.dataDetailLoadedListener = dataDetailLoadedListener;
        volleySingleton = VolleySingleton.getsInstance();
        this.requestQueue = volleySingleton.getmRequestQueue();
        this.url = url;
    }

    @Override
    protected String doInBackground(Void... params) {
        String jsonObject = Requestor.requestDataJson(requestQueue, url);
        return jsonObject;
    }

    @Override
    protected void onPostExecute(String jsonObject) {
        super.onPostExecute(jsonObject);
        if (dataDetailLoadedListener != null) {
            if (jsonObject != null) {
                dataDetailLoadedListener.onDataDetailLoaded(jsonObject);
            }

        }
    }
}
