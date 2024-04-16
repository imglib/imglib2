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

import net.imglib2.FinalRealInterval;
import net.imglib2.RealInterval;


/**
 * Stores the positions of the nodes in a KDTree and provides access to them.
 * <p>
 * Currently, there are two implementations:
 * <ul>
 * <li>{@link Nested} stores the positions as a {@code double[][]} where {@code
 * positions[d][i]} is dimension {@code d} of the {@code i}-th point. This
 * allows for a total of {@code 2^31-8} nodes but doesn't keep the positions
 * contiguous in memory.</li>
 * <li>{@link Flat} stores the positions as a {@code double[]} where {@code
 * positions[d + i*n]} is dimension {@code d} of the {@code i}-th point, with
 * {@code n} the number of dimensions. This means that the positions are
 * contiguous in memory but the number of nodes is limited to {@code
 * (2^31-8)/n}.</li>
 * </ul>
 * {@link #asNestedArray()} returns positions in nested {@code double[][]}
 * (which is created if class is {@link Flat}). {@link #asFlatArray()}
 * returns flat {@code double[]} if class is {@link Flat}, otherwise {@code null}.
 */
public abstract class KDTreePositions
{
	/**
	 * With {@code NESTED} layout, positions are stored as a nested {@code
	 * double[][]} array where {@code positions[d][i]} is dimension {@code d} of
	 * the {@code i}-th point. With {@code FLAT} layout, positions are stored as
	 * a flat {@code double[]} array, where {@code positions[d + i*n]} is
	 * dimension {@code d} of the {@code i}-th point, with {@code n} the number
	 * of dimensions.
	 */
	public enum PositionsLayout
	{
		FLAT,
		NESTED
	}

	final int numDimensions;

	final int numPoints;

	private volatile RealInterval boundingBox;

	private static class Nested extends KDTreePositions
	{
		private final double[][] positions;

		Nested( final double[][] positions )
		{
			super( positions.length, positions[ 0 ].length );
			this.positions = positions;
		}

		@Override
		public double get( final int i, final int d )
		{
			return positions[ d ][ i ];
		}

		@Override
		public double[] asFlatArray()
		{
			// positions in this case might be too large to fit in a single array
			return null;
		}

		@Override
		public double[][] asNestedArray()
		{
			return positions;
		}

		@Override
		protected RealInterval createBoundingBox()
		{
			final double[] min = new double[ numDimensions ];
			final double[] max = new double[ numDimensions ];
			KDTreeUtils.computeMinMax( positions, min, max );
			return FinalRealInterval.wrap( min, max );
		}

		@Override
		public PositionsLayout layout()
		{
			return PositionsLayout.NESTED;
		}
	}

	private static class Flat extends KDTreePositions
	{
		private final double[] positions;

		Flat( final double[] positions, final int numDimensions )
		{
			super( numDimensions, positions.length / numDimensions );
			this.positions = positions;
		}

		@Override
		public double get( final int i, final int d )
		{
			return positions[ numDimensions * i + d ];
		}

		@Override
		public double[] asFlatArray()
		{
			return positions;
		}

		@Override
		public double[][] asNestedArray()
		{
			return KDTreeUtils.unflatten( positions, numDimensions );
		}

		@Override
		protected RealInterval createBoundingBox()
		{
			final double[] min = new double[ numDimensions ];
			final double[] max = new double[ numDimensions ];
			KDTreeUtils.computeMinMax( positions, min, max );
			return FinalRealInterval.wrap( min, max );
		}

		@Override
		public PositionsLayout layout()
		{
			return PositionsLayout.FLAT;
		}
	}

	KDTreePositions( final int numDimensions, final int numPoints )
	{
		this.numDimensions = numDimensions;
		this.numPoints = numPoints;
	}

	/**
	 * Get the coordinates of the node {@code i} in dimension {@code d}.
	 *
	 * @return the coordinate
	 */
	public abstract double get( final int i, final int d );

	/**
	 * Get positions of points in the tree as a flat {@code double[]} array
	 * where {@code positions[d + i*n]} is dimension {@code d} of the {@code i}-th
	 * point.
	 * <p>
	 * For serialisation and usage by the tree.
	 * <p>
	 * Internal storage may be a {@code NESTED} {@code double[][]} array. In
	 * this case, {@code flatPositions()} returns {@code null}.
	 */
	public abstract double[] asFlatArray();

	/**
	 * Get positions of points in the tree as a nested {@code double[][]} array
	 * where {@code positions[d][i]} is dimension {@code d} of the {@code i}-th
	 * point.
	 * <p>
	 * For serialisation and usage by the tree.
	 * <p>
	 * Internal storage may be flattened into single {@code double[]} array. In
	 * this case, the nested {@code double[][]} array is created here.
	 */
	public abstract double[][] asNestedArray();

	abstract RealInterval createBoundingBox();

	/**
	 * Get the internal layout of positions.
	 * <p>
	 * Positions are stored in either {@code FLAT} or {@code NESTED} {@link
	 * PositionsLayout layout}. With {@code NESTED} layout, positions are stored
	 * as a nested {@code double[][]} array where {@code positions[d][i]} is
	 * dimension {@code d} of the {@code i}-th point. With {@code FLAT} layout,
	 * positions are stored as a flat {@code double[]} array, where {@code
	 * positions[d + i*n]} is dimension {@code d} of the {@code i}-th point,
	 * with {@code n} the number of dimensions.
	 */
	public abstract PositionsLayout layout();

	/**
	 * @return dimensionality of points in the tree
	 */
	public int numDimensions()
	{
		return numDimensions;
	}

	/**
	 * @return number of points in the tree
	 */
	public int numPoints()
	{
		return numPoints;
	}

	/**
	 * Create {@code KDTreePositions} with NESTED {@link #layout}.
	 */
	public static KDTreePositions createNested( final double[][] positions )
	{
		return new Nested( positions );
	}

	/**
	 * Create {@code KDTreePositions} with FLAT  {@link #layout}.
	 */
	public static KDTreePositions createFlat( final double[] positions, final int numDimensions )
	{
		return new Flat( positions, numDimensions );
	}
}
