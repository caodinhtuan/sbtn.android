package onworld.sbtn.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.RequestQueue;

import onworld.sbtn.R;
import onworld.sbtn.callbacks.DataSignUpLoadedListener;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.volley.Requestor;
import onworld.sbtn.volley.VolleySingleton;

/**
 * Created by linhnguyen on 12/12/15.
 */
public class TaskLoadDataSignUp extends AsyncTask<Void, Void, String> {
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private DataSignUpLoadedListener dataLoadedListener;
    private String url;
    private String email, password;
    private Context mContext;

    public TaskLoadDataSignUp(Context context, DataSignUpLoadedListener dataLoadedListener, String url, String email, String password) {
        this.dataLoadedListener = dataLoadedListener;
        this.url = url;
        this.email = email;
        this.password = password;
        this.mContext = context;
        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getmRequestQueue();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        String response = Requestor.requestDataJsonSignUp(requestQueue, url, email, password);
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if (dataLoadedListener != null) {
            if (response != null) {
                dataLoadedListener.onDataSignUpLoaded(response);
            } else {
                Utils.showOopsDialog(mContext, R.string.msg_server_error_sign_up);
            }
        }

    }
}
