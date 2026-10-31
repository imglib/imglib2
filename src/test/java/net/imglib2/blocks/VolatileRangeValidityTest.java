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

import static net.imglib2.blocks.VolatilePrimitiveBlocks.AllValid;
import static net.imglib2.blocks.PrimitiveBlocks.OnFallback.FAIL;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.function.IntPredicate;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.img.cell.Cell;
import net.imglib2.img.cell.CellGrid;
import net.imglib2.img.cell.CellImg;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileShortArray;
import net.imglib2.img.list.ListImg;
import net.imglib2.img.list.ListLocalizingCursor;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.type.volatiles.VolatileUnsignedShortType;
import net.imglib2.util.Cast;
import net.imglib2.util.Fraction;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

import org.junit.Test;

/**
 * The {@link RangeValidity} produced by {@link VolatileCellImgRangeCopier}, once
 * {@link RangeValidity#expandTo expanded}, must equal the per-pixel validity
 * computed pixel by pixel from the source cell grid.
 * <p>
 * Out-of-bounds pixels count as valid, matching the behaviour of the copier.
 * <p>
 * The copied data is checked against a per-pixel reference as well: a pixel
 * that comes from an invalid {@code Cell} must read as zero, not as whatever
 * the invalid {@code Cell} happens to contain (here, non-zero values). Every
 * block is copied through a zero and through a non-zero constant extension, so
 * that a cleared pixel and an out-of-bounds pixel are distinguishable.
 * <p>
 * {@link VolatileFallbackPrimitiveBlocksTest} runs the same checks against the
 * fall-back, by overriding {@link #createBlocks}.
 */
public class VolatileRangeValidityTest
{
	/**
	 * The {@code PrimitiveBlocks} under test. {@code FAIL}, so that this test
	 * cannot silently end up testing the fall-back.
	 */
	protected VolatilePrimitiveBlocks< VolatileUnsignedShortType > createBlocks( final RandomAccessible< VolatileUnsignedShortType > extended )
	{
		return VolatilePrimitiveBlocks.of( extended, FAIL );
	}

	private static final long[] DIMENSIONS = { 13, 11, 7 };

	private static final int[] CELL_DIMENSIONS = { 4, 5, 3 };

	/**
	 * fully inside; straddling min; straddling max; covering everything;
	 * entirely outside past max; entirely outside before min.
	 */
	private static final long[][] SRC_POS = {
			{ 2, 3, 1 },
			{ -3, -2, -4 },
			{ 9, 7, 4 },
			{ -4, -3, -2 },
			{ 20, 20, 20 },
			{ -10, -10, -10 } };

	/** Non-zero out-of-bounds value, to tell oob pixels from cleared ones. */
	private static final int OOB_VALUE = 7;

	/**
	 * Pre-filled into {@code dest} before every copy, so that a pixel the
	 * copier fails to write is not mistaken for a correctly cleared one. Not a
	 * value any cell holds.
	 */
	private static final short UNWRITTEN = 0x5A5A;

	/** Pre-filled into the validity buffer before every copy. See {@link #UNWRITTEN}. */
	private static final byte UNWRITTEN_VALID = 2;

	private static final int[][] SIZE = {
			{ 5, 4, 3 },
			{ 8, 6, 7 },
			{ 8, 7, 6 },
			{ 21, 17, 11 },
			{ 4, 4, 4 },
			{ 4, 4, 4 } };

	@Test
	public void allCellsValid()
	{
		check( i -> true );
	}

	@Test
	public void noCellValid()
	{
		check( i -> false );
	}

	@Test
	public void everyOtherCellValid()
	{
		check( i -> i % 2 == 0 );
	}

	@Test
	public void singleCellInvalid()
	{
		check( i -> i != 5 );
	}

	@Test
	public void everyThirdCellInvalid()
	{
		check( i -> i % 3 != 0 );
	}

	/**
	 * @param cellValid
	 * 		whether the cell with the given flattened grid index is valid
	 */
	private void check( final IntPredicate cellValid )
	{
		final CellImg< VolatileUnsignedShortType, VolatileShortArray > img = createVolatileCellImg( cellValid );
		check( img, Views.extendZero( img ), 0, cellValid );
		check( img, Views.extendValue( img, new VolatileUnsignedShortType( OOB_VALUE ) ), OOB_VALUE, cellValid );
	}

