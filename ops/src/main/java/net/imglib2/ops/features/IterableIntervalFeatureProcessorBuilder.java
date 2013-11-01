package net.imglib2.ops.features;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.imglib2.IterableInterval;
import net.imglib2.Pair;
import net.imglib2.ops.features.annotations.RequiredFeature;
import net.imglib2.ops.features.providers.SourceGetIterableInterval;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.ValuePair;

public class IterableIntervalFeatureProcessorBuilder< T extends Type< T >> implements FeatureProcessorBuilder< IterableInterval< T >, DoubleType >, FeatureSetProcessor< IterableInterval< T >, DoubleType >
{
	private final HashMap< String, Feature< DoubleType >> m_visibleFeatures;

	private final HashMap< String, Feature< ? >> m_allFeatures;

	private final HashMap< String, String > m_featureGroups;

	private final Set< FeatureSet< IterableInterval< T >, DoubleType > > m_featureSets;

	private final Set< Class< ? extends SmartFeature< ? >> > m_smartList;

	private Source< IterableInterval< T >> m_localSource;

	public IterableIntervalFeatureProcessorBuilder()
	{
		m_allFeatures = new HashMap< String, Feature< ? >>();
		m_visibleFeatures = new HashMap< String, Feature< DoubleType >>();
		m_featureGroups = new HashMap< String, String >();
		m_featureSets = new HashSet< FeatureSet< IterableInterval< T >, DoubleType >>();
		m_smartList = new HashSet< Class< ? extends SmartFeature< ? >> >();

		m_localSource = new SourceGetIterableInterval< T >();

		registerGeneric( m_localSource );
	}

	@SuppressWarnings( "unchecked" )
	private void register( final Feature< DoubleType > feature, String groupName )
	{
		registerGeneric( feature );

		// we may want to calculate the same feature for two different feature
		if ( !m_visibleFeatures.containsKey( feature.getClass().getCanonicalName() + groupName ) )
		{
			m_visibleFeatures.put( feature.getClass().getCanonicalName() + groupName, ( Feature< DoubleType > ) m_allFeatures.get( feature.getClass().getCanonicalName() ) );
			m_featureGroups.put( feature.getClass().getCanonicalName() + groupName, groupName );
		}
	}

	/**
	 * Registers a {@link Feature}. This {@link Feature} will not be available
	 * as a public feature after registration. This method can be used to
	 * register a {@link Feature} which can't be instantiated automatically.
	 * 
	 * @param feature
	 */
	private void registerGeneric( final Feature< ? > feature )
	{
		if ( !m_allFeatures.containsKey( feature.getClass().getCanonicalName() ) )
		{
			parse( feature );
		}
	}

	// Parsing for dependencies
	private void parse( final Feature< ? > feature )
	{
		try
		{
			if ( !m_allFeatures.containsKey( feature ) )
			{
				for ( Field f : feature.getClass().getDeclaredFields() )
				{
					if ( f.isAnnotationPresent( RequiredFeature.class ) )
					{
						Class< ? extends Feature< ? >> fieldType = ( Class< ? extends Feature< ? >> ) f.getType();

						// if its not a feature, we can't instantiate it
						if ( !Feature.class.isAssignableFrom( fieldType ) ) { throw new IllegalArgumentException( "Only Features can be annotated with @RequiredInput since now" ); }

						// if its are source we need to check whether we have
						// this source available
						if ( Source.class.isAssignableFrom( fieldType ) )
						{
							if ( !isSourcePresent( ( Class< ? extends Source< ? >> ) fieldType ) )
								throw new IllegalArgumentException( "Sources and Parametrized Features can't be automatically added. Add them manually to the feature set." );
						}

						// TODO dirty but works for now
						AccessibleObject.setAccessible( new AccessibleObject[] { f }, true );

						// check whether we found a lazy one
						if ( SmartFeature.class.isAssignableFrom( fieldType ) )
						{
							Feature< ? > underlyingFeature = ( ( SmartFeature< ? > ) fieldType.newInstance() ).instantiate( this );
							if ( underlyingFeature == null )
							{
								m_smartList.add( ( Class< ? extends SmartFeature< ? >> ) fieldType );
							}
							else
							{
								checkFieldType( feature, f, underlyingFeature.getClass() );
							}
						}
						else
						{
							checkFieldType( feature, f, fieldType );
						}

						// TODO dirty but works for now
						AccessibleObject.setAccessible( new AccessibleObject[] { f }, false );

					}

				}
			}

			// Cycle detected
			if ( m_allFeatures.containsKey( feature ) ) { throw new IllegalStateException( "Cycle detected" ); }

			m_allFeatures.put( feature.getClass().getCanonicalName(), feature );

		}
		catch ( IllegalArgumentException e )
		{
			e.printStackTrace();
		}
		catch ( IllegalAccessException e )
		{
			e.printStackTrace();
		}
		catch ( InstantiationException e )
		{
			// TODO
			throw new IllegalStateException( "Couldn't instantiate a class. Please not that parametrized features have to be added manually." );
		}
	}

