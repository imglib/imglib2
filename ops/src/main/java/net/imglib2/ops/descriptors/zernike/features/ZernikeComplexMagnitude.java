package net.imglib2.ops.descriptors.zernike.features;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.zernike.Zernike;

public class ZernikeComplexMagnitude extends AbstractFeatureModule
{
	@ModuleInput
	Zernike zernike;
	
	@Override
	public String name() 
	{
		return "Zernike Moment Magnitude";
	}

	@Override
	protected double calculateFeature() 
	{
		double[] val = zernike.get();
		return Math.sqrt( (val[0]*val[0]) + (val[1]*val[1]) );
	}

}