	/**
	 * @param extended
	 * 		{@code img}, extended with the constant {@code oobValue}
	 * @param oobValue
	 * 		the out-of-bounds value of {@code extended}
	 * @param cellValid
	 * 		whether the cell with the given flattened grid index is valid
	 */
	private void check(
			final CellImg< VolatileUnsignedShortType, VolatileShortArray > img,
			final RandomAccessible< VolatileUnsignedShortType > extended,
			final int oobValue,
			final IntPredicate cellValid )
	{
		final CellGrid grid = img.getCellGrid();
		final VolatilePrimitiveBlocks< VolatileUnsignedShortType > blocks = createBlocks( extended );

		for ( int b = 0; b < SRC_POS.length; ++b )
		{
			final long[] srcPos = SRC_POS[ b ];
			final int[] size = SIZE[ b ];
			final int length = ( int ) Intervals.numElements( size );

			final short[] destData = new short[ length ];
			final byte[] destValid = new byte[ length ];
			Arrays.fill( destData, UNWRITTEN );
			Arrays.fill( destValid, UNWRITTEN_VALID );
			final VolatileArray< short[] > dest = new VolatileArray<>( destData, destValid );
			blocks.copy( srcPos, dest, size );

			final String msg = "block " + b + " " + Arrays.toString( srcPos ) + " " + Arrays.toString( size )
					+ " (oob=" + oobValue + ")";

			final byte[] expected = referenceValid( grid, cellValid, srcPos, size );
			assertArrayEquals( msg, expected, dest.valid() );

			assertArrayEquals( msg + " (data)",
					referenceData( grid, cellValid, srcPos, size, oobValue ), dest.data() );

			// allValid( Interval ) must agree with copying and asking the
			// resulting Validity, but without copying anything
			boolean expectedAllValid = true;
			for ( final byte v : expected )
				expectedAllValid &= v != 0;
			assertEquals( msg + " (Validity.allValid)",
					expectedAllValid, dest.getValidity().allValid() );

			final Interval interval = BlockInterval.wrap( srcPos, size );
			final AllValid expectedAll = AllValid.of( expectedAllValid );
			assertEquals( msg + " (allValid, failFast)",
					expectedAll, blocks.allValid( interval, true ) );
			assertEquals( msg + " (allValid, visit all cells)",
					expectedAll, blocks.allValid( interval, false ) );
			assertEquals( msg + " (allValid, threadSafe)",
					expectedAll, blocks.threadSafe().allValid( interval, true ) );
		}
	}

	/**
	 * Per-pixel reference: an out-of-bounds pixel is valid; an in-bounds pixel
	 * is valid iff the cell it falls into is valid.
	 */
	private static byte[] referenceValid(
			final CellGrid grid,
			final IntPredicate cellValid,
			final long[] srcPos,
			final int[] size )
	{
		final int n = size.length;
		final int length = ( int ) Intervals.numElements( size );
		final byte[] valid = new byte[ length ];

		final long[] gridDims = new long[ n ];
		grid.gridDimensions( gridDims );

		final int[] pos = new int[ n ];
		for ( int i = 0; i < length; ++i )
		{
			int rem = i;
			for ( int d = 0; d < n; ++d )
			{
				pos[ d ] = rem % size[ d ];
				rem /= size[ d ];
			}

			boolean oob = false;
			int gridIndex = 0;
			int gridStep = 1;
			for ( int d = 0; d < n; ++d )
			{
				final long x = srcPos[ d ] + pos[ d ];
				if ( x < 0 || x >= grid.imgDimension( d ) )
				{
					oob = true;
					break;
				}
				gridIndex += ( int ) ( x / grid.cellDimension( d ) ) * gridStep;
				gridStep *= gridDims[ d ];
			}
			valid[ i ] = ( byte ) ( ( oob || cellValid.test( gridIndex ) ) ? 1 : 0 );
		}
		return valid;
	}

