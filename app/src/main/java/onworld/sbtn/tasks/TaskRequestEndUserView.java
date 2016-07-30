package onworld.sbtn.tasks;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;

import onworld.sbtn.callbacks.EndUserViewDataLoadedListener;
import onworld.sbtn.volley.Requestor;
import onworld.sbtn.volley.VolleySingleton;

/**
 * Created by onworldtv on 6/7/16.
 */
public class TaskRequestEndUserView extends AsyncTask<Void, Void, String> {
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private EndUserViewDataLoadedListener dataLoadedListener;
    private String url;

    public TaskRequestEndUserView(EndUserViewDataLoadedListener dataLoadedListener, String url) {
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
        String response = Requestor.requestEndUserView(requestQueue, url);
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if (dataLoadedListener != null) {
            if (response != null) {
                dataLoadedListener.onEndUserDataLoaded(response);
            }

        }

    }
}
