package com.roboloco.tune.tunable;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.Preferences;

public class TunablePIDController extends Tunable<PIDController> {

    public TunablePIDController(PIDController target, String name) {
        super(target, name);
        Preferences.initDouble(name + "kP", target.getP());
        Preferences.initDouble(name + "kI", target.getI());
        Preferences.initDouble(name + "kD", target.getD());
        Preferences.initDouble(name + "tolerance", target.getErrorTolerance());
        Preferences.initDouble(name + "errorDerivativeTolerance", target.getErrorDerivativeTolerance());
    }

    @Override
    public void reload() {
        target.setPID(Preferences.getDouble(name + "kP", target.getP()),
                Preferences.getDouble(name + "kI", target.getI()), Preferences.getDouble(name + "kD", target.getD()));
        target.setTolerance(Preferences.getDouble(name + "tolerance", target.getErrorTolerance()),
        Preferences.getDouble(name + "errorDerivativeTolerance", target.getErrorDerivativeTolerance()));
    }

}
