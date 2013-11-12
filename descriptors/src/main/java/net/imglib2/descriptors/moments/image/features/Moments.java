package net.imglib2.descriptors.moments.image.features;

import net.imglib2.descriptors.AbstractDescriptorModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.moments.image.helper.Moment00;
import net.imglib2.descriptors.moments.image.helper.Moment01;
import net.imglib2.descriptors.moments.image.helper.Moment10;
import net.imglib2.descriptors.moments.image.helper.Moment11;

public class Moments extends AbstractDescriptorModule
{
	
	@ModuleInput
	Moment00 m00;
	
	@ModuleInput
	Moment01 m01;
	
	@ModuleInput
	Moment10 m10;
	
	@ModuleInput
	Moment11 m11;
	
	@Override
	public String name() {
		return "Image moments";
	}

	@Override
	protected double[] recompute() {
		double[] result = new double[4];
		
		result[0] = m00.value();
		result[1] = m10.value();
		result[2] = m01.value();
		result[3] = m11.value();
		
		return result;
	}

}
