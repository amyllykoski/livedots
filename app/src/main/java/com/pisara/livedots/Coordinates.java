package com.pisara.livedots;

public class Coordinates {
  public int x;
  public int y;

  public Coordinates(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public boolean isClose(Coordinates dot, int margin) {
    return Math.abs(x - dot.x) <= margin
        && Math.abs(y - dot.y) <= margin;
  }

  @Override
  public String toString() {
    return "Coordinates{" +
        "x=" + x +
        ", y=" + y +
        '}';
  }
}
