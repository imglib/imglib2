/**
 * 
 */
package net.imglib2.converter;

import net.imglib2.type.Type;
import net.imglib2.view.composite.Composite;

/**
 * A converter to extract one channel of a {@link Composite}.
 * 
 * @author Stephan Saalfeld <saalfelds@janelia.hhmi.org>
 *
 */
public class CompositeChannelConverter< T extends Type< T >, A extends Composite< T > > implements Converter< A, T >
{
	final protected long i;
	
	public CompositeChannelConverter( final long i )
	{
		this.i = i;
	}
	
	@Override
	public void convert( A input, T output )
	{
		output.set( input.get( i ) );
	}

}