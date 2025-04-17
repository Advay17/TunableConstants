package com.roboloco.tune.tunable;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Preferences;

public class TunableRotation2d extends Tunable<edu.wpi.first.math.geometry.Rotation2d> {
	public TunableRotation2d(edu.wpi.first.math.geometry.Rotation2d target, String name) {
		super(target, name);
		Preferences.initDouble(name + "degrees", target.getDegrees());
	}

	@Override
	public void reload() {
		target = new Rotation2d(Units.degreesToRadians(Preferences.getDouble(name + "degrees", target.getDegrees())));
	}
}
