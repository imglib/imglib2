package net.imglib2.ops.operation;

public interface BinaryObjectFactory< A, B, C >
{
	C instantiate( A inputA, B inputB );
}
