package net.imglib2.ops.features.todo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.imglib2.IterableInterval;
import net.imglib2.ops.features.Descriptor;
import net.imglib2.ops.features.FeatureTreeBuilder;
import net.imglib2.ops.features.Module;
import net.imglib2.ops.features.TreeSource;
import net.imglib2.ops.features.TreeSourceListener;
import net.imglib2.ops.features.sets.FirstOrderFeatureSet;
import net.imglib2.type.numeric.RealType;

public class ImplTests< T extends RealType< T >>
{

	private FeatureTreeBuilder builder;

	private ExampleIterableIntervalSource source;

	public ImplTests()
	{
		// each feature is only calculated once, even if present in various
		// feature sets

		// Create feature sets
		source = new ExampleIterableIntervalSource();

		builder = new FeatureTreeBuilder();

		builder.registerFeatureSet( new FirstOrderFeatureSet() );
		builder.registerSource( source );

		builder.build();
	}

	public void runFirstOrderTest( final IterableInterval< T > ii )
	{
		source.update( ii );
		Iterator< Descriptor > iterator = builder.iterator();
		while ( iterator.hasNext() )
		{
			final Descriptor next = iterator.next();
			System.out.println( " [" + next.name() + "]: " + next.get()[ 0 ] );
		}
	}

	class ExampleIterableIntervalSource implements TreeSource< IterableInterval< T >>
	{

		private IterableInterval< T > ii;

		private List< TreeSourceListener > listeners = new ArrayList< TreeSourceListener >();

		void update( IterableInterval< T > ii )
		{
			this.ii = ii;
			notifyListeners();
		}

		@Override
		public IterableInterval< T > get()
		{
			return ii;
		}

		@Override
		public boolean isEquivalentModule( Module< ? > output )
		{
			// we are the one
			return false;
		}

		@Override
		public boolean isCompatibleOutput( Class< ? > annotatedType )
		{
			return annotatedType.isAssignableFrom( IterableInterval.class );
		}

		@Override
		public double priority()
		{
			// we are the one
			return Double.MAX_VALUE;
		}

		@Override
		public void markDirty()
		{
			// well this is not needed, but thanks anyway
		}

		@Override
		public boolean isDirty()
		{
			// we are dirty, so our neighbors can be updated
			return true;
		}

		@Override
		public void notifyListeners()
		{
			for ( TreeSourceListener listener : listeners )
			{
				listener.updated( this );
			}
		}

		@Override
		public void registerListener( TreeSourceListener listener )
		{
			listeners.add( listener );
		}

		@Override
		public boolean isRegistered( TreeSourceListener listener )
		{
			return listeners.contains( listener );
		}

	}
}
