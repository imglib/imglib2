package net.imglib2.ops.descriptors.todo;

import java.awt.Polygon;
import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.data.CooccurrenceMatrix.MatrixOrientation;
import net.imglib2.ops.descriptors.Descriptor;
import net.imglib2.ops.descriptors.DescriptorTreeBuilder;
import net.imglib2.ops.descriptors.haralick.helpers.CoocParameter;
import net.imglib2.ops.descriptors.sets.FirstOrderDescriptors;
import net.imglib2.ops.descriptors.sets.GeometricFeatureSet;
import net.imglib2.ops.descriptors.sets.HaralickFeatureSet;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;

public class ImplTests< T extends RealType< T >>
{

	private DescriptorTreeBuilder builder;

	private ExampleIterableIntervalSource sourceII;

	private ExamplePolygonSource sourcePolygon;

	private ExampleHaralickParameterSource sourceHaralickParam;

	public ImplTests()
	{
		// each feature is only calculated once, even if present in various
		// feature sets

		// Create feature sets
		sourceII = new ExampleIterableIntervalSource();
		sourcePolygon = new ExamplePolygonSource();
		sourceHaralickParam = new ExampleHaralickParameterSource();

		builder = new DescriptorTreeBuilder();
		builder.registerDescriptorSet( new FirstOrderDescriptors() );
		builder.registerDescriptorSet( new GeometricFeatureSet() );
		builder.registerDescriptorSet( new HaralickFeatureSet() );

		builder.registerSource( sourceII );
		builder.registerSource( sourcePolygon );
		builder.registerSource( sourceHaralickParam );

		builder.build();
	}

	public void runFirstOrderTest( final IterableInterval< T > ii )
	{
		// updating source
		sourceII.update( ii );

		// extracting polygon (test reasons)
		long[] min = new long[ ii.numDimensions() ];
		ii.min( min );

		sourcePolygon.update( extractPolygon( binaryMask( ii ), min ) );
		sourceHaralickParam.update( createHaralickParam( 1 ) );

		// iterating over results
		Iterator< Descriptor > iterator = builder.iterator();
		while ( iterator.hasNext() )
		{
			final Descriptor next = iterator.next();
			System.out.println( " [" + next.name() + "]: " + next.get()[ 0 ] );
		}

		// We update some haralick parameters which are then recomputed
		sourceHaralickParam.update( createHaralickParam( 2 ) );
		iterator = builder.dirtyIterator();
		while ( iterator.hasNext() )
		{
			final Descriptor next = iterator.next();
			System.out.println( " [" + next.name() + "]: " + next.get()[ 0 ] );
		}

	}

	class ExampleIterableIntervalSource extends AbstractTreeSource< IterableInterval< T >>
	{
		@Override
		public boolean hasCompatibleOutput( Class< ? > clazz )
		{
			return clazz.isAssignableFrom( IterableInterval.class );
		}

		@Override
		public double priority()
		{
			return 1;
		}

	}

	class ExampleHaralickParameterSource extends AbstractTreeSource< CoocParameter >
	{

		@Override
		public boolean hasCompatibleOutput( Class< ? > clazz )
		{
			return clazz.isAssignableFrom( CoocParameter.class );
		}

		@Override
		public double priority()
		{
			return Double.MAX_VALUE;
		}
	}

	class ExamplePolygonSource extends AbstractTreeSource< Polygon >
	{

		@Override
		public boolean hasCompatibleOutput( Class< ? > clazz )
		{
			return clazz.isAssignableFrom( Polygon.class );
		}

		@Override
		public double priority()
		{
			return 10;
		}
	}

	/**
	 * FOR TESTING
	 */
	private CoocParameter createHaralickParam( int distance )
	{
		CoocParameter param = new CoocParameter();
		param.nrGrayLevels = 32;
		param.distance = distance;
		param.orientation = MatrixOrientation.HORIZONTAL;
		return param;
	}

	/**
	 * FOR TESTING
	 */
	private Img< BitType > binaryMask( final IterableInterval< T > ii )
	{
		Img< BitType > binaryMask = new ArrayImgFactory< BitType >().create( ii, new BitType() );
		final RandomAccess< BitType > maskRA = binaryMask.randomAccess();

		final Cursor< T > cur = ii.localizingCursor();
		while ( cur.hasNext() )
		{
			cur.fwd();
			for ( int d = 0; d < cur.numDimensions(); d++ )
			{
				maskRA.setPosition( cur.getLongPosition( d ) - ii.min( d ), d );
			}
			maskRA.get().set( true );

		}
		return binaryMask;
	}

	/**
	 * FOR TESTING
	 */
	private Polygon extractPolygon( final RandomAccessibleInterval< BitType > img, final long[] offset )
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
				p.addPoint( ( int ) offset[ 0 ] + cur.getIntPosition( 0 ), ( int ) offset[ 1 ] + cur.getIntPosition( 1 ) );
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
