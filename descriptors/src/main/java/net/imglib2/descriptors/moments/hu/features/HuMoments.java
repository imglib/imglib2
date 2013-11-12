package net.imglib2.descriptors.moments.hu.features;


import net.imglib2.IterableInterval;
import net.imglib2.descriptors.AbstractDescriptorModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.geometric.centerofgravity.CenterOfGravity;
import net.imglib2.descriptors.moments.image.helper.NormalizedCentralMoment02;
import net.imglib2.descriptors.moments.image.helper.NormalizedCentralMoment03;
import net.imglib2.descriptors.moments.image.helper.NormalizedCentralMoment11;
import net.imglib2.descriptors.moments.image.helper.NormalizedCentralMoment12;
import net.imglib2.descriptors.moments.image.helper.NormalizedCentralMoment20;
import net.imglib2.descriptors.moments.image.helper.NormalizedCentralMoment21;
import net.imglib2.descriptors.moments.image.helper.NormalizedCentralMoment30;
import net.imglib2.type.numeric.RealType;

public class HuMoments extends AbstractDescriptorModule
{
	@ModuleInput
	IterableInterval< ? extends RealType< ? >> ii;

	@ModuleInput
	CenterOfGravity center;
	
	@ModuleInput
	NormalizedCentralMoment20 _n20;
	
	@ModuleInput
	NormalizedCentralMoment02 _n02;
	
	@ModuleInput
	NormalizedCentralMoment30 _n30;
	
	@ModuleInput
	NormalizedCentralMoment12 _n12;
	
	@ModuleInput
	NormalizedCentralMoment21 _n21;
	
	@ModuleInput
	NormalizedCentralMoment03 _n03;
	
	@ModuleInput
	NormalizedCentralMoment11 _n11;

	@Override
	protected double[] recompute()
	{
		double n20 = _n20.value();
		double n02 = _n02.value();
		double n30 = _n30.value();
		double n12 = _n12.value();
		double n21 = _n21.value();
		double n03 = _n03.value();
		double n11 = _n11.value();
		
		double[] result = new double[7];
		for (int i = 1; i <= 7; i++)
		{
			switch (i) {
            case 1:
                    result[0] = n20 + n02;
                    break;
            case 2:
                    result[1] = Math.pow((n20 - n02), 2) + Math.pow(2 * n11, 2);
                    break;
            case 3:
                    result[2] = Math.pow(n30 - (3 * (n12)), 2)
                                    + Math.pow((3 * n21 - n03), 2);
                    break;
            case 4:
                    result[3] = Math.pow((n30 + n12), 2) + Math.pow((n12 + n03), 2);
                    break;
            case 5:
                    result[4] = (n30 - 3 * n12) * (n30 + n12)
                                    * (Math.pow((n30 + n12), 2) - 3 * Math.pow((n21 + n03), 2))
                                    + (3 * n21 - n03) * (n21 + n03)
                                    * (3 * Math.pow((n30 + n12), 2) - Math.pow((n21 + n03), 2));
                    break;
            case 6:
                    result[5] = (n20 - n02)
                                    * (Math.pow((n30 + n12), 2) - Math.pow((n21 + n03), 2))
                                    + 4 * n11 * (n30 + n12) * (n21 + n03);
                    break;
            case 7:
                    result[6] = (3 * n21 - n03) * (n30 + n12)
                                    * (Math.pow((n30 + n12), 2) - 3 * Math.pow((n21 + n03), 2))
                                    + (n30 - 3 * n12) * (n21 + n03)
                                    * (3 * Math.pow((n30 + n12), 2) - Math.pow((n21 + n03), 2));
                    break;	
			}
		}

		return result;
		
	}

	@Override
	public String name() {
		return "Hu moments";
	}
}
