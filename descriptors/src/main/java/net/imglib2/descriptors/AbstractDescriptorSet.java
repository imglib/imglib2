package net.imglib2.descriptors;

import java.util.ArrayList;
import java.util.List;

/**
 * AbstractDescriptorSet
 * 
 * @param <T>
 * @param <V>
 */
public abstract class AbstractDescriptorSet implements DescriptorSet
{
	private final List< Class< ? extends Descriptor > > features;

	public AbstractDescriptorSet()
	{
		features = new ArrayList< Class< ? extends Descriptor > >();
	}

	@Override
	public int numFeatures()
	{
		return features.size();
	}

	/**
	 * Register public {@link NumericFeature}
	 * 
	 * @param feature
	 */
	protected void registerFeature( Class< ? extends Descriptor > feature )
	{
		features.add( feature );
	}

	@Override
	public List< Class< ? extends Descriptor > > descriptors()
	{
		return features;
	}
}
