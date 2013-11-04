package net.imglib2.ops.features;

public interface Module< O >
{

	O get();

	boolean isEquivalentModule( Module< ? > output );

	boolean isCompatibleOutput( Class< ? > annotatedType );

	double priority();

	void markDirty();

	boolean isDirty();
}
