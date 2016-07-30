package onworld.sbtn.fragments.homedetails;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.adapters.VotingCandidateListAdapter;
import onworld.sbtn.callbacks.DataLoadedListener;
import onworld.sbtn.josonmodel.VotingCandidateData;
import onworld.sbtn.josonmodel.VotingCandidateListData;
import onworld.sbtn.josonmodel.VotingRoundData;
import onworld.sbtn.josonmodel.VotingRoundDetailData;
import onworld.sbtn.tasks.TaskLoadData;
import onworld.sbtn.utils.NetworkUtils;
import onworld.sbtn.utils.URL;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.utils.VotingUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class VotingRoundDetailFragment extends Fragment implements DataLoadedListener {
    private static VotingCandidateClickListener votingCandidateClickListener;
    ListView listVotingCandidate;
    VotingRoundDetailData votingRoundDetailData;
    int mTotalItemCount;
    private int roundId;
    private int candidateId;
    private int totalPage;
    private int currentPage, roundStatus;
    private boolean showResult;
    private LoadMore loadMore;
    private VotingCandidateListAdapter votingProgramListAdapter;
    private ArrayList<VotingCandidateListData> votingCandidateDatas;
    private RelativeLayout loadingView;
    private VotingCandidateData votingCandidateData;
    private ProgressBar mVotingCandidateLoading;

    public VotingRoundDetailFragment() {
        // Required empty public constructor
    }

    public static VotingRoundDetailFragment newInstance(int votingId) {
        VotingRoundDetailFragment votingRoundDetailFragment = new VotingRoundDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("ROUND_ID", votingId);
        votingRoundDetailFragment.setArguments(bundle);
        return votingRoundDetailFragment;
    }

    public static void setVotingCandidateClickListener(VotingCandidateClickListener listener) {
        votingCandidateClickListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voting_candidate, container, false);
        listVotingCandidate = (ListView) view.findViewById(R.id.list_voting_candidate);
        loadingView = (RelativeLayout) view.findViewById(R.id.loadingView);
        mVotingCandidateLoading = (ProgressBar) view.findViewById(R.id.voting_candidate_loading);
        roundId = getArguments().getInt("ROUND_ID");
        getVotingCandidate();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        votingRoundDetailData = new VotingRoundDetailData();
        votingCandidateDatas = new ArrayList<>();

        super.onActivityCreated(savedInstanceState);
    }

    public void getVotingCandidate() {
        //MainActivity.showDialog();
        String urlString = URL.VOTING_ROUND_DETAIL + roundId;
        new TaskLoadData(this, urlString).execute();
        loadMore = LoadMore.FIRST;
    }

    @Override
    public void onDataLoaded(String jsonObject) {
        if (jsonObject != null) {
            Gson gson = new Gson();
            final VotingRoundData votingRoundData = gson.fromJson(jsonObject.toString(), VotingRoundData.class);
            if (votingRoundData.getCode() == VotingUtils.REQUEST_SUCCESS) {
                votingRoundDetailData = votingRoundData.getData();
                roundStatus = votingRoundDetailData.getRoundStatus();
                showResult = votingRoundDetailData.getShowResult();
                votingCandidateData = votingRoundDetailData.getExminees();
                totalPage = votingCandidateData.getTotalPage();
                currentPage = votingCandidateData.getCurrentPage();
                if (votingCandidateData.getData().size() != 0) {

                    for (int i = 0; i < votingCandidateData.getData().size(); i++) {
                        VotingCandidateListData votingCandidateListData = votingCandidateData.getData().get(i);
                        votingCandidateListData.setRoundStatus(roundStatus);
                        votingCandidateListData.setShowResult(showResult);
                        votingCandidateDatas.add(votingCandidateListData);
                    }
                }
                if (loadMore == LoadMore.FIRST) {

                    votingProgramListAdapter = new VotingCandidateListAdapter(getActivity(), votingRoundDetailData, votingCandidateDatas);
                    listVotingCandidate.setAdapter(votingProgramListAdapter);
                    listVotingCandidate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            candidateId = votingCandidateDatas.get(position - 1).getId();
                            votingCandidateClickListener.onVotingCandidateListener(roundId, candidateId);
                        }
                    });
                    listVotingCandidate.setOnScrollListener(onScrollListener());
                }

                votingProgramListAdapter.notifyDataSetChanged();
                loadingView.setVisibility(View.INVISIBLE);
            }

        } else {
            if (NetworkUtils.getConnectivityStatus(getActivity()) == 0) {
                Utils.alert(getActivity(), "No internet connection. Please check your connection settings and try again.");
            }
        }

        mVotingCandidateLoading.setVisibility(View.GONE);
    }

    private AbsListView.OnScrollListener onScrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int count = listVotingCandidate.getCount();
                int nextPage = currentPage + 1;
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listVotingCandidate.getLastVisiblePosition() == count - 1 && nextPage <= totalPage) {
                        loadingView.setVisibility(View.VISIBLE);

                        String urlString = URL.VOTING_ROUND_DETAIL + roundId + "&page_index=" + nextPage;
                        new TaskLoadData(VotingRoundDetailFragment.this, urlString).execute();
                        loadMore = LoadMore.MORE;
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mTotalItemCount = totalItemCount;


            }
        };
    }

    public enum LoadMore {
        FIRST, MORE

    }

    public interface VotingCandidateClickListener {
        void onVotingCandidateListener(int roundId, int candidateId);
    }
}
