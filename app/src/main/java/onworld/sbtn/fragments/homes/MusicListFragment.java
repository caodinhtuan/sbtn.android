package onworld.sbtn.fragments.homes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.adapters.MusicListAdapter;
import onworld.sbtn.callbacks.DataLoadedWithoutParamsListener;
import onworld.sbtn.josonmodel.ContentModel;
import onworld.sbtn.josonmodel.InfoVideoArrayModel;
import onworld.sbtn.josonmodel.Related;
import onworld.sbtn.josonmodel.ViewDetail;
import onworld.sbtn.tasks.TaskLoadDataWithoutParams;
import onworld.sbtn.utils.URL;
import onworld.sbtn.views.HorizontalDividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicListFragment extends Fragment implements RecyclerView.OnItemTouchListener, DataLoadedWithoutParamsListener {

    public static final String MUSIC_LIST = "music list";
    public static OnMusicListClickListener onMusicListClickListener;
    ArrayList<Related> relateds = new ArrayList<>();
    RecyclerView mMusicListRelated;
    MusicListAdapter mMusicListAdapter;
    private GestureDetector gestureDetector;
    private String mMusicDescriptonText = "";
    private ArrayList<InfoVideoArrayModel> infoVideoArrayModels;

    public MusicListFragment() {
        // Required empty public constructor
    }

    public static MusicListFragment newInstance(ArrayList<Related> relateds) {
        MusicListFragment musicPlayerFragment = new MusicListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(MUSIC_LIST, relateds);
        musicPlayerFragment.setArguments(bundle);
        return musicPlayerFragment;
    }

    public static void setOnMusicListClickListener(OnMusicListClickListener listener) {
        onMusicListClickListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        relateds = getArguments().getParcelableArrayList(MUSIC_LIST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_list, container, false);
        mMusicListRelated = (RecyclerView) view.findViewById(R.id.music_list_related);
        mMusicListRelated.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mMusicListAdapter = new MusicListAdapter(getActivity());
        mMusicListRelated.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .paintProvider(mMusicListAdapter)
                .visibilityProvider(mMusicListAdapter)
                .marginProvider(mMusicListAdapter)
                .build());
        mMusicListRelated.setAdapter(mMusicListAdapter);
        mMusicListAdapter.setData(relateds);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = mMusicListRelated.findChildViewUnder(e.getX(), e.getY());
        if (child != null && gestureDetector.onTouchEvent(e)) {
            int position = mMusicListRelated.getChildAdapterPosition(child);
            Related related = relateds.get(position);
            final String url = URL.DETAIL + related.getRelatedId();

            new TaskLoadDataWithoutParams(this, url).execute();
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public void onDataLoadedWithoutParams(JSONObject jsonObject) {
        String linkMusic;
        String imageLink;
        String title;
        boolean isLive;
        ArrayList<Related> relateds = new ArrayList<>();
        Gson gson = new Gson();
        ViewDetail jsonHome = gson.fromJson(jsonObject.toString(), ViewDetail.class);
        if (jsonHome.getError() == onworld.sbtn.utils.Utils.OLD_REQUEST_SUCCESS) {
            infoVideoArrayModels = jsonHome.getActors();
            ContentModel contentModel = jsonHome.getContent();
            linkMusic = contentModel.getLink();
            title = contentModel.getName();
            isLive = contentModel.getIsLive();
            imageLink = contentModel.getImage();
            relateds = jsonHome.getRelated();
            if (infoVideoArrayModels.size() > 0) {
                int infoSize = infoVideoArrayModels.size();
                for (int i = 0; i < infoSize; i++) {
                    if (i == 0) {
                        mMusicDescriptonText = mMusicDescriptonText + infoVideoArrayModels.get(i).getName();
                    } else {
                        mMusicDescriptonText = mMusicDescriptonText + " - " + infoVideoArrayModels.get(i).getName();
                    }

                }

            } else {
            }
            onMusicListClickListener.onMusicListClick(linkMusic, title, mMusicDescriptonText, imageLink, this.relateds, isLive);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMusicListRelated.addOnItemTouchListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMusicListRelated.removeOnItemTouchListener(this);
    }

    public interface OnMusicListClickListener {
        void onMusicListClick(String linkMusic, String title, String des, String imageLink, ArrayList<Related> relateds, boolean isLive);
    }
}
