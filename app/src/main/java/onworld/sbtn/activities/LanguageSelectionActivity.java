package onworld.sbtn.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.cache.CacheDataManager;
import onworld.sbtn.callbacks.DataLoadedWithoutParamsListener;
import onworld.sbtn.josonmodel.Language;
import onworld.sbtn.josonmodel.LanguageItem;
import onworld.sbtn.tasks.TaskLoadDataWithoutParams;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.URL;
import onworld.sbtn.utils.Utils;


public class LanguageSelectionActivity extends AppCompatActivity implements DataLoadedWithoutParamsListener {
    public static final String LANGUAGE_CONTENT_ID = "languageContentId";
    public static final String PREFS_NAME = "MyPrefsLanguageFile";
    public VideoCastManager mCastManager;
    private int languageContentId;
    private Spinner spinner;
    private int languageContentIdSaved;
    private ImageView startImage;
    private LinearLayout languageAcitivtyContent;
    private CacheDataManager cacheDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_selection_activity_layout);
        startImage = (ImageView) findViewById(R.id.image_start_app);
        languageAcitivtyContent = (LinearLayout) findViewById(R.id.language_activity_content);
        spinner = (Spinner)
                findViewById(R.id.spinner);

        mCastManager = VideoCastManager.getInstance();
        cacheDataManager = CacheDataManager.getInstance(this);
        SharedPreferences settings = getSharedPreferences(this.PREFS_NAME, 0);
        boolean hasOpened = settings.getBoolean("hasOpened", false);
        languageContentIdSaved = settings.getInt(LANGUAGE_CONTENT_ID, -1);
        mCastManager.langOfContentId = languageContentIdSaved;
        mCastManager.langSelectPosition = settings.getInt(MainActivity.LANGUAGE_CONTENT_POSITION, 0);

        if (hasOpened) {
            spinner.setSelection(mCastManager.langSelectPosition, true);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(LANGUAGE_CONTENT_ID, languageContentIdSaved);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            LanguageSelectionActivity.this.finish();
        } else {
            new TaskLoadDataWithoutParams(this, URL.LANGUAGE).execute();
        }


        TextView nextBtn = (TextView) findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(LANGUAGE_CONTENT_ID, languageContentId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        LanguageSelectionActivity.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        SharedPreferences settings = getSharedPreferences(this.PREFS_NAME, 0); // 0 - for private mode
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("hasOpened", true);
        editor.putInt(LANGUAGE_CONTENT_ID, mCastManager.langOfContentId);
        editor.putInt(MainActivity.LANGUAGE_CONTENT_POSITION, mCastManager.langSelectPosition);
        editor.commit();
        super.onPause();
    }

    @Override
    public void onDataLoadedWithoutParams(JSONObject jsonObject) {
        String jsonObjectString = jsonObject.toString();
        Gson gson = new Gson();
        Language language = gson.fromJson(jsonObjectString, Language.class);
        int errorCode = language.getError();
        if (errorCode == Constant.ERROR_VERSION_CODE) {
            Utils.showUpdateNewVersionDialog(this);
        } else if (errorCode == Utils.OLD_REQUEST_SUCCESS) {
            final ArrayList<LanguageItem> languageItems = language.getLanguageItems();
            cacheDataManager.cacheLanguageOfContent(languageItems);

            if (languageItems.size() > 1) {
                startImage.setVisibility(View.GONE);
                languageAcitivtyContent.setVisibility(View.VISIBLE);
                mCastManager.isShowLanguageOfContent = true;
                String[] arrayLanguage = new String[languageItems.size() + 1];
                for (int i = 0; i < languageItems.size() + 1; i++) {
                    if (i == 0) {
                        arrayLanguage[0] = "All";
                    } else {
                        arrayLanguage[i] = languageItems.get(i - 1).getNameLanguage();
                    }

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_layout, arrayLanguage);

                adapter.setDropDownViewResource(R.layout.spinner_dropdown);

                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            languageContentId = -1;
                            mCastManager.langOfContentId = -1;
                        } else {
                            languageContentId = languageItems.get(position - 1).getLanguageId();
                            mCastManager.langOfContentId = languageItems.get(position - 1).getLanguageId();
                        }
                        mCastManager.langSelectPosition = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spinner.setSelection(0);
            } else {
                mCastManager.isShowLanguageOfContent = false;
                spinner.setSelection(mCastManager.langSelectPosition, true);
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(LANGUAGE_CONTENT_ID, languageContentIdSaved);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                LanguageSelectionActivity.this.finish();
            }
        } else {

        }


    }

}
