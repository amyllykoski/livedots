package com.pisara.livedots;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DotLand {
  private int mWidth;
  private int mHeight;

  private Bitmap mDot;
  private List<Coordinates> mDots = new ArrayList<>();

  public DotLand(Context context) {
    mDot = BitmapFactory.decodeResource(context.getResources(),
        R.drawable.a_device_icon_ac_bt_bace_orange);
  }

  public void generateDots(int numberOfDots) {
    Random random = new Random();
    for (int i = 0; i < numberOfDots; i++) {
      mDots.add(new Coordinates(random.nextInt(mWidth), random.nextInt(mHeight)));
    }
  }

  public void setDimensions(int width, int height) {
    mWidth = width;
    mHeight = height;
  }

  public void draw(Canvas canvas) {
    int i = 0;
    Random rnd = new Random();
    for (Coordinates dot : mDots) {
      canvas.drawBitmap(mDot, dot.x, dot.y, null);
      dot.x += rnd.nextBoolean() ? rnd.nextInt(5) : -rnd.nextInt(5);
      dot.y += rnd.nextBoolean() ? rnd.nextInt(5) : -rnd.nextInt(5);
      mDots.set(i++, dot);
    }
  }

  public void reset() {
    mDots.clear();
  }

  public boolean isHit(final Coordinates theDot, int margin) {
    for (Coordinates dot : mDots) {
      if (dot.isClose(theDot, margin)) {
        mDots.remove(dot);
        return true;
      }
    }
    return false;
  }
}
