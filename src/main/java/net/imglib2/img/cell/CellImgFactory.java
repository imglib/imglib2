/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.Dimensions;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.ArrayDataAccessFactory;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.list.ListImg;
import net.imglib2.img.list.ListLocalizingCursor;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.util.Fraction;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;

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

	public CellImgFactory( final T type )
	{
		this( type, 10 );
	}

	public CellImgFactory( final T type, final int... cellDimensions )
	{
		super( type );
		defaultCellDimensions = Dimensions.verify( cellDimensions ).clone();
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
	public static int[] getCellDimensions( final int[] defaultCellDimensions, final int n, final Fraction entitiesPerPixel )
			throws IllegalArgumentException
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
	public CellImg< T, ? > create( final long... dimensions )
	{
		@SuppressWarnings( { "unchecked", "rawtypes" } )
		final CellImg< T, ? > img = create( dimensions, type(), ( NativeTypeFactory ) type().getNativeTypeFactory() );
		return img;
	}

	@Override
	public CellImg< T, ? > create( final Dimensions dimensions )
	{
		return create( Intervals.dimensionsAsLongArray( dimensions ) );
	}

	@Override
	public CellImg< T, ? > create( final int[] dimensions )
	{
		return create( Util.int2long( dimensions ) );
	}

	private < A extends ArrayDataAccess< A > > CellImg< T, A > create(
			final long[] dimensions,
			final T type,
			final NativeTypeFactory< T, A > typeFactory )
	{
		Dimensions.verify( dimensions );

		final int n = dimensions.length;
		final Fraction entitiesPerPixel = type.getEntitiesPerPixel();
		final int[] cellDimensions = getCellDimensions( defaultCellDimensions, n, entitiesPerPixel );

		final CellGrid grid = new CellGrid( dimensions, cellDimensions );
		final long[] gridDimensions = new long[ grid.numDimensions() ];
		grid.gridDimensions( gridDimensions );

		final Cell< A > cellType = new Cell<>( new int[] { 1 }, new long[] { 1 }, null );
		final ListImg< Cell< A > > cells = new ListImg<>( gridDimensions, cellType );

		final long[] cellGridPosition = new long[ n ];
		final long[] cellMin = new long[ n ];
		final int[] cellDims = new int[ n ];
		final ListLocalizingCursor< Cell< A > > cellCursor = cells.localizingCursor();
		while ( cellCursor.hasNext() )
		{
			cellCursor.fwd();
			cellCursor.localize( cellGridPosition );
			grid.getCellDimensions( cellGridPosition, cellMin, cellDims );
			final A data = ArrayDataAccessFactory.get( typeFactory )
					.createArray( ( int ) entitiesPerPixel.mulCeil( Intervals.numElements( cellDims ) ) );
			cellCursor.set( new Cell<>( cellDims, cellMin, data ) );
		}

		final CellImg< T, A > img = new CellImg<>( this, grid, cells, entitiesPerPixel );
		img.setLinkedType( typeFactory.createLinkedType( img ) );
		return img;
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@Override
	public < S > ImgFactory< S > imgFactory( final S type ) throws IncompatibleTypeException
	{
		if ( NativeType.class.isInstance( type ) )
			return new CellImgFactory( ( NativeType ) type, defaultCellDimensions );
		throw new IncompatibleTypeException( this, type.getClass().getCanonicalName() + " does not implement NativeType." );
	}

	/*
	 * -----------------------------------------------------------------------
	 *
	 * Deprecated API.
	 *
	 * Supports backwards compatibility with ImgFactories that are constructed
	 * without a type instance or supplier.
	 *
	 * -----------------------------------------------------------------------
	 */

	@Deprecated
	public CellImgFactory()
	{
		this( 10 );
	}

	@Deprecated
	public CellImgFactory( final int... cellDimensions )
	{
		defaultCellDimensions = Dimensions.verify( cellDimensions ).clone();
	}

	@Deprecated
	@Override
	public CellImg< T, ? > create( final long[] dimensions, final T type )
	{
		cache( type );
		@SuppressWarnings( { "unchecked", "rawtypes" } )
		final CellImg< T, ? > img = create( dimensions, type, ( NativeTypeFactory ) type.getNativeTypeFactory() );
		return img;
	}

	/*
	 * -----------------------------------------------------------------------
	 *
	 * Deprecated API.
	 *
	 * -----------------------------------------------------------------------
	 */

	/**
	 * @deprecated This method has been deprecated in favor of
	 * {@link Dimensions#verify(int...)}.
	 */
	@Deprecated
	public static void verifyDimensions( final int[] dimensions ) throws IllegalArgumentException
	{
		Dimensions.verify( dimensions );
	}

	/**
	 * @deprecated This method has been deprecated in favor of
	 * {@link Dimensions#verify(long...)}.
	 */
	@Deprecated
	public static void verifyDimensions( final long dimensions[] ) throws IllegalArgumentException
	{
		Dimensions.verify( dimensions );
	}
}
