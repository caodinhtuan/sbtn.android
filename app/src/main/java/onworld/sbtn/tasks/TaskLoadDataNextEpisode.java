package onworld.sbtn.tasks;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;

import org.json.JSONObject;

import onworld.sbtn.callbacks.DataLoadedNextEpisodeListener;
import onworld.sbtn.volley.Requestor;
import onworld.sbtn.volley.VolleySingleton;

/**
 * Created by linhnguyen on 12/12/15.
 */
public class TaskLoadDataNextEpisode extends AsyncTask<Void, Void, String> {
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private DataLoadedNextEpisodeListener dataLoadedListener;
    private String url;

    public TaskLoadDataNextEpisode(DataLoadedNextEpisodeListener dataLoadedListener, String url) {
        this.dataLoadedListener = dataLoadedListener;
        this.url = url;
        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getmRequestQueue();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        String response = Requestor.requestDataJson(requestQueue, url);
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if (dataLoadedListener != null) {
            if (response != null) {
                dataLoadedListener.onDataLoadedNextEpisode(response);
            }
        }

    }
}
