package onworld.sbtn.tasks;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;

import onworld.sbtn.callbacks.DataLoginFacebookLoadedListener;
import onworld.sbtn.volley.Requestor;
import onworld.sbtn.volley.VolleySingleton;

/**
 * Created by linhnguyen on 12/12/15.
 */
public class TaskLoadDataSignInFacebook extends AsyncTask<Void, Void, String> {
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private DataLoginFacebookLoadedListener dataLoadedListener;
    private String url;
    private String email, password;
    private String facebookId, facebookToken, facebookEmail, fullName, avatar;

    public TaskLoadDataSignInFacebook(DataLoginFacebookLoadedListener dataLoadedListener, String url, String facebookId,
                                      String facebookToken, String avataString,
                                      String fullName, String facebookEmail) {
        this.dataLoadedListener = dataLoadedListener;
        this.url = url;
        this.facebookId = facebookId;
        this.facebookEmail = facebookEmail;
        this.facebookToken = facebookToken;
        this.avatar = avataString;
        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getmRequestQueue();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        String response = Requestor.requestDataJsonSignInFacebook(requestQueue, url, facebookId, facebookToken, avatar, fullName, facebookEmail);
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if (dataLoadedListener != null) {
            dataLoadedListener.onDataLoginFacebookLoaded(response);
        }

    }
}
