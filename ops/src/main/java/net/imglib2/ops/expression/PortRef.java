package net.imglib2.ops.expression;

import net.imglib2.Sampler;

public class PortRef<T > implements Sampler< T >, Port< T >
{
	protected Port< T > port;

	public PortRef()
	{
		port = null;
	}

	public void assign( final Port< T > port )
	{
		this.port = port;
	}

	@Override
	public void set( final Sampler< T > sampler )
	{
		port.set( sampler );
	}

	@Override
	public void setConst( final T t )
	{
		port.setConst( t );
	}

	@Override
	public T get()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Sampler< T > copy()
	{
		throw new UnsupportedOperationException();
	}
}
