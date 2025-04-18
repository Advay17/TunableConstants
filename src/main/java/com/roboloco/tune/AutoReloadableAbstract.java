package com.roboloco.tune;

import java.util.EnumSet;

import edu.wpi.first.networktables.MultiSubscriber;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableListenerPoller;

/**
 * This class is a base class for non-subsystems that need to reload tunable
 * constants from Preferences. It uses a {@link NetworkTableListenerPoller} to
 * listen for changes in the values of the tunable constants and reloads them
 * when they change.
 *
 * @author Kavin Muralikrishnan
 * @see IsTunableConstants
 * @see AutoReloadableSubsystem
 */
public abstract class AutoReloadableAbstract {
	private final TunableConstants[] linkedTunableConstants;
	private final NetworkTableListenerPoller poller;
	private final MultiSubscriber multiSubscriber;

	public AutoReloadableAbstract(TunableConstants... linkedTunableConstants) {
		this(NetworkTableInstance.getDefault(), linkedTunableConstants);
	}

	public AutoReloadableAbstract(NetworkTableInstance nTableInstance, TunableConstants... linkedTunableConstants) {
		this.linkedTunableConstants = linkedTunableConstants;
		this.poller = new NetworkTableListenerPoller(nTableInstance);
		String[] tableNames = new String[linkedTunableConstants.length];
		for (int i = 0; i < linkedTunableConstants.length; i++) {
			tableNames[i] = "/Preferences/" + linkedTunableConstants.getClass().getSimpleName().substring(7) + "/";
		}
		multiSubscriber = new MultiSubscriber(nTableInstance, tableNames);
		poller.addListener(multiSubscriber, EnumSet.of(NetworkTableEvent.Kind.kValueAll));
	}

	public void periodic() {
		if (poller.readQueue().length > 0) {
			for (TunableConstants c : linkedTunableConstants) {
				c.reload();
			}
			reload();
		}
	}

	public abstract void reload();
}
