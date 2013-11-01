package net.imglib2.ops.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Abstract Feature Set
 * 
 * @param <T>
 * @param <V>
 */
public abstract class AbstractFeatureSet< T, V > implements FeatureSet< T, V >
{
	private final List< Feature< V > > list;

	private Set< String > active;

	public AbstractFeatureSet( Set< String > active )
	{
		list = new ArrayList< Feature< V > >();
		this.active = active;
	}

	@Override
	public int numFeatures()
	{
		return list.size();
	}

	@Override
	public List< Feature< V >> features()
	{
		return list;
	}

	public void register( Feature< V > feature )
	{
		if ( active == null || active.contains( feature.name() ) )
			list.add( feature );
	}
}
