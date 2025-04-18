package com.roboloco.tune;

import com.roboloco.tune.IsTunableConstants;
import edu.wpi.first.wpilibj.Preferences;
/**
 * Interface for files generated from a class with the
 * {@link IsTunableConstants} annotation. This class should not be modified
 * directly.
 *
 * @author Kavin Muralikrishnan
 */
@SuppressWarnings("unused")
public interface TunableConstants {
	/**
	 * Reloads the values of this class from {@link Preferences}.
	 */
	public void reload();
}
