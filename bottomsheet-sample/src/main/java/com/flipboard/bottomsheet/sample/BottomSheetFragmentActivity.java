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

    protected BottomSheetLayout bottomSheetLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet_fragment);
        bottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomsheet);
        findViewById(R.id.bottomsheet_fragment_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MyFragment myFragment = new MyFragment();
                myFragment.setBottomSheetLayout(bottomSheetLayout);
                myFragment.show(getSupportFragmentManager(), R.id.bottomsheet);
            }
        });
        final MyFragment myFragment = (MyFragment) getSupportFragmentManager().findFragmentByTag(Integer.toString(R.id.bottomsheet));
        if (myFragment != null)
            myFragment.dismiss();
        bottomSheetLayout.setShouldDimContentView(false);
        bottomSheetLayout.setPeekOnDismiss(true);
        bottomSheetLayout.setPeekSheetTranslation(200);
        bottomSheetLayout.setInterceptContentTouch(false);
    }
}
