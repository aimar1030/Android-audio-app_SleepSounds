package com.zenlabs.sleepsounds.customview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zenlabs.sleepsounds.R;
import com.zenlabs.sleepsounds.model.MSound;
import com.zenlabs.sleepsounds.utils.Common;
import com.zenlabs.sleepsounds.utils.UtilsMethods;
import com.zenlabs.sleepsounds.utils.UtilsValues;

import org.w3c.dom.Text;

/**
 * Created by fedoro on 5/23/16.
 */
public class MFavoritContainViewItem extends RelativeLayout implements View.OnClickListener {

    MSound sound;
    RelativeLayout removeBar;
    MFavoriteItemHandler handler;
    int positionInFavorite;
    int positionInSounds;

    public interface MFavoriteItemHandler   {

        public void dataChanged(int positionInFavorite, int positionInSounds);
    }

    public MFavoritContainViewItem(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.view_favorit_contain_item, this);
    }

    public MFavoritContainViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.view_favorit_contain_item, this);
    }

    public MFavoritContainViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.view_favorit_contain_item, this);
    }

    public void sethandler(MFavoriteItemHandler handler)    {
        this.handler = handler;
    }

    public void updateItem(MSound sound)    {

        this.sound = sound;

        this.setOnClickListener(this);
        ImageView background = (ImageView)findViewById(R.id.favorit_contain_item_back);
        TextView title = (TextView) findViewById(R.id.favorit_contain_item_title);

        removeBar = (RelativeLayout)findViewById(R.id.remove_bar);
        ImageView remove_bt = (ImageView)findViewById(R.id.remove_favorite_bt);

        remove_bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handler != null)
                    handler.dataChanged(positionInFavorite, positionInSounds);
            }
        });

        removeBar.setVisibility(GONE);

        Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(), "fonts/MyriadPro-Light.otf");
        title.setTypeface(custom_font);

        int position = Common.sounds.indexOf(sound);

        if (sound.getBackground() != null)
            background.setImageResource(UtilsValues.imageArray[position]);

        if (sound.getName() != null)
            title.setText(sound.getName());
    }

    public void setRemove(boolean isEditing, int positionInFavorite, int positionInSounds)  {
        if (isEditing) {
            removeBar.setVisibility(VISIBLE);
            this.positionInFavorite = positionInFavorite;
            this.positionInSounds = positionInSounds;

            int width = UtilsMethods.dpToPx(getContext(), 40);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.setMargins(width, 0, 0-width, 0);

            removeBar.setLayoutParams(params);

            animateRemoveBar(0, 0 - width);
        }
        else {
            int width = UtilsMethods.dpToPx(getContext(), 40);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.setMargins(0, 0, 0, 0);
            removeBar.setLayoutParams(params);

            animateRemoveBar(0, width);
        }
    }

    private void animateRemoveBar(float fx, float tx)   {

        ObjectAnimator objectAnimatorButton
                = ObjectAnimator.ofFloat(removeBar, "translationX", fx, tx);

        objectAnimatorButton.setDuration(100);
        objectAnimatorButton.start();
    }

    @Override
    public void onClick(View v) {

        int position = Common.unlockSounds.indexOf(this.sound);

        if (!Common.unlockSounds.get(position).isPlaying)
            Common.unlockViews.get(position).playSound();
        else
            Common.unlockViews.get(position).stopPlayer();
    }
}
