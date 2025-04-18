package com.roboloco.tune.tunable;

import edu.wpi.first.math.geometry.Pose2d;

public final class TunablePose2d extends ImmutableTunable<Pose2d> {
	TunableRotation2d rotation;
	TunableTranslation2d translation;

	public TunablePose2d(Pose2d target, String name) {
		super(target, name);
	}

	@Override
	public void reload() {
		rotation.reload();
		translation.reload();
		target = new Pose2d(translation.getTarget(), rotation.getTarget());
	}

	@Override
	public void init() {
		rotation = new TunableRotation2d(target.getRotation(), name + "rotation/");
		translation = new TunableTranslation2d(target.getTranslation(), name + "translation/");
	}
}
