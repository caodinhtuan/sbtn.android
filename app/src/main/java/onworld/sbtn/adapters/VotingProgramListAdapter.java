package onworld.sbtn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.josonmodel.Episode;
import onworld.sbtn.josonmodel.VotingProgramListDetailData;
import onworld.sbtn.utils.ImageUtils;
import onworld.sbtn.utils.Utils;

/**
 * Created by onworldtv on 11/2/15.
 */
public class VotingProgramListAdapter extends BaseAdapter {
    public static VotingProgramClickListener votingProgramClickListener;
    private Context context;
    private ArrayList<Episode> episodes;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private ArrayList<VotingProgramListDetailData> votingProgramListDetailDatas;
    private int votingId;
    private int voteStatus;

    public VotingProgramListAdapter(Context context, ArrayList<VotingProgramListDetailData> votingProgramListDetailDatas) {
        this.context = context;
        this.votingProgramListDetailDatas = votingProgramListDetailDatas;
        options = ImageUtils.getOptionsImageRectangle();
        imageLoader = imageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

    }

    public static void setVotingProgramClickListener(VotingProgramClickListener listener) {

        votingProgramClickListener = listener;
    }

    @Override
    public int getCount() {
        if (votingProgramListDetailDatas != null) {
            return votingProgramListDetailDatas.size();
        } else {
            return 0;
        }

    }

    @Override
    public Object getItem(int position) {
        return votingProgramListDetailDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private VotingProgramListDetailData getVotingProgram(int position) {
        return (VotingProgramListDetailData) getItem(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = myInflater.inflate(R.layout.votting_program_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.img_episode_list_thumnail);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.title_votting_program);
            viewHolder.btnDetailVote = (Button) convertView.findViewById(R.id.btn_detail_vote);
            viewHolder.startDate = (TextView) convertView.findViewById(R.id.date_start_votting_program);
            viewHolder.endDate = (TextView) convertView.findViewById(R.id.date_end_votting_program);
            viewHolder.txtDescription = (TextView) convertView.findViewById(R.id.description_voting_program);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        VotingProgramListDetailData votingProgramListDetailData = votingProgramListDetailDatas.get(position);
        voteStatus = votingProgramListDetailData.getStatus();
        if (voteStatus == 0) {
            viewHolder.btnDetailVote.setText("FINISHED");
        } else if (voteStatus == 1) {
            viewHolder.btnDetailVote.setText("VOTE NOW");
        } else {
            viewHolder.btnDetailVote.setText("COMING SOON");
        }
        votingId = votingProgramListDetailData.getId();
        imageLoader.getInstance().displayImage(votingProgramListDetailData.getBanner(), viewHolder.thumbnail, options);
        viewHolder.txtTitle.setText(votingProgramListDetailData.getTitle());

        viewHolder.txtDescription.setText(votingProgramListDetailData.getShortDescription());

        String startDate = votingProgramListDetailData.getStartDate();
        String endDate = votingProgramListDetailData.getEndDate();

        if (startDate == null || endDate == null) {

        } else if (startDate.length() > 0 && endDate.length() > 0) {
            viewHolder.startDate.setText(Utils.getDate(startDate));
            viewHolder.endDate.setText(Utils.getDate(endDate));
        }


        viewHolder.btnDetailVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                votingProgramClickListener.votingProgramItemSelect(v, getVotingProgram(position).getId(), position);
            }
        });
        return convertView;
    }

    public interface VotingProgramClickListener {
        void votingProgramItemSelect(View view, int votingId, int position);
    }

    private class ViewHolder {
        private ImageView thumbnail;
        private TextView txtTitle;
        private TextView startDate;
        private TextView endDate;
        private Button btnDetailVote;
        private TextView txtDescription;
    }
}
