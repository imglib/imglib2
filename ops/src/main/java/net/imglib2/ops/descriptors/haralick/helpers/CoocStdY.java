package net.imglib2.ops.descriptors.haralick.helpers;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;

public class CoocStdY extends AbstractFeatureModule
{
	// for symmetric cooccurence matrices stdx = stdy
	@ModuleInput
	private CoocStdX coocStdX = new CoocStdX();

	@Override
	public String name()
	{
		return "CoocStdX";
	}

	@Override
	protected double calculateFeature()
	{
		return coocStdX.value();
	}

}
