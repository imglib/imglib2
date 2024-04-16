/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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
package net.imglib2.kdtree;

import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.img.Img;
import net.imglib2.img.list.ListImg;
import net.imglib2.type.NativeType;
import net.imglib2.util.Util;

import net.imglib2.kdtree.KDTreePositions.PositionsLayout;

/**
 * Stores the KDTree data, that is, positions and values.
 * <p>
 * Positions are stored in either {@code FLAT} or {@code NESTED} {@link
 * PositionsLayout layout}. With {@code NESTED} layout, positions are stored as
 * a nested {@code double[][]} array where {@code positions[d][i]} is dimension
 * {@code d} of the {@code i}-th point. With {@code FLAT} layout, positions are
 * stored as a flat {@code double[]} array, where {@code positions[d + i*n]} is
 * dimension {@code d} of the {@code i}-th point, with {@code n} the number of
 * dimensions.
 * <p>
 * Values (of type {@code T}) are stored as either a 1D {@code
 * RandomAccessibleInterval<T>}, or a {@code List<T>}. Individual values can be
 * accessed by {@link #valuesSupplier()}{@code .get().apply(i)}. {@code
 * valueSupplier().get()} returns a reusable {@code IntFunction<T>}. Here {@code
 * T} maybe a proxy that is reused in subsequent {@code apply(i)}.
 * <p>
 * {@link #values()} returns all values as a 1D {@code
 * RandomAccessibleInterval<T>}. (If data is stored as {@code List<T>}, it is
 * wrapped into a {@code ListImg}.)
 *
 * @param <T>
 * 		the type of values stored in the tree.
 */
public class KDTreeData< T >
{
	private final KDTreePositions positions;

	private final RandomAccessibleInterval< T > valuesImg;
	private final Supplier< IntFunction< T > > valuesSupplier;

	private final T type;

	private volatile RealInterval boundingBox;

	public KDTreeData( final KDTreePositions positions, final List< T > values )
	{
		this.positions = positions;

		valuesImg = ListImg.wrap( values, positions().numPoints() );
		valuesSupplier = () -> values::get;

		type = KDTreeUtils.getType( values );
	}

	public KDTreeData( final KDTreePositions positions, final List< T > values, final RealInterval boundingBox )
	{
		this( positions, values );
		this.boundingBox = boundingBox;
	}

	public KDTreeData( final KDTreePositions positions, final RandomAccessibleInterval< T > values )
	{
		this.positions = positions;

		valuesImg = values;
		valuesSupplier = () -> valuesImg.randomAccess()::setPositionAndGet;

		type = Util.getTypeFromInterval( values );
	}

	public KDTreeData( final KDTreePositions positions, final RandomAccessibleInterval< T > values, final RealInterval boundingBox )
	{
		this( positions, values );
		this.boundingBox = boundingBox;
	}

	public KDTreePositions positions()
	{
		return positions;
	}

	public T getType()
	{
		return type;
	}

	/**
	 * Get the values as a 1D {@code RandomAccessibleInterval}, for
	 * serialization. (If the underlying storage is a {@code List<T>}, it will
	 * be wrapped as a {@code ListImg}.)
	 */
	public RandomAccessibleInterval< T > values()
	{
		return valuesImg;
	}

	/**
	 * Get a {@code Supplier} that return {@code IntFunction<T>} to provide
	 * values for a given node indices. If the returned {@code IntFunction<T>}
	 * is stateful ({@code T} maybe a proxy that is reused in subsequent {@code
	 * apply(i)}} every {@link Supplier#get()} creates a new instance of the
	 * {@code IntFunction<T>}.
	 */
	public Supplier< IntFunction< T > > valuesSupplier()
	{
		return valuesSupplier;
	}

	public RealInterval boundingBox()
	{
		if ( boundingBox == null )
			boundingBox = positions().createBoundingBox();
		return boundingBox;
	}

	/**
	 * Create {@link KDTreeData} from the given {@code values} and {@code positions}).
	 * (copies {@code positions} and sorts into a KDTree structure).
	 *
	 * @param numPoints
	 * 		number of points (number of elements in {@code values} and {@code positions}).
	 * @param values
	 * 		values associated with points
	 * @param positions
	 * 		points positions
	 * @param storeValuesAsNativeImg
	 * 		If {@code true} and {@code T} is a {@code NativeType},
	 * 		store values into {@code NativeImg}.
	 * 		Otherwise, store values as a {@code List<T>}.
	 */
	public static < L extends RealLocalizable, T > KDTreeData< T > create(
			final int numPoints,
			final Iterable< T > values,
			final Iterable< L > positions,
			final boolean storeValuesAsNativeImg )
	{
		if ( numPoints <= 0 )
			throw new IllegalArgumentException( "At least one point is required to construct a KDTree." );
		final int numDimensions = KDTreeUtils.getNumDimensions( positions );
		final double[][] points = KDTreeUtils.initPositions( numDimensions, numPoints, positions );
		final int[] tree = KDTreeUtils.makeTree( points );
		final int[] invtree = KDTreeUtils.invert( tree );

		final boolean useFlatLayout = (long) numDimensions * numPoints <= KDTreeUtils.MAX_ARRAY_SIZE;
		final KDTreePositions treePositions = ( useFlatLayout )
				? KDTreePositions.createFlat( KDTreeUtils.reorderToFlatLayout( points, tree ), numDimensions )
				: KDTreePositions.createNested( KDTreeUtils.reorder( points, tree ) );

		final boolean storeAsImg = ( storeValuesAsNativeImg && KDTreeUtils.getType( values ) instanceof NativeType );
		if ( storeAsImg )
		{
			@SuppressWarnings( "unchecked" )
			final Img< T > treeValues = ( Img< T > ) KDTreeUtils.orderValuesImg( invtree, ( Iterable ) values );
			return new KDTreeData<>( treePositions, treeValues );
		}
		else
		{
			final List< T > treeValues = KDTreeUtils.orderValuesList( invtree, values );
			return new KDTreeData<>( treePositions, treeValues );
		}
	}
}
