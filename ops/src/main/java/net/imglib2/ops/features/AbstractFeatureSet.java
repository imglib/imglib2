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

	private Source< T > source;

	private Set< String > active;

	public AbstractFeatureSet( Source< T > source, Set< String > active )
	{
		list = new ArrayList< Feature< V > >();
		this.active = active;
		this.source = source;
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
		if ( active.contains( feature.name() ) )
			list.add( feature );
	}

	@Override
	public Source< T > source()
	{
		return source;
	}
}
