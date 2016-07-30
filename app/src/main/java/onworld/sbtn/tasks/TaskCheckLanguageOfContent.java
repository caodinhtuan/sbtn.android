package onworld.sbtn.tasks;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;

import org.json.JSONObject;

import onworld.sbtn.callbacks.CheckLanguageOfContentListener;
import onworld.sbtn.volley.Requestor;
import onworld.sbtn.volley.VolleySingleton;

/**
 * Created by onworldtv on 12/25/15.
 */
public class TaskCheckLanguageOfContent extends AsyncTask<Void, Void, JSONObject> {
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private CheckLanguageOfContentListener checkLanguageOfContentListener;
    private String url;

    public TaskCheckLanguageOfContent(CheckLanguageOfContentListener checkLanguageOfContentListener, String url) {
        this.checkLanguageOfContentListener = checkLanguageOfContentListener;
        volleySingleton = VolleySingleton.getsInstance();
        this.requestQueue = volleySingleton.getmRequestQueue();
        this.url = url;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject jsonObject = Requestor.requestDataJsonWithoutHeader(requestQueue, url);
        return jsonObject;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        if (checkLanguageOfContentListener != null) {
            if (jsonObject != null) {
                checkLanguageOfContentListener.onCheckLanguageOfContent(jsonObject);
            }

        }
    }
}
