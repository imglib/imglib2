/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.CharArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.NativeType;
import net.imglib2.util.Fraction;

/**
 * Factory for creating {@link CellImg CellImgs}. The cell dimensions for a
 * standard cell can be supplied in the constructor of the factory. If no cell
 * dimensions are given, the factory creates cells of size
 * <em>10 x 10 x ... x 10</em>.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public final class CellImgFactory< T extends NativeType< T > > extends AbstractCellImgFactory< T >
{
	public CellImgFactory()
	{}

	public CellImgFactory( final int cellSize )
	{
		super( cellSize );
	}

	public CellImgFactory( final int[] cellDimensions )
	{
		super( cellDimensions );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public CellImg< T, ?, ? > create( final long[] dim, final T type )
	{
		return ( CellImg< T, ?, ? > ) type.createSuitableNativeImg( this, dim );
	}

	@Override
	public CellImg< T, ByteArray, DefaultCell< ByteArray > > createByteInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		return createInstance( new ByteArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public CellImg< T, CharArray, DefaultCell< CharArray > > createCharInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		return createInstance( new CharArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public CellImg< T, ShortArray, DefaultCell< ShortArray > > createShortInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		return createInstance( new ShortArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public CellImg< T, IntArray, DefaultCell< IntArray > > createIntInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		return createInstance( new IntArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public CellImg< T, LongArray, DefaultCell< LongArray > > createLongInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		return createInstance( new LongArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public CellImg< T, FloatArray, DefaultCell< FloatArray > > createFloatInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		return createInstance( new FloatArray( 1 ), dimensions, entitiesPerPixel );
	}

	@Override
	public CellImg< T, DoubleArray, DefaultCell< DoubleArray > > createDoubleInstance( final long[] dimensions, final Fraction entitiesPerPixel )
	{
		return createInstance( new DoubleArray( 1 ), dimensions, entitiesPerPixel );
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@Override
	public < S > ImgFactory< S > imgFactory( final S type ) throws IncompatibleTypeException
	{
		if ( NativeType.class.isInstance( type ) )
			return new CellImgFactory( defaultCellDimensions );
		throw new IncompatibleTypeException( this, type.getClass().getCanonicalName() + " does not implement NativeType." );
	}

	private < A extends ArrayDataAccess< A > > CellImg< T, A, DefaultCell< A > > createInstance( final A array, long[] dimensions, final Fraction entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		final int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellImg< T, A, DefaultCell< A > >( this, new ListImgCells< A >( array, entitiesPerPixel, dimensions, cellSize ) );
	}
}
