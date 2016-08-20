package com.pisara.livedots;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
  private static final int UI_ANIMATION_DELAY = 300;

  private SwipeRefreshLayout mSwipeContainer;
  private final Handler mHideHandler = new Handler();
  private DrawArea mDrawArea;
  final Handler handler = new Handler();
  private final Runnable updater = new Runnable() {

    @Override
    public void run() {
      redraw();
    }
  };

  private final Runnable mHidePart2Runnable = new Runnable() {
    @SuppressLint("InlinedApi")
    @Override
    public void run() {
      mDrawArea.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
          | View.SYSTEM_UI_FLAG_FULLSCREEN
          | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
          | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
          | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
  };

  private final Runnable mShowPart2Runnable = new Runnable() {
    @Override
    public void run() {
      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null) {
        actionBar.show();
      }
    }
  };

  private boolean mVisible;
  private final Runnable mHideRunnable = new Runnable() {
    @Override
    public void run() {
      hide();
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mVisible = true;
    mDrawArea = (DrawArea) findViewById(R.id.canvas);
    mSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
    mSwipeContainer.setOnRefreshListener(this);

    mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
        android.R.color.holo_green_light,
        android.R.color.holo_orange_light,
        android.R.color.holo_red_light);

    setupTouchObservable();
  }

  @Override
  public void onRefresh() {
//    toggle();
    mSwipeContainer.setRefreshing(false);
  }

  private void setupTouchObservable() {
    TouchListener touchListener = new TouchListener();
    mDrawArea.setOnTouchListener(touchListener);
    touchListener
        .createObservable()
        .debounce(1, TimeUnit.SECONDS)
        .distinctUntilChanged()
        .filter(isDotCloseEnoughFilter())
        .subscribe(getTouchSubscriber());
  }

  @NonNull
  private Subscriber<Coordinates> getTouchSubscriber() {
    return new Subscriber<Coordinates>() {

      @Override
      public void onCompleted() {
        Log.d("MainActivity", "onCompleted");
      }

      @Override
      public void onError(Throwable e) {
        Log.d("MainActivity", "onError" + e.getLocalizedMessage());
      }

      @Override
      public void onNext(final Coordinates coordinates) {
        Log.d("MainActivity", "onNext=" + coordinates);
        handleTouchHitOnDot(coordinates);
      }
    };
  }

  private void handleTouchHitOnDot(final Coordinates coordinates) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(getApplicationContext(), "Hit=" + coordinates, Toast.LENGTH_SHORT).show();
      }
    });
  }

  @NonNull
  private Func1 isDotCloseEnoughFilter() {
    return new Func1() {
      @Override
      public Object call(Object dot) {
        return mDrawArea.isClose((Coordinates) dot, 50);
      }
    };
  }

  @Override
  protected void onResume() {
    super.onResume();
    updater.run();
  }

  protected void onPause() {
    super.onPause();
    handler.removeCallbacks(updater);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
//    delayedHide(100);
  }

  private void redraw() {
    mDrawArea.invalidate();
    handler.postDelayed(updater, 1);
  }

  private void toggle() {
    if (mVisible) {
      hide();
    } else {
      show();
    }
    mSwipeContainer.setRefreshing(false);
  }

  private void hide() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.hide();
    }
    mVisible = false;
    mHideHandler.removeCallbacks(mShowPart2Runnable);
    mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
  }

  @SuppressLint("InlinedApi")
  private void show() {
    mDrawArea.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    mVisible = true;
    mHideHandler.removeCallbacks(mHidePart2Runnable);
    mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
  }

  private void delayedHide(int delayMillis) {
    mHideHandler.removeCallbacks(mHideRunnable);
    mHideHandler.postDelayed(mHideRunnable, delayMillis);
  }
}
