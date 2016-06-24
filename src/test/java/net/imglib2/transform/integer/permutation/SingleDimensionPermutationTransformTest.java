/**
 *
 */
package net.imglib2.transform.integer.permutation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.img.array.ArrayCursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.TransformView;
import net.imglib2.view.Views;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Philipp Hanslovsky (hanslovskyp@janelia.hhmi.org)
 *
 */
public class SingleDimensionPermutationTransformTest
{

	private final Random rng = new Random();

	private final int width = 100;

	private final int height = 200;

	private final long[] dim = new long[] { this.width, this.height };

	private final int d = 1;

	private final int size = ( int ) this.dim[ this.d ];

	private final int nDim = this.dim.length;

	private final int[] lut = new int[ this.size ];

	private final int[] inv = new int[ this.size ];

	private final int nRandomReps = 1000;

	private final ArrayImg< IntType, IntArray > img = ArrayImgs.ints( this.dim );

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		final ArrayList< Integer > al = new ArrayList< Integer >();
		for ( int i = 0; i < this.size; ++i )
			al.add( i );

		Collections.shuffle( al );

		for ( int i = 0; i < this.lut.length; ++i )
		{
			this.lut[ i ] = al.get( i );
			this.inv[ this.lut[ i ] ] = i;
		}

		for ( final IntType l : this.img )
			l.set( this.rng.nextInt() );
	}

	@Test
	public void test()
	{
		final SingleDimensionPermutationTransform transform = new SingleDimensionPermutationTransform( this.lut, this.nDim, this.nDim, this.d );
		final SingleDimensionPermutationTransform inverse = transform.inverse();

		final TransformView< IntType > transformed = new TransformView< IntType >( this.img, transform );
		final TransformView< IntType > inversed = new TransformView< IntType >( transformed, inverse );

		final IntervalView< IntType > viewTransformed = Views.permuteCoordinateInverse( this.img, this.lut, this.d );
		final IntervalView< IntType > identity = Views.permuteCoordinates( viewTransformed, this.lut, this.d );

		final ArrayCursor< IntType > ref = this.img.cursor();
		final Cursor< IntType > res = Views.flatIterable( Views.interval( inversed, this.img ) ).cursor();
		while ( ref.hasNext() )
			Assert.assertEquals( ref.next().get(), res.next().get() );

		final ArrayRandomAccess< IntType > raFwd = this.img.randomAccess();
		final ArrayRandomAccess< IntType > raBck = this.img.randomAccess();
		final ArrayRandomAccess< IntType > ra = this.img.randomAccess();

		final long[] fwdLong = new long[ this.nDim ];
		final long[] bckLong = new long[ this.nDim ];

		final int[] fwdInt = new int[ this.nDim ];
		final int[] bckInt = new int[ this.nDim ];

		for ( int i = 0; i < this.nRandomReps; ++i )
		{
			final int x = this.rng.nextInt( this.width );
			final int y = this.rng.nextInt( this.width );
			final int[] xyInt = new int[] { x, y };
			final long[] xyLong = new long[] { x, y };
			ra.setPosition( xyInt );

			transform.apply( xyInt, fwdInt );
			transform.apply( xyLong, fwdLong );
			transform.apply( ra, raFwd );

			transform.applyInverse( bckInt, xyInt );
			transform.applyInverse( bckLong, xyLong );
			transform.applyInverse( raBck, ra );

			for ( int d = 0; d < this.nDim; ++d )
			{
				final int fwdVal;
				final int bckVal;
				if ( d == this.d )
				{
					fwdVal = this.lut[ xyInt[ d ] ];
					bckVal = this.inv[ xyInt[ d ] ];
				}
				else
				{
					fwdVal = xyInt[ d ];
					bckVal = xyInt[ d ];
				}

				Assert.assertEquals( fwdVal, fwdInt[ d ] );
				Assert.assertEquals( bckVal, bckInt[ d ] );

				Assert.assertEquals( fwdVal, fwdLong[ d ] );
				Assert.assertEquals( bckVal, bckLong[ d ] );

				Assert.assertEquals( fwdVal, raFwd.getIntPosition( d ) );
				Assert.assertEquals( bckVal, raBck.getIntPosition( d ) );

			}

		}

		{
			final Cursor< IntType > v = Views.flatIterable( viewTransformed ).cursor();
			final Cursor< IntType > i = Views.flatIterable( identity ).cursor();
			final ArrayRandomAccess< IntType > r = this.img.randomAccess();
			final Cursor< IntType > t = Views.flatIterable( Views.interval( transformed, this.img ) ).cursor();
			while ( t.hasNext() )
			{
				t.fwd();
				v.fwd();
				r.setPosition( t );
				r.setPosition( this.lut[ t.getIntPosition( this.d ) ], this.d );

				Assert.assertEquals( r.get().get(), t.get().get() );
				Assert.assertEquals( t.get().get(), v.get().get() );

				i.fwd();
				r.setPosition( i );

				Assert.assertEquals( r.get().get(), i.get().get() );

			}
		}

	}

}
