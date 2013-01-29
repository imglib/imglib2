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
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.script.algorithm.fn;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RealPositionable;

/**
 * TODO
 *
 */
public class IterableIntervalProxy<T> implements IterableInterval<T>
{
	protected final IterableInterval<T> iti;
	
	public IterableIntervalProxy(final IterableInterval<T> iti) {
		this.iti = iti;
	}
	
	/** The {@link IterableInterval} underlying this proxy. */
	public IterableInterval<T> get() {
		return iti;
	}
	
	@Override
	public long size() {
		return iti.size();
	}

	@Override
	public T firstElement() {
		return iti.firstElement();
	}

	@Override
	public Object iterationOrder()
	{
		return iti.iterationOrder();
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		return iterationOrder().equals( f.iterationOrder() );
	}

	@Override
	public double realMin(int d) {
		return iti.realMin(d);
	}

	@Override
	public void realMin(double[] min) {
		iti.realMin(min);
	}

	@Override
	public void realMin(RealPositionable min) {
		iti.realMin(min);
	}

	@Override
	public double realMax(int d) {
		return iti.realMax(d);
	}

	@Override
	public void realMax(double[] max) {
		iti.realMax(max);
	}

	@Override
	public void realMax(RealPositionable max) {
		iti.realMax(max);
	}

	@Override
	public int numDimensions() {
		return iti.numDimensions();
	}

	@Override
	public Iterator<T> iterator() {
		return iti.iterator();
	}

	@Override
	public long min(int d) {
		return iti.min(d);
	}

	@Override
	public void min(long[] min) {
		iti.min(min);
	}

	@Override
	public void min(Positionable min) {
		iti.min(min);
	}

	@Override
	public long max(int d) {
		return iti.max(d);
	}

	@Override
	public void max(long[] max) {
		iti.max(max);
	}

	@Override
	public void max(Positionable max) {
		iti.max(max);
	}

	@Override
	public void dimensions(long[] dimensions) {
		iti.dimensions(dimensions);
	}

	@Override
	public long dimension(int d) {
		return iti.dimension(d);
	}

	@Override
	public Cursor<T> cursor() {
		return iti.cursor();
	}

	@Override
	public Cursor<T> localizingCursor() {
		return iti.localizingCursor();
	}
}
