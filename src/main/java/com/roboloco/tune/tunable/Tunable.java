package com.roboloco.tune.tunable;

import edu.wpi.first.wpilibj.Preferences;
import com.roboloco.tune.IsTunableConstants;

/**
 * A base class for all non primative and non {@link String} automatically
 * tunable objects.
 *
 * <p>
 * Any Object that has a corresponding Tunable class in either this package or
 * the frc.robot.util.tunable package(in your own code) can be automatically
 * logged through {@link Preferences}.
 *
 * @param <T>
 *            The type of the object to be tuned.
 * @see ImmutableTunable
 * @see IsTunableConstants
 * @author Kavin Muralikrishnan
 */
public abstract class Tunable<T> {
	protected T target;
	protected String name;

	/**
	 * Creates a new tunable object.
	 *
	 * @param target
	 *            The target object to be tuned.
	 * @param name
	 *            The name of the tunable object. Will follow the format
	 *            "[TunableConstants class name]/[name]/" (eg.
	 *            "ArmConstants/armPID/"") for easy use with AdvantageScope.
	 *            <p>
	 *            This object will show up in AdvantageScope as a child of the
	 *            Preferences tab.
	 */
	public Tunable(T target, String name) {
		this.target = target;
		this.name = name;
		init();
		reload();
	}

	/**
	 * Initializes all {@link Preferences} values for this tunable.
	 */
	public abstract void init();
	/**
	 * Reloads the target object from {@link Preferences}.
	 */
	public abstract void reload();

	/**
	 * Returns the tunable target. Useful for custom Tunable implementations.
	 *
	 * @return The target object, of type {@link T}
	 */
	public T getTarget() {
		return target;
	}
	/**
	 * Returns the name of this tunable. Useful for custom Tunable implementations.
	 *
	 * @return The name of this tunable, as a {@link String}
	 */
	public String getName() {
		return name;
	}
}
