package onworld.sbtn.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.json.JSONObject;

import java.util.ArrayList;

import onworld.sbtn.fragments.homedetails.ListFromDataFragment;
import onworld.sbtn.fragments.homedetails.RelatedFragment;
import onworld.sbtn.fragments.homedetails.TimeLinesFromDataFragment;
import onworld.sbtn.fragments.homedetails.InfoFragment;
import onworld.sbtn.josonmodel.Episode;
import onworld.sbtn.josonmodel.Related;
import onworld.sbtn.josonmodel.TimeLine;
import onworld.sbtn.josonmodel.VideoInfoData;

/**
 * Created by Steve on 8/21/2015.
 */
public class PagerAdapterViewDetail extends FragmentStatePagerAdapter {
    public static final String INFO = "INFORMATION";
    public static final String EPIS = "EPISODES";
    public static final String REL = "RELATED";
    public static final String TIM = "TIMELINES";
    private JSONObject jsonObject;
    private ArrayList<Episode> episodes = new ArrayList<>();
    private ArrayList<Related> relateds = new ArrayList<>();
    private ArrayList<TimeLine> timeLines = new ArrayList<>();
    private VideoInfoData videoInfoData = new VideoInfoData();
    private boolean isLive;


    public PagerAdapterViewDetail(VideoInfoData videoInfoData, ArrayList<Episode> episodes, ArrayList<Related> relateds, ArrayList<TimeLine> timeLines, FragmentManager fm) {
        super(fm);
        this.videoInfoData = videoInfoData;
        this.episodes = episodes;
        this.relateds = relateds;
        this.timeLines = timeLines;
    }

    public PagerAdapterViewDetail(FragmentManager fm) {
        super(fm);
    }

    public PagerAdapterViewDetail(JSONObject jsonObject, ArrayList<Related> relateds, ArrayList<TimeLine> timeLines, FragmentManager fm) {
        super(fm);
        this.jsonObject = jsonObject;
        this.relateds = relateds;
        this.timeLines = timeLines;
    }

    public void setData(VideoInfoData videoInfoData, ArrayList<Episode> episodes, ArrayList<Related> relateds, ArrayList<TimeLine> timeLines, boolean isLive) {
        this.videoInfoData = videoInfoData;
        this.episodes = episodes;
        this.relateds = relateds;
        this.timeLines = timeLines;
        this.isLive = isLive;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int pos) {
        if (videoInfoData != null) {
            switch (pos) {
                case 0:
                    return InfoFragment.newInstance(videoInfoData);
                case 1:
                    if (timeLines.size() != 0) {
                        return TimeLinesFromDataFragment.newInstance(this.timeLines, this.isLive);
                    } else if (episodes.size() != 0) {
                        return ListFromDataFragment.newInstance(this.episodes);
                    } else {
                        return RelatedFragment.newInstance(this.relateds);
                    }
                case 2:
                    return RelatedFragment.newInstance(this.relateds);
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        if (episodes.size() == 0 && timeLines.size() == 0) {
            return 2;
        }
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return INFO;
        } else if (position == 1) {
            if (timeLines.size() != 0) {
                return TIM;
            } else if (episodes.size() != 0) {
                return EPIS;
            } else {
                return REL;
            }
        } else {
            return REL;
        }

    }
}
