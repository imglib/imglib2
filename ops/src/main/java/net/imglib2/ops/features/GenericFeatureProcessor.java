package net.imglib2.ops.features;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;

import net.imglib2.IterableInterval;
import net.imglib2.Pair;
import net.imglib2.ops.features.providers.GetIterableInterval;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.ValuePair;

public class GenericFeatureProcessor< T extends Type< T >> implements FeatureProcessor< IterableInterval< T >, DoubleType >
{
	// all public features
	private final HashMap< String, Feature< DoubleType >> m_publicFeatures;

	// all features including dependencies which are hidden
	private final HashMap< String, Feature< ? >> m_requiredFeatures;

	// group of the feature
	private final HashMap< String, String > m_featureGroups;

	// the actual updater
	private final GetIterableInterval< T > m_updater;

	/**
     *
     */
	public GenericFeatureProcessor()
	{
		m_requiredFeatures = new HashMap< String, Feature< ? >>();
		m_publicFeatures = new HashMap< String, Feature< DoubleType >>();
		m_featureGroups = new HashMap< String, String >();

		// we need to register our source
		registerNonPublic( m_updater = new GetIterableInterval< T >() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void register( final Feature< DoubleType > feature, String groupName )
	{
		registerNonPublic( feature );

		// we may want to calculate the same feature for two different feature
		// sets
		if ( !m_publicFeatures.containsKey( feature.getClass().getCanonicalName() + groupName ) )
		{
			m_publicFeatures.put( feature.getClass().getCanonicalName() + groupName, ( Feature< DoubleType > ) m_requiredFeatures.get( feature.getClass().getCanonicalName() ) );
			m_featureGroups.put( feature.getClass().getCanonicalName() + groupName, groupName );
		}

	}

	/**
	 * Registers a feature. This feature will not be available as a public
	 * feature after registration. This method can be used to register a
	 * {@link Feature} which can't be instantiated automatically.
	 * 
	 * @param feature
	 */
	public void registerNonPublic( final Feature< ? > feature )
	{
		if ( !m_requiredFeatures.containsKey( feature.getClass().getCanonicalName() ) )
		{
			parse( feature );
		}
	}

	// Parsing for dependencies
	private void parse( final Feature< ? > feature )
	{
		if ( !m_requiredFeatures.containsKey( feature ) )
		{

			for ( Field f : feature.getClass().getDeclaredFields() )
			{

				if ( f.isAnnotationPresent( RequiredFeature.class ) )
				{
					try
					{
						Class< ? extends Feature< ? >> fieldType = ( Class< ? extends Feature< ? >> ) f.getType();

						if ( !Feature.class.isAssignableFrom( fieldType ) ) { throw new IllegalArgumentException( "Only Features can be annotated with @RequiredInput since now" ); }

						// TODO dirty but works for now
						AccessibleObject.setAccessible( new AccessibleObject[] { f }, true );
						Feature< ? > storedFeature = m_requiredFeatures.get( fieldType.getCanonicalName() );

						if ( storedFeature == null )
						{
							if ( Source.class.isAssignableFrom( fieldType ) ) { throw new IllegalArgumentException( "Sources and Parametrized Features can't be automatically added. Add them manually to the feature set." ); }
							Feature< ? > newFeature = fieldType.newInstance();
							parse( newFeature );
							m_requiredFeatures.put( fieldType.getCanonicalName(), newFeature );
							f.set( feature, newFeature );
						}
						else
						{
							f.set( feature, storedFeature );
						}

						// TODO dirty but works for now
						AccessibleObject.setAccessible( new AccessibleObject[] { f }, false );

					}
					catch ( IllegalArgumentException e )
					{
						// TODO
						e.printStackTrace();
					}
					catch ( IllegalAccessException e )
					{
						// TODO
						e.printStackTrace();
					}
					catch ( InstantiationException e )
					{
						// TODO
						throw new IllegalStateException( "Couldn't instantiate a class. Please not that parametrized features have to be added manually." );
					}
				}
			}

			// Cycle detected
			if ( m_requiredFeatures.containsKey( feature ) ) { throw new IllegalStateException( "Cycle detected" ); }

			m_requiredFeatures.put( feature.getClass().getCanonicalName(), feature );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator< Pair< String, Feature< DoubleType >>> iterator( final IterableInterval< T > objectOfInterest )
	{

		// update all public features
		m_updater.update( objectOfInterest );

		// Notify the features that something happened
		for ( Feature< ? > f : m_requiredFeatures.values() )
		{
			f.update();
		}

		final Iterator< Feature< DoubleType >> iterator = m_publicFeatures.values().iterator();

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
	public void register( FeatureSet< IterableInterval< T >, DoubleType > featureSet )
	{
		for ( Feature< DoubleType > feature : featureSet.features() )
		{
			register( feature, featureSet.name() );
		}
	}
}
