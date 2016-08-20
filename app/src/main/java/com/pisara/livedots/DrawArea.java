package com.pisara.livedots;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by antti on 8/6/16.
 */
public class DrawArea extends View {

  private static final int NUMBER_OF_DOTS = 50;

  private DotLand mDotLand;
  private boolean mAreDimensionSet = false;

  public DrawArea(Context context) {
    super(context);
    init(context);
  }

  public DrawArea(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public DrawArea(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  public void reset() {
    mAreDimensionSet = false;
    mDotLand.reset();
  }

  public boolean isClose(Coordinates coordinates, int margin) {
    return mDotLand.isHit(coordinates, margin);
  }

  private void init(Context context) {
    mDotLand = new DotLand(context);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (!mAreDimensionSet) {
      initDotLand(canvas);
    }
    mDotLand.draw(canvas);
  }

  private void initDotLand(Canvas canvas) {
    mDotLand.setDimensions(canvas.getWidth(), canvas.getHeight());
    mDotLand.generateDots(NUMBER_OF_DOTS);
    mAreDimensionSet = true;
  }
}
