package com.roboloco.tune.tunable;

import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.Preferences;

public final class TunableTranslation3d extends ImmutableTunable<Translation3d> {
	public TunableTranslation3d(Translation3d target, String name) {
		super(target, name);
	}

	@Override
	public void init() {
		Preferences.initDouble(name + "x", target.getX());
		Preferences.initDouble(name + "y", target.getY());
		Preferences.initDouble(name + "z", target.getZ());
	}

	@Override
	public void reload() {
		target = new Translation3d(Preferences.getDouble(name + "x", target.getX()),
				Preferences.getDouble(name + "y", target.getY()), Preferences.getDouble(name + "z", target.getZ()));
	}
}
