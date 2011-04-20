package net.imglib2.algorithm.gauss2;

import net.imglib2.RandomAccess;
import net.imglib2.type.Type;

public class WritableLineIterator< T extends Type< T > > extends AbstractWritableLineIterator< T >
{
	final RandomAccess< T > randomAccess;
	
	public WritableLineIterator( final int dim, final long size, final RandomAccess< T > randomAccess )
	{
		super( dim, size, randomAccess );

		this.randomAccess = randomAccess;
	}

	@Override
	public void set( final T type )
	{
		randomAccess.get().set( type );
	}

}
