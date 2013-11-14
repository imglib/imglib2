package net.imglib2.algorithm.region.localneighborhood;

import static org.junit.Assert.assertEquals;

import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.algorithm.morphology.MorphologicalOperations;
import net.imglib2.algorithm.morphology.StructuringElements;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

public class SquareStrelTest
{

	@Test
	public final void test2Doptimization()
	{
		// We test that we get the same results whether we optimize or not
		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 39, 39 );
		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
		ra.setPosition( new long[] { 19, 19 } );
		ra.get().set( 255 );

		final int[] radiuses = new int[] { 2, 5, 7 };
		for ( final int radius : radiuses )
		{
			final List< Shape > strelStd = StructuringElements.square( radius, img.numDimensions(), false );
			final Img< UnsignedByteType > std = MorphologicalOperations.dilate( img, strelStd, 1 );
			final List< Shape > strelOpt = StructuringElements.square( radius, img.numDimensions(), true );
			final Img< UnsignedByteType > opt = MorphologicalOperations.dilate( img, strelOpt, 1 );

			final Cursor< UnsignedByteType > cStd = std.cursor();
			final RandomAccess< UnsignedByteType > raOpt = opt.randomAccess( opt );
			while ( cStd.hasNext() )
			{
				cStd.fwd();
				raOpt.setPosition( cStd );
				assertEquals( cStd.get().get(), raOpt.get().get() );
			}
		}

	}

	@Test
	public final void test3Doptimization()
	{
		// We test that we get the same results whether we optimize or not
		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 19, 19, 19 );
		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
		ra.setPosition( new long[] { 9, 9, 9 } );
		ra.get().set( 255 );

		final int[] radiuses = new int[] { 2, 3, 5 };
		for ( final int radius : radiuses )
		{
			final List< Shape > strelStd = StructuringElements.square( radius, img.numDimensions(), false );
			final Img< UnsignedByteType > std = MorphologicalOperations.dilate( img, strelStd, 1 );
			final List< Shape > strelOpt = StructuringElements.square( radius, img.numDimensions(), true );
			final Img< UnsignedByteType > opt = MorphologicalOperations.dilate( img, strelOpt, 1 );

			final Cursor< UnsignedByteType > cStd = std.cursor();
			final RandomAccess< UnsignedByteType > raOpt = opt.randomAccess( opt );
			while ( cStd.hasNext() )
			{
				cStd.fwd();
				raOpt.setPosition( cStd );
				assertEquals( cStd.get().get(), raOpt.get().get() );
			}
		}

	}

}
