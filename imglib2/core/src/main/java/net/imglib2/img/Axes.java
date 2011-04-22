/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */

package net.imglib2.img;

import java.util.Hashtable;

/**
 * An enumeration of common dimensional axis labels, for describing the
 * dimensional axes of a {@link Metadata} object (such as an {@link ImgPlus}).
 * 
 * @author Curtis Rueden
 */
public enum Axes implements Axis {

	/**
	 * Identifies the <i>X</i> dimensional type, representing a dimension in the
	 * first (X) spatial dimension.
	 */
	X("X"),

	/**
	 * Identifies the <i>Y</i> dimensional type, representing a dimension in the
	 * second (Y) spatial dimension.
	 */
	Y("Y"),

	/**
	 * Identifies the <i>Z</i> dimensional type, representing a dimension in the
	 * third (Z) spatial dimension.
	 */
	Z("Z"),

	/**
	 * Identifies the <i>Time</i> dimensional type, representing a dimension
	 * consisting of time points.
	 */
	TIME("Time"),

	/**
	 * Identifies the <i>Channel</i> dimensional type, representing a generic
	 * channel dimension.
	 */
	CHANNEL("Channel"),

	/**
	 * Identifies the <i>Spectra</i> dimensional type, representing a dimension
	 * consisting of spectral channels.
	 */
	SPECTRA("Spectra"),

	/**
	 * Identifies the <i>Lifetime</i> dimensional type, representing a dimension
	 * consisting of a lifetime histogram.
	 */
	LIFETIME("Lifetime"),

	/**
	 * Identifies the <i>Polarization</i> dimensional type, representing a
	 * dimension consisting of polarization states.
	 */
	POLARIZATION("Polarization"),

	/**
	 * Identifies the <i>Phase</i> dimensional type, representing a dimension
	 * consisting of phases.
	 */
	PHASE("Phase"),

	/**
	 * Identifies the <i>Frequency</i> dimensional type, representing a dimension
	 * consisting of frequencies.
	 */
	FREQUENCY("Frequency"),

	/** Represents an unknown dimensional type. */
	UNKNOWN("Unknown");

	private static Hashtable<String, Axis> axes =
		new Hashtable<String, Axis>();

	static {
		for (final Axis axis : Axes.values()) {
			axes.put(axis.getLabel(), axis);
		}
	}

	public static Axis get(final String label) {
		Axis axis = axes.get(label);
		if (axis == null) {
			axis = new CustomAxis(label);
			axes.put(label, axis);
		}
		return axis;
	}

	public static boolean isXY(final Axis dimLabel) {
		return dimLabel == Axes.X || dimLabel == Axes.Y;
	}

	private String label;

	private Axes(final String label) {
		this.label = label;
	}

	// -- Axis methods --

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public boolean isXY() {
		return this == Axes.X || this == Axes.Y;
	}

	@Override
	public boolean isSpatial() {
		return isXY() || this == Axes.Z;
	}

	// -- Object methods --

	@Override
	public String toString() {
		return label;
	}

	// -- Helper classes --

	/**
	 * A custom dimensional axis label, for describing the dimensional axes of a
	 * {@link Metadata} object (such as an {@link ImgPlus}).
	 */
	public static class CustomAxis implements Axis {

		private final String label;

		public CustomAxis(final String label) {
			this.label = label;
		}

		// -- Axis methods --

		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public boolean isXY() {
			return false;
		}

		@Override
		public boolean isSpatial() {
			return false;
		}

		// -- Object methods --

		@Override
		public String toString() {
			return label;
		}

	}

}
