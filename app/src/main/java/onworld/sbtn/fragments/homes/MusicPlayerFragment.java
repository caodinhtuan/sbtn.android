package onworld.sbtn.fragments.homes;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import onworld.sbtn.R;
import onworld.sbtn.activities.MusicPlayerActivity;
import onworld.sbtn.views.musicplayerviews.MusicPlayerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicPlayerFragment extends Fragment {
    public static final String MUSIC_TITLE = "music title";
    public static final String MUSIC_DES = "music desription";
    public static final String MUSIC_THUMB = "music thumb";

    MusicPlayerView mpv;
    ProgressBar mLoading;
    TextView mMusicTitle, mMusicSinger;
    String albumArt = "http://img.onworldtv.com/250x250/poster/2015/11/18/289803-Tinh.Dep_.Nhu_.Mo_copy_.jpg";

    private String musicTitle = "";
    private String musicDescription = "";
    private String musicThumb = "";
    private LinearLayout mMusicDescription;
    private RelativeLayout mMusicPlayerFragment;
    private ProgressBar mMusicLoading;

    public MusicPlayerFragment() {
        // Required empty public constructor
    }

    public static MusicPlayerFragment newInstance(String title, String description, String thumbLink) {
        MusicPlayerFragment musicPlayerFragment = new MusicPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MUSIC_TITLE, title);
        bundle.putString(MUSIC_DES, description);
        bundle.putString(MUSIC_THUMB, thumbLink);
        musicPlayerFragment.setArguments(bundle);
        return musicPlayerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicTitle = getArguments().getString(MUSIC_TITLE, "");
        musicDescription = getArguments().getString(MUSIC_DES, "");
        musicThumb = getArguments().getString(MUSIC_THUMB, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);
        mpv = (MusicPlayerView) view.findViewById(R.id.mpv);
        mMusicTitle = (TextView) view.findViewById(R.id.music_title);
        mMusicSinger = (TextView) view.findViewById(R.id.music_singer);
        mMusicDescription = (LinearLayout) view.findViewById(R.id.music_description);
        mMusicPlayerFragment = (RelativeLayout) view.findViewById(R.id.music_player_fragment);
        mMusicLoading = (ProgressBar) view.findViewById(R.id.music_fragment_loading);
        initView();
        MusicPlayerActivity.OnRollingPlayerListener onRollingPlayerListener = new MusicPlayerActivity.OnRollingPlayerListener() {
            @Override
            public void onRollingPlayer(boolean isRolling) {
                if (isRolling) {
                    mpv.start();
                } else {
                    mpv.stop();
                }
            }
        };
        MusicPlayerActivity.setOnRollingPlayerListener(onRollingPlayerListener);
        MusicPlayerActivity.OnShowPlayerListener onShowPlayerListener = new MusicPlayerActivity.OnShowPlayerListener() {
            @Override
            public void onShowPlayer() {
                mMusicLoading.setVisibility(View.GONE);
                mMusicDescription.setVisibility(View.VISIBLE);
                mpv.setVisibility(View.VISIBLE);
            }
        };
        MusicPlayerActivity.setOnShowPlayerListener(onShowPlayerListener);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView() {
        if (musicThumb.length() != 0) {
            mpv.setCoverURL(this.musicThumb);
        }
        mpv.setMax(400);
        mpv.setProgressVisibility(false);
        mMusicTitle.setText(musicTitle);
        mMusicSinger.setText(musicDescription);
    }

}
