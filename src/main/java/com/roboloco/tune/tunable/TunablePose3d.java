package com.roboloco.tune.tunable;

import edu.wpi.first.math.geometry.Pose3d;

public final class TunablePose3d extends Tunable<Pose3d> {

	public TunableRotation3d rotation;
	public TunableTranslation3d translation;

	public TunablePose3d(Pose3d target, String name) {
		super(target, name);
	}

	@Override
	public void init() {
		rotation = new TunableRotation3d(target.getRotation(), name + "rotation/");
		translation = new TunableTranslation3d(target.getTranslation(), name + "translation/");
	}
	@Override
	public void reload() {
		rotation.reload();
		translation.reload();
		target = new Pose3d(translation.getTarget(), rotation.getTarget());
	}

}
