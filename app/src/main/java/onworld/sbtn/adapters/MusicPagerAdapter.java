package onworld.sbtn.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import onworld.sbtn.fragments.homes.MusicListFragment;
import onworld.sbtn.fragments.homes.MusicPlayerFragment;
import onworld.sbtn.josonmodel.Related;

/**
 * Created by Steve on 8/21/2015.
 */
public class MusicPagerAdapter extends FragmentStatePagerAdapter {
    Context context;
    private String title;
    private String description;
    private String thumbLink;
    private ArrayList<Related> relateds;

    public MusicPagerAdapter(Context context, FragmentManager fm) {
        super(fm);

    }

    public MusicPagerAdapter(Context context, FragmentManager fm, String title, String des, String thumbLink, ArrayList<Related> relateds) {
        super(fm);
        this.context = context;
        this.title = title;
        this.description = des;
        this.thumbLink = thumbLink;
        this.relateds = relateds;

    }

    public void setMusicPagerData(String title, String des, String thumbLink, ArrayList<Related> relateds) {
        this.title = title;
        this.description = des;
        this.thumbLink = thumbLink;
        this.relateds = relateds;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int pos) {
        switch (pos) {
            case 0:
                return MusicPlayerFragment.newInstance(title, description, thumbLink);
            case 1:
                return MusicListFragment.newInstance(relateds);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
