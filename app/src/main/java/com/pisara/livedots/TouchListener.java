package com.pisara.livedots;

import android.view.MotionEvent;
import android.view.View;

import rx.subjects.PublishSubject;

public class TouchListener implements View.OnTouchListener {
  private PublishSubject<Coordinates> mTouchSubject;

  public TouchListener(PublishSubject<Coordinates> touchSubject) {
    mTouchSubject = touchSubject;
  }

  @Override
  public boolean onTouch(View view, MotionEvent event) {
    Coordinates coordinates = new Coordinates((int) event.getX(), (int) event.getY());
    mTouchSubject.onNext(coordinates);
    return true;
  }
}
