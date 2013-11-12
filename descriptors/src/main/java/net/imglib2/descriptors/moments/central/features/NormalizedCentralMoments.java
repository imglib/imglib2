package net.imglib2.descriptors.moments.central.features;

import net.imglib2.IterableInterval;
import net.imglib2.descriptors.AbstractDescriptorModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.geometric.centerofgravity.CenterOfGravity;
import net.imglib2.descriptors.moments.central.helper.NormalizedCentralMoment02;
import net.imglib2.descriptors.moments.central.helper.NormalizedCentralMoment03;
import net.imglib2.descriptors.moments.central.helper.NormalizedCentralMoment11;
import net.imglib2.descriptors.moments.central.helper.NormalizedCentralMoment12;
import net.imglib2.descriptors.moments.central.helper.NormalizedCentralMoment20;
import net.imglib2.descriptors.moments.central.helper.NormalizedCentralMoment21;
import net.imglib2.descriptors.moments.central.helper.NormalizedCentralMoment30;
import net.imglib2.type.numeric.RealType;

public class NormalizedCentralMoments extends AbstractDescriptorModule
{
	@ModuleInput
	IterableInterval< ? extends RealType< ? >> ii;

	@ModuleInput
	CenterOfGravity center;
	
	@ModuleInput
	NormalizedCentralMoment20 n20;
	
	@ModuleInput
	NormalizedCentralMoment02 n02;
	
	@ModuleInput
	NormalizedCentralMoment30 n30;
	
	@ModuleInput
	NormalizedCentralMoment12 n12;
	
	@ModuleInput
	NormalizedCentralMoment21 n21;
	
	@ModuleInput
	NormalizedCentralMoment03 n03;
	
	@ModuleInput
	NormalizedCentralMoment11 n11;

	@Override
	protected double[] recompute()
	{
		double[] result = new double[7];
		
		result[0] = n11.value();
		result[1] = n02.value();
		result[2] = n20.value();
		result[3] = n12.value();
		result[4] = n21.value();
		result[5] = n30.value();
		result[6] = n03.value();
		
		return result;
		
	}

	@Override
	public String name() {
		return "Normalized central moments";
	}
}
