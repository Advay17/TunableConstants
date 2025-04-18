package com.roboloco.tune.tunable;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Preferences;

public final class TunableRotation2d extends ImmutableTunable<Rotation2d> {
	public TunableRotation2d(Rotation2d target, String name) {
		super(target, name);
	}

	@Override
	public void init() {
		Preferences.initDouble(name + "degrees", target.getDegrees());
		Preferences.initDouble(name + "radians", target.getRadians());
		Preferences.initDouble(name + "rotations", target.getRotations());
	}

	@Override
	public void reload() {
		if (Preferences.getDouble(name + "rotations", target.getRotations()) != target.getRotations())
			target = Rotation2d.fromRotations(Preferences.getDouble(name + "rotations", target.getRotations()));
		else if (Preferences.getDouble(name + "degrees", target.getDegrees()) != target.getDegrees())
			target = Rotation2d.fromDegrees(Preferences.getDouble(name + "degrees", target.getDegrees()));
		else if (Preferences.getDouble(name + "radians", target.getRadians()) != target.getRadians())
			target = Rotation2d.fromRadians(Preferences.getDouble(name + "radians", target.getRadians()));
		Preferences.setDouble(name + "rotations", target.getRotations());
		Preferences.setDouble(name + "degrees", target.getDegrees());
		Preferences.setDouble(name + "radians", target.getRadians());
	}
}
