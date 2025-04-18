// Copyright 2021-2025 FRC 5338
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// version 3 as published by the Free Software Foundation or
// available in the root directory of this project.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
package com.roboloco.tune;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.roboloco.tune.TunableConstants;

/**
 * Annotation to mark a class as a Constants class that will have a
 * {@link TunableConstants} generated for it.
 *
 * @implSpec Ensure that any fields you would like to be tunable are public and
 *           static, and ensure this class is not final.
 *
 * @author Kavin Muralikrishnan
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface IsTunableConstants {
}
