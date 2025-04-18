package com.roboloco.tune;

import edu.wpi.first.networktables.MultiSubscriber;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableListenerPoller;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.EnumSet;

import com.roboloco.tune.IsTunableConstants;

/**
 * This class is a base class for subsystems that need to reload tunable
 * constants from Preferences. It uses a {@link NetworkTableListenerPoller} to
 * listen for changes in the values of the tunable constants and reloads them
 * when they change.
 *
 * @author Kavin Muralikrishnan
 * @see IsTunableConstants
 * @see AutoReloadableAbstract
 */
@SuppressWarnings("unused")
public abstract class AutoReloadableSubsystem extends SubsystemBase {
	private final TunableConstants[] linkedTunableConstants;
	private final NetworkTableListenerPoller poller;
	private final MultiSubscriber multiSubscriber;

	public AutoReloadableSubsystem(TunableConstants... linkedTunableConstants) {
		this(NetworkTableInstance.getDefault(), linkedTunableConstants);
	}

	/**
	 * Constructor for AutoReloadableSubsystem, when you want to specify the
	 * NetworkTableInstance.
	 *
	 * @param nTableInstance
	 *            The {@link NetworkTableInstance} to use
	 * @param linkedTunableConstants
	 *            All the {@link TunableConstants} that are linked to this.
	 *
	 * @implSpec If you do not want values to be able to be automatically updated by
	 *           TunableConstants, leave the constructor blank and do not initialize
	 *           the TunableConstants <i>anywhere</i>.
	 */
	public AutoReloadableSubsystem(NetworkTableInstance nTableInstance, TunableConstants... linkedTunableConstants) {
		this.linkedTunableConstants = linkedTunableConstants;
		this.poller = new NetworkTableListenerPoller(nTableInstance);
		String[] tableNames = new String[linkedTunableConstants.length];
		for (int i = 0; i < linkedTunableConstants.length; i++) {
			tableNames[i] = "/Preferences/" + linkedTunableConstants.getClass().getSimpleName().substring(7) + "/";
		}
		multiSubscriber = new MultiSubscriber(nTableInstance, tableNames);
		poller.addListener(multiSubscriber, EnumSet.of(NetworkTableEvent.Kind.kValueAll));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec If you want to use this method, make sure to call super.periodic()
	 *           in this method.
	 */
	@Override
	public void periodic() {
		if (poller.readQueue().length > 0) {
			for (TunableConstants c : linkedTunableConstants) {
				c.reload();
			}
			reload();
		}
	}

	/**
	 * This method is called when one of the {@link TunableConstants} linked to this
	 * subsystem have been updated. This method should handle any necessary
	 * additional logic to ensure that all code is being updated with the new
	 * constants properly.
	 */
	public abstract void reload();
}
