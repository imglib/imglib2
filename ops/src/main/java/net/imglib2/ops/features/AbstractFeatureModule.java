package net.imglib2.ops.features;

public abstract class AbstractFeatureModule extends AbstractDescriptorModule
{
	private final double[] descriptor;

	public AbstractFeatureModule()
	{
		this.descriptor = new double[ 1 ];
	}

	@Override
	protected double[] calculateDescriptor()
	{
		descriptor[ 0 ] = calculateFeature();
		return descriptor;
	}

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