	private void checkFieldType( Feature< ? > current, Field f, Class< ? extends Feature > fieldType ) throws InstantiationException, IllegalAccessException
	{
		Feature< ? > storedFeature = m_allFeatures.get( fieldType.getCanonicalName() );

		if ( storedFeature == null )
		{
			Feature< ? > newFeature = fieldType.newInstance();
			parse( newFeature );
			f.set( current, newFeature );
		}
		else
		{
			f.set( current, storedFeature );
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSourcePresent( Class< ? extends Source< ? >> source )
	{
		return source.isAssignableFrom( m_localSource.getClass() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator< Pair< String, Feature< DoubleType >>> iterator( final IterableInterval< T > objectOfInterest )
	{

		// update all public features
		m_localSource.update( objectOfInterest );

		// Notify the features that something happened
		for ( Feature< ? > f : m_allFeatures.values() )
		{
			f.update();
		}

		final Iterator< Feature< DoubleType >> iterator = m_visibleFeatures.values().iterator();

		return new Iterator< Pair< String, Feature< DoubleType >> >()
		{

			@Override
			public boolean hasNext()
			{
				return iterator.hasNext();
			}

			@Override
			public Pair< String, Feature< DoubleType >> next()
			{
				Feature< DoubleType > next = iterator.next();
				return new ValuePair< String, Feature< DoubleType >>( m_featureGroups.get( next.getClass().getCanonicalName() ), next );
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException( "remove not supported" );
			}
		};
	}

	@Override
	public void registerFeatureSet( FeatureSet< IterableInterval< T >, DoubleType > featureSet )
	{
		m_featureSets.add( featureSet );
	}

	@Override
	public FeatureSetProcessor< IterableInterval< T >, DoubleType > build()
	{

		// we really need to setup our smart list first

		int initsize = 0;
		while ( m_smartList.size() != initsize )
		{

			HashSet< Class< ? extends SmartFeature >> removeSet = new HashSet< Class< ? extends SmartFeature > >();
			Iterator< Class< ? extends SmartFeature< ? >>> iterator = new HashSet< Class< ? extends SmartFeature< ? >>>( m_smartList ).iterator();
			while ( iterator.hasNext() )
			{
				Class< ? extends SmartFeature< ? >> next = iterator.next();

				Feature< ? > instantiate;
				try
				{
					instantiate = next.newInstance().instantiate( this );

					if ( instantiate != null )
					{
						parse( instantiate );
						removeSet.add( next );
					}
				}
				catch ( InstantiationException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch ( IllegalAccessException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			m_smartList.removeAll( removeSet );
			initsize -= removeSet.size();
		}

		if ( initsize > 0 ) { throw new IllegalArgumentException( "Couldn't add all features to processor. something went wrong or there is an unresolvable dependency" ); }
		for ( FeatureSet< IterableInterval< T >, DoubleType > set : m_featureSets )
		{
			for ( Feature< DoubleType > f : set.features() )
			{
				register( f, set.name() );
			}
		}

		return this;
	}

	@Override
	public Feature< ? > getFeature( Class< ? extends Feature > clazz )
	{
		for ( Feature< ? > f : m_allFeatures.values() )
		{
			if ( clazz.isAssignableFrom( f.getClass() ) ) { return f; }
		}
		return null;
	}
}