	/**
	 * Per-pixel reference for the copied data: an out-of-bounds pixel holds
	 * {@code oobValue}; a pixel of an invalid cell holds {@code 0}; a pixel of
	 * a valid cell holds the value that {@link #createVolatileCellImg} put
	 * there.
	 */
	private static short[] referenceData(
			final CellGrid grid,
			final IntPredicate cellValid,
			final long[] srcPos,
			final int[] size,
			final int oobValue )
	{
		final int n = size.length;
		final int length = ( int ) Intervals.numElements( size );
		final short[] data = new short[ length ];

		final long[] gridDims = new long[ n ];
		grid.gridDimensions( gridDims );

		final int[] pos = new int[ n ];
		for ( int i = 0; i < length; ++i )
		{
			int rem = i;
			for ( int d = 0; d < n; ++d )
			{
				pos[ d ] = rem % size[ d ];
				rem /= size[ d ];
			}

			boolean oob = false;
			int gridIndex = 0;
			int gridStep = 1;
			int cellIndex = 0;
			int cellStep = 1;
			for ( int d = 0; d < n; ++d )
			{
				final long x = srcPos[ d ] + pos[ d ];
				if ( x < 0 || x >= grid.imgDimension( d ) )
				{
					oob = true;
					break;
				}
				final int gridx = ( int ) ( x / grid.cellDimension( d ) );
				final long cellMin = ( long ) gridx * grid.cellDimension( d );
				final int cellDim = ( int ) Math.min( grid.cellDimension( d ), grid.imgDimension( d ) - cellMin );
				gridIndex += gridx * gridStep;
				gridStep *= gridDims[ d ];
				cellIndex += ( int ) ( x - cellMin ) * cellStep;
				cellStep *= cellDim;
			}

			if ( oob )
				data[ i ] = ( short ) oobValue;
			else if ( !cellValid.test( gridIndex ) )
				data[ i ] = 0;
			else
				data[ i ] = ( short ) ( gridIndex * 1000 + cellIndex );
		}
		return data;
	}

	/**
	 * Build a {@code CellImg} whose cell data are {@code VolatileShortArray}s
	 * with per-cell validity. Mirrors {@code CellImgFactory.create}, which
	 * always produces non-volatile accesses.
	 */
	static CellImg< VolatileUnsignedShortType, VolatileShortArray > createVolatileCellImg( final IntPredicate cellValid )
	{
		final VolatileUnsignedShortType type = new VolatileUnsignedShortType();
		final NativeTypeFactory< VolatileUnsignedShortType, ? super VolatileShortArray > typeFactory =
				Cast.unchecked( type.getNativeTypeFactory() );
		final CellImgFactory< VolatileUnsignedShortType > factory = new CellImgFactory<>( type, CELL_DIMENSIONS );

		final int n = DIMENSIONS.length;
		final Fraction entitiesPerPixel = type.getEntitiesPerPixel();
		final CellGrid grid = new CellGrid( DIMENSIONS, CELL_DIMENSIONS );
		final long[] gridDimensions = new long[ n ];
		grid.gridDimensions( gridDimensions );

		final Cell< VolatileShortArray > cellType = new Cell<>( new int[] { 1 }, new long[] { 1 }, null );
		final ListImg< Cell< VolatileShortArray > > cells = new ListImg<>( gridDimensions, cellType );

		final long[] cellGridPosition = new long[ n ];
		final ListLocalizingCursor< Cell< VolatileShortArray > > cellCursor = cells.localizingCursor();
		int index = 0;
		while ( cellCursor.hasNext() )
		{
			cellCursor.fwd();
			cellCursor.localize( cellGridPosition );
			final long[] cellMin = new long[ n ];
			final CellGrid.CellDimensionsAndSteps dimsAndSteps = grid.getCellDimensions( cellGridPosition, cellMin );
			final int numEntities = ( int ) entitiesPerPixel.mulCeil( dimsAndSteps.numPixels() );
			final short[] data = new short[ numEntities ];
			for ( int i = 0; i < numEntities; ++i )
				data[ i ] = ( short ) ( index * 1000 + i );
			cellCursor.set( new Cell<>( dimsAndSteps, cellMin,
					new VolatileShortArray( data, cellValid.test( index ) ) ) );
			++index;
		}

		final CellImg< VolatileUnsignedShortType, VolatileShortArray > img =
				new CellImg<>( factory, grid, cells, entitiesPerPixel );
		img.setLinkedType( typeFactory.createLinkedType( img ) );
		return img;
	}
}
