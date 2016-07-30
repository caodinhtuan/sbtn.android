package onworld.sbtn.fragments.homedetails;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.StatedFragment;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.adapters.ImageSlidePagerAdapter;
import onworld.sbtn.callbacks.DataLoadedListener;
import onworld.sbtn.josonmodel.VotingProgramData;
import onworld.sbtn.josonmodel.VotingProgramDetailData;
import onworld.sbtn.josonmodel.VotingRoundItemData;
import onworld.sbtn.tasks.TaskLoadData;
import onworld.sbtn.utils.NetworkUtils;
import onworld.sbtn.utils.URL;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.utils.VotingUtils;
import onworld.sbtn.views.ExpandableViewItem;
import onworld.sbtn.views.RoundViewItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class VotingProgramFragment extends StatedFragment implements RoundViewItem.RoundViewItemListener, ImageSlidePagerAdapter.ChangeImageSlideListener, DataLoadedListener {

    private static OpenRoundDetailListener openRoundDetailListener;
    LinearLayout roundViewItemLayout;
    LinearLayout expandableViewItemLayout;
    String[] arrImageSlide;
    int imageSlideLength;
    private ViewPager imageSlideVotingProgramPager;
    private int votingId;
    private TextView title;
    private TextView startDate;
    private TextView endDate;
    private TextView startDateTitle, endDateTitle;
    private VotingProgramDetailData votingProgramDetailData;
    private int isMultilRound;
    private ProgressBar mVotingProgramLoading;

    public VotingProgramFragment() {
        // Required empty public constructor
    }

    public static VotingProgramFragment newInstance(int votingId) {
        VotingProgramFragment votingProgramFragment = new VotingProgramFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("VOTING_ID", votingId);
        votingProgramFragment.setArguments(bundle);
        return votingProgramFragment;
    }

    public static void setOpenRoundDetailListener(OpenRoundDetailListener listener) {
        openRoundDetailListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        votingId = getArguments().getInt("VOTING_ID");
        getVotingProgramData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voting_program, container, false);
        roundViewItemLayout = (LinearLayout) view.findViewById(R.id.round_view_item);
        expandableViewItemLayout = (LinearLayout) view.findViewById(R.id.expandable_view_item);
        imageSlideVotingProgramPager = (ViewPager) view.findViewById(R.id.pager_slide_voting_program);
        mVotingProgramLoading = (ProgressBar) view.findViewById(R.id.voting_program_loading);
        title = (TextView) view.findViewById(R.id.title_program);
        startDate = (TextView) view.findViewById(R.id.start_time_program);
        endDate = (TextView) view.findViewById(R.id.end_time_program);
        startDateTitle = (TextView) view.findViewById(R.id.start_votting_program);
        endDateTitle = (TextView) view.findViewById(R.id.end_votting_program);
        return view;
    }

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        outState.putParcelable("VOTING_PROGRAM_DETAIL_DATA_SAVE", votingProgramDetailData);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        if (savedInstanceState == null) {
            //todo
            //Restore the fragment's state here
            getVotingProgramData();
        } else {
            VotingProgramDetailData votingProgramDetailData = savedInstanceState.getParcelable("VOTING_PROGRAM_DETAIL_DATA_SAVE");
            VotingProgramFragment.this.votingProgramDetailData = votingProgramDetailData;
            loadView(votingProgramDetailData);
            mVotingProgramLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void roundItemDetailListener(RoundViewItem roundViewItem, VotingRoundItemData votingRoundItemData) {
        openRoundDetailListener.onOpenRoundDetail(votingRoundItemData.getId());
    }

    public void getVotingProgramData() {
        //MainActivity.showDialog();
        String urlString = URL.VOTING_PROGRAM_DETAIL + votingId;
        new TaskLoadData(this, urlString).execute();
    }

    private void loadView(VotingProgramDetailData votingProgramDetailData) {
        ArrayList<VotingRoundItemData> votingRoundItemDatas = votingProgramDetailData.getRound();
        title.setText(votingProgramDetailData.getTitle());
        startDate.setText(Utils.getDate(votingProgramDetailData.getStartDate()));
        endDate.setText(Utils.getDate(votingProgramDetailData.getEndDate()));
        startDateTitle.setVisibility(View.VISIBLE);
        endDateTitle.setVisibility(View.VISIBLE);
        isMultilRound = votingProgramDetailData.getIsMultilRound();


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (votingRoundItemDatas != null) {
            arrImageSlide = votingProgramDetailData.getImages();
            //String temp = arrImageSlide[0];
            //String tem2 = VotingUtils.updateImageUrl(arrImageSlide[0],"640x240");
            imageSlideLength = arrImageSlide.length;
            int numberRound = votingRoundItemDatas.size();

            for (int i = 0; i < numberRound; i++) {
                RoundViewItem roundViewItem = new RoundViewItem(getActivity(), votingRoundItemDatas.get(i));
                roundViewItem.setRoundViewItemListener(VotingProgramFragment.this);
                VotingRoundItemData votingRoundItemData = votingRoundItemDatas.get(i);
                int status = votingRoundItemData.getStatus();

                if (isMultilRound == 0) {
                    roundViewItem.setTitle("VOTE NOW");
                } else {
                    roundViewItem.setTitle(votingRoundItemData.getName());
                }
                if (status == 0) {
                    roundViewItem.setBackgroundRoundLayout((R.color.background_onworld_grey));
                }
                VotingProgramFragment.this.roundViewItemLayout.addView(roundViewItem, params);
            }
            for (int j = 0; j < 4; j++) {
                ExpandableViewItem expandableViewItem = new ExpandableViewItem(getActivity());
                if (j == 0) {
                    expandableViewItem.setTitleExpandable("DESCRIPTIONS");
                    expandableViewItem.setDescriptionExpandable(votingProgramDetailData.getDescription());
                    expandableViewItem.showExpandableDescription();
                } else if (j == 1) {
                    expandableViewItem.setTitleExpandable("ORGANIZATIONS");
                    expandableViewItem.setDescriptionExpandable(votingProgramDetailData.getOwner());
                } else if (j == 2) {
                    expandableViewItem.setTitleExpandable("CANDIDATES");
                    expandableViewItem.setDescriptionExpandable(votingProgramDetailData.getExaminee());
                } else if (j == 3) {
                    expandableViewItem.setTitleExpandable("RULES");
                    expandableViewItem.setDescriptionExpandable(votingProgramDetailData.getRule());
                }
                if (expandableViewItem.getParent() == null) {
                    //((LinearLayout)expandableViewItem.getParent()).removeView(expandableViewItem);
                    VotingProgramFragment.this.expandableViewItemLayout.addView(expandableViewItem, params);
                }

            }

            if (arrImageSlide != null) {
                ImageSlidePagerAdapter imageSlidePagerAdapter = new ImageSlidePagerAdapter(getActivity(), arrImageSlide, false);
                imageSlideVotingProgramPager.setAdapter(imageSlidePagerAdapter);
                imageSlidePagerAdapter.setChangeImageSlideListener(VotingProgramFragment.this);
            }

        }
    }

    @Override
    public void onNextImageSlideListener() {
        int current = imageSlideVotingProgramPager.getCurrentItem();
        int next = current + 1;
        if (next < imageSlideLength) {
            imageSlideVotingProgramPager.setCurrentItem(next, true);
        } else {
            imageSlideVotingProgramPager.setCurrentItem(0, true);
        }

    }

    @Override
    public void onPreviousImageSlideListener() {
        int current = imageSlideVotingProgramPager.getCurrentItem();
        int pre = current - 1;
        if (pre >= 0) {
            imageSlideVotingProgramPager.setCurrentItem(pre, true);
        } else {
            imageSlideVotingProgramPager.setCurrentItem(imageSlideLength - 1, true);
        }
    }

    @Override
    public void onDataLoaded(String jsonObject) {
        if (jsonObject != null) {
            Gson gson = new Gson();
            VotingProgramData votingProgramData = gson.fromJson(jsonObject.toString(), VotingProgramData.class);
            if (votingProgramData.getCode() == VotingUtils.REQUEST_SUCCESS) {
                votingProgramDetailData = votingProgramData.getData();
                loadView(votingProgramDetailData);
            }
        } else {
            if (NetworkUtils.getConnectivityStatus(getActivity()) == 0) {
                Utils.alert(getActivity(), "No internet connection. Please check your connection settings and try again.");
            }
        }
        //MainActivity.hideDialog();
        mVotingProgramLoading.setVisibility(View.GONE);
    }

    public interface OpenRoundDetailListener {
        void onOpenRoundDetail(int id);
    }

}
