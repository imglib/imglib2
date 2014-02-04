/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.meta;

import java.util.HashMap;

/**
 * An extensible enumeration of dimensional {@link AxisType}s. Provides a core
 * set ({@link #X}, {@link #Y}, {@link #Z}, {@link #TIME} and {@link #CHANNEL})
 * of AxisTypes. The {@link #get} methods can be used to create new, custom
 * AxisTypes which will be cached for future use.
 * 
 * @author Curtis Rueden
 * @author Mark Hiner
 */
public final class Axes {

	// -- Constants --

	/** Label for unknown axis types, which are returned by {@link #unknown()}. */
	public static final String UNKNOWN_LABEL = "Unknown";

	// -- Fields --

	/**
	 * Table of existing AxisTypes
	 */
	private static HashMap<String, AxisType> axes =
		new HashMap<String, AxisType>();

	// -- Constructor to prevent instantiation --

	private Axes() {}

	// -- Core axes constants --

	/**
	 * Identifies the <i>X</i> dimensional type, representing a dimension in the
	 * first (X) spatial dimension.
	 */
	public static final AxisType X = get("X", true);

	/**
	 * Identifies the <i>Y</i> dimensional type, representing a dimension in the
	 * second (Y) spatial dimension.
	 */
	public static final AxisType Y = get("Y", true);

	/**
	 * Identifies the <i>Z</i> dimensional type, representing a dimension in the
	 * third (Z) spatial dimension.
	 */
	public static final AxisType Z = get("Z", true);

	/**
	 * Identifies the <i>Time</i> dimensional type, representing a dimension
	 * consisting of time points.
	 */
	public static final AxisType TIME = get("Time");

	/**
	 * Identifies the <i>Channel</i> dimensional type, representing a generic
	 * channel dimension.
	 */
	public static final AxisType CHANNEL = get("Channel");

	// -- Static utility methods --

	/**
	 * Accessor for an AxisType of the given label. Creates and caches the
	 * AxisType as a non-spatial axis if it does not already exist.
	 */
	public static AxisType get(final String label) {
		return get(label, false);
	}

	/**
	 * Accessor for an AxisType of the given label. Creates and caches the
	 * AxisType with the given label and spatial flag if it does not already
	 * exist.
	 */
	public static AxisType get(final String label, final boolean spatial) {
		if (UNKNOWN_LABEL.equals(label)) return unknown();

		AxisType axis = axes.get(label);

		// if the axis is null, create it
		if (axis == null) {
			// synchronized to ensure the axis is only created once
			synchronized (axes) {
				// see if another thread already created our axis
				axis = axes.get(label);
				// if not, create and store it
				if (axis == null) {
					axis = new DefaultAxisType(label, spatial);
					axes.put(label, axis);
				}
			}
		}
		return axis;
	}

	/**
	 * @return an array of {@code AxisType}s corresponding to all currently
	 *         defined axes.
	 */
	public static AxisType[] knownTypes() {
		return axes.values().toArray(new AxisType[0]);
	}

	/**
	 * Gets an "unknown" axis type.
	 * <p>
	 * Always returns a new object, which is not part of the extended enumeration.
	 * In this way, two unknown axis types are never equal.
	 * </p>
	 */
	public static AxisType unknown() {
		return new DefaultAxisType(UNKNOWN_LABEL);
	}
}
