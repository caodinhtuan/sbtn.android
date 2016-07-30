package onworld.sbtn.fragments.homedetails;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.common.images.WebImage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import onworld.sbtn.MyApplication;
import onworld.sbtn.R;
import onworld.sbtn.adapters.ListAdapter;
import onworld.sbtn.callbacks.DataDetailLoadedListener;
import onworld.sbtn.josonmodel.ContentModel;
import onworld.sbtn.josonmodel.Episode;
import onworld.sbtn.josonmodel.ViewDetail;
import onworld.sbtn.mediaplayer.LocalPlayerActivity;
import onworld.sbtn.tasks.TaskLoadDataDetail;
import onworld.sbtn.utils.URL;
import onworld.sbtn.views.HorizontalDividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFromDataFragment extends Fragment implements RecyclerView.OnItemTouchListener, DataDetailLoadedListener {


    public static final String DETAIL_LIST_SAVED = "detaillistsaved";
    public static final String LIST_DETAIL_RECEIVED_DATA = "listdetailreceiveddata";
    private RecyclerView mListRecyclerView;
    private ListAdapter mListAdapter;
    private GestureDetector gestureDetector;
    private ArrayList<Episode> episodes;
    private TextView textEmpty;
    private MediaInfo item;
    private String link;
    private long duration;
    private String title;
    private String imageLink;
    private JSONObject jsonDataSend;
    private Context context;

    public ListFromDataFragment() {
    }

    public static ListFromDataFragment newInstance(ArrayList<Episode> episodes) {
        ListFromDataFragment detailFragment = new ListFromDataFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(LIST_DETAIL_RECEIVED_DATA, episodes);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        episodes = getArguments().getParcelableArrayList(LIST_DETAIL_RECEIVED_DATA);
        context = MyApplication.getAppContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aslist_from_data, container, false);
        mListRecyclerView = (RecyclerView) view.findViewById(R.id.home_grid_recyclerview);
        mListRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        textEmpty = (TextView) view.findViewById(R.id.text_empty);
        mListAdapter = new ListAdapter(MyApplication.getAppContext(), false);
        mListRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .paintProvider(mListAdapter)
                .visibilityProvider(mListAdapter)
                .marginProvider(mListAdapter)
                .build());
        mListRecyclerView.setAdapter(mListAdapter);
        if (episodes.size() == 0) {
            textEmpty.setVisibility(View.VISIBLE);
        } else {
            mListAdapter.setData(episodes);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(DETAIL_LIST_SAVED, episodes);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = mListRecyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child != null && gestureDetector.onTouchEvent(e)) {
            int position = mListRecyclerView.getChildAdapterPosition(child);
            Episode episode = episodes.get(position);
            final String url = URL.DETAIL + episode.getEpisodeId();

            new TaskLoadDataDetail(this, url).execute();
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
    public void onResume() {
        super.onResume();
        mListRecyclerView.addOnItemTouchListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mListRecyclerView.removeOnItemTouchListener(this);
    }

    @Override
    public void onDataDetailLoaded(String jsonObject) {
        if (jsonObject != null) {
            String jsonString = jsonObject.toString();
            Gson gson = new Gson();
            ViewDetail jsonHome = gson.fromJson(jsonString, ViewDetail.class);
            if (jsonHome.getError() == 0) {
                ContentModel contentModel = jsonHome.getContent();
                link = contentModel.getLink();

                duration = contentModel.getDuration();
                title = contentModel.getName();
                imageLink = contentModel.getImage();

                try {
                    jsonDataSend = new JSONObject(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
                movieMetadata.putString(MediaMetadata.KEY_TITLE, title);
                movieMetadata.addImage(new WebImage(Uri.parse(imageLink)));
                item = new MediaInfo.Builder(link).setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                        .setContentType("application/vnd.apple.mpegurl")//"application/vnd.apple.mpegurl"
                        .setMetadata(movieMetadata)
                        .setCustomData(jsonDataSend)
                        .setStreamDuration(duration * 1000).build();

                ((LocalPlayerActivity) getActivity()).toggPlaybackPrepare(item, jsonHome, contentModel);
            }

        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
