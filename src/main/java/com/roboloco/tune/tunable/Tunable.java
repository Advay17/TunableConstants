package com.roboloco.tune.tunable;

public abstract class Tunable<T> {
  protected T target;
  protected String name;

  public Tunable(T target, String name) {
    this.target = target;
    this.name = name;
  }

  public abstract void reload();
}
