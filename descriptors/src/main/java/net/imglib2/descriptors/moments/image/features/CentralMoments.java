package net.imglib2.descriptors.moments.image.features;

import net.imglib2.descriptors.AbstractDescriptorModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.moments.image.helper.CentralMoment02;
import net.imglib2.descriptors.moments.image.helper.CentralMoment03;
import net.imglib2.descriptors.moments.image.helper.CentralMoment11;
import net.imglib2.descriptors.moments.image.helper.CentralMoment12;
import net.imglib2.descriptors.moments.image.helper.CentralMoment20;
import net.imglib2.descriptors.moments.image.helper.CentralMoment21;
import net.imglib2.descriptors.moments.image.helper.CentralMoment30;

public class CentralMoments extends AbstractDescriptorModule
{
	
	@ModuleInput
	CentralMoment02 m02;
	
	@ModuleInput
	CentralMoment03 m03;
	
	@ModuleInput
	CentralMoment11 m11;
	
	@ModuleInput
	CentralMoment12 m12;
	
	@ModuleInput
	CentralMoment20 m20;
	
	@ModuleInput
	CentralMoment21 m21;
	
	@ModuleInput
	CentralMoment30 m30;
	
	@Override
	public String name() {
		return "Central Moments";
	}

	@Override
	protected double[] recompute() 
	{
		double[] result = new double[7];
		
		result[0] = m11.value();
		result[1] = m02.value();
		result[2] = m20.value();
		result[3] = m12.value();
		result[4] = m21.value();
		result[5] = m30.value();
		result[6] = m03.value();
		
		return result;
	}

}
