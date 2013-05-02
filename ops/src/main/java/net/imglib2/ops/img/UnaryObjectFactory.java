package net.imglib2.ops.img;

public interface UnaryObjectFactory< A, B >
{
	B instantiate( A a );

}
