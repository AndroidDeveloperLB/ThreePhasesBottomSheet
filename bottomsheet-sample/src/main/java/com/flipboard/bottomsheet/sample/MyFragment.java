package com.flipboard.bottomsheet.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.R;
import com.flipboard.bottomsheet.commons.BottomSheetFragment;

public class MyFragment extends BottomSheetFragment {

    private BottomSheetLayout mBottomSheetLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my, container, false);
        view.setMinimumHeight(getResources().getDisplayMetrics().heightPixels);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Title");
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(getActivity());
            }
        });
        final ImageView imageView = (ImageView) view.findViewById(R.id.backdrop);
        Glide.with(this).load(R.drawable.cheese_1).centerCrop().into(imageView);
        if (mBottomSheetLayout != null)
            mBottomSheetLayout.setAppBarLayout((AppBarLayout) view.findViewById(R.id.appbar));
        return view;
    }

    public void setBottomSheetLayout(final BottomSheetLayout bottomSheetLayout) {
        mBottomSheetLayout = bottomSheetLayout;
        if (mBottomSheetLayout != null&&getView()!=null)
            mBottomSheetLayout.setAppBarLayout((AppBarLayout) getView().findViewById(R.id.appbar));
    }
}
