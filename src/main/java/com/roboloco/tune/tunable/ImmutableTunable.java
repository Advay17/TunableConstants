package com.roboloco.tune.tunable;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.Command;
/**
 * An base class for a tunable that has an immutable target, such as a
 * {@link Pose2d}
 *
 * @implSpec The target class is immutable, meaning that tuning is done by
 *           creating a new instance of the target type with values updated from
 *           {@link Preferences}. This means that in certain scenarios(such as
 *           passing the target as one of the parameters for a {@link Command}),
 *           extra steps may need to be taken to ensure that the target is
 *           actually updated in its implementation.
 * @see Tunable
 * @author Kavin Muralikrishnan
 */
public abstract class ImmutableTunable<T> extends Tunable<T> {
	public ImmutableTunable(T target, String name) {
		super(target, name);
	}
	/**
	 * Sets the target of this tunable. Useful for custom ImmutableTunable
	 * implementations.
	 *
	 * @param target
	 *            The new target of this tunable.
	 */
	public void setTarget(T target) {
		this.target = target;
	}
}
