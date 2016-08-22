package com.pisara.livedots;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity {
  private final Handler handler = new Handler();
  private DrawArea mDrawArea;
  private final Runnable updater = new Runnable() {
    @Override
    public void run() {
      redraw();
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mDrawArea = (DrawArea) findViewById(R.id.canvas);
    setupTouchObservable();
  }

  @Override
  protected void onResume() {
    super.onResume();
    updater.run();
  }

  @Override
  protected void onPause() {
    super.onPause();
    handler.removeCallbacks(updater);
  }

  private void setupTouchObservable() {
    PublishSubject<Coordinates> touchSubject = PublishSubject.create();
    TouchListener touchListener = new TouchListener(touchSubject);
    mDrawArea.setOnTouchListener(touchListener);
    touchSubject
        .debounce(300, TimeUnit.MILLISECONDS)
        .distinctUntilChanged()
        .filter(isDotCloseEnoughFilter())
        .subscribe(getTouchSubscriber());
  }

  @NonNull
  private Func1<Coordinates, Boolean> isDotCloseEnoughFilter() {
    return new Func1<Coordinates, Boolean>() {
      @Override
      public Boolean call(Coordinates dot) {
        return mDrawArea.isClose(dot, 50);
      }
    };
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

  private void redraw() {
    mDrawArea.invalidate();
    handler.postDelayed(updater, 1);
  }
}
