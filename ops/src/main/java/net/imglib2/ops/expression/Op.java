package net.imglib2.ops.expression;

import net.imglib2.Sampler;

public interface Op< O > extends Sampler< O >
{
	public Port< O > output();
}