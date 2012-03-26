package net.imglib2.img.cell;

import static org.junit.Assert.assertArrayEquals;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImg;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.IntervalIndexer;

import org.junit.Before;
import org.junit.Test;

public class CopyTest
{
	long[] dimensions;

	int numValues;

	int[] intData;

	long intDataSum;

	CellImg< IntType, ?, ? > intImg;

	@Before
	public void createSourceData()
	{
		dimensions = new long[] { 48, 17, 102 };

		numValues = 1;
		for ( int d = 0; d < dimensions.length; ++d )
			numValues *= dimensions[ d ];

		intData = new int[ numValues ];
		intDataSum = 0;
		Random random = new Random( 0 );
		for ( int i = 0; i < numValues; ++i )
		{
			intData[ i ] = random.nextInt();
			intDataSum += intData[ i ];
		}
		
		intImg = new CellImgFactory< IntType >( 10 ).create( dimensions, new IntType() );

		long[] pos = new long[ dimensions.length ];
		RandomAccess< IntType > a = intImg.randomAccess();

		for ( int i = 0; i < numValues; ++i )
		{
			IntervalIndexer.indexToPosition( i, dimensions, pos );
			a.setPosition( pos );
			a.get().set( intData[ i ] );
		}
	}

	void copyWithSourceIteration(Img< IntType > srcImg, Img< IntType > dstImg)
	{
		long[] pos = new long[ dimensions.length ];
		Cursor< IntType > src = srcImg.localizingCursor();
		RandomAccess< IntType > dst = dstImg.randomAccess();
		while( src.hasNext() ) {
			src.fwd();
			src.localize( pos );
			dst.setPosition( pos );
			dst.get().set( src.get() );
		}
	}

	void copyWithDestIteration(Img< IntType > srcImg, Img< IntType > dstImg)
	{
		long[] pos = new long[ dstImg.numDimensions() ];
		Cursor< IntType > dst = dstImg.localizingCursor();
		RandomAccess< IntType > src = srcImg.randomAccess();
		while( dst.hasNext() ) {
			dst.fwd();
			dst.localize( pos );
			src.setPosition( pos );
			dst.get().set( src.get() );
		}
	}

	public void copyWithIterationBoth(Img< IntType > srcImg, Img< IntType > dstImg)
	{
		Cursor< IntType > src = srcImg.cursor();
		Cursor< IntType > dst = dstImg.cursor();
		while( src.hasNext() ) {
			dst.next().set( src.next().get() );
		}
	}

	int[] getImgAsInts( Img< IntType > img )
	{
		RandomAccess< IntType > a = img.randomAccess();
		final int N = ( int ) img.size();
		int[] data = new int[ N ];
		final long[] dim = new long[ img.numDimensions() ];
		final long[] pos = new long[ img.numDimensions() ];
		img.dimensions( dim );
		for ( int i = 0; i < N; ++i ) {
			IntervalIndexer.indexToPosition( i, dim, pos );
			a.setPosition( pos );
			data[ i ] = a.get().get(); 
		}
		return data;
	}

	@Test
	public void testCopyToArrayContainerWithSourceIteration()
	{
		ArrayImg< IntType, ? > array = new ArrayImgFactory< IntType >().create( dimensions, new IntType() );
		copyWithSourceIteration( intImg, array );
		assertArrayEquals( intData, getImgAsInts( array ) );
	}

	@Test
	public void testCopyToArrayContainerWithDestIteration()
	{
		ArrayImg< IntType, ? > array = new ArrayImgFactory< IntType >().create( dimensions, new IntType() );
		copyWithDestIteration( intImg, array );
		assertArrayEquals( intData, getImgAsInts( array ) );
	}

	@Test
	public void testCopyToCellContainerWithSourceIteration()
	{
		CellImg< IntType, ?, ? > cellImg = new CellImgFactory< IntType >( new int[] {2, 7, 4} ).create( dimensions, new IntType() );
		copyWithSourceIteration( intImg, cellImg );
		assertArrayEquals( intData, getImgAsInts( cellImg ) );
	}

	@Test
	public void testCopyToCellContainerWithDestIteration()
	{
		CellImg< IntType, ?, ? > cellImg = new CellImgFactory< IntType >( new int[] {2, 7, 4} ).create( dimensions, new IntType() );
		copyWithDestIteration( intImg, cellImg );
		assertArrayEquals( intData, getImgAsInts( cellImg ) );
	}

	@Test
	public void testCopyArrayToArrayWithIterationBoth()
	{
		ArrayImg< IntType, ? > array2 = new ArrayImgFactory< IntType >().create( dimensions, new IntType() );
		ArrayImg< IntType, ? > array = new ArrayImgFactory< IntType >().create( dimensions, new IntType() );
		copyWithDestIteration( intImg, array2 );
		copyWithIterationBoth( array2, array );
		assertArrayEquals( intData, getImgAsInts( array ) );
	}
}
