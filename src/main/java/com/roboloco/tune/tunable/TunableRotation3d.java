package com.roboloco.tune.tunable;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;

public final class TunableRotation3d extends ImmutableTunable<Rotation3d> {
	private TunableRotation2d roll;
	private TunableRotation2d pitch;
	private TunableRotation2d yaw;
	public TunableRotation3d(Rotation3d target, String name) {
		super(target, name);
	}

	@Override
	public void reload() {
		roll.reload();
		pitch.reload();
		yaw.reload();
		target = new Rotation3d(roll.getTarget().getRadians(), pitch.getTarget().getRadians(),
				yaw.getTarget().getRadians());
	}

	@Override
	public void init() {
		roll = new TunableRotation2d(Rotation2d.fromRadians(target.getX()), name + "roll/");
		pitch = new TunableRotation2d(Rotation2d.fromRadians(target.getY()), name + "pitch/");
		yaw = new TunableRotation2d(Rotation2d.fromRadians(target.getZ()), name + "yaw/");
	}
}
