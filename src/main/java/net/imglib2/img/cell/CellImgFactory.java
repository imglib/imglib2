/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.img.cell;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.ArrayDataAccessFactory;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.list.ListImg;
import net.imglib2.img.list.ListLocalizingCursor;
import net.imglib2.type.NativeType;
import net.imglib2.type.PrimitiveTypeInfo;
import net.imglib2.util.Fraction;
import net.imglib2.util.Intervals;

/**
 * Factory for creating {@link AbstractCellImg CellImgs}. The cell dimensions
 * for a standard cell can be supplied in the constructor of the factory. If no
 * cell dimensions are given, the factory creates cells of size <em>10 x 10 x
 * ... x 10</em>.
 *
 * @author Tobias Pietzsch
 */
public class CellImgFactory< T extends NativeType< T > > extends NativeImgFactory< T >
{
	private final int[] defaultCellDimensions;

	public CellImgFactory()
	{
		this( 10 );
	}

	public CellImgFactory( final int... cellDimensions )
	{
		defaultCellDimensions = cellDimensions.clone();
		verifyDimensions( defaultCellDimensions );
	}

	/**
	 * Verify that {@code dimensions} is not null or empty, and that no
	 * dimension is less than 1. Throw {@link IllegalArgumentException}
	 * otherwise.
	 *
	 * @param dimensions
	 * @throws IllegalArgumentException
	 */
	public static void verifyDimensions( final int[] dimensions ) throws IllegalArgumentException
	{
		if ( dimensions == null )
			throw new IllegalArgumentException( "dimensions == null" );

		if ( dimensions.length == 0 )
			throw new IllegalArgumentException( "dimensions.length == 0" );

		for ( int d = 0; d < dimensions.length; d++ )
			if ( dimensions[ d ] <= 0 )
				throw new IllegalArgumentException( "dimensions[ " + d + " ] <= 0" );
	}

	/**
	 * Verify that {@code dimensions} is not null or empty, and that no
	 * dimension is less than 1. Throw {@link IllegalArgumentException}
	 * otherwise.
	 *
	 * @param dimensions
	 * @throws IllegalArgumentException
	 */
	public static void verifyDimensions( final long dimensions[] ) throws IllegalArgumentException
	{
		if ( dimensions == null )
			throw new IllegalArgumentException( "dimensions == null" );

		if ( dimensions.length == 0 )
			throw new IllegalArgumentException( "dimensions.length == 0" );

		for ( int d = 0; d < dimensions.length; d++ )
			if ( dimensions[ d ] <= 0 )
				throw new IllegalArgumentException( "dimensions[ " + d + " ] <= 0" );
	}

	/**
	 * Computes cell size array by truncating or expanding
	 * {@code defaultCellDimensions} to length {@code n}. Then verifies that a
	 * cell does not contain more than {@code Integer.MAX_VALUE} entities.
	 *
	 * @param defaultCellDimensions
	 * @param n
	 * @param entitiesPerPixel
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static int[] getCellDimensions( final int[] defaultCellDimensions, final int n, final Fraction entitiesPerPixel ) throws IllegalArgumentException
	{
		final int[] cellDimensions = new int[ n ];
		final int max = defaultCellDimensions.length - 1;
		for ( int i = 0; i < n; i++ )
			cellDimensions[ i ] = defaultCellDimensions[ ( i < max ) ? i : max ];

		final long numEntities = entitiesPerPixel.mulCeil( Intervals.numElements( cellDimensions ) );
		if ( numEntities > Integer.MAX_VALUE )
			throw new IllegalArgumentException( "Number of entities in cell too large. Use smaller cell size." );

		return cellDimensions;
	}

	@Override
	public CellImg< T, ? > create( final long[] dimensions, final T type )
	{
		return create( type.getPrimitiveTypeInfo(), dimensions, type.getEntitiesPerPixel() );
	}

	private < A > CellImg< T, ? > create(
			final PrimitiveTypeInfo< T, A > info,
			final long[] dimensions,
			final Fraction entitiesPerPixel )
	{
		return createInstance( ArrayDataAccessFactory.get( info ).createArray( 0 ), info, dimensions, entitiesPerPixel );
		// calling createArray( 0 ) is necessary here, because otherwise javac
		// will not infer the ArrayDataAccess type
	}

	private < A extends ArrayDataAccess< A > > CellImg< T, A > createInstance(
			final A creator,
			final PrimitiveTypeInfo< T, ? super A > info,
			final long[] dimensions,
			final Fraction entitiesPerPixel )
	{
		verifyDimensions( dimensions );

		final int n = dimensions.length;
		final int[] cellDimensions = getCellDimensions( defaultCellDimensions, n, entitiesPerPixel );

		final CellGrid grid = new CellGrid( dimensions, cellDimensions );
		final long[] gridDimensions = new long[ grid.numDimensions() ];
		grid.gridDimensions( gridDimensions );

		final Cell< A > type = new Cell<>( new int[] { 1 }, new long[] { 1 }, null );
		final ListImg< Cell< A > > cells = new ListImg<>( gridDimensions, type );

		final long[] cellGridPosition = new long[ n ];
		final long[] cellMin = new long[ n ];
		final int[] cellDims = new int[ n ];
		final ListLocalizingCursor< Cell< A > > cellCursor = cells.localizingCursor();
		while ( cellCursor.hasNext() )
		{
			cellCursor.fwd();
			cellCursor.localize( cellGridPosition );
			grid.getCellDimensions( cellGridPosition, cellMin, cellDims );
			final A data = creator.createArray( ( int ) entitiesPerPixel.mulCeil( Intervals.numElements( cellDims ) ) );
			cellCursor.set( new Cell<>( cellDims, cellMin, data ) );
		}

		final CellImg< T, A > img = new CellImg<>( this, grid, cells, entitiesPerPixel );
		img.setLinkedType( info.createLinkedType( img ) );
		return img;
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@Override
	public < S > ImgFactory< S > imgFactory( final S type ) throws IncompatibleTypeException
	{
		if ( NativeType.class.isInstance( type ) )
			return new CellImgFactory( defaultCellDimensions );
		throw new IncompatibleTypeException( this, type.getClass().getCanonicalName() + " does not implement NativeType." );
	}
}
