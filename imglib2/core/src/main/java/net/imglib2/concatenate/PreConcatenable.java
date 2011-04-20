package net.imglib2.concatenate;

public interface PreConcatenable< A >
{
	public Concatenable< A > preConcatenate( A a );

	public Class< A > getPreConcatenableClass();
}
