/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.blocks;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

import net.imglib2.Dimensions;
import net.imglib2.RandomAccessible;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.nio.BufferAccess;
import net.imglib2.img.basictypeaccess.nio.BufferDataAccessFactory;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Fraction;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

import org.junit.Test;

/**
 * Copying with {@link PrimitiveBlocks} out of a {@code BufferAccess}-backed
 * image must give the same result as copying out of the equivalent primitive
 * array-backed image.
 * <p>
 * This exercises both halves of the {@link MemCopy} source matrix. In
 * particular, out-of-bounds values are always extracted into a primitive array,
 * never into a {@code Buffer} (see {@link
 * PrimitiveBlocksUtils#extractOobValue}), so filling them must not go through
 * the {@code Buffer}-sourced {@code MemCopy}. Only the value-dependent
 * extension methods ({@code extendZero}, {@code extendValue}) reach that code
 * path.
 */
public class PrimitiveBlocksBufferAccessTest
{
	private static final long[] DIMENSIONS = { 13, 11, 7 };

	/**
	 * Blocks to copy: fully inside, straddling the min corner, straddling the
	 * max corner, and covering the whole image with margin on all sides.
	 */
	private static final long[][] SRC_POS = {
			{ 2, 3, 1 },
			{ -3, -2, -4 },
			{ 9, 7, 4 },
			{ -4, -3, -2 } };

	private static final int[][] SIZE = {
			{ 5, 4, 3 },
			{ 8, 6, 7 },
			{ 8, 7, 6 },
			{ 21, 17, 11 } };

	@Test
	public void testUnsignedByteType()
	{
		testAllExtensions( new UnsignedByteType() );
	}

	@Test
	public void testUnsignedShortType()
	{
		testAllExtensions( new UnsignedShortType() );
	}

	@Test
	public void testIntType()
	{
		testAllExtensions( new IntType() );
	}

	@Test
	public void testLongType()
	{
		testAllExtensions( new LongType() );
	}

	@Test
	public void testFloatType()
	{
		testAllExtensions( new FloatType() );
	}

	@Test
	public void testDoubleType()
	{
		testAllExtensions( new DoubleType() );
	}

	private < T extends NativeType< T > & RealType< T > > void testAllExtensions( final T type )
	{
		final ArrayImg< T, ? > withArray = new ArrayImgFactory<>( type ).create( DIMENSIONS );
		final ArrayImg< T, ? > withBuffer = createBufferBacked( DIMENSIONS, type, type.getNativeTypeFactory() );

		final Random random = new Random( 1L );
		withArray.forEach( t -> t.setReal( random.nextInt( 100 ) + 1 ) );
		LoopBuilder.setImages( withBuffer, withArray ).forEachPixel( ( o, i ) -> o.setReal( i.getRealDouble() ) );

		compare( type, "extendZero", withArray, withBuffer, Views::extendZero );
		compare( type, "extendValue(7)", withArray, withBuffer, img -> Views.extendValue( img, 7.0 ) );
		compare( type, "extendBorder", withArray, withBuffer, Views::extendBorder );
		compare( type, "extendMirrorSingle", withArray, withBuffer, Views::extendMirrorSingle );
		compare( type, "extendMirrorDouble", withArray, withBuffer, Views::extendMirrorDouble );
	}

	private < T extends NativeType< T > & RealType< T > > void compare(
			final T type,
			final String extension,
			final ArrayImg< T, ? > withArray,
			final ArrayImg< T, ? > withBuffer,
			final Function< ArrayImg< T, ? >, RandomAccessible< T > > extend )
	{
		final PrimitiveBlocks< T > fromArray = PrimitiveBlocks.of( extend.apply( withArray ) );
		final PrimitiveBlocks< T > fromBuffer = PrimitiveBlocks.of( extend.apply( withBuffer ) );

		final PrimitiveTypeProperties< ?, ? > props =
				PrimitiveTypeProperties.get( type.getNativeTypeFactory().getPrimitiveType() );

		for ( int i = 0; i < SRC_POS.length; ++i )
		{
			final long[] srcPos = SRC_POS[ i ];
			final int[] size = SIZE[ i ];
			final int length = ( int ) Intervals.numElements( size );

			final Object expected = props.allocate( length );
			final Object actual = props.allocate( length );
			fromArray.copy( srcPos, expected, size );
			fromBuffer.copy( srcPos, actual, size );

			if ( !equals( expected, actual ) )
				fail( type.getClass().getSimpleName() + " / " + extension + " / block " + i
						+ ": expected " + toString( expected ) + " but was " + toString( actual ) );
		}
	}

	/**
	 * Equality of two primitive arrays of the same (statically unknown) type.
	 */
	private static boolean equals( final Object a, final Object b )
	{
		return Arrays.deepEquals( new Object[] { a }, new Object[] { b } );
	}

	private static String toString( final Object array )
	{
		final String s = Arrays.deepToString( new Object[] { array } );
		return s.substring( 1, s.length() - 1 );
	}

	private static < T extends NativeType< T >, A extends BufferAccess< A > > ArrayImg< T, A > createBufferBacked(
			final long[] dimensions,
			final T type,
			final NativeTypeFactory< T, ? super A > typeFactory )
	{
		Dimensions.verify( dimensions );
		final Fraction entitiesPerPixel = type.getEntitiesPerPixel();
		final int numEntities = ArrayImgFactory.numEntitiesRangeCheck( dimensions, entitiesPerPixel );
		final A access = BufferDataAccessFactory.get( typeFactory );
		final A data = access.createArray( numEntities );
		final ArrayImg< T, A > img = new ArrayImg<>( data, dimensions, entitiesPerPixel );
		img.setLinkedType( typeFactory.createLinkedType( img ) );
		return img;
	}
}
