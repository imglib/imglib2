package net.imglib2.ops.descriptors.haralick.helpers;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;

public class CoocMeanY extends AbstractFeatureModule
{
	// for symmetric cooccurence matrices stdx = stdy
	@ModuleInput
	private CoocMeanX coocMeanX;

	@Override
	public String name()
	{
		return "CoocMeanY";
	}

	@Override
	protected double calculateFeature()
	{
		return coocMeanX.value();
	}

}
