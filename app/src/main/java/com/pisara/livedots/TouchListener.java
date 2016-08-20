package com.pisara.livedots;

import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class TouchListener<T> implements View.OnTouchListener {
  private Observable mObservable = null;
  private Subscriber mSubscriber;

  public Observable<T> createObservable() {
    if (mObservable == null) {
      mObservable = Observable.create(new Observable.OnSubscribe<T>() {
        @Override
        public void call(Subscriber<? super T> subscriber) {
          mSubscriber = subscriber;
        }
      });
    }
    return mObservable;
  }

  @Override
  public boolean onTouch(View view, MotionEvent event) {
    Coordinates coordinates = new Coordinates((int) event.getX(), (int) event.getY());
    mSubscriber.onNext(coordinates);
    return false;
  }
}
