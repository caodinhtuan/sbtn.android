package onworld.sbtn.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import onworld.sbtn.fragments.homedetails.SearchResultFragment;
import onworld.sbtn.fragments.homes.HomeFragment;
import onworld.sbtn.josonmodel.Related;

/**
 * Created by Steve on 8/21/2015.
 */
public class PagerAdapterSearch extends FragmentStatePagerAdapter {
    private ArrayList<Related> views = new ArrayList<>();
    private ArrayList<Related> listens = new ArrayList<>();


    public PagerAdapterSearch(FragmentManager fm) {
        super(fm);

    }
    public PagerAdapterSearch(ArrayList<Related> views, ArrayList<Related> listens,FragmentManager fm) {
        super(fm);
        this.views = views;
        this.listens = listens;
    }

    public void setSearchResultData(ArrayList<Related> views, ArrayList<Related> listens) {
        this.views = views;
        this.listens = listens;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int pos) {
        switch (pos) {
            case 0:
                return SearchResultFragment.newInstance(this.views, HomeFragment.VIDEO_TYPE);
            case 1:
                return SearchResultFragment.newInstance(this.listens,HomeFragment.AUDIO_TYPE);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "VIEW RESULTS";
        } else {
            return "LISTEN RESULTS";
        }
    }
}
