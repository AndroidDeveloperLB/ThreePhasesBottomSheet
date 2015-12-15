package com.lb.three_phases_bottom_sheet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;

import com.flipboard.bottomsheet.BaseViewTransformer;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.BottomSheetLayout.OnSheetStateChangeListener;
import com.flipboard.bottomsheet.BottomSheetLayout.State;
import com.flipboard.bottomsheet.ViewTransformer;
import com.flipboard.bottomsheet.commons.BottomSheetFragment;

public class MyFragment extends BottomSheetFragment {
    private BottomSheetLayout mBottomSheetLayout;
    private ImageView mBottomSheetBackgroundImageView;
    private int mBottomSheetHeight;
    private int mMovingImageviewSize;
    private ImageView movingIconImageView;
    private AppBarLayout mAppBarLayout;
    private Toolbar mLeftToolbar;
    private TextView mTitleExpanded, mTitleCollapsed;
    private ViewPropertyAnimator mTitleExpandedAnimation, mToolbarAnimation;
    private View mBottomsheetContentView;
    private int mMovingImageExpandedBottomSheetMarginLeft;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my, container, false);
        view.setMinimumHeight(getResources().getDisplayMetrics().heightPixels);
        mBottomSheetHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
        mMovingImageviewSize = getResources().getDimensionPixelSize(R.dimen.moving_image_size);
        mTitleExpanded = (TextView) view.findViewById(R.id.fragment_search_activity_result__expandedTitleTextView);
        mTitleCollapsed = (TextView) view.findViewById(R.id.fragment_search_activity_result__collapsedTitleTextView);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsingToolbarLayout);
        mBottomsheetContentView = view.findViewById(R.id.bottomsheetContentView);
        ((MarginLayoutParams) mBottomsheetContentView.getLayoutParams()).topMargin = mMovingImageviewSize / 2;
        mBottomsheetContentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                mBottomSheetLayout.expandSheet();
            }
        });

        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        mLeftToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mLeftToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mLeftToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetLayout.dismissSheet();
            }
        });
        mLeftToolbar.setAlpha(0);
        mBottomSheetBackgroundImageView = (ImageView) view.findViewById(R.id.backdrop);
        mBottomSheetBackgroundImageView.setAlpha(0.0f);
        mBottomSheetBackgroundImageView.setY(mMovingImageviewSize / 2);
        //mBottomSheetBackgroundImageView.setImageResource(R.drawable.background);
        final Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        mBottomSheetBackgroundImageView.setImageBitmap(bm);
        movingIconImageView = (ImageView) view.findViewById(R.id.movingIconImageView);
        if (mBottomSheetLayout != null)
            mBottomSheetLayout.setAppBarLayout(mAppBarLayout);
        final int actionBarHeight = Utils.getActionBarHeight(getActivity());

        movingIconImageView.setPivotX(0);
        movingIconImageView.setPivotY(0);
        mTitleExpanded.setPivotX(0);
        mTitleExpanded.setAlpha(0);
        mTitleCollapsed.setAlpha(0);
        mMovingImageExpandedBottomSheetMarginLeft = getResources().getDimensionPixelSize(R.dimen.moving_image_expanded_bottom_sheet_margin_left);

        mAppBarLayout.addOnOffsetChangedListener(new OnOffsetChangedListener() {
            final float actionBarIconPadding = getResources().getDimensionPixelSize(R.dimen.moving_image_collapsed_toolbar_top_and_bottom_padding);
            final float actionBarIconPaddingText = getResources().getDimensionPixelSize(R.dimen.moving_text_collapsed_toolbar_top_and_bottom_padding);
            final float startMarginBottomText = getResources().getDimensionPixelSize(R.dimen.moving_text_expanded_toolbar_bottom_margin);
            final float startMarginLeftText = getResources().getDimensionPixelSize(R.dimen.moving_text_expanded_toolbar_margin_left);
            float targetY;
            boolean init = false;
            float startY;
            float scaleDiff;
            float startScale;
            //
            float scaleDiffTitle;
            float startYTitle;

            @Override
            public void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset) {
                if (mBottomSheetLayout != null && mBottomSheetLayout.isSheetShowing() && mBottomSheetLayout.getState() == State.EXPANDED) {
                    float progress = (float) -verticalOffset / mAppBarLayout.getTotalScrollRange();
                    movingIconImageView.setX(mMovingImageExpandedBottomSheetMarginLeft + (progress * (actionBarHeight - mMovingImageExpandedBottomSheetMarginLeft)));
                    if (!init) {
                        //imageview
                        startScale = movingIconImageView.getScaleX();
                        startY = movingIconImageView.getY();
                        final float targetImageViewSize = actionBarHeight - actionBarIconPadding * 2;
                        targetY = mBottomSheetHeight - actionBarIconPadding - targetImageViewSize;
                        float targetScale = targetImageViewSize / mMovingImageviewSize;
                        scaleDiff = startScale - targetScale;
                        //text:
                        scaleDiffTitle = 1 - (actionBarHeight - actionBarIconPaddingText * 2) / mTitleExpanded.getHeight();
                        startYTitle = mTitleExpanded.getY();
                        mTitleExpanded.setPivotY(mTitleExpanded.getHeight());
                        init = true;
                    }
                    if (mToolbarAnimation != null) {
                        mToolbarAnimation.cancel();
                        mToolbarAnimation = null;
                        mLeftToolbar.setAlpha(1);
                        mLeftToolbar.setY(0);
                    }
                    if (mTitleExpandedAnimation != null) {
                        mTitleExpandedAnimation.cancel();
                        mTitleExpandedAnimation = null;
                    }
                    final float newScale = startScale - progress * scaleDiff;
                    movingIconImageView.setScaleX(newScale);
                    movingIconImageView.setScaleY(newScale);
                    movingIconImageView.setY(startY - progress * (startY - targetY));

                    mTitleExpanded.setScaleX(1f - progress * scaleDiffTitle);
                    mTitleExpanded.setScaleY(1f - progress * scaleDiffTitle);
                    mTitleExpanded.setX(startMarginLeftText + (progress * (2 * actionBarHeight - startMarginLeftText)));
                    mTitleExpanded.setY(startYTitle - progress * actionBarIconPaddingText + startMarginBottomText * progress);

                    mTitleExpanded.setAlpha(progress <= 0.7f ? 1f : progress >= 0.9f ? 0f : 1 - ((progress - 0.7f) / 0.2f));
                    mTitleCollapsed.setAlpha(progress <= 0.9f ? 0f : progress >= 1f ? 1f : (progress - 0.9f) / 0.1f);
                }
            }
        });
        return view;
    }

    @Override
    public ViewTransformer getViewTransformer() {
        final float targetSizeWhenBottomSheetExpanded = getResources().getDimensionPixelSize(R.dimen.moving_image_expanded_bottom_sheet_size);
        final float startSizeWhenBottomSheetCollapsed = getResources().getDimensionPixelSize(R.dimen.moving_image_size);
        final float startMarginBottom = getResources().getDimensionPixelSize(R.dimen.moving_image_expanded_bottom_sheet_margin_bottom);
        return new BaseViewTransformer() {
            boolean init = false;
            ViewPropertyAnimator mBottomSheetBackgroundImageViewFadeInAnimation, mBottomSheetBackgroundImageViewFadeOutAnimation;
            float mOriginalContactPhotoXCoordinate;
            float mOriginalBottomSheetBackgroundImageViewY;
            float scaleDiff;
            OnTouchListener mOnTouchListener = new OnTouchListener() {
                @Override
                public boolean onTouch(final View v, final MotionEvent event) {
                    return true;
                }
            };

            @Override
            public void transformView(final float translation, final float maxTranslation, final float peekedTranslation, final BottomSheetLayout parent, final View view) {
                if (!init) {
                    mOriginalBottomSheetBackgroundImageViewY = mMovingImageviewSize / 2;
                    mOriginalContactPhotoXCoordinate = movingIconImageView.getX();
                    scaleDiff = (startSizeWhenBottomSheetCollapsed - targetSizeWhenBottomSheetExpanded) / startSizeWhenBottomSheetCollapsed;
                    init = true;
                }
                //Log.d("AppLog", "translation:" + translation + " mBottomSheetHeight:" + mBottomSheetHeight + " maxTranslation:" + maxTranslation);
                //mAppBarLayout.sett(translation > mBottomSheetHeight);
                //mNestedScrollView.isTouchEnabled = translation > mBottomSheetHeight;
                //((ViewGroup) mNestedScrollView.getChildAt(0)).requestDisallowInterceptTouchEvent(translation <= mBottomSheetHeight);
                //boolean allowScrolling = translation >= maxTranslation;
                //Log.d("AppLog", "should allow scrolling?" + allowScrolling);
                //Log.d("AppLog", "");
                //mAppBarLayout.allowTouch = allowScrolling;
                if (translation < mBottomSheetHeight) {
                    return;
                }
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
                    if (mBottomSheetBackgroundImageViewFadeInAnimation == null)
                        mBottomSheetBackgroundImageViewFadeInAnimation = mBottomSheetBackgroundImageView.animate().alpha(1);
                }
                float progress = (translation - mBottomSheetHeight) / (maxTranslation - mBottomSheetHeight);
                //Log.d("AppLog", "translation:" + translation + " maxTranslation:" + maxTranslation + " progress:" + progress);
                movingIconImageView.setX(mOriginalContactPhotoXCoordinate - progress * (mOriginalContactPhotoXCoordinate - mMovingImageExpandedBottomSheetMarginLeft));
                final float scaleForImageView = 1 - progress * scaleDiff;
                movingIconImageView.setScaleX(scaleForImageView);
                movingIconImageView.setScaleY(scaleForImageView);
                movingIconImageView.setY(progress * (mBottomSheetHeight - startMarginBottom - movingIconImageView.getHeight() * scaleForImageView));
                mBottomSheetBackgroundImageView.setY(mOriginalBottomSheetBackgroundImageViewY * (1 - progress));
            }
        };
    }

    public void setBottomSheetLayout(final BottomSheetLayout bottomSheetLayout) {
        mBottomSheetLayout = bottomSheetLayout;
        if (mBottomSheetLayout != null && mAppBarLayout != null)
            mBottomSheetLayout.setAppBarLayout(mAppBarLayout);
        mBottomSheetLayout.addOnSheetStateChangeListener(new OnSheetStateChangeListener() {
            State lastState;

            @Override
            public void onSheetStateChanged(final State state) {
                if (lastState == state || !isAdded())
                    return;
                lastState = state;
                if (state != State.EXPANDED) {
                    mTitleCollapsed.setAlpha(0);
                    if (mToolbarAnimation != null)
                        mToolbarAnimation.cancel();
                    mToolbarAnimation = null;
                    if (mTitleExpandedAnimation != null)
                        mTitleExpandedAnimation.cancel();
                    mTitleExpandedAnimation = null;
                    mTitleExpanded.setAlpha(0);
                    mTitleExpanded.setVisibility(View.INVISIBLE);
                    mLeftToolbar.setAlpha(0);
                    mLeftToolbar.setVisibility(View.INVISIBLE);
                } else if (mToolbarAnimation == null) {
                    mTitleExpanded.setVisibility(View.VISIBLE);
                    mLeftToolbar.setVisibility(View.VISIBLE);
                    mLeftToolbar.setY(-mLeftToolbar.getHeight() / 3);
                    mToolbarAnimation = mLeftToolbar.animate().setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
                    mToolbarAnimation.alpha(1).y(0).start();
                    mTitleExpanded.setAlpha(0);
                    mTitleExpandedAnimation = mTitleExpanded.animate().setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
                    mTitleExpandedAnimation.alpha(1).start();
                }
            }
        });
    }


}
