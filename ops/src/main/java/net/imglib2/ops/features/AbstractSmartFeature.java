package net.imglib2.ops.features;


public abstract class AbstractSmartFeature< T > extends AbstractFeature< T > implements SmartFeature< T >
{
	private Feature< T > wrapped;

	@Override
	public String name()
	{
		return wrapped.name();
	}

	@Override
	public Feature< T > copy()
	{
		return wrapped.copy();
	}

	@Override
	public Feature< T > instantiate( FeatureProcessorBuilder< ?, ? > processor )
	{
		wrapped = create( processor );
		return wrapped;
	}

	@Override
	protected T recompute()
	{
		return wrapped.get();
	}

	protected abstract Feature< T > create( FeatureProcessorBuilder< ?, ? > processor );
}
