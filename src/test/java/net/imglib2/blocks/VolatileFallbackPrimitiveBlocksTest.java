/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2026 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import static net.imglib2.blocks.VolatilePrimitiveBlocks.AllValid;
import static net.imglib2.blocks.PrimitiveBlocks.OnFallback.ACCEPT;
import static net.imglib2.blocks.PrimitiveBlocks.OnFallback.FAIL;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileShortArray;
import net.imglib2.img.cell.CellImg;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.type.volatiles.VolatileUnsignedShortType;
import net.imglib2.util.Cast;
import net.imglib2.util.Fraction;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

import org.junit.Test;

/**
 * Runs all of {@link VolatileRangeValidityTest} against {@link
 * VolatileFallbackPrimitiveBlocks}, which must produce the same data, the same
 * (expanded) validity, and the same {@code allValid} answers as the optimized
 * copier on the same View. That is valid for this fixture because there is no
 * converter in the View sequence: with one, the fall-back writes zero after
 * conversion rather than before.
 * <p>
 * In addition, checks that {@link VolatilePrimitiveBlocks#of} routes to the
 * fall-back exactly when it should.
 */
public class VolatileFallbackPrimitiveBlocksTest extends VolatileRangeValidityTest
{
	@Override
	protected VolatilePrimitiveBlocks< VolatileUnsignedShortType > createBlocks( final RandomAccessible< VolatileUnsignedShortType > extended )
	{
		return new VolatileFallbackPrimitiveBlocks<>( new FallbackProperties<>( extended.getType(), extended ) );
	}

	@Test
	public void cellImgRootIsFullySupported()
	{
		final CellImg< VolatileUnsignedShortType, VolatileShortArray > img = createVolatileCellImg( i -> true );
		assertTrue( VolatilePrimitiveBlocks.of( Views.extendZero( img ), ACCEPT ) instanceof VolatileViewPrimitiveBlocks );
	}

	@Test
	public void unsupportedViewUsesFallback()
	{
		final CellImg< VolatileUnsignedShortType, VolatileShortArray > img = createVolatileCellImg( i -> true );
		// two out-of-bounds extensions are not understood by ViewAnalyzer
		final RandomAccessible< VolatileUnsignedShortType > view = Views.extendZero( Views.interval( Views.extendZero( img ), img ) );
		assertTrue( VolatilePrimitiveBlocks.of( view, ACCEPT ) instanceof VolatileFallbackPrimitiveBlocks );
		assertThrows( IllegalArgumentException.class, () -> VolatilePrimitiveBlocks.of( view, FAIL ) );
	}

	@Test
	public void arrayImgRootUsesFallback()
	{
		checkArrayImg( true );
		checkArrayImg( false );
	}

	/**
	 * A volatile {@code ArrayImg} is not supported by the optimized copier,
	 * which used to fail with a message-less exception from deep inside {@code
	 * VolatileRangeCopier.create}. Now {@code FAIL} explains why, and {@code
	 * ACCEPT} copies through the fall-back, with the validity of the single
	 * access applying to every pixel.
	 */
	private void checkArrayImg( final boolean isValid )
	{
		final long[] dims = { 5, 4, 3 };
		final int length = ( int ) Intervals.numElements( dims );
		final short[] data = new short[ length ];
		for ( int i = 0; i < length; ++i )
			data[ i ] = ( short ) ( i + 1 );
		final ArrayImg< VolatileUnsignedShortType, VolatileShortArray > img =
				new ArrayImg<>( new VolatileShortArray( data, isValid ), dims, new Fraction() );
		final NativeTypeFactory< VolatileUnsignedShortType, ? super VolatileShortArray > typeFactory =
				Cast.unchecked( new VolatileUnsignedShortType().getNativeTypeFactory() );
		img.setLinkedType( typeFactory.createLinkedType( img ) );
		final RandomAccessible< VolatileUnsignedShortType > view = Views.extendZero( img );

		final IllegalArgumentException e = assertThrows( IllegalArgumentException.class, () -> VolatilePrimitiveBlocks.of( view, FAIL ) );
		assertTrue( e.getMessage(), e.getMessage().contains( "AbstractCellImg" ) );

		final VolatilePrimitiveBlocks< VolatileUnsignedShortType > blocks = VolatilePrimitiveBlocks.of( view, ACCEPT );
		assertTrue( blocks instanceof VolatileFallbackPrimitiveBlocks );

		// the whole image plus one pixel of zero-extension on every side
		final long[] srcPos = { -1, -1, -1 };
		final int[] size = { 7, 6, 5 };
		final int blockLength = ( int ) Intervals.numElements( size );
		final short[] destData = new short[ blockLength ];
		final byte[] destValid = new byte[ blockLength ];
		Arrays.fill( destData, ( short ) 0x5A5A );
		Arrays.fill( destValid, ( byte ) 2 );
		final VolatileArray< short[] > dest = new VolatileArray<>( destData, destValid );
		blocks.copy( srcPos, dest, size );

		final short[] expectedData = new short[ blockLength ];
		final byte[] expectedValid = new byte[ blockLength ];
		final int[] pos = new int[ 3 ];
		for ( int i = 0; i < blockLength; ++i )
		{
			int rem = i;
			boolean oob = false;
			int srcIndex = 0;
			int srcStep = 1;
			for ( int d = 0; d < 3; ++d )
			{
				pos[ d ] = rem % size[ d ];
				rem /= size[ d ];
				final long x = srcPos[ d ] + pos[ d ];
				oob |= x < 0 || x >= dims[ d ];
				srcIndex += ( int ) x * srcStep;
				srcStep *= dims[ d ];
			}
			expectedValid[ i ] = ( byte ) ( oob || isValid ? 1 : 0 );
			expectedData[ i ] = oob || !isValid ? 0 : data[ srcIndex ];
		}
		final String msg = "isValid=" + isValid;
		assertArrayEquals( msg, expectedValid, dest.valid() );
		assertArrayEquals( msg + " (data)", expectedData, dest.data() );
		assertEquals( msg + " (Validity.allValid)", isValid, dest.getValidity().allValid() );

		final Interval interval = BlockInterval.wrap( srcPos, size );
		assertEquals( msg + " (allValid, failFast)", AllValid.of( isValid ), blocks.allValid( interval, true ) );
		assertEquals( msg + " (allValid, visit all pixels)", AllValid.of( isValid ), blocks.allValid( interval, false ) );
	}
}
