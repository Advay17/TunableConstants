package com.roboloco.tune.tunable;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Preferences;

public final class TunableTranslation2d extends ImmutableTunable<Translation2d> {
	public TunableTranslation2d(Translation2d target, String name) {
		super(target, name);
	}

	@Override
	public void reload() {
		target = new Translation2d(Preferences.getDouble(name + "x", target.getX()),
				Preferences.getDouble(name + "y", target.getY()));
	}

	@Override
	public void init() {
		Preferences.initDouble(name + "x", target.getX());
		Preferences.initDouble(name + "y", target.getY());
	}
}
