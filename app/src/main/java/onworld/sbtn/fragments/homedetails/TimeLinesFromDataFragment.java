package onworld.sbtn.fragments.homedetails;


import android.content.Context;
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

import org.json.JSONObject;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.adapters.TimeLinesAdapter;
import onworld.sbtn.josonmodel.Episode;
import onworld.sbtn.josonmodel.TimeLine;
import onworld.sbtn.mediaplayer.LocalPlayerActivity;
import onworld.sbtn.views.HorizontalDividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeLinesFromDataFragment extends Fragment implements RecyclerView.OnItemTouchListener {


    public static final String DETAIL_LIST_SAVED = "detaillistsaved";
    public static final String LIST_DETAIL_RECEIVED_DATA = "listdetailreceiveddata";
    public static final String TIMELINE_LIVE = "time line live";
    public static final String DAY_TITLE = "day.title";
    private RecyclerView mListRecyclerView;
    private TimeLinesAdapter mListAdapter;
    private GestureDetector gestureDetector;
    private ArrayList<TimeLine> timeLines;
    private TextView textEmpty, timelineTile;
    private MediaInfo item;
    private String link;
    private long duration;
    private String title;
    private String imageLink;
    private JSONObject jsonDataSend;
    private boolean isLive;
    private Context context;
    private String dayTitle;

    public TimeLinesFromDataFragment() {
    }

    public static TimeLinesFromDataFragment newInstance(ArrayList<TimeLine> episodes, boolean isLive) {
        TimeLinesFromDataFragment detailFragment = new TimeLinesFromDataFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(LIST_DETAIL_RECEIVED_DATA, episodes);
        bundle.putBoolean(TIMELINE_LIVE, isLive);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeLines = getArguments().getParcelableArrayList(LIST_DETAIL_RECEIVED_DATA);
        isLive = getArguments().getBoolean(TIMELINE_LIVE);
        dayTitle = getArguments().getString(DAY_TITLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aslist_from_data, container, false);
        mListRecyclerView = (RecyclerView) view.findViewById(R.id.home_grid_recyclerview);
        mListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        timelineTile = (TextView) view.findViewById(R.id.timeline_title);
        textEmpty = (TextView) view.findViewById(R.id.text_empty);
        mListAdapter = new TimeLinesAdapter(getActivity(), false);
        mListRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .paintProvider(mListAdapter)
                .visibilityProvider(mListAdapter)
                .marginProvider(mListAdapter)
                .build());
        mListRecyclerView.setAdapter(mListAdapter);

        if (timeLines.size() == 0) {
            textEmpty.setVisibility(View.VISIBLE);
        } else {
            mListAdapter.setData(timeLines);
        }
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(DETAIL_LIST_SAVED, timeLines);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = mListRecyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child != null && gestureDetector.onTouchEvent(e)) {
            int position = mListRecyclerView.getChildAdapterPosition(child);
            TimeLine timeLine = timeLines.get(position);
            link = timeLine.getLink();
            String title = timeLine.getName();
            if (isLive) {
                ((LocalPlayerActivity) getActivity()).playVideo(timeLine.getId(), link, title, true);
            } else {
                Episode episode = new Episode();
                episode.setEpisodeId(timeLine.getId());
                episode.setEpisodeImage(timeLine.getImage());
                episode.setEpisodeLink(timeLine.getLink());
                episode.setEpisodeName(timeLine.getName());
                episode.setStartTime(timeLine.getStart());
                ((LocalPlayerActivity) getActivity()).playTimeline(episode);
            }


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
}