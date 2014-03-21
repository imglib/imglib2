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

import java.util.ArrayList;
import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RealPositionable;
import net.imglib2.display.ColorTable;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.WrappedImg;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.meta.axis.DefaultLinearAxis;
import net.imglib2.meta.axis.LinearAxis;

/**
 * A simple container for storing an {@link Img} together with its metadata.
 * Metadata includes name, dimensional axes and calibration information.
 * 
 * @author Curtis Rueden
 */
public class ImgPlus<T> extends AbstractCalibratedRealInterval<CalibratedAxis>
	implements Img<T>, WrappedImg<T>, ImgPlusMetadata
{

	/** The name assigned to the ImgPlus if none is provided. */
	private static final String DEFAULT_NAME = "Untitled";

	private final Img<T> img;

	private String name;
	private String source = "";
	private int validBits;

	private ArrayList<Double> channelMin;
	private ArrayList<Double> channelMax;

	private int compositeChannelCount = 1;
	private final ArrayList<ColorTable> colorTable;

	// -- Constructors --

	public ImgPlus(final Img<T> img) {
		this(img, null, null, null);
	}

	public ImgPlus(final Img<T> img, final String name) {
		this(img, name, null, null);
	}

	public ImgPlus(final Img<T> img, final String name, final AxisType[] axes) {
		this(img, name, axes, null);
	}

	public ImgPlus(final Img<T> img, final ImgPlusMetadata metadata) {
		this(img, metadata.getName(), copyAxes(metadata));

		validBits = metadata.getValidBits();
		compositeChannelCount = metadata.getCompositeChannelCount();
		final int count = metadata.getColorTableCount();
		for (int i = 0; i < count; i++) {
			colorTable.add(metadata.getColorTable(i));
		}
	}

	public ImgPlus(final Img<T> img, final String name,
		final AxisType[] axisTypes, final double[] cal)
	{
		this(img, name, axisTypes, cal, null);
	}

	public ImgPlus(final Img<T> img, final String name,
		final AxisType[] axisTypes, final double[] cal, final String[] units)
	{
		this(img, name, createAxes(img, axisTypes, cal, units));
	}

	public ImgPlus(final Img<T> img, final String name,
		final CalibratedAxis... axes)
	{
		super(img, axes);
		this.img = img;
		this.name = validateName(name);

		channelMin = new ArrayList<Double>();
		channelMax = new ArrayList<Double>();
		colorTable = new ArrayList<ColorTable>();
		setSource("");
	}

	// -- ImgPlus methods --

	/**
	 * Gets the backing {@link Img} of this {@code ImgPlus}.
	 * <p>
	 * Note that most of the time, you do <em>not</em> need to call this method.
	 * Rather, you can use the {@code ImgPlus} directly because it implements all
	 * of the same interfaces. However, there are legitimate cases where you may
	 * need access to the backing container (e.g., for performance, to cast to the
	 * appropriate {@link Img} implementation such as {@link ArrayImg}).
	 * </p>
	 */
	@Override
	public Img<T> getImg() {
		return img;
	}

	// -- RandomAccessible methods --

	@Override
	public RandomAccess<T> randomAccess() {
		return img.randomAccess();
	}

	@Override
	public RandomAccess<T> randomAccess(final Interval interval) {
		return img.randomAccess(interval);
	}

	// -- Interval methods --

	@Override
	public long min(final int d) {
		return img.min(d);
	}

	@Override
	public void min(final long[] min) {
		img.min(min);
	}

	@Override
	public void min(final Positionable min) {
		img.min(min);
	}

	@Override
	public long max(final int d) {
		return img.max(d);
	}

	@Override
	public void max(final long[] max) {
		img.max(max);
	}

	@Override
	public void max(final Positionable max) {
		img.max(max);
	}

	// -- Dimensions methods --

	@Override
	public void dimensions(final long[] dimensions) {
		img.dimensions(dimensions);
	}

	@Override
	public long dimension(final int d) {
		return img.dimension(d);
	}

	// -- RealInterval methods --

	@Override
	public double realMin(final int d) {
		return img.realMin(d);
	}

	@Override
	public void realMin(final double[] min) {
		img.realMin(min);
	}

	@Override
	public void realMin(final RealPositionable min) {
		img.realMin(min);
	}

	@Override
	public double realMax(final int d) {
		return img.realMax(d);
	}

	@Override
	public void realMax(final double[] max) {
		img.realMax(max);
	}

	@Override
	public void realMax(final RealPositionable max) {
		img.realMax(max);
	}

	// -- IterableInterval methods --

	@Override
	public Cursor<T> cursor() {
		return img.cursor();
	}

	@Override
	public Cursor<T> localizingCursor() {
		return img.localizingCursor();
	}

	// -- IterableRealInterval methods --

	@Override
	public long size() {
		return img.size();
	}

	@Override
	public T firstElement() {
		return img.firstElement();
	}

	@Override
	public Object iterationOrder() {
		return img.iterationOrder();
	}

	@Override
	public boolean equalIterationOrder(final IterableRealInterval<?> f) {
		return iterationOrder().equals(f.iterationOrder());
	}

	// -- Iterable methods --

	@Override
	public Iterator<T> iterator() {
		return img.iterator();
	}

	// -- Img methods --

	@Override
	public ImgFactory<T> factory() {
		return img.factory();
	}

	@Override
	public ImgPlus<T> copy() {
		return new ImgPlus<T>(img.copy(), this);
	}

	// -- Named methods --

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	// -- ImageMetadata methods --

	@Override
	public int getValidBits() {
		return validBits;
	}

	@Override
	public void setValidBits(final int bits) {
		validBits = bits;
	}

	@Override
	public double getChannelMinimum(final int c) {
		if (c < 0 || c >= channelMin.size()) return Double.NaN;
		final Double d = channelMin.get(c);
		return d == null ? Double.NaN : d;
	}

	@Override
	public void setChannelMinimum(final int c, final double min) {
		if (c < 0) throw new IllegalArgumentException("Invalid channel: " + c);
		if (c >= channelMin.size()) {
			channelMin.ensureCapacity(c + 1);
			for (int i = channelMin.size(); i <= c; i++)
				channelMin.add(null);
		}
		channelMin.set(c, min);
	}

	@Override
	public double getChannelMaximum(final int c) {
		if (c < 0 || c >= channelMax.size()) return Double.NaN;
		final Double d = channelMax.get(c);
		return d == null ? Double.NaN : d;
	}

	@Override
	public void setChannelMaximum(final int c, final double max) {
		if (c < 0) throw new IllegalArgumentException("Invalid channel: " + c);
		if (c >= channelMax.size()) {
			channelMax.ensureCapacity(c + 1);
			for (int i = channelMax.size(); i <= c; i++)
				channelMax.add(null);
		}
		channelMax.set(c, max);
	}

	@Override
	public int getCompositeChannelCount() {
		return compositeChannelCount;
	}

	@Override
	public void setCompositeChannelCount(final int value) {
		compositeChannelCount = value;
	}

	@Override
	public ColorTable getColorTable(final int no) {
		if (no >= colorTable.size()) return null;
		return colorTable.get(no);
	}

	@Override
	public void setColorTable(final ColorTable cT, final int no) {
		colorTable.set(no, cT);
	}

	@Override
	public void initializeColorTables(final int count) {
		colorTable.ensureCapacity(count);
		colorTable.clear();
		for (int i = 0; i < count; i++) {
			colorTable.add(null);
		}
	}

	@Override
	public int getColorTableCount() {
		return colorTable.size();
	}

	// -- Sourced methods --

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public void setSource(final String source) {
		this.source = source;
	}

	// -- Utility methods --

	/** Ensures the given {@link Img} is an ImgPlus, wrapping if necessary. */
	public static <T> ImgPlus<T> wrap(final Img<T> img) {
		if (img instanceof ImgPlus) return (ImgPlus<T>) img;
		return new ImgPlus<T>(img);
	}

	/** Ensures the given {@link Img} is an ImgPlus, wrapping if necessary. */
	public static <T> ImgPlus<T> wrap(final Img<T> img,
		final ImgPlusMetadata metadata)
	{
		if (img instanceof ImgPlus) return (ImgPlus<T>) img;
		return new ImgPlus<T>(img, metadata);
	}

	// -- Helper methods --

	/** Creates {@link LinearAxis} objects matching the given arguments. */
	private static CalibratedAxis[] createAxes(final Img<?> img,
		final AxisType[] axisTypes, final double[] cal, final String[] units)
	{
		// validate arguments
		final int numDims = img.numDimensions();
		final AxisType[] validTypes = validateAxisTypes(numDims, axisTypes);
		if (numDims != validTypes.length) {
			throw new IllegalArgumentException("Axis type count does not match " +
				"dimensionality: " + validTypes.length + " != " + numDims);
		}
		final double[] validCal = validateCalibration(numDims, cal);
		if (numDims != validCal.length) {
			throw new IllegalArgumentException("Calibration count does not match " +
				"dimensionality: " + validCal.length + " != " + numDims);
		}
		final String[] validUnits = validateUnits(numDims, units);
		if (numDims != validUnits.length) {
			throw new IllegalArgumentException("Unit count does not match " +
				"dimensionality: " + validUnits.length + " != " + numDims);
		}

		// create axes
		final LinearAxis[] axes = new LinearAxis[validTypes.length];
		for (int d = 0; d < numDims; d++) {
			axes[d] =
				new DefaultLinearAxis(validTypes[d], validUnits[d], validCal[d]);
		}
		return axes;
	}

	/** Ensures the given name is valid. */
	private static String validateName(final String name) {
		if (name == null) return DEFAULT_NAME;
		return name;
	}

	/** Ensures the given axis types are valid. */
	private static AxisType[] validateAxisTypes(final int numDims,
		final AxisType[] types)
	{
		if (types != null && numDims == types.length) return types;
		final AxisType[] valid = new AxisType[numDims];
		for (int i = 0; i < valid.length; i++) {
			if (types != null && types.length > i) valid[i] = types[i];
			else {
				switch (i) {
					case 0:
						valid[i] = Axes.X;
						break;
					case 1:
						valid[i] = Axes.Y;
						break;
					default:
						valid[i] = Axes.unknown();
				}
			}
		}
		return valid;
	}

	/** Ensures the given calibration values are valid. */
	private static double[] validateCalibration(final int numDims,
		final double[] cal)
	{
		if (cal != null && numDims == cal.length) return cal;
		final double[] valid = new double[numDims];
		for (int i = 0; i < valid.length; i++) {
			if (cal != null && cal.length > i) valid[i] = cal[i];
			else valid[i] = 1;
		}
		return valid;
	}

	/** Ensures the given unit values are valid. */
	private static String[]
		validateUnits(final int numDims, final String[] units)
	{
		if (units != null && numDims == units.length) return units;
		final String[] valid = new String[numDims];
		for (int i = 0; i < valid.length; i++) {
			if (units != null && units.length > i) valid[i] = units[i];
		}
		return valid;
	}

	/** Makes a deep copy of the given space's axes. */
	private static CalibratedAxis[] copyAxes(final CalibratedSpace<?> space) {
		final CalibratedAxis[] axes = new CalibratedAxis[space.numDimensions()];
		for (int d = 0; d < axes.length; d++) {
			axes[d] = space.axis(d).copy();
		}
		return axes;
	}
	

}
