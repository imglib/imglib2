package net.imglib2.descriptors.haralick.helpers;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;

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
