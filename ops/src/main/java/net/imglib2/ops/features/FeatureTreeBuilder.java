package net.imglib2.ops.features;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.imglib2.IterableInterval;
import net.imglib2.Pair;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.ValuePair;

//TODO: this class needs a cleanup!! works for demonstration
public class FeatureTreeBuilder< T extends Type< T >> implements FeatureProcessorBuilder< IterableInterval< T >, DoubleType >
{

	private final List< CachedSampler< ? > > m_samplers;

	private final List< Pair< String, Feature > > m_features;

	private final Set< FeatureSet< IterableInterval< T >, DoubleType > > m_featureSets;

	private final List< PostponedSampler > m_postponed;

	private final List< Pair< ? extends CachedSampler< ? >, Field > > m_sourceUpdateListeners;

	private final Map< CachedSampler< ? >, List< Pair< Field, Object >>> m_nativeTypeSamplers;

	public FeatureTreeBuilder()
	{
		m_nativeTypeSamplers = new HashMap< CachedSampler< ? >, List< Pair< Field, Object >> >();
		m_samplers = new ArrayList< CachedSampler< ? > >();
		m_features = new ArrayList< Pair< String, Feature > >();
		m_postponed = new ArrayList< PostponedSampler >();
		m_featureSets = new HashSet< FeatureSet< IterableInterval< T >, DoubleType >>();
		m_sourceUpdateListeners = new ArrayList< Pair< ? extends CachedSampler< ? >, Field > >();
	}

	private void register( final Feature feature, String group )
	{
		Pair< CachedSampler< ? >, Boolean > sampler = registerSampler( feature );

		// we may want to calculate the same feature for two different feature
		if ( sampler.getB() )
		{
			m_features.add( new ValuePair< String, Feature >( group, ( Feature ) sampler.getA() ) );
		}
	}

	private Pair< CachedSampler< ? >, Boolean > registerSampler( final CachedSampler< ? > sampler )
	{
		boolean added = false;
		CachedSampler< ? > res = null;
		if ( ( res = findSampler( sampler.getClass() ) ) == null )
		{
			res = parse( sampler );
			added = true;
		}

		return new ValuePair< CachedSampler< ? >, Boolean >( res, added );
	}

	private CachedSampler< ? > parse( final CachedSampler< ? > parentSampler )
	{
		for ( Field samplerField : parentSampler.getClass().getDeclaredFields() )
		{
			Class< ? > clazz = samplerField.getType();

			if ( samplerField.isAnnotationPresent( RequiredInput.class ) )
			{
				if ( CachedSampler.class.isAssignableFrom( clazz ) )
				{
					process( parentSampler, samplerField, clazz );
				}
				else
				{
					CachedSampler< ? > stored;

					// first case: we have a sampler which can produce the
					// wished input
					if ( isCompatibleToSource( clazz ) )
					{
						m_sourceUpdateListeners.add( new ValuePair< CachedSampler< ? >, Field >( parentSampler, samplerField ) );
					}
					else if ( ( stored = findSampler( clazz ) ) != null )
					{
						List< Pair< Field, Object >> list;
						if ( ( list = m_nativeTypeSamplers.get( stored ) ) == null )
						{
							list = new ArrayList< Pair< Field, Object >>();
							m_nativeTypeSamplers.put( stored, list );
						}

						list.add( new ValuePair< Field, Object >( samplerField, parentSampler ) );
					}
					else
					{
						throw new RuntimeException( "actually we could postpone the event now and try again later etc. but for now, we force the developer to have an intelligent ordering in his required features" );
					}
				}
			}

			// works for testing
		}

		m_samplers.add( parentSampler );
		return parentSampler;
	}

	private boolean isCompatibleToSource( Class< ? > clazz )
	{
		return clazz.isAssignableFrom( IterableInterval.class );
	}

