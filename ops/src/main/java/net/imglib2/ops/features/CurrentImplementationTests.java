package net.imglib2.ops.features;

import java.util.Iterator;

import net.imglib2.IterableInterval;
import net.imglib2.Pair;
import net.imglib2.ops.data.CooccurrenceMatrix.MatrixOrientation;
import net.imglib2.ops.features.datastructures.Feature;
import net.imglib2.ops.features.datastructures.FeatureSetProcessor;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class CurrentImplementationTests< T extends RealType< T >>
{

	private FeatureTreeBuilder< T > builder;

	private FeatureSetProcessor< IterableInterval< T >, DoubleType > build;

	public CurrentImplementationTests()
	{
		FirstOrderFeatureSet< IterableInterval< T >> firstOrderFeatureSet = new FirstOrderFeatureSet< IterableInterval< T >>();
		WrappedPolygonFeatureSet< IterableInterval< T >> wrappedPolygon = new WrappedPolygonFeatureSet< IterableInterval< T > >( 2 );
		GeometricFeatureSet< IterableInterval< T > > geometricNative = new GeometricFeatureSet< IterableInterval< T > >( 2 );
		HaralickFeatureSet< IterableInterval< T > > haralick = new HaralickFeatureSet< IterableInterval< T > >( 32, 1, MatrixOrientation.ANTIDIAGONAL );

		builder = new FeatureTreeBuilder< T >();
		builder.registerFeatureSet( firstOrderFeatureSet );
		builder.registerFeatureSet( wrappedPolygon );
		builder.registerFeatureSet( geometricNative );
		builder.registerFeatureSet( haralick );
		build = builder.build();
	}

	public void runFirstOrderTest( final IterableInterval< T > ii )
	{
		final Iterator< Pair< String, Feature >> iterator = build.iterator( ii );
		while ( iterator.hasNext() )
		{
			final Pair< String, Feature > next = iterator.next();
			final Feature b = next.getB();
//			System.out.println( next.getA() + " [" + b.name() + "]: " + b.get() );
		}
	}
}
