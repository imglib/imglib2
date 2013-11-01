package net.imglib2.ops.features;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;

import net.imglib2.IterableInterval;
import net.imglib2.ops.features.providers.GetIterableInterval;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * 
 * @author dietzc
 * @param <T>
 *            Type must be the most generic type which is supported by a
 *            updatehandler
 */
public class GenericFeatureProcessor< T extends Type< T >> implements FeatureSet< IterableInterval< T >, DoubleType >
{

	// all public features
	private final HashMap< String, Feature< DoubleType >> m_publicFeatures;

	// all features including dependencies which are hidden
	private final HashMap< String, Feature< ? >> m_requiredFeatures;

	// the actual updater (can be multithreaded etc)
	private final GetIterableInterval< T > m_updater;

	/**
     *
     */
	public GenericFeatureProcessor()
	{
		m_requiredFeatures = new HashMap< String, Feature< ? >>();
		m_publicFeatures = new HashMap< String, Feature< DoubleType >>();
		registerHidden( m_updater = new GetIterableInterval< T >() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int numFeatures()
	{
		return m_publicFeatures.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void register( final Feature< DoubleType > feature )
	{
		registerHidden( feature );

		if ( !m_publicFeatures.containsKey( feature.getClass().getCanonicalName() ) )
		{
			m_publicFeatures.put( feature.getClass().getCanonicalName(), ( Feature< DoubleType > ) m_requiredFeatures.get( feature.getClass().getCanonicalName() ) );
		}
	}

	public void registerHidden( final Feature< ? > feature )
	{
		if ( !m_requiredFeatures.containsKey( feature.getClass().getCanonicalName() ) )
		{
			parse( feature );
		}
	}

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

						// TODO UGLY make sure that access manager is closed
						// again
						AccessibleObject.setAccessible( new AccessibleObject[] { f }, false );

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
						throw new IllegalStateException( "Couldn't instantiate a class. Please not that parametrized features have to be added manually." );
					}
				}
			}// end loop over dependencies

			// Cycle detected
			if ( m_requiredFeatures.containsKey( feature ) ) { throw new IllegalStateException( "cycle detected" ); }

			m_requiredFeatures.put( feature.getClass().getCanonicalName(), feature );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String featureSetName()
	{
		return "MyFeatureSet";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator< Feature< DoubleType >> iterator( final IterableInterval< T > objectOfInterest )
	{

		m_updater.update( objectOfInterest );

		// Notify the features that something happened
		for ( Feature< ? > f : m_requiredFeatures.values() )
		{
			f.update();
		}

		return m_publicFeatures.values().iterator();
	}
}
