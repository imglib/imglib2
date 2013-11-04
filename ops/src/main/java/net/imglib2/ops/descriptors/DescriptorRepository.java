package net.imglib2.ops.descriptors;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.imglib2.ops.descriptors.firstorder.impl.SumRealIterable;
import net.imglib2.ops.descriptors.geometric.area.AreaIterableInterval;
import net.imglib2.ops.descriptors.geometric.area.AreaPolygon;
import net.imglib2.ops.descriptors.geometric.centerofgravity.CoGIterableInterval;
import net.imglib2.ops.descriptors.geometric.centerofgravity.CoGPolygon;
import net.imglib2.ops.descriptors.geometric.eccentricity.EccentricityIterableInterval;
import net.imglib2.ops.descriptors.geometric.eccentricity.EccentricityPolygon;
import net.imglib2.ops.descriptors.geometric.perimeter.PerimeterPolygon;

/**
 * The central {@link DescriptorRepository} keeps all available implementations of
 * {@link NotAutoInstantiableFeature} {@link NumericFeature}s.
 * 
 * In the future this eventually can be replaced with a SCIJAVA Service
 */
public class DescriptorRepository
{

	private static DescriptorRepository instance;

	private List< Module< ? >> modules;

	private DescriptorRepository()
	{
		modules = new ArrayList< Module< ? > >();

		// registered
		// TODO: use also just classes here?
		add( new SumRealIterable() );
		add( new AreaIterableInterval() );
		add( new CoGIterableInterval() );
		add( new CoGPolygon() );
		add( new EccentricityPolygon() );
		add( new EccentricityIterableInterval() );
		add( new PerimeterPolygon() );
		add( new AreaPolygon() );
	}

	private void add( Module< ? > module )
	{
		modules.add( module );
	}

	public Module< ? > findFeatureModule( Class< ? > superClass, List< TreeSource< ? > > sources )
	{

		Module< ? > res = null;
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
						if ( !CachedModule.class.isAssignableFrom( f.getType() ) )
						{
							for ( TreeSource< ? > source : sources )
							{
								if ( source.isCompatibleOutput( f.getType() ) )
								{
									if ( res == null || module.priority() > res.priority() )
									{
										res = module;
									}
								}
							}
						}
					}
				}
			}
		}

		if ( res == null )
			throw new IllegalArgumentException( "Repository couldn't find feature which is compatible to given sources! " + superClass.getCanonicalName() );
		else
			return res;

	}

	public static DescriptorRepository getInstance()
	{
		if ( instance == null )
			instance = new DescriptorRepository();

		return instance;
	}
}
