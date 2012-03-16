package net.imglib2.img.list;

import static org.junit.Assert.assertArrayEquals;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
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

	ListImg< IntType > intImg;

	@Before
	public void createSourceData()
	{
		dimensions = new long[] { 48, 17, 102 };

		numValues = 1;
		for ( int d = 0; d < dimensions.length; ++d )
			numValues *= dimensions[ d ];

		intData = new int[ numValues ];
		intDataSum = 0;
		final Random random = new Random( 0 );
		for ( int i = 0; i < numValues; ++i )
		{
			intData[ i ] = random.nextInt();
			intDataSum += intData[ i ];
		}

		intImg = new ListImgFactory< IntType >().create( dimensions, new IntType() );

		final long[] pos = new long[ dimensions.length ];
		final RandomAccess< IntType > a = intImg.randomAccess();

		for ( int i = 0; i < numValues; ++i )
		{
			IntervalIndexer.indexToPosition( i, dimensions, pos );
			a.setPosition( pos );
			a.get().set( intData[ i ] );
		}
	}

	void copyWithSourceIteration(final Img< IntType > srcImg, final Img< IntType > dstImg)
	{
		final long[] pos = new long[ dimensions.length ];
		final Cursor< IntType > src = srcImg.localizingCursor();
		final RandomAccess< IntType > dst = dstImg.randomAccess();
		while( src.hasNext() ) {
			src.fwd();
			src.localize( pos );
			dst.setPosition( pos );
			dst.get().set( src.get() );
		}
	}

	void copyWithDestIteration(final Img< IntType > srcImg, final Img< IntType > dstImg)
	{
		final long[] pos = new long[ dstImg.numDimensions() ];
		final Cursor< IntType > dst = dstImg.localizingCursor();
		final RandomAccess< IntType > src = srcImg.randomAccess();
		while( dst.hasNext() ) {
			dst.fwd();
			dst.localize( pos );
			src.setPosition( pos );
			dst.get().set( src.get() );
		}
	}

	int[] getImgAsInts( final Img< IntType > img )
	{
		final RandomAccess< IntType > a = img.randomAccess();
		final int N = ( int ) img.size();
		final int[] data = new int[ N ];
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
	public void testCopyToArrayImgWithSourceIteration()
	{
		final ArrayImg< IntType, ? > array = new ArrayImgFactory< IntType >().create( dimensions, new IntType() );
		copyWithSourceIteration( intImg, array );
		assertArrayEquals( intData, getImgAsInts( array ) );
	}

	@Test
	public void testCopyToArrayImgWithDestIteration()
	{
		final ArrayImg< IntType, ? > array = new ArrayImgFactory< IntType >().create( dimensions, new IntType() );
		copyWithDestIteration( intImg, array );
		assertArrayEquals( intData, getImgAsInts( array ) );
	}

	@Test
	public void testCopyToListImgWithSourceIteration()
	{
		final ListImg< IntType > listImg = new ListImgFactory< IntType >().create( dimensions, new IntType() );
		copyWithSourceIteration( intImg, listImg );
		assertArrayEquals( intData, getImgAsInts( listImg ) );
	}

	@Test
	public void testCopyToPlanarImgWithDestIteration()
	{
		final ListImg< IntType > listImg = new ListImgFactory< IntType >().create( dimensions, new IntType() );
		copyWithDestIteration( intImg, listImg );
		assertArrayEquals( intData, getImgAsInts( listImg ) );
	}
}
