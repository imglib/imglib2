package net.imglib2.ops.descriptors.moments.zernike.features;

import net.imglib2.ops.descriptors.AbstractDescriptorModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.moments.zernike.ZernikeMomentComputer;
import net.imglib2.ops.descriptors.moments.zernike.helper.ZernikeParameter;

public class ZernikeMagnitude extends AbstractDescriptorModule
{
	@ModuleInput
	ZernikeParameter param;
	
	@ModuleInput
	ZernikeMomentComputer zernike;
	
	@Override
	public String name() 
	{
		return "Magnitude of Zernike Moment up to order " + param.getOrder();
	}

	@Override
	protected double[] recompute() 
	{
		
		double[] val = zernike.get();
		
		if (val.length % 2 != 0)
		{
			throw new IllegalArgumentException("Number of zernike features must be even!");
		}
		
		double[] result = new double[val.length/2];
		
		int j = 0;
		for (int i = 0; i < val.length; i+=2)
		{
			result[j] = Math.sqrt( (val[i]*val[i]) + (val[i+1]*val[i+1]) );
			j++;
		}
		
		return result;
	}
}
