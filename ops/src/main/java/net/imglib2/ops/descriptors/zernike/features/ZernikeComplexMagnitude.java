package net.imglib2.ops.descriptors.zernike.features;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.zernike.Zernike;
import net.imglib2.ops.descriptors.zernike.ZernikeParameter;

public class ZernikeComplexMagnitude extends AbstractFeatureModule
{
	@ModuleInput
	Zernike zernike;
	
	@ModuleInput
	ZernikeParameter param;
	
	@Override
	public String name() 
	{
		return "Magnitude of Zernike Moment of order: " + param.getN() + " and repetition: " + param.getM();
	}

	@Override
	protected double calculateFeature() 
	{
		double[] val = zernike.get();
		return Math.sqrt( (val[0]*val[0]) + (val[1]*val[1]) );
	}

}
