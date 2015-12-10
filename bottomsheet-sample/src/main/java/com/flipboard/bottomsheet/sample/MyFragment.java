package com.flipboard.bottomsheet.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.flipboard.bottomsheet.BaseViewTransformer;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.BottomSheetLayout.OnSheetStateChangeListener;
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
    private int mMStartMarginBottom;
    private int mMStartMarginLeft;
    private Toolbar mToolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my, container, false);
        mBottomSheetHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        view.setMinimumHeight(getResources().getDisplayMetrics().heightPixels);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        //collapsingToolbar.setTitle("Title");
        collapsingToolbar.setTitleEnabled(false);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //final AppCompatActivity activity = (AppCompatActivity) getActivity();
        //activity.setSupportActionBar(toolbar);
        //final ActionBar actionBar = activity.getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setTitle(null);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (NavUtils.getParentActivityIntent(getActivity()) != null)
                //    NavUtils.navigateUpFromSameTask(getActivity());
                //else getActivity().onBackPressed();
                if (mToolbar.getAlpha() == 1)
                    mBottomSheetLayout.dismissSheet();
            }
        });
        mToolbar.setAlpha(0);
        mBottomSheetBackgroundImageView = (ImageView) view.findViewById(R.id.backdrop);
        mBottomSheetBackgroundImageView.setAlpha(0.0f);
        movingIconImageView = (ImageView) view.findViewById(R.id.movingIconImageView);
        Glide.with(this).load(R.drawable.cheese_1).centerCrop().into(mBottomSheetBackgroundImageView);
        if (mBottomSheetLayout != null)
            mBottomSheetLayout.setAppBarLayout(mAppBarLayout);
        final int actionBarHeight = getActionBarHeight(getActivity());
        mMStartMarginBottom = getResources().getDimensionPixelSize(R.dimen.header_view_start_margin_bottom);
        mMStartMarginLeft = getResources().getDimensionPixelSize(R.dimen.header_view_start_margin_left);
        movingIconImageView.setPivotX(0);
        final float actionBarIconPadding = getResources().getDimensionPixelSize(R.dimen.action_bar_icon_padding);
        mAppBarLayout.addOnOffsetChangedListener(new OnOffsetChangedListener() {
            float startY = 0;
            float scaleDiff = 0;

            @Override
            public void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset) {
                if (mBottomSheetLayout != null && mBottomSheetLayout.isSheetShowing() && mBottomSheetLayout.getState() == State.EXPANDED) {
                    float progress = (float) -verticalOffset / mAppBarLayout.getTotalScrollRange();
                    movingIconImageView.setX(mMStartMarginLeft + (progress * (actionBarHeight - mMStartMarginLeft)));
                    if (startY == 0)
                        startY = movingIconImageView.getY();
                    if (scaleDiff == 0) {
                        scaleDiff = 1 - (actionBarHeight - actionBarIconPadding) / movingIconImageView.getHeight();
                        movingIconImageView.setPivotY(movingIconImageView.getHeight());
                    }
                    movingIconImageView.setScaleX(1f - progress * scaleDiff);
                    movingIconImageView.setScaleY(1f - progress * scaleDiff);
                    movingIconImageView.setY(startY - progress * actionBarIconPadding / 2 + mMStartMarginBottom * progress);
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
        mBottomSheetLayout.addOnSheetStateChangeListener(new OnSheetStateChangeListener() {
            private ViewPropertyAnimator mToolbarAnimation;
            State lastState;

            @Override
            public void onSheetStateChanged(final State state) {
                if (lastState == state)
                    return;
                lastState = state;
                if (state != State.EXPANDED) {
                    if (mToolbarAnimation != null)
                        mToolbarAnimation.cancel();
                    mToolbarAnimation = null;
                    mToolbar.setAlpha(0);
                } else if (mToolbarAnimation == null) {
                    mToolbar.setTranslationY(-mToolbar.getHeight() / 3);
                    mToolbarAnimation = mToolbar.animate().setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
                    mToolbarAnimation.alpha(1).translationY(0).start();
                }
            }
        });
    }

    @Override
    public ViewTransformer getViewTransformer() {
        return new BaseViewTransformer() {
            private ViewPropertyAnimator mBottomSheetBackgroundImageViewFadeInAnimation, mBottomSheetBackgroundImageViewFadeOutAnimation;
            private Float mOriginalContactPhotoXCoordinate = null;
            private final float mOriginalBottomSheetBackgroundImageViewTranslationY = mBottomSheetBackgroundImageView.getTranslationY();

            @Override
            public void transformView(final float translation, final float maxTranslation, final float peekedTranslation, final BottomSheetLayout parent, final View view) {
                if (mOriginalContactPhotoXCoordinate == null)
                    mOriginalContactPhotoXCoordinate = movingIconImageView.getX();
                if (translation < mBottomSheetHeight)
                    return;
                Log.d("AppLog", "translation:" + translation + " mBottomSheetHeight:" + mBottomSheetHeight);
                if (translation == mBottomSheetHeight) {
                    if (mBottomSheetBackgroundImageViewFadeInAnimation != null)
                        mBottomSheetBackgroundImageViewFadeInAnimation.cancel();
                    mBottomSheetBackgroundImageViewFadeInAnimation = null;
                    if (mBottomSheetBackgroundImageViewFadeOutAnimation == null)
                        mBottomSheetBackgroundImageViewFadeOutAnimation = mBottomSheetBackgroundImageView.animate().alpha(0);
                } else {
                    if (mBottomSheetBackgroundImageViewFadeOutAnimation != null)
                        mBottomSheetBackgroundImageViewFadeOutAnimation.cancel();
                    mBottomSheetBackgroundImageViewFadeOutAnimation = null;
                    if (mBottomSheetBackgroundImageViewFadeInAnimation == null) {
                        mBottomSheetBackgroundImageViewFadeInAnimation = mBottomSheetBackgroundImageView.animate().alpha(1);
                    }
                }
                float progress = (translation - mBottomSheetHeight) / (maxTranslation - mBottomSheetHeight);
                //movingIconImageView.setY(progress * (mBottomSheetHeight - movingIconImageView.getHeight()));
                movingIconImageView.setY(progress * (mBottomSheetHeight - movingIconImageView.getHeight() - mMStartMarginBottom));
                movingIconImageView.setX(mOriginalContactPhotoXCoordinate - progress * (mOriginalContactPhotoXCoordinate - mMStartMarginLeft));
                //mBottomSheetBackgroundImageView.setAlpha(progress);
                mBottomSheetBackgroundImageView.setTranslationY(mOriginalBottomSheetBackgroundImageViewTranslationY - progress * mOriginalBottomSheetBackgroundImageViewTranslationY);
                //Log.d("AppLog", "translation:" + translation + " maxTranslation:" + maxTranslation + " peekedTranslation:" + peekedTranslation + " progress:" + progress);

            }
        };
    }
}
