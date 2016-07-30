package onworld.sbtn.fragments.homedetails;


import android.content.Intent;
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

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.activities.MusicPlayerActivity;
import onworld.sbtn.adapters.HomeGridAdapter;
import onworld.sbtn.fragments.homes.HomeFragment;
import onworld.sbtn.josonmodel.Related;
import onworld.sbtn.mediaplayer.LocalPlayerActivity;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.URL;

public class SearchResultFragment extends Fragment implements RecyclerView.OnItemTouchListener {
    public static final String DETAIL_GRID_SAVED = "homegridsaved";
    public static final String DETAIL_RECEIVED_DATA = "detailreceiveddata";
    public static final String DETAIL_RECEIVED_BUNDLE = "detailreceivedbundle";
    private RecyclerView mHomeGridRecyclerView;
    private HomeGridAdapter mHomeGridAdapter;
    private GestureDetector gestureDetector;
    private ArrayList<Related> relateds;
    private TextView textEmpty;
    private int type;

    public SearchResultFragment() {
    }

    public static SearchResultFragment newInstance(ArrayList<Related> relateds, int type) {
        SearchResultFragment detailFragment = new SearchResultFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(DETAIL_RECEIVED_DATA, relateds);
        bundle.putInt(HomeFragment.AS_HOME_TYPE, type);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        relateds = getArguments().getParcelableArrayList(DETAIL_RECEIVED_DATA);
        type = getArguments().getInt(HomeFragment.AS_HOME_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.as_fragment_detail, container, false);
        mHomeGridRecyclerView = (RecyclerView) view.findViewById(R.id.home_grid_recyclerview);

        //mHomeGridRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        final GridLayoutManager manager;
        if (type == HomeFragment.AUDIO_TYPE) {
            manager = new GridLayoutManager(getActivity(), 3);
            mHomeGridRecyclerView.setLayoutManager(manager);
        } else {
            manager = new GridLayoutManager(getActivity(), 2);
            mHomeGridRecyclerView.setLayoutManager(manager);
        }
        //mHomeGridRecyclerView.addItemDecoration(new MarginDecoration(getActivity()));
        textEmpty = (TextView) view.findViewById(R.id.text_empty);

        mHomeGridAdapter = new HomeGridAdapter(getActivity(), mHomeGridRecyclerView);
        mHomeGridRecyclerView.setAdapter(mHomeGridAdapter);
        if (savedInstanceState != null) {
            relateds = savedInstanceState.getParcelableArrayList(DETAIL_GRID_SAVED);
            if (relateds.size() == 0) {
                textEmpty.setVisibility(View.VISIBLE);
            } else {
                mHomeGridAdapter.setData(relateds);
            }
        } else {
            if (relateds.size() == 0) {
                textEmpty.setVisibility(View.VISIBLE);
            } else {
                mHomeGridAdapter.setData(relateds);
            }

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
            Intent intent;
            if (type == HomeFragment.AUDIO_TYPE) {
                intent = new Intent(getActivity(), MusicPlayerActivity.class);
                intent.putExtra(MusicPlayerActivity.THUMB, related.getRelatedImage());
            } else {
                intent = new Intent(getActivity(), LocalPlayerActivity.class);
                intent.putExtra(LocalPlayerActivity.SHOULD_START, false);
            }
            intent.putExtra(Constant.DETAIL_ID_KEY, related.getRelatedId());
            startActivity(intent);

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

}
