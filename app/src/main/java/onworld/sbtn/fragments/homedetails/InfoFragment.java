package onworld.sbtn.fragments.homedetails;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import onworld.sbtn.R;
import onworld.sbtn.josonmodel.InfoVideoArrayModel;
import onworld.sbtn.josonmodel.VideoInfoData;
import onworld.sbtn.utils.ASDateTimeUtils;
import onworld.sbtn.views.InfoItemViewLinearLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {
    public static final String VIDEO_INFO_DATA = "videoinfodata";
    private VideoInfoData videoInfoData;
    private TextView videoTitle, videoReleaseYear, videoDuration, videoDescription;
    private TextView genreTitle, nationTitle, durationTitle, releaseYearTitle, directorTitle, actorTitle, tagTitle, descriptionTitle;
    private LinearLayout genresLayout, tagsLayout, actorsLayout, directorsLayout, nationLayout;

    public InfoFragment() {
        // Required empty public constructor
    }

    public static InfoFragment newInstance(VideoInfoData videoInfoData) {
        InfoFragment infoFragment = new InfoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(VIDEO_INFO_DATA, videoInfoData);
        infoFragment.setArguments(bundle);
        return infoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoInfoData = getArguments().getParcelable(VIDEO_INFO_DATA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        genresLayout = (LinearLayout) view.findViewById(R.id.video_info_genre);
        tagsLayout = (LinearLayout) view.findViewById(R.id.video_info_tags);
        actorsLayout = (LinearLayout) view.findViewById(R.id.video_info_actors);
        directorsLayout = (LinearLayout) view.findViewById(R.id.video_info_directors);
        nationLayout = (LinearLayout) view.findViewById(R.id.video_info_national);
        videoTitle = (TextView) view.findViewById(R.id.video_title);
        videoReleaseYear = (TextView) view.findViewById(R.id.video_info_year);
        videoDuration = (TextView) view.findViewById(R.id.video_info_duration);
        videoDescription = (TextView) view.findViewById(R.id.video_info_description);

        genreTitle = (TextView) view.findViewById(R.id.video_info_genre_title);
        nationTitle = (TextView) view.findViewById(R.id.video_info_national_title);
        durationTitle = (TextView) view.findViewById(R.id.video_info_duration_title);
        releaseYearTitle = (TextView) view.findViewById(R.id.video_info_year_title);
        directorTitle = (TextView) view.findViewById(R.id.video_info_directors_title);
        actorTitle = (TextView) view.findViewById(R.id.video_info_actors_title);
        tagTitle = (TextView) view.findViewById(R.id.video_info_tags_title);
        descriptionTitle = (TextView) view.findViewById(R.id.video_info_description_title);

        if (videoInfoData != null) {
            videoTitle.setText(videoInfoData.getVideoTitle());
            videoReleaseYear.setText(videoInfoData.getVideoReleaseYear());
            if (videoInfoData.getVideoReleaseYear() != null) {
                releaseYearTitle.setVisibility(View.VISIBLE);
            }
            videoDuration.setText(ASDateTimeUtils.formatMillis(Integer.parseInt(videoInfoData.getVideoDuration())));
            if (videoInfoData.getVideoDuration() != null) {
                durationTitle.setVisibility(View.VISIBLE);
            }
            videoDescription.setText(videoInfoData.getVideoDescription());
            if (videoInfoData.getVideoDescription() != null) {
                if (videoInfoData.getVideoDescription().length() > 0) {
                    descriptionTitle.setVisibility(View.VISIBLE);
                }

            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ArrayList<InfoVideoArrayModel> genres = videoInfoData.getGenre();
            if (genres != null) {
                for (int i = 0; i < genres.size(); i++) {
                    genreTitle.setVisibility(View.VISIBLE);
                    InfoItemViewLinearLayout infoItemViewLinearLayout = new InfoItemViewLinearLayout(getContext());
                    infoItemViewLinearLayout.setInfoTitle(genres.get(i).getName());
                    if (infoItemViewLinearLayout.getParent() == null) {
                        this.genresLayout.addView(infoItemViewLinearLayout, params);
                    }
                }
            }

            ArrayList<InfoVideoArrayModel> tags = videoInfoData.getTags();
            if (tags != null) {
                for (int i = 0; i < tags.size(); i++) {
                    tagTitle.setVisibility(View.VISIBLE);
                    InfoItemViewLinearLayout infoItemViewLinearLayout = new InfoItemViewLinearLayout(getContext());
                    infoItemViewLinearLayout.setInfoTitle(tags.get(i).getName());
                    if (infoItemViewLinearLayout.getParent() == null) {
                        this.tagsLayout.addView(infoItemViewLinearLayout, params);
                    }
                }
            }

            ArrayList<InfoVideoArrayModel> directors = videoInfoData.getDirectors();
            if (directors != null) {
                for (int i = 0; i < directors.size(); i++) {
                    directorTitle.setVisibility(View.VISIBLE);
                    InfoItemViewLinearLayout infoItemViewLinearLayout = new InfoItemViewLinearLayout(getContext());
                    infoItemViewLinearLayout.setInfoTitle(directors.get(i).getName());
                    if (infoItemViewLinearLayout.getParent() == null) {
                        this.directorsLayout.addView(infoItemViewLinearLayout, params);
                    }
                }
            }

            ArrayList<InfoVideoArrayModel> actors = videoInfoData.getActors();
            if (actors != null) {
                for (int i = 0; i < actors.size(); i++) {
                    actorTitle.setVisibility(View.VISIBLE);
                    InfoItemViewLinearLayout infoItemViewLinearLayout = new InfoItemViewLinearLayout(getContext());
                    infoItemViewLinearLayout.setInfoTitle(actors.get(i).getName());
                    if (infoItemViewLinearLayout.getParent() == null) {
                        this.actorsLayout.addView(infoItemViewLinearLayout, params);
                    }
                }
            }

            ArrayList<InfoVideoArrayModel> nations = videoInfoData.getNations();
            if (nations != null) {
                for (int i = 0; i < nations.size(); i++) {
                    nationTitle.setVisibility(View.VISIBLE);
                    InfoItemViewLinearLayout infoItemViewLinearLayout = new InfoItemViewLinearLayout(getContext());
                    infoItemViewLinearLayout.setInfoTitle(nations.get(i).getName());
                    if (infoItemViewLinearLayout.getParent() == null) {
                        this.nationLayout.addView(infoItemViewLinearLayout, params);
                    }
                }
            }

        }
        return view;
    }

}
