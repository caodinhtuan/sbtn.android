package onworld.sbtn.views;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import onworld.sbtn.josonmodel.VotingRoundItemData;


/**
 * Created by onworldtv on 11/13/15.
 */
public class RoundViewItem extends RoundView {


    RoundViewItemListener roundViewItemListener;
    private FragmentActivity activity;
    private VotingRoundItemData votingRoundItemData;

    public RoundViewItem(Context context) {
        super(context);
    }

    public RoundViewItem(FragmentActivity activity, VotingRoundItemData votingRoundItemData) {
        super(activity);
        this.activity = activity;
        this.votingRoundItemData = votingRoundItemData;
    }

    @Override
    protected void initViewAction() {
        this.roundTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roundViewItemListener != null) {
                    roundViewItemListener.roundItemDetailListener(RoundViewItem.this, votingRoundItemData);
                }

            }
        });
        this.roundDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roundViewItemListener.roundItemDetailListener(RoundViewItem.this, votingRoundItemData);
            }
        });
    }

    public void setRoundViewItemListener(RoundViewItemListener roundViewItemListener) {
        this.roundViewItemListener = roundViewItemListener;
    }

    public interface RoundViewItemListener {
        void roundItemDetailListener(RoundViewItem roundViewItem, VotingRoundItemData votingRoundItemData);
    }
}
