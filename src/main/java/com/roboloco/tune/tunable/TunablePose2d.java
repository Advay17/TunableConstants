package com.roboloco.tune.tunable;

import edu.wpi.first.math.geometry.Pose2d;

public class TunablePose2d extends Tunable<Pose2d> {
  TunableRotation2d rotation;
  TunableTranslation2d translation;

  public TunablePose2d(Pose2d target, String name) {
    super(target, name);
    rotation = new TunableRotation2d(target.getRotation(), name + "rotation/");
    translation = new TunableTranslation2d(target.getTranslation(), name + "translation/");
  }

  @Override
  public void reload() {
    rotation.reload();
    translation.reload();
  }
}
