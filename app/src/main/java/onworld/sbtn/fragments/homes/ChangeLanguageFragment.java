package onworld.sbtn.fragments.homes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.cache.CacheDataManager;
import onworld.sbtn.callbacks.DataLoadedWithoutParamsListener;
import onworld.sbtn.callbacks.LanguageSelectListener;
import onworld.sbtn.josonmodel.Language;
import onworld.sbtn.josonmodel.LanguageItem;
import onworld.sbtn.tasks.TaskLoadDataWithoutParams;
import onworld.sbtn.utils.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeLanguageFragment extends Fragment implements DataLoadedWithoutParamsListener {
    public VideoCastManager mCastManager;
    private View view;
    private ListView listLanguage;
    private LanguageSelectListener mLanguageSelectListener;
    private CacheDataManager cacheDataManager;
    private ArrayList<LanguageItem> languageItem;

    public ChangeLanguageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLanguageSelectListener = (LanguageSelectListener) getActivity();
        cacheDataManager = CacheDataManager.getInstance(getActivity());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_change_language, container, false);
        listLanguage = (ListView) view.findViewById(R.id.list_language);
        mCastManager = VideoCastManager.getInstance();
        if (mCastManager != null) {
            languageItem = cacheDataManager.getCacheLanguageOfContent();
        }
        if (languageItem != null && languageItem.size() > 0) {
            setupLanguageContent(languageItem);
        } else {
            new TaskLoadDataWithoutParams(this, URL.LANGUAGE).execute();
        }


        return view;
    }

    @Override
    public void onDataLoadedWithoutParams(JSONObject jsonObject) {
        String jsonObjectString = jsonObject.toString();
        Gson gson = new Gson();
        Language language = gson.fromJson(jsonObjectString, Language.class);
        final ArrayList<LanguageItem> languageItems = language.getLanguageItems();
        setupLanguageContent(languageItems);


    }

    private void setupLanguageContent(final ArrayList<LanguageItem> languageItems) {
        String[] arrayLanguage = new String[languageItems.size() + 1];
        for (int i = 0; i < languageItems.size() + 1; i++) {
            if (i == 0) {
                arrayLanguage[0] = "All";
            } else {
                arrayLanguage[i] = languageItems.get(i - 1).getNameLanguage();
            }

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_language, arrayLanguage);
        listLanguage.setAdapter(adapter);
        listLanguage.setItemChecked(mCastManager.langSelectPosition, true);
        listLanguage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mCastManager.langOfContentId = -1;
                } else {
                    mCastManager.langOfContentId = languageItems.get(position - 1).getLanguageId();
                }
                //
                mLanguageSelectListener.onLanguageSelectListener();

                mCastManager.langSelectPosition = position;
            }
        });
    }
}
