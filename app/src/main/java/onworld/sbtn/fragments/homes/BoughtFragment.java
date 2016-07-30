package onworld.sbtn.fragments.homes;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.activities.DetailActivity;
import onworld.sbtn.activities.LoginActivity;
import onworld.sbtn.activities.MusicPlayerActivity;
import onworld.sbtn.adapters.CategoryRecyclerHorizontalHolder;
import onworld.sbtn.adapters.CategoryRecyclerVerticalApdater;
import onworld.sbtn.cache.CacheDataManager;
import onworld.sbtn.callbacks.BoughtContentClickListener;
import onworld.sbtn.callbacks.DataDetailLoadedListener;
import onworld.sbtn.josonmodel.DataDetailItem;
import onworld.sbtn.josonmodel.Shows;
import onworld.sbtn.josonmodel.ShowsHome;
import onworld.sbtn.mediaplayer.LocalPlayerActivity;
import onworld.sbtn.tasks.TaskLoadDataDetail;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.URL;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.utils.VotingUtils;
import onworld.sbtn.views.HorizontalDividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class BoughtFragment extends Fragment implements DataDetailLoadedListener,
        RecyclerView.OnItemTouchListener {
    public static final int BOUGHT_FRAGMENT_REQUEST = 1111;
    public static final int BOUGHT_FRAGMENT_RESULT = 1112;
    public static final String TAG = "BoughtFragment";
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private ImageView mEmptyBought;
    private CategoryRecyclerVerticalApdater mCategoryRecyclerVerticalApdater;
    private GestureDetector gestureDetector;
    private ArrayList<Shows> showses;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private LinearLayout emptyLayout;
    private Button mBuyContent;
    private BoughtContentClickListener boughtContentClickListener;
    private CacheDataManager mCacheDataManager;

    public BoughtFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bought, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.bought_recyclerview);
        mProgressBar = (ProgressBar) view.findViewById(R.id.bought_loading);
        mEmptyBought = (ImageView) view.findViewById(R.id.bought_text_tempty);
        emptyLayout = (LinearLayout) view.findViewById(R.id.bought_layout_empty);
        mBuyContent = (Button) view.findViewById(R.id.bought_buy_content);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCategoryRecyclerVerticalApdater = new CategoryRecyclerVerticalApdater(getActivity());
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .paintProvider(mCategoryRecyclerVerticalApdater)
                .visibilityProvider(mCategoryRecyclerVerticalApdater)
                .marginProvider(mCategoryRecyclerVerticalApdater)
                .build());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mCategoryRecyclerVerticalApdater);
        mCacheDataManager = CacheDataManager.getInstance(getActivity());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        boughtContentClickListener = (BoughtContentClickListener) getActivity();
        gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        if (VotingUtils.checkLoginStatus(getActivity()) == true) {
            mProgressBar.setVisibility(View.VISIBLE);
            ShowsHome showsHome = null;
            if (mCacheDataManager != null) {
                showsHome = mCacheDataManager.getCacheYourPackageContent();
            }
            if (showsHome != null && showsHome.getShowses().size() > 0) {
                setupYourContentData(showsHome);
            } else {
                new TaskLoadDataDetail(this, URL.PACKAGE_LIST_CONTENT).execute();
            }

        } else {
            //you have to login to view your content
            openDiaLogAskLogin();
        }
        mBuyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boughtContentClickListener.onBoughtBuyContentClickListener();
            }
        });
    }

    private void setupYourContentData(ShowsHome showsHome) {
        mProgressBar.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
        showses = new ArrayList<>();
        ArrayList<Shows> showsesList = showsHome.getShowses();
        if (showsHome.getError() == Utils.OLD_REQUEST_SUCCESS) {
            showsesList = showsHome.getShowses();
            int realShowSize = showsesList.size() * 2;
            if (realShowSize > 0) {
                for (int i = 0; i < realShowSize; i++) {
                    Shows mShows = new Shows();
                    if (i % 2 == 0) {
                        mShows = showsesList.get(i / 2);
                    } else {
                        mShows = showsesList.get(i / 2);
                    }
                    this.showses.add(mShows);
                }
                mCategoryRecyclerVerticalApdater.setHomeVerticalData(this.showses);


            } else {
                emptyLayout.setVisibility(View.VISIBLE);
            }

        } else {
            //you have to login to view bought
        }
    }

    @Override
    public void onDataDetailLoaded(String jsonObject) {
        showses = new ArrayList<>();
        ArrayList<Shows> showses;
        Gson viewGson = new Gson();
        ShowsHome showsHome = viewGson.fromJson(jsonObject.toString(), ShowsHome.class);
        mCacheDataManager.cacheYourPackageContent(showsHome);
        setupYourContentData(showsHome);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child != null && gestureDetector.onTouchEvent(e)) {
            int position = mRecyclerView.getChildAdapterPosition(child);
            if (position % 2 == 0) {
                Shows shows = showses.get(position);
                ArrayList<DataDetailItem> dataDetailItems = shows.getShowsDetails();
                if (dataDetailItems.size() > 6) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putParcelableArrayListExtra(DetailActivity.ASGRID_DETAIL_DATA, dataDetailItems);
                    intent.putExtra(DetailActivity.ASGRID_DETAIL_TITLE, shows.getShowsName());
                    intent.putExtra(HomeFragment.AS_HOME_TYPE, shows.getMode());
                    getActivity().startActivityForResult(intent, Constant.LOGIN_MAIN_REQUEST_CODE);
                }

            } else {
                CategoryRecyclerHorizontalHolder homeRecyclerHorizontalHolder =
                        (CategoryRecyclerHorizontalHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
                int childPosition = homeRecyclerHorizontalHolder.getChildItemPosition(e.getX() - child.getX(), e.getY() - child.getY());
                if (childPosition < 0) {
                    return false;
                }
                if (showses != null) {
                    Shows shows = showses.get(position);
                    ArrayList<DataDetailItem> showsDetails = shows.getShowsDetails();
                    DataDetailItem dataDetailItem = showsDetails.get(childPosition);
                    final String url = URL.DETAIL + dataDetailItem.getId();
                    Intent intent;
                    if (shows.getMode() == HomeFragment.AUDIO_TYPE) {
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
        mRecyclerView.addOnItemTouchListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRecyclerView.removeOnItemTouchListener(this);
        super.onPause();
    }

    public void openDiaLogAskLogin() {

        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(getString(R.string.msg_sign_in_to_view_content));

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, BOUGHT_FRAGMENT_REQUEST);
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                emptyLayout.setVisibility(View.VISIBLE);
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BOUGHT_FRAGMENT_REQUEST) {
            mProgressBar.setVisibility(View.VISIBLE);
            new TaskLoadDataDetail(this, URL.PACKAGE_LIST_CONTENT).execute();
        }
    }
}
