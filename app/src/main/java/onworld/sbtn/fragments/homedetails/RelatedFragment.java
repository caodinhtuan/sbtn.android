package onworld.sbtn.fragments.homedetails;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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

import onworld.sbtn.R;
import onworld.sbtn.adapters.HomeGridAdapter;
import onworld.sbtn.callbacks.DataDetailLoadedListener;
import onworld.sbtn.josonmodel.ContentModel;
import onworld.sbtn.josonmodel.Related;
import onworld.sbtn.josonmodel.ViewDetail;
import onworld.sbtn.mediaplayer.LocalPlayerActivity;
import onworld.sbtn.tasks.TaskLoadDataDetail;
import onworld.sbtn.utils.URL;

public class RelatedFragment extends Fragment implements RecyclerView.OnItemTouchListener, DataDetailLoadedListener {
    public static final String DETAIL_GRID_SAVED = "homegridsaved";
    public static final String DETAIL_RECEIVED_DATA = "detailreceiveddata";
    private RecyclerView mHomeGridRecyclerView;
    private HomeGridAdapter mHomeGridAdapter;
    private GestureDetector gestureDetector;
    private ArrayList<Related> relateds;
    private TextView textEmpty;

    public RelatedFragment() {
    }

    public static RelatedFragment newInstance(ArrayList<Related> relateds) {
        RelatedFragment detailFragment = new RelatedFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(DETAIL_RECEIVED_DATA, relateds);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        relateds = getArguments().getParcelableArrayList(DETAIL_RECEIVED_DATA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.as_fragment_detail, container, false);
        mHomeGridRecyclerView = (RecyclerView) view.findViewById(R.id.home_grid_recyclerview);
        mHomeGridRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        textEmpty = (TextView) view.findViewById(R.id.text_empty);

        mHomeGridAdapter = new HomeGridAdapter(getActivity(), mHomeGridRecyclerView);
        mHomeGridRecyclerView.setAdapter(mHomeGridAdapter);

        if (relateds.size() == 0) {
            textEmpty.setVisibility(View.VISIBLE);
        } else {
            mHomeGridAdapter.setData(relateds);
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
        outState.putParcelableArrayList(DETAIL_GRID_SAVED, relateds);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = mHomeGridRecyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child != null && gestureDetector.onTouchEvent(e)) {
            int position = mHomeGridRecyclerView.getChildAdapterPosition(child);
            Related related = relateds.get(position);

            final String url = URL.DETAIL + related.getRelatedId();

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
        mHomeGridRecyclerView.addOnItemTouchListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHomeGridRecyclerView.removeOnItemTouchListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDataDetailLoaded(String jsonObject) {
        if (jsonObject != null) {
            String jsonString = jsonObject.toString();
            Gson gson = new Gson();
            ViewDetail jsonHome = gson.fromJson(jsonString, ViewDetail.class);
            if (jsonHome.getError() == 0) {
                ContentModel contentModel = jsonHome.getContent();
                String link = contentModel.getLink();

                long duration = contentModel.getDuration();
                String title = contentModel.getName();
                String imageLink = contentModel.getImage();

                JSONObject jsonDataSend = null;
                try {
                    jsonDataSend = new JSONObject(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
                movieMetadata.putString(MediaMetadata.KEY_TITLE, title);
                movieMetadata.addImage(new WebImage(Uri.parse(imageLink)));
                MediaInfo item = new MediaInfo.Builder(link).setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                        .setContentType("application/vnd.apple.mpegurl")//"application/vnd.apple.mpegurl"
                        .setMetadata(movieMetadata)
                        .setCustomData(jsonDataSend)
                        .setStreamDuration(duration * 1000).build();

                ((LocalPlayerActivity) getActivity()).toggPlaybackPrepare(item, jsonHome, contentModel);
            }

        }
    }
}
