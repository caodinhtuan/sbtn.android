package onworld.sbtn.tasks;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;

import org.json.JSONObject;

import onworld.sbtn.callbacks.DataLoadedWithoutParamsListener;
import onworld.sbtn.volley.Requestor;
import onworld.sbtn.volley.VolleySingleton;

/**
 * Created by linhnguyen on 12/12/15.
 */
public class TaskLoadDataWithoutParams extends AsyncTask<Void, Void, JSONObject> {
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private DataLoadedWithoutParamsListener dataLoadedListener;
    private String url;

    public TaskLoadDataWithoutParams(DataLoadedWithoutParamsListener dataLoadedListener, String url) {
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
    protected JSONObject doInBackground(Void... voids) {
        JSONObject response = Requestor.requestDataJsonWithoutHeader(requestQueue, url);
        return response;
    }

    @Override
    protected void onPostExecute(JSONObject response) {
        super.onPostExecute(response);
        if (dataLoadedListener != null) {
            if (response != null) {
                dataLoadedListener.onDataLoadedWithoutParams(response);
            }

        }

    }
}
