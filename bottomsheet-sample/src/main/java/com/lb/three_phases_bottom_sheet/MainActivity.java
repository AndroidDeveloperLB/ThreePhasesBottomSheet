package com.lb.three_phases_bottom_sheet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.ImagePickerSheetView;

/**
 * Activity demonstrating the use of {@link ImagePickerSheetView}
 */
public final class MainActivity extends AppCompatActivity
  {
  protected BottomSheetLayout mBottomSheetLayout;
  private View mFocusStealer;
  private static boolean useAppBarLayoutMethod=true;

  @Override
  protected void onCreate(Bundle savedInstanceState)
    {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setSupportActionBar((Toolbar)findViewById(R.id.main_toolbar));
    mFocusStealer=findViewById(R.id.focusStealer);
    mBottomSheetLayout=(BottomSheetLayout)findViewById(R.id.bottomsheet);
    findViewById(R.id.bottomsheet_fragment_button).setOnClickListener(new View.OnClickListener()
      {
      @Override
      public void onClick(View v)
        {
        useAppBarLayoutMethod=true;
        showBottomSheet();
        }
      });
    findViewById(R.id.bottomsheet_fragment_button2).setOnClickListener(new OnClickListener()
      {
      @Override
      public void onClick(final View v)
        {
        useAppBarLayoutMethod=false;
        showBottomSheet();
        }
      });

    EditText editText=(EditText)findViewById(R.id.bottomsheet_fragment_editText);
    editText.setOnEditorActionListener(new OnEditorActionListener()
      {

      @Override
      public boolean onEditorAction(final TextView v,final int actionId,final KeyEvent event)
        {
        if(actionId==EditorInfo.IME_ACTION_DONE||actionId==EditorInfo.IME_ACTION_SEARCH)
          {
          showBottomSheet();
          return true;
          }
        return false;
        }
      });
    editText.setOnKeyListener(new OnKeyListener()
      {
      @Override
      public boolean onKey(final View v,final int keyCode,final KeyEvent event)
        {
        if(keyCode==KeyEvent.KEYCODE_ENTER)
          {
          showBottomSheet();
          return true;
          }
        return false;
        }
      });
    editText.setOnFocusChangeListener(new OnFocusChangeListener()
      {
      @Override
      public void onFocusChange(final View v,final boolean hasFocus)
        {
        if(hasFocus)
          mBottomSheetLayout.dismissSheet();
        }
      });

    final Fragment fragment=getSupportFragmentManager().findFragmentByTag(Integer.toString(R.id.bottomsheet));

    if(fragment!=null&&fragment instanceof MyFragment)
      {
      MyFragment myFragment=(MyFragment)fragment;
      getSupportFragmentManager().beginTransaction().remove(myFragment).commit();
      myFragment=new MyFragment();
      myFragment.setBottomSheetLayout(mBottomSheetLayout);
      myFragment.show(getSupportFragmentManager(),R.id.bottomsheet);
      }

    if(fragment!=null&&fragment instanceof MyFragment2)
      {
      MyFragment2 myFragment2=(MyFragment2)fragment;
      getSupportFragmentManager().beginTransaction().remove(myFragment2).commit();
      myFragment2=new MyFragment2();
      myFragment2.setBottomSheetLayout(mBottomSheetLayout);
      myFragment2.show(getSupportFragmentManager(),R.id.bottomsheet);
      }
    mBottomSheetLayout.setEnableDismissByScroll(false);
    mBottomSheetLayout.setShouldDimContentView(false);
    mBottomSheetLayout.setPeekOnDismiss(true);
    mBottomSheetLayout.setPeekSheetTranslation(getResources().getDimensionPixelSize(R.dimen.header_height_peeked));
    mBottomSheetLayout.setInterceptContentTouch(false);
    }

  @Override
  public boolean onCreateOptionsMenu(final Menu menu)
    {
    getMenuInflater().inflate(R.menu.main,menu);
    return true;
    }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item)
    {
    String url=null;
    switch(item.getItemId())
      {
      case R.id.menuItem_all_my_apps:
        url="https://play.google.com/store/apps/developer?id=AndroidDeveloperLB";
        break;
      case R.id.menuItem_all_my_repositories:
        url="https://github.com/AndroidDeveloperLB";
        break;
      case R.id.menuItem_current_repository_website:
        url="https://github.com/AndroidDeveloperLB/ThreePhasesBottomSheet";
        break;
      }
    if(url==null)
      return true;
    final Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(url));
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    startActivity(intent);
    return true;
    }

  @Override
  public void onBackPressed()
    {
    switch(mBottomSheetLayout.getState())
      {
      case HIDDEN:
        super.onBackPressed();
        break;
      case PREPARING:
        super.onBackPressed();
        break;
      case PEEKED:
        mBottomSheetLayout.dismissSheet();
        break;
      case EXPANDED:
        mBottomSheetLayout.peekSheet();
        break;
      }
    }

  private void showBottomSheet()
    {
    mFocusStealer.requestFocus();
    Utils.hideSoftKeyboardFromFocusedView(MainActivity.this);
    if(useAppBarLayoutMethod)
      {
      final MyFragment myFragment=new MyFragment();
      //final MyFragment2 myFragment = new MyFragment2();
      myFragment.setBottomSheetLayout(mBottomSheetLayout);
      myFragment.show(getSupportFragmentManager(),R.id.bottomsheet);
      }
    else
      {
      final MyFragment2 myFragment=new MyFragment2();
      myFragment.setBottomSheetLayout(mBottomSheetLayout);
      myFragment.show(getSupportFragmentManager(),R.id.bottomsheet);
      }
    }

  }
