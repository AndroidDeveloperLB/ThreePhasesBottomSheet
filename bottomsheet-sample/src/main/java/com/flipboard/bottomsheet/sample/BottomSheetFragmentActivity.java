package com.flipboard.bottomsheet.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.R;
import com.flipboard.bottomsheet.commons.ImagePickerSheetView;

/**
 * Activity demonstrating the use of {@link ImagePickerSheetView}
 */
public final class BottomSheetFragmentActivity extends AppCompatActivity {
    protected BottomSheetLayout mBottomSheetLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet_fragment);
        mBottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomsheet);
        findViewById(R.id.bottomsheet_fragment_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MyFragment myFragment = new MyFragment();
                myFragment.setBottomSheetLayout(mBottomSheetLayout);
                myFragment.show(getSupportFragmentManager(), R.id.bottomsheet);
            }
        });
        MyFragment myFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag(Integer.toString(R.id.bottomsheet));
        if (myFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(myFragment).commit();
            //myFragment.dismiss();
            myFragment = new MyFragment();
            myFragment.setBottomSheetLayout(mBottomSheetLayout);
            myFragment.show(getSupportFragmentManager(), R.id.bottomsheet);
        }
        mBottomSheetLayout.setShouldDimContentView(false);
        mBottomSheetLayout.setPeekOnDismiss(true);
        mBottomSheetLayout.setPeekSheetTranslation(getResources().getDimensionPixelSize(R.dimen.header_height));
        mBottomSheetLayout.setInterceptContentTouch(false);

    }

}
