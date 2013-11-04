package net.imglib2.ops.descriptors;

import net.imglib2.type.numeric.real.DoubleType;

public abstract class AbstractDescriptorModule extends AbstractModule< double[] > implements Descriptor
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEquivalentModule( Module< ? > output )
	{
		return getClass() == output.getClass();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCompatibleOutput( Class< ? > annotatedType )
	{
		return DoubleType.class.isAssignableFrom( annotatedType );
	}
}
