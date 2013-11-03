package net.imglib2.ops.features.firstorder;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredInput;
import net.imglib2.ops.features.firstorder.sums.Sum;
import net.imglib2.ops.features.geometric.area.Area;
import net.imglib2.type.numeric.real.DoubleType;

public class Mean extends AbstractFeature
{
	@RequiredInput
	Sum sum;

	@RequiredInput
	Area area;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Mean";
	}

	@Override
	public DoubleType recompute()
	{
		return new DoubleType( sum.get().get() / area.get().get() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Mean copy()
	{
		return new Mean();
	}
}
