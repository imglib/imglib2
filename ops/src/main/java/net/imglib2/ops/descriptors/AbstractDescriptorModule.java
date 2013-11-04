package net.imglib2.ops.descriptors;

import java.lang.reflect.Array;

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
	public boolean hasCompatibleOutput( Class< ? > annotatedType )
	{
		// does this work? how else?
		return Array.class.isAssignableFrom( annotatedType );
	}
}
