package net.imglib2.descriptors;


public abstract class AbstractFeatureModule extends AbstractDescriptorModule
{
	private final double[] descriptor;

	public AbstractFeatureModule()
	{
		this.descriptor = new double[ 1 ];
	}

	@Override
	protected final double[] recompute()
	{
		descriptor[ 0 ] = calculateFeature();
		return descriptor;
	}

	/**
	 * Calculate the value of this feature and get it back
	 * 
	 * @return
	 */
	protected abstract double calculateFeature();

	/**
	 * returns the features value
	 * 
	 * @return
	 */
	public final double value()
	{
		return get()[ 0 ];
	}
}
