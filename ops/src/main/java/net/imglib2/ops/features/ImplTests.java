package net.imglib2.ops.features;

import java.util.Iterator;

import net.imglib2.IterableInterval;
import net.imglib2.Pair;
import net.imglib2.ops.data.CooccurrenceMatrix.MatrixOrientation;
import net.imglib2.ops.features.sets.FirstOrderFeatureSet;
import net.imglib2.ops.features.sets.GeometricFeatureSet;
import net.imglib2.ops.features.sets.HaralickFeatureSet;
import net.imglib2.ops.features.sets.WrappedPolygonFeatureSet;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class ImplTests< T extends RealType< T >>
{

	// Somehow intelligent class to optimize feature calculation
	private FeatureTreeBuilder< T > builder;

	// the processor dervived from the builder
	private FeatureSetProcessor< IterableInterval< T >, DoubleType > processor;

	public ImplTests()
	{
		// each feature is only calculated once, even if present in various
		// feature sets

		// Create feature sets
		FirstOrderFeatureSet< IterableInterval< T >> firstOrderFeatureSet = new FirstOrderFeatureSet< IterableInterval< T >>();
		WrappedPolygonFeatureSet< IterableInterval< T >> wrappedPolygon = new WrappedPolygonFeatureSet< IterableInterval< T > >( 2 );
		GeometricFeatureSet< IterableInterval< T > > geometricNative = new GeometricFeatureSet< IterableInterval< T > >( 2 );
		HaralickFeatureSet< IterableInterval< T > > haralick = new HaralickFeatureSet< IterableInterval< T > >( 32, 1, MatrixOrientation.ANTIDIAGONAL );

		builder = new FeatureTreeBuilder< T >();

		// add feature sets
		builder.registerFeatureSet( firstOrderFeatureSet );
		builder.registerFeatureSet( wrappedPolygon );
		builder.registerFeatureSet( geometricNative );
		builder.registerFeatureSet( haralick );

		// Build the processor (maybe this is not needed)
		processor = builder.build();
	}

	public void runFirstOrderTest( final IterableInterval< T > ii )
	{
		final Iterator< Pair< String, Feature >> iterator = processor.iterator( ii );
		while ( iterator.hasNext() )
		{
			final Pair< String, Feature > next = iterator.next();
			final Feature b = next.getB();
			System.out.println( next.getA() + " [" + b.name() + "]: " + b.get() );
		}
	}
}
