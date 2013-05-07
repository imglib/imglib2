package net.imglib2.ops.expression;

import net.imglib2.Sampler;

public interface Port< T >
{
	public void set( Sampler< T > sampler );

	public void setConst( T t );
}