package onworld.sbtn.tasks;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;

import onworld.sbtn.callbacks.DataStartTrackingLoadedListener;
import onworld.sbtn.volley.Requestor;
import onworld.sbtn.volley.VolleySingleton;

/**
 * Created by linhnguyen on 12/12/15.
 */
public class TaskLoadDataStartTracking extends AsyncTask<Void, Void, String> {
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private DataStartTrackingLoadedListener dataLoadedListener;
    private String url;
    private int id;

    public TaskLoadDataStartTracking(DataStartTrackingLoadedListener dataLoadedListener, String url, int id) {
        this.dataLoadedListener = dataLoadedListener;
        this.url = url;
        this.id = id;
        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getmRequestQueue();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        String response = Requestor.requestDataJsonStartTracking(requestQueue, url, id);
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if (dataLoadedListener != null) {
            if (response != null) {
                dataLoadedListener.onDataStartTrackingLoaded(response);
            }

        }

    }
}
