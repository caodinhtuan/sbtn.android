package onworld.sbtn.fragments.homedetails;


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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.activities.LoginActivity;
import onworld.sbtn.activities.PackageActivity;
import onworld.sbtn.activities.PackageDetailActivity;
import onworld.sbtn.adapters.PackageListAdapter;
import onworld.sbtn.cache.CacheDataManager;
import onworld.sbtn.callbacks.DataDetailLoadedListener;
import onworld.sbtn.callbacks.PackageListClickListener;
import onworld.sbtn.fragments.homes.OWLoginFragment;
import onworld.sbtn.josonmodel.GroupPackage;
import onworld.sbtn.josonmodel.PackagesData;
import onworld.sbtn.tasks.TaskLoadDataDetail;
import onworld.sbtn.utils.Constant;
import onworld.sbtn.utils.URL;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.utils.VotingUtils;
import onworld.sbtn.views.HorizontalDividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class PackageFragment extends Fragment implements DataDetailLoadedListener,
        RecyclerView.OnItemTouchListener {

    public static final int PACKAGE_FRAGMENT_REQUEST_CODE = 2015;
    private RecyclerView recyclerView;
    private PackageListAdapter packageListAdapter;
    private GestureDetector gestureDetector;
    private ArrayList<GroupPackage> packageDetails;
    private PackageListClickListener mPackageListClickListener;
    private ProgressBar packageLoading;
    private CacheDataManager mCacheDataManager;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private TextView emptyText;

    public PackageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_package, container, false);
        emptyText = (TextView) view.findViewById(R.id.empty_list_package);
        packageLoading = (ProgressBar) view.findViewById(R.id.package_loading);
        recyclerView = (RecyclerView) view.findViewById(R.id.package_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCacheDataManager = CacheDataManager.getInstance(getContext());
        packageListAdapter = new PackageListAdapter(getActivity());
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                .paintProvider(packageListAdapter)
                .visibilityProvider(packageListAdapter)
                .marginProvider(packageListAdapter)
                .build());
        recyclerView.setAdapter(packageListAdapter);
        mPackageListClickListener = (PackageListClickListener) getActivity();
        gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        PackagesData packagesData = null;
        if (mCacheDataManager != null) {
            packagesData = mCacheDataManager.getCachePackageData();
        }
        if (packagesData != null) {
            setupPackageData(packagesData);
        } else {
            new TaskLoadDataDetail(this, URL.PACKAGE_LIST).execute();
        }

    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null && gestureDetector.onTouchEvent(e)) {
            if (VotingUtils.checkLoginStatus(getActivity()) == true) {
                int position = recyclerView.getChildAdapterPosition(view);
                GroupPackage packageDetail = packageDetails.get(position);
                Intent intent = new Intent(getActivity(), PackageDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(PackageDetailFragment.PACKAGE_DETAIL, packageDetail);
                intent.putExtra(PackageDetailFragment.PACKAGE_DETAIL_BUNDLE, bundle);
                getActivity().startActivityForResult(intent, Constant.LOGIN_MAIN_REQUEST_CODE);
            } else {
                openDiaLogAskLogin(true);
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
        recyclerView.addOnItemTouchListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        recyclerView.removeOnItemTouchListener(this);
    }

    @Override
    public void onDataDetailLoaded(String jsonObject) {

        Gson gson = new Gson();
        PackagesData packagesData = gson.fromJson(jsonObject.toString(), PackagesData.class);
        //mCacheDataManager.cachePackageData(packagesData);
        setupPackageData(packagesData);
    }

    private void setupPackageData(PackagesData packagesData) {
        packageLoading.setVisibility(View.GONE);
        packageDetails = new ArrayList<>();
        if (packagesData.getError() == Utils.OLD_REQUEST_SUCCESS) {
            packageDetails = packagesData.getData();
            if (packageDetails.size() > 0) {
                packageListAdapter.setPackageListData(packageDetails);
                emptyText.setVisibility(View.GONE);
            } else {
                emptyText.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PACKAGE_FRAGMENT_REQUEST_CODE) {
            new TaskLoadDataDetail(this, URL.PACKAGE_LIST).execute();

            Intent intent = getActivity().getIntent();
            intent.putExtra(OWLoginFragment.IS_LOGIN, true);
            getActivity().setResult(Constant.LOGIN_MAIN_RESULT_CODE, intent);
        }
    }

    public void openDiaLogAskLogin(boolean isSignInFromPackage) {

        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(getString(R.string.msg_sign_in_to_view_content));

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra(PackageActivity.IS_SIGN_IN_FROM_PACKAGE, true);
                startActivityForResult(intent, PACKAGE_FRAGMENT_REQUEST_CODE);
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
