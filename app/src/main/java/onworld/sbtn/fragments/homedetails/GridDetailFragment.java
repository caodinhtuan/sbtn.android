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

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.activities.DetailActivity;
import onworld.sbtn.activities.MusicPlayerActivity;
import onworld.sbtn.adapters.GridDetailAdapter;
import onworld.sbtn.fragments.homes.HomeFragment;
import onworld.sbtn.josonmodel.DataDetailItem;
import onworld.sbtn.mediaplayer.LocalPlayerActivity;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.URL;

public class GridDetailFragment extends Fragment implements
        RecyclerView.OnItemTouchListener {
    private RecyclerView mHomeGridRecyclerView;
    private GridDetailAdapter mHomeGridAdapter;
    private ArrayList<DataDetailItem> dataDetailItems = new ArrayList<>();
    private GestureDetector gestureDetector;
    private int type;
    private String title;

    public GridDetailFragment() {
    }

    public static GridDetailFragment newInstance(ArrayList<DataDetailItem> dataDetailItems, String title, int type) {
        GridDetailFragment homeGridFragment = new GridDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(DetailActivity.ASGRID_DETAIL_DATA, dataDetailItems);
        bundle.putInt(HomeFragment.AS_HOME_TYPE, type);
        bundle.putString(DetailActivity.ASGRID_DETAIL_TITLE, title);
        homeGridFragment.setArguments(bundle);
        return homeGridFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataDetailItems = getArguments().getParcelableArrayList(DetailActivity.ASGRID_DETAIL_DATA);
        type = getArguments().getInt(HomeFragment.AS_HOME_TYPE);
        title = getArguments().getString(DetailActivity.ASGRID_DETAIL_TITLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.as_fragment_grid_detail, container, false);
        mHomeGridRecyclerView = (RecyclerView) view.findViewById(R.id.home_grid_recyclerview);
        mHomeGridRecyclerView.setHasFixedSize(true);
        final GridLayoutManager manager;
        if (type == HomeFragment.AUDIO_TYPE) {
            manager = new GridLayoutManager(getActivity(), 3);
            mHomeGridRecyclerView.setLayoutManager(manager);
        } else {
            manager = new GridLayoutManager(getActivity(), 2);
            mHomeGridRecyclerView.setLayoutManager(manager);
        }

        mHomeGridAdapter = new GridDetailAdapter(getActivity(), mHomeGridRecyclerView, title, type);
        mHomeGridRecyclerView.setAdapter(mHomeGridAdapter);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mHomeGridAdapter.isHeader(position) ? manager.getSpanCount() : 1;
            }
        });
        if (savedInstanceState != null) {
            dataDetailItems = savedInstanceState.getParcelableArrayList(DetailActivity.ASGRID_DETAIL_DATA);
            title = savedInstanceState.getString(DetailActivity.ASGRID_DETAIL_TITLE, "");
            type = savedInstanceState.getInt(HomeFragment.AS_HOME_TYPE, 0);
            mHomeGridAdapter.setData(dataDetailItems, title, type);
        } else {
            mHomeGridAdapter.setData(dataDetailItems, title, type);
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
        outState.putParcelableArrayList(DetailActivity.ASGRID_DETAIL_DATA, dataDetailItems);
        outState.putString(DetailActivity.ASGRID_DETAIL_TITLE, title);
        outState.putInt(HomeFragment.AS_HOME_TYPE, type);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = mHomeGridRecyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child != null && gestureDetector.onTouchEvent(e)) {
            int position = mHomeGridRecyclerView.getChildAdapterPosition(child);
            if (position == 0) {

            } else {
                DataDetailItem dataDetailItem = dataDetailItems.get(position - 1);
                final String url = URL.DETAIL + dataDetailItem.getId();
                Intent intent;
                if (type == HomeFragment.AUDIO_TYPE) {
                    intent = new Intent(getActivity(), MusicPlayerActivity.class);
                    intent.putExtra(MusicPlayerActivity.THUMB, dataDetailItem.getImage());

                } else {
                    intent = new Intent(getActivity(), LocalPlayerActivity.class);
                    intent.putExtra(LocalPlayerActivity.SHOULD_START, false);
                }
                intent.putExtra(Constant.DETAIL_ID_KEY, dataDetailItem.getId());

                getActivity().startActivityForResult(intent, Constant.LOGIN_MAIN_REQUEST_CODE);
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
        mHomeGridRecyclerView.addOnItemTouchListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHomeGridRecyclerView.removeOnItemTouchListener(this);
    }

}