	@SuppressWarnings( "unchecked" )
	private void process( CachedSampler< ? > parentSampler, Field samplerField, Class< ? > samplerClazz )
	{
		try
		{
			// go through all registered samplers an check if
			// there is a sampler compatible to the current
			// one...
			CachedSampler< ? > storedSampler = findSampler( samplerClazz );

			// If stored feature == null
			if ( storedSampler == null )
			{
				// if it's an interface, we postpone parsing &
				// creation
				if ( samplerClazz.isInterface() )
				{
					m_postponed.add( new PostponedSampler( parentSampler, samplerField, ( Class< ? extends CachedSampler< ? >> ) samplerClazz ) );
				}
				else
				{
					storedSampler = ( net.imglib2.ops.features.CachedSampler< ? > ) samplerClazz.newInstance();
					parse( storedSampler );
				}
			}

			// works for testing..
			injectObject( parentSampler, samplerField, storedSampler );
		}
		catch ( IllegalArgumentException e )
		{
			new RuntimeException( e );
		}
		catch ( IllegalAccessException e )
		{
			new RuntimeException( e );
		}
		catch ( InstantiationException e )
		{
			new RuntimeException( e );
		}
	}

	// TODO: Super dirty access here. we need to find another way:)
	private void injectObject( Object to, Field field, Object o )
	{
		AccessibleObject.setAccessible( new AccessibleObject[] { field }, true );
		try
		{
			field.set( to, o );
		}
		catch ( IllegalArgumentException e )
		{
			new RuntimeException( e );
		}
		catch ( IllegalAccessException e )
		{
			new RuntimeException( e );
		}
		AccessibleObject.setAccessible( new AccessibleObject[] { field }, false );
	}

	/**
	 * @param samplerClazz
	 * @param storedSampler
	 * @return
	 */
	private CachedSampler< ? > findSampler( final Class< ? > samplerClazz )
	{
		CachedSampler< ? > storedSampler = null;
		for ( CachedSampler< ? > registered : m_samplers )
		{
			if ( registered.isCompatible( samplerClazz ) )
			{
				if ( storedSampler == null )
				{
					storedSampler = registered;
				}
			}
		}
		return storedSampler;
	}

	@Override
	public void registerFeatureSet( FeatureSet< IterableInterval< T >, DoubleType > featureSet )
	{
		m_featureSets.add( featureSet );
	}

	@Override
	public FeatureSetProcessor< IterableInterval< T >, DoubleType > build()
	{
		// Cleanup
		m_features.clear();
		m_nativeTypeSamplers.clear();
		m_postponed.clear();
		m_samplers.clear();
		m_sourceUpdateListeners.clear();

		for ( FeatureSet< IterableInterval< T >, DoubleType > set : m_featureSets )
		{
			// First registered any required
			for ( CachedSampler< ? > sampler : set.required() )
			{
				registerSampler( sampler );
			}

			// Then the features
			for ( Feature f : set.features() )
			{
				register( f, set.name() );
			}

			// try postponed ones again, as we added everything now
			List< PostponedSampler > itList = new ArrayList< PostponedSampler >( m_postponed );
			m_postponed.clear();
			for ( PostponedSampler sampler : itList )
			{
				process( sampler.sampler, sampler.samplerField, sampler.samplerChild );
			}
		}

		return new FeatureSetProcessor< IterableInterval< T >, DoubleType >()
		{
			@Override
			public Iterator< Pair< String, Feature >> iterator( IterableInterval< T > objectOfInterest )
			{

				// TODO
				if ( m_postponed.size() > 0 ) { throw new IllegalArgumentException( "This shouldn't happen. if so, we need to implement more iterations of postponing.. " ); }

				// update the sources
				for ( Pair< ? extends CachedSampler< ? >, Field > o : m_sourceUpdateListeners )
				{
					injectObject( o.getA(), o.getB(), objectOfInterest );
					o.getA().markDirty();
				}

				// update the native type samplers
				for ( CachedSampler< ? > sampler : m_nativeTypeSamplers.keySet() )
				{
					sampler.markDirty();
					for ( Pair< Field, Object > pair : m_nativeTypeSamplers.get( sampler ) )
					{
						injectObject( pair.getB(), pair.getA(), sampler.get() );
					}
				}

				// mark all features as dirty
				for ( Pair< String, Feature > f : m_features )
				{
					f.getB().markDirty();
				}
				return m_features.iterator();
			}
		};
	}

	class PostponedSampler
	{
		private Field samplerField;

		private CachedSampler< ? > sampler;

		private Class< ? extends CachedSampler< ? >> samplerChild;

		private PostponedSampler( CachedSampler< ? > sampler, Field samplerField, Class< ? extends CachedSampler< ? >> childClass )
		{
			this.sampler = sampler;
			this.samplerField = samplerField;
			this.samplerChild = childClass;
		}
	}
}
