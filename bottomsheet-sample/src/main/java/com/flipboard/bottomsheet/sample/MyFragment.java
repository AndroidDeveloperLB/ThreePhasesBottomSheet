package com.flipboard.bottomsheet.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.flipboard.bottomsheet.BaseViewTransformer;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.BottomSheetLayout.State;
import com.flipboard.bottomsheet.R;
import com.flipboard.bottomsheet.ViewTransformer;
import com.flipboard.bottomsheet.commons.BottomSheetFragment;

public class MyFragment extends BottomSheetFragment {
    private BottomSheetLayout mBottomSheetLayout;
    private ImageView mBottomSheetBackgroundImageView;
    private int mBottomSheetHeight;
    private ImageView movingIconImageView;
    private AppBarLayout mAppBarLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my, container, false);
        mBottomSheetHeight = getResources().getDimensionPixelSize(R.dimen.detail_backdrop_height);
        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        view.setMinimumHeight(getResources().getDisplayMetrics().heightPixels);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        //collapsingToolbar.setTitle("Title");
        collapsingToolbar.setTitleEnabled(false);
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //final AppCompatActivity activity = (AppCompatActivity) getActivity();
        //activity.setSupportActionBar(toolbar);
        //final ActionBar actionBar = activity.getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setTitle(null);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NavUtils.getParentActivityIntent(getActivity()) != null)
                    NavUtils.navigateUpFromSameTask(getActivity());
                else getActivity().onBackPressed();
            }
        });
        mBottomSheetBackgroundImageView = (ImageView) view.findViewById(R.id.backdrop);
        mBottomSheetBackgroundImageView.setAlpha(0.0f);
        movingIconImageView = (ImageView) view.findViewById(R.id.movingIconImageView);
        Glide.with(this).load(R.drawable.cheese_1).centerCrop().into(mBottomSheetBackgroundImageView);
        if (mBottomSheetLayout != null)
            mBottomSheetLayout.setAppBarLayout(mAppBarLayout);
        final int actionBarHeight = getActionBarHeight(getActivity());
        mAppBarLayout.addOnOffsetChangedListener(new OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset) {
                if (mBottomSheetLayout != null && mBottomSheetLayout.isSheetShowing() && mBottomSheetLayout.getState() == State.EXPANDED) {
                    float progress = (float) -verticalOffset / mAppBarLayout.getTotalScrollRange();
                    //Log.d("AppLog", "verticalOffset:" + verticalOffset + " progress:" + progress);
                    movingIconImageView.setX(progress * actionBarHeight);
                }
            }
        });
        return view;
    }

    /**
     * returns the height of the action bar
     */
    public static int getActionBarHeight(final Context context) {
        // based on http://stackoverflow.com/questions/12301510/how-to-get-the-actionbar-height
        final TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (context.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources()
                    .getDisplayMetrics());
        return actionBarHeight;
    }

    public void setBottomSheetLayout(final BottomSheetLayout bottomSheetLayout) {
        mBottomSheetLayout = bottomSheetLayout;
        if (mBottomSheetLayout != null && mAppBarLayout != null)
            mBottomSheetLayout.setAppBarLayout(mAppBarLayout);
    }

    @Override
    public ViewTransformer getViewTransformer() {
        return new BaseViewTransformer() {
            private Float mOriginalContactPhotoXCoordinate;

            @Override
            public void transformView(final float translation, final float maxTranslation, final float peekedTranslation, final BottomSheetLayout parent, final View view) {
                if (mOriginalContactPhotoXCoordinate == null)
                    mOriginalContactPhotoXCoordinate = movingIconImageView.getX();
                if (translation < mBottomSheetHeight)
                    return;
                float progress = (translation - mBottomSheetHeight) / (maxTranslation - mBottomSheetHeight);
                movingIconImageView.setX(mOriginalContactPhotoXCoordinate - progress * mOriginalContactPhotoXCoordinate);
                movingIconImageView.setY(progress * (mBottomSheetHeight - movingIconImageView.getHeight()));
                mBottomSheetBackgroundImageView.setAlpha(progress);
                //Log.d("AppLog", "translation:" + translation + " maxTranslation:" + maxTranslation + " peekedTranslation:" + peekedTranslation + " progress:" + progress);
            }
        };
    }
}
