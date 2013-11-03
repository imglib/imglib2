package net.imglib2.ops.features;

import java.awt.Polygon;

import net.imglib2.Cursor;
import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Sampler;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.features.sets.PolygonFeatureSet;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class WrappedPolygonFeatureSet< I extends IterableInterval< ? extends RealType< ? >> > extends AbstractFeatureSet< I, DoubleType >
{
	public WrappedPolygonFeatureSet( int numDims )
	{
		PolygonFeatureSet< Polygon > polygonFeatureSet = new PolygonFeatureSet< Polygon >( numDims );

		// Ordering is important since now
		registerRequired( new BitMaskFromIterableInterval() );
		registerRequired( new IterableIntervalToPolygon() );

		for ( Feature f : polygonFeatureSet.features() )
		{
			registerFeature( f );
		}

		for ( CachedSampler< ? > sampler : polygonFeatureSet.required() )
		{
			registerRequired( sampler );
		}

	}

	@Override
	public String name()
	{
		return "Geometric Features (Polygon based)";
	}

	class BitMaskFromIterableInterval extends CachedAbstractSampler< RandomAccessibleInterval< BitType > > implements BitMask
	{
		@RequiredInput
		IterableInterval< ? > ii;

		@Override
		public Sampler< RandomAccessibleInterval< BitType >> copy()
		{
			return new BitMaskFromIterableInterval();
		}

		@Override
		protected RandomAccessibleInterval< BitType > recompute()
		{
			Img< BitType > img = new ArrayImgFactory< BitType >().create( ii, new BitType() );
			RandomAccess< BitType > access = img.randomAccess();
			Cursor< ? > cursor = ii.cursor();
			while ( cursor.hasNext() )
			{
				cursor.fwd();

				for ( int d = 0; d < cursor.numDimensions(); d++ )
				{
					access.setPosition( cursor.getIntPosition( d ) - ii.min( d ), d );
				}

				access.get().set( true );
			}

			return img;
		}

	}

	class IterableIntervalToPolygon extends CachedAbstractSampler< Polygon >
	{
		@RequiredInput
		BitMask ii;

		@Override
		public Sampler< Polygon > copy()
		{
			return new IterableIntervalToPolygon();
		}

		@Override
		protected Polygon recompute()
		{
			return extractPolygon( ii.get(), new int[ ii.get().numDimensions() ] );
		}

		@Override
		public boolean isCompatible( Class< ? > clazz )
		{
			return clazz.isAssignableFrom( Polygon.class );
		}

		/**
		 * Extracts a polygon of a 2D binary image using the Square Tracing
		 * Algorithm (be aware of its drawbacks, e.g. if the pattern is
		 * 4-connected!)
		 * 
		 * @param img
		 *            the image, note that only the first and second dimension
		 *            are taken into account
		 * @param offset
		 *            an offset for the points to be set in the new polygon
		 * @return
		 */
		private Polygon extractPolygon( final RandomAccessibleInterval< BitType > img, final int[] offset )
		{
			final RandomAccess< BitType > cur = new ExtendedRandomAccessibleInterval< BitType, RandomAccessibleInterval< BitType >>( img, new OutOfBoundsConstantValueFactory< BitType, RandomAccessibleInterval< BitType >>( new BitType( false ) ) ).randomAccess();
			boolean start = false;
			// find the starting point
			for ( int i = 0; i < img.dimension( 0 ); i++ )
			{
				for ( int j = 0; j < img.dimension( 1 ); j++ )
				{
					cur.setPosition( i, 0 );
					cur.setPosition( j, 1 );
					if ( cur.get().get() )
					{
						cur.setPosition( i, 0 );
						cur.setPosition( j, 1 );
						start = true;
						break;
					}
				}
				if ( start )
				{
					break;
				}
			}
			int dir = 1;
			int dim = 0;
			final int[] startPos = new int[] { cur.getIntPosition( 0 ), cur.getIntPosition( 1 ) };
			final Polygon p = new Polygon();
			while ( !( ( cur.getIntPosition( 0 ) == startPos[ 0 ] ) && ( cur.getIntPosition( 1 ) == startPos[ 1 ] ) && ( dim == 0 ) && ( dir == 1 ) && !start ) )
			{
				if ( cur.get().get() )
				{
					p.addPoint( offset[ 0 ] + cur.getIntPosition( 0 ), offset[ 1 ] + cur.getIntPosition( 1 ) );
					cur.setPosition( cur.getIntPosition( dim ) - dir, dim );
					if ( ( ( dim == 1 ) && ( dir == 1 ) ) || ( ( dim == 1 ) && ( dir == -1 ) ) )
					{
						dir *= -1;
					}
				}
				else
				{
					cur.setPosition( cur.getIntPosition( dim ) + dir, dim );
					if ( ( ( dim == 0 ) && ( dir == 1 ) ) || ( ( dim == 0 ) && ( dir == -1 ) ) )
					{
						dir *= -1;
					}
				}

				dim = ( dim + 1 ) % 2;
				start = false;
			}
			return p;
		}
	}
}
