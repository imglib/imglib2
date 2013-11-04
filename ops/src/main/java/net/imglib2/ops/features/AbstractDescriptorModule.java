package net.imglib2.ops.features;

import net.imglib2.type.numeric.real.DoubleType;

public abstract class AbstractDescriptorModule extends AbstractModule< double[] > implements Descriptor
{
	@Override
	public boolean isEquivalentModule( Module< ? > output )
	{
		// if they are equal
		return getClass() == output.getClass();
	}

	@Override
	public boolean isCompatibleOutput( Class< ? > annotatedType )
	{
		return DoubleType.class.isAssignableFrom( annotatedType );
	}
}
