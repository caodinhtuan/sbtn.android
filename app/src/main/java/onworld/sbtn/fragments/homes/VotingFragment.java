package onworld.sbtn.fragments.homes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.StatedFragment;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.adapters.VotingProgramListAdapter;
import onworld.sbtn.callbacks.DataLoadedListener;
import onworld.sbtn.josonmodel.VotingProgramListData;
import onworld.sbtn.josonmodel.VotingProgramListDetailData;
import onworld.sbtn.tasks.TaskLoadData;
import onworld.sbtn.utils.NetworkUtils;
import onworld.sbtn.utils.URL;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.utils.VotingUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class VotingFragment extends StatedFragment implements DataLoadedListener {

    public static VotingProgramClickListener1 votingProgramClickListener;
    private ListView listVotingProgram;
    private ArrayList<VotingProgramListDetailData> votingProgramListDetailDatas;
    private ProgressBar mVotingLoading;

    public VotingFragment() {
        // Required empty public constructor
    }

    public static void setVotingProgramClickListener(VotingProgramClickListener1 listener) {

        votingProgramClickListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getVotingList();
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voting, container, false);
        listVotingProgram = (ListView) view.findViewById(R.id.list_voting_program);
        mVotingLoading = (ProgressBar) view.findViewById(R.id.voting_loading);

        return view;
    }

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        outState.putParcelableArrayList("VOTING_PROGRAM_LIST_DETAIL_DATA_SAVE", votingProgramListDetailDatas);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        if (savedInstanceState == null) {
            //todo
            //Restore the fragment's state here
            getVotingList();
        } else {
            ArrayList<VotingProgramListDetailData> votingProgramListDetailDatas = savedInstanceState.getParcelableArrayList("VOTING_PROGRAM_LIST_DETAIL_DATA_SAVE");
            VotingFragment.this.votingProgramListDetailDatas = votingProgramListDetailDatas;
            VotingProgramListAdapter votingProgramListAdapter = new VotingProgramListAdapter(getActivity(), votingProgramListDetailDatas);
            listVotingProgram.setAdapter(votingProgramListAdapter);

            listVotingProgram.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //votingProgramClickListener.votingProgramItemSelect(view, position);
                }
            });
            mVotingLoading.setVisibility(View.GONE);
        }
    }

    private void getVotingList() {
        String urlString = URL.VOTING_PROGRAM_LIST;
        new TaskLoadData(this, urlString).execute();
    }

    @Override
    public void onDataLoaded(String jsonObject) {
        if (jsonObject != null) {
            Gson gson = new Gson();
            VotingProgramListData votingProgramListData = gson.fromJson(jsonObject.toString(), VotingProgramListData.class);
            if (votingProgramListData.getCode() == VotingUtils.REQUEST_SUCCESS) {
                votingProgramListDetailDatas = votingProgramListData.getData();
                final VotingProgramListAdapter votingProgramListAdapter = new VotingProgramListAdapter(getActivity(), votingProgramListDetailDatas);
                listVotingProgram.setAdapter(votingProgramListAdapter);

                listVotingProgram.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //votingProgramClickListener.votingProgramItemSelect(view, position);
                    }
                });
            }
        } else {
            if (NetworkUtils.getConnectivityStatus(getActivity()) == 0) {
                Utils.alert(getActivity(), "No internet connection. Please check your connection settings and try again.");
            }
        }
        mVotingLoading.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.action_search).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public interface VotingProgramClickListener1 {
        void votingProgramItemSelect(View view, int position);
    }
}
