package net.imglib2.ops.features;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.imglib2.ops.features.firstorder.impl.SumRealIterable;
import net.imglib2.ops.features.geometric.area.AreaIterableInterval;
import net.imglib2.ops.features.geometric.centerofgravity.CoGIterableInterval;

/**
 * The central {@link FeatureRepository} keeps all available implementations of
 * {@link NotAutoInstantiableFeature} {@link NumericFeature}s.
 * 
 * In the future this eventually can be replaced with a SCIJAVA Service
 */
public class FeatureRepository
{

	private static FeatureRepository instance;

	private List< Module< ? >> modules;

	private FeatureRepository()
	{
		modules = new ArrayList< Module< ? > >();

		// registered
		add( new SumRealIterable() );
		add( new AreaIterableInterval() );
		add( new CoGIterableInterval() );
	}

	private void add( Module< ? > module )
	{
		modules.add( module );
	}

	public Module< ? > findFeatureModule( Class< ? > superClass, List< TreeSource< ? > > sources )
	{
		for ( Module< ? > module : modules )
		{
			if ( superClass.isAssignableFrom( module.getClass() ) )
			{
				// check for compatible sources and for now: simply take the
				// first compatible one. else throw exception.

				for ( Field f : module.getClass().getDeclaredFields() )
				{

					if ( f.isAnnotationPresent( ModuleInput.class ) )
					{
						if ( !Module.class.isAssignableFrom( f.getType() ) )
						{
							boolean valid = true;
							for ( TreeSource< ? > source : sources )
							{
								if ( !source.isCompatibleOutput( f.getType() ) )
								{
									valid = false;
								}
							}

							if ( valid )
								return module;
						}
					}
				}

			}
		}

		throw new IllegalArgumentException( "Repository couldn't find feature which is compatible to given sources! " + superClass.getCanonicalName() );
	}

	public static FeatureRepository getInstance()
	{
		if ( instance == null )
			instance = new FeatureRepository();

		return instance;
	}
}
