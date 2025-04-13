package com.roboloco.tune.tunable;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.wpilibj.Preferences;

public class TunableArmFeedforward extends Tunable<ArmFeedforward> {
    public TunableArmFeedforward(ArmFeedforward target, String name) {
        super(target, name);
        Preferences.initDouble(name + "kS", target.getKs());
        Preferences.initDouble(name + "kV", target.getKv());
        Preferences.initDouble(name + "kA", target.getKg());
        Preferences.initDouble(name + "kG", target.getKg());
    }

    @Override
    public void reload() {
        target.setKs(Preferences.getDouble(name + "kS", target.getKs()));
        target.setKv(Preferences.getDouble(name + "kV", target.getKv()));
        target.setKg(Preferences.getDouble(name + "kG", target.getKg()));
        target.setKa(Preferences.getDouble(name + "kA", target.getKa()));
    }
}
