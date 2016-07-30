package onworld.sbtn.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import onworld.sbtn.R;
import onworld.sbtn.josonmodel.VotingCandidateListData;
import onworld.sbtn.josonmodel.VotingRoundDetailData;
import onworld.sbtn.utils.ImageUtils;
import onworld.sbtn.utils.Utils;
import onworld.sbtn.utils.VotingUtils;
import onworld.sbtn.views.ExpandableViewItem;

/**
 * Created by onworldtv on 11/12/15.
 */
public class VotingCandidateListAdapter extends BaseAdapter {
    public static VotingProgramClickListener votingProgramClickListener;
    private Context context;
    private VotingRoundDetailData votingRoundDetailData;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private ArrayList<VotingCandidateListData> votingCandidateDatas;
    private ExpandableViewItem expandableViewItem;
    private boolean showResult;
    private int roundStatus;

    public VotingCandidateListAdapter(Context context, VotingRoundDetailData votingRoundDetailData, ArrayList<VotingCandidateListData> votingCandidateDatas) {
        this.context = context;
        this.votingRoundDetailData = votingRoundDetailData;
        this.votingCandidateDatas = votingCandidateDatas;

        options = ImageUtils.getOptionsImageRectangle();
        imageLoader = imageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

    }

    public static void setVotingProgramClickListener(VotingProgramClickListener listener) {

        votingProgramClickListener = listener;
    }

    @Override
    public int getCount() {
        int count;
        if (votingCandidateDatas == null) {
            count = 1;
        } else {
            count = votingCandidateDatas.size()+1;
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return votingRoundDetailData;
        } else {
            return votingCandidateDatas.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? 0 : 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewHolder viewHolder = null;
        View view = convertView;
        int type = getItemViewType(position);
        if (view == null) {
            if (type == 0) {
                view = myInflater.inflate(R.layout.round_detail_header, parent, false);
                viewHolder = new ViewHolder();
                //viewHolder.roundNumber = (TextView) view.findViewById(R.id.round_number);
                viewHolder.roundName = (TextView) view.findViewById(R.id.round_name);
                viewHolder.roundStartTime = (TextView) view.findViewById(R.id.start_time_round);
                viewHolder.roundEndTime = (TextView) view.findViewById(R.id.end_time_round);
                viewHolder.roundDescription = (TextView) view.findViewById(R.id.round_description);
                viewHolder.expandableRule = (LinearLayout) view.findViewById(R.id.expandable_round_rule);
                expandableViewItem = new ExpandableViewItem(context);
                expandableViewItem.setTitleExpandable("RULES");
                view.setTag(viewHolder);
                view.setOnClickListener(null);
                view.setOnLongClickListener(null);
            } else {
                view = myInflater.inflate(R.layout.voting_candidate_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.thumbnail = (ImageView) view.findViewById(R.id.img_episode_list_thumnail);
                viewHolder.txtTitle = (TextView) view.findViewById(R.id.candidate_name);
                viewHolder.txtTotalVote = (TextView) view.findViewById(R.id.candidate_vote_number);
                viewHolder.txtCandidateOrder = (TextView)view.findViewById(R.id.candidate_order);
                view.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (type == 0) {
            viewHolder.roundName.setText(votingRoundDetailData.getName());
            String startDate = votingRoundDetailData.getStartDate();
            String endDate = votingRoundDetailData.getEndDate();

            if (startDate == null || endDate == null) {

            } else if(startDate.length()>0 && endDate.length()>0){
                viewHolder.roundStartTime.setText(Utils.getDate(startDate));
                viewHolder.roundEndTime.setText(Utils.getDate(endDate));
            }

            //viewHolder.roundDescription.setText(votingRoundDetailData.getRule());
            expandableViewItem.setDescriptionExpandable(votingRoundDetailData.getRule());
            expandableViewItem.showExpandableDescription();
            //expandableViewItem.hideButton();
            if (expandableViewItem.getParent() == null) {
                //((LinearLayout) expandableViewItem.getParent()).removeView(expandableViewItem);
                viewHolder.expandableRule.addView(expandableViewItem, params);
            }

        } else {
            VotingCandidateListData votingCandidateData = votingCandidateDatas.get(position - 1);
            roundStatus = votingCandidateData.getRoundStatus();
            showResult = votingCandidateData.getShowResult();
            imageLoader.getInstance().displayImage(votingCandidateData.getAvatar(), viewHolder.thumbnail, options, animateFirstListener);
            viewHolder.txtTitle.setText(votingCandidateData.getLastName() + " " + votingCandidateData.getFirstName());
            if(showResult == true || roundStatus == 0){
                viewHolder.txtTotalVote.setText(VotingUtils.reformatDouble(votingCandidateData.getTotalVote()) + "/" + VotingUtils.reformatDouble(votingCandidateData.getVotePercent())+"%");
                viewHolder.txtTotalVote.setVisibility(View.VISIBLE);
                viewHolder.txtCandidateOrder.setText(position + "");
                viewHolder.txtCandidateOrder.setVisibility(View.VISIBLE);
            }


        }
        return view;

    }

    public void hideTotalVote(){

    }
    public interface VotingProgramClickListener {
        void votingProgramItemSelect(View view, int position);
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    private class ViewHolder {
        private ImageView thumbnail;
        private TextView txtTitle;
        private TextView txtTotalVote, txtCandidateOrder;

        private TextView roundNumber;
        private TextView roundName;
        private TextView roundStartTime;
        private TextView roundEndTime;
        private TextView roundDescription;
        private LinearLayout expandableRule;
    }
}