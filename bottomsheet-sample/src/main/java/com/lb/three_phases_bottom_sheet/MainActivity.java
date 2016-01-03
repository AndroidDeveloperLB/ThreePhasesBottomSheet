package com.lb.three_phases_bottom_sheet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.BottomSheetLayout.State;
import com.flipboard.bottomsheet.commons.ImagePickerSheetView;
import com.google.android.gms.maps.GoogleMap;

/**
 * Activity demonstrating the use of {@link ImagePickerSheetView}
 */
public final class MainActivity extends AppCompatActivity {
    protected BottomSheetLayout mBottomSheetLayout;
    private View mFocusStealer;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet_fragment);
        mFocusStealer = findViewById(R.id.focusStealer);
        mBottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomsheet);
        findViewById(R.id.bottomsheet_fragment_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });
        EditText editText = (EditText) findViewById(R.id.bottomsheet_fragment_editText);
        editText.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    showBottomSheet();
                    return true;
                }
                return false;
            }
        });
        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    showBottomSheet();
                    return true;
                }
                return false;
            }
        });
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (hasFocus)
                    mBottomSheetLayout.dismissSheet();
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
        mBottomSheetLayout.setEnableDismissByScroll(false);
        mBottomSheetLayout.setShouldDimContentView(false);
        mBottomSheetLayout.setPeekOnDismiss(true);
        mBottomSheetLayout.setPeekSheetTranslation(getResources().getDimensionPixelSize(R.dimen.header_height_peeked));
        mBottomSheetLayout.setInterceptContentTouch(false);
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetLayout.getState() != State.HIDDEN) {
            mBottomSheetLayout.dismissSheet();
            return;
        }
        super.onBackPressed();
    }

    private void showBottomSheet() {
        mFocusStealer.requestFocus();
        Utils.hideSoftKeyboardFromFocusedView(MainActivity.this);
        final MyFragment myFragment = new MyFragment();
        myFragment.setBottomSheetLayout(mBottomSheetLayout);
        myFragment.show(getSupportFragmentManager(), R.id.bottomsheet);
    }

}
