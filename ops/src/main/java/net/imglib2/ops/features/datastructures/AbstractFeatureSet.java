package net.imglib2.ops.features.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Feature Set
 * 
 * @param <T>
 * @param <V>
 */
public abstract class AbstractFeatureSet< T, V > implements FeatureSet< T, V >
{
	private final List< Feature > features;

	// private final List< Source< ? > > sources;

	private final List< CachedSampler< ? > > required;

	public AbstractFeatureSet()
	{
		features = new ArrayList< Feature >();
		required = new ArrayList< CachedSampler< ? > >();
		// sources = new ArrayList< Source< ? > >();
	}

	@Override
	public int numFeatures()
	{
		return features.size();
	}

	@Override
	public List< Feature > features()
	{
		return features;
	}

	/**
	 * Register public {@link Feature}
	 * 
	 * @param feature
	 */
	protected void registerFeature( Feature feature )
	{
		features.add( feature );
	}

	// /**
	// * Register {@link Source}s
	// *
	// * @param source
	// */
	// protected void registerSource( Source< ? > source )
	// {
	// sources.add( source );
	// }
	//
	// @Override
	// public List< Source< ? >> sources()
	// {
	// return sources;
	// }

	/**
	 * Register required {@link Feature}s
	 * 
	 * @param feature
	 */
	protected void registerRequired( CachedSampler< ? > feature )
	{
		required.add( feature );
	}

	public List< CachedSampler< ? > > required()
	{
		return required;
	}
}
