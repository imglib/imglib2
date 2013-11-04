package net.imglib2.ops.descriptors.haralick.features;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.haralick.helpers.CoocStdX;

public class Variance extends AbstractFeatureModule
{
	@ModuleInput
	private CoocStdX coocStdX;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Variance";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double calculateFeature()
	{
		return coocStdX.value() * coocStdX.value();
	}

}
