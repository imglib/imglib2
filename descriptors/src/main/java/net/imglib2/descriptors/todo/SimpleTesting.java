package net.imglib2.descriptors.todo;

import java.awt.Polygon;
import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.descriptors.Descriptor;
import net.imglib2.descriptors.DescriptorTreeBuilder;
import net.imglib2.descriptors.firstorder.percentile.helper.PercentileParameter;
import net.imglib2.descriptors.haralick.helpers.CoocParameter;
import net.imglib2.descriptors.moments.zernike.helper.ZernikeParameter;
import net.imglib2.descriptors.sets.FirstOrderDescriptors;
import net.imglib2.descriptors.sets.GeometricFeatureSet;
import net.imglib2.descriptors.sets.HaralickFeatureSet;
import net.imglib2.descriptors.sets.ImageMomentsDescriptorSet;
import net.imglib2.descriptors.sets.TamuraFeatureSet;
import net.imglib2.descriptors.sets.ZernikeDescriptorSet;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.data.CooccurrenceMatrix.MatrixOrientation;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;

/**
 * Simple Class for demo of the DescriptorTreeBuilder
 * 
 * @param <T>
 */
public class SimpleTesting< T extends RealType< T >>
{

	private DescriptorTreeBuilder builder;

	private ExampleIterableIntervalSource sourceII;

	private ExamplePolygonSource sourcePolygon;

	private ExampleHaralickParameterSource sourceHaralickParam;

	private ExamplePercentileParameterSource sourcePercentileParam;

	private ExamplezernikeParameterSource sourceZernikeParam;


	public SimpleTesting()
	{
		// each descriptor is only calculated once per source, even if present
		// in various
		// feature sets

		// Create sources sets
		sourceII = new ExampleIterableIntervalSource();
		sourcePolygon = new ExamplePolygonSource();
		sourceHaralickParam = new ExampleHaralickParameterSource();
		sourcePercentileParam = new ExamplePercentileParameterSource();
		sourceZernikeParam = new ExamplezernikeParameterSource();

		// create the builder
		builder = new DescriptorTreeBuilder();
		builder.registerDescriptorSet( new FirstOrderDescriptors() );
		builder.registerDescriptorSet( new GeometricFeatureSet() );
		builder.registerDescriptorSet( new HaralickFeatureSet() );
		builder.registerDescriptorSet( new TamuraFeatureSet() );
		builder.registerDescriptorSet( new ZernikeDescriptorSet() );
		builder.registerDescriptorSet( new ImageMomentsDescriptorSet() );

		// set the sources
		builder.registerSource( sourceII );
		builder.registerSource( sourcePolygon );
		builder.registerSource( sourceHaralickParam );
		builder.registerSource( sourcePercentileParam );
		builder.registerSource( sourceZernikeParam );


		// optimize the featureset
		builder.build();
	}

	public void test( final IterableInterval< T > ii )
	{
		// updating sources
		sourceII.update( ii );
		sourcePolygon.update( extractPolygon( binaryMask( ii ) ) );
		sourceHaralickParam.update( createHaralickParam( 1 ) );
		sourcePercentileParam.update( createPercentileParam( 0.5 ) );
		sourceZernikeParam.update( createZernikeParam( 3 ) );


		// iterating over results
		Iterator< Descriptor > iterator = builder.iterator();
		while ( iterator.hasNext() )
		{
			final Descriptor descriptor = iterator.next();
			final double[] res = descriptor.get();

			for ( int i = 0; i < res.length; i++ )
				System.out.println( " [" + descriptor.name() + "](" + i + "): " + res[ i ] );
		}

		// // We update some haralick parameters which are then recomputed. The
		// // updatedIterator only contains dirty features
		// sourceHaralickParam.update( createHaralickParam( 2 ) );
		//
		// // we iterate over all features which were affected by the parameter
		// // change
		// iterator = builder.updatedIterator();
		// while ( iterator.hasNext() )
		// {
		// final Descriptor descriptor = iterator.next();
		// final double[] res = descriptor.get();
		//
		// for ( int i = 0; i < res.length; i++ )
		// System.out.println( " [" + descriptor.name() + "](" + i + "): " +
		// res[ i ] );
		// }

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

	class ExamplePercentileParameterSource extends AbstractTreeSource< PercentileParameter >
	{
		@Override
		public boolean hasCompatibleOutput( Class< ? > clazz )
		{
			return clazz.isAssignableFrom( PercentileParameter.class );
		}

		@Override
		public double priority()
		{
			return Double.MAX_VALUE;
		}
	}

	class ExamplezernikeParameterSource extends AbstractTreeSource< ZernikeParameter >
	{
		@Override
		public boolean hasCompatibleOutput( Class< ? > clazz )
		{
			return clazz.isAssignableFrom( ZernikeParameter.class );
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

	/*
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

	/*
	 * FOR TESTING
	 */
	private PercentileParameter createPercentileParam( double p )
	{
		PercentileParameter param = new PercentileParameter();
		param.setP( p );
		return param;
	}

	/*
	 * FOR TESTING
	 */
	private ZernikeParameter createZernikeParam( int _order )
	{
		ZernikeParameter param = new ZernikeParameter();
		param.setOrder( _order );
		return param;
	}


	/*
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

	/*
	 * FOR TESTING
	 */
	private Polygon extractPolygon( final RandomAccessibleInterval< BitType > in )
	{
		final RandomAccess< BitType > cur = new ExtendedRandomAccessibleInterval< BitType, RandomAccessibleInterval< BitType >>( in, new OutOfBoundsConstantValueFactory< BitType, RandomAccessibleInterval< BitType >>( new BitType( false ) ) ).randomAccess();
		boolean start = false;
		// find the starting point
		for ( int i = 0; i < in.dimension( 0 ); i++ )
		{
			for ( int j = 0; j < in.dimension( 1 ); j++ )
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
				p.addPoint( ( int ) in.min( 0 ) + cur.getIntPosition( 0 ), ( int ) in.min( 1 ) + cur.getIntPosition( 1 ) );
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
