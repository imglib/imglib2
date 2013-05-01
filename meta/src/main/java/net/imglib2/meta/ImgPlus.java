/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
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

/**
 * A simple container for storing an {@link Img} together with its metadata.
 * Metadata includes name, dimensional axes and calibration information.
 * 
 * @author Curtis Rueden
 */
public class ImgPlus<T> implements Img<T>, Metadata {

	/** The name assigned to the ImgPlus if none is provided. */
	private static final String DEFAULT_NAME = "Untitled";

	private final Img<T> img;

	private String name;
	private String source = "";
	private final AxisType[] axes;
	private final double[] cal;
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

	public ImgPlus(final Img<T> img, final Metadata metadata) {
		this(img, metadata.getName(), getAxes(img, metadata), getCalibration(img,
			metadata));
		validBits = metadata.getValidBits();
		compositeChannelCount = metadata.getCompositeChannelCount();
		final int count = metadata.getColorTableCount();
		for (int i = 0; i < count; i++) {
			colorTable.add(metadata.getColorTable(i));
		}
	}

	public ImgPlus(final Img<T> img, final String name, final AxisType[] axes,
		final double[] cal)
	{
		this.img = img;
		this.name = validateName(name);
		this.axes = validateAxes(img.numDimensions(), axes);
		this.cal = validateCalibration(img.numDimensions(), cal);
		channelMin = new ArrayList<Double>();
		channelMax = new ArrayList<Double>();
		colorTable = new ArrayList<ColorTable>();
		setSource("");
	}

	// -- ImgPlus methods --

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

	// -- EuclideanSpace methods --

	@Override
	public int numDimensions() {
		return img.numDimensions();
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
	public Object iterationOrder()
	{
		return img.iterationOrder();
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		return iterationOrder().equals( f.iterationOrder() );
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

	// -- CalibratedSpace methods --

	@Override
	public int getAxisIndex(final AxisType axis) {
		for (int i = 0; i < axes.length; i++) {
			if (axes[i] == axis) return i;
		}
		return -1;
	}

	@Override
	public AxisType axis(final int d) {
		return axes[d];
	}

	@Override
	public void axes(final AxisType[] target) {
		for (int i = 0; i < target.length; i++)
			target[i] = axes[i];
	}

	@Override
	public void setAxis(final AxisType axis, final int d) {
		axes[d] = axis;
	}

	@Override
	public double calibration(final int d) {
		return cal[d];
	}

	@Override
	public void calibration(final double[] target) {
		for (int i = 0; i < target.length; i++)
			target[i] = cal[i];
	}

	@Override
	public void calibration(final float[] target) {
		for (int i = 0; i < target.length; i++)
			target[i] = (float)cal[i];
	}

	@Override
	public void setCalibration(final double value, final int d) {
		cal[d] = value;
	}

	@Override
	public void setCalibration(final double[] cal) {
		for ( int d = 0; d < cal.length; ++d )
			this.cal[d] = cal[ d ];
	}

	@Override
	public void setCalibration(final float[] cal) {
		for ( int d = 0; d < cal.length; ++d )
			this.cal[d] = cal[ d ];
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
	public void setSource(String source) {
		this.source = source;
	}

	// -- Utility methods --

	/** Ensures the given {@link Img} is an ImgPlus, wrapping if necessary. */
	public static <T> ImgPlus<T> wrap(final Img<T> img) {
		if (img instanceof ImgPlus) return (ImgPlus<T>) img;
		return new ImgPlus<T>(img);
	}

	/** Ensures the given {@link Img} is an ImgPlus, wrapping if necessary. */
	public static <T> ImgPlus<T> wrap(final Img<T> img, final Metadata metadata)
	{
		if (img instanceof ImgPlus) return (ImgPlus<T>) img;
		return new ImgPlus<T>(img, metadata);
	}

	// -- Helper methods --

	/** Ensures the given name is valid. */
	private static String validateName(final String name) {
		if (name == null) return DEFAULT_NAME;
		return name;
	}

	/** Ensures the given axis labels are valid. */
	private static AxisType[] validateAxes(final int numDims, final AxisType[] axes) {
		if (axes != null && numDims == axes.length) return axes;
		final AxisType[] validAxes = new AxisType[numDims];
		for (int i = 0; i < validAxes.length; i++) {
			if (axes != null && axes.length > i) validAxes[i] = axes[i];
			else {
				switch (i) {
					case 0:
						validAxes[i] = Axes.X;
						break;
					case 1:
						validAxes[i] = Axes.Y;
						break;
					default:
						validAxes[i] = Axes.unknown();
				}
			}
		}
		return validAxes;
	}

	/** Ensures the given calibration values are valid. */
	private static double[] validateCalibration(final int numDims,
		final double[] cal)
	{
		if (cal != null && numDims == cal.length) return cal;
		final double[] validCal = new double[numDims];
		for (int i = 0; i < validCal.length; i++) {
			if (cal != null && cal.length > i) validCal[i] = cal[i];
			else validCal[i] = 1;
		}
		return validCal;
	}

	private static AxisType[] getAxes(final Img<?> img, final Metadata metadata) {
		final AxisType[] axes = new AxisType[img.numDimensions()];
		for (int i = 0; i < axes.length; i++) {
			axes[i] = metadata.axis(i);
		}
		return axes;
	}

	private static double[] getCalibration(final Img<?> img,
		final Metadata metadata)
	{
		final double[] cal = new double[img.numDimensions()];
		for (int i = 0; i < cal.length; i++) {
			cal[i] = metadata.calibration(i);
		}
		return cal;
	}

}
