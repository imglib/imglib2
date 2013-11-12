package net.imglib2.descriptors.moments.image.helper;

import net.imglib2.IterableInterval;
import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.type.numeric.RealType;

public class NormalizedCentralMoment30 extends AbstractFeatureModule
{
	@ModuleInput
	IterableInterval< ? extends RealType< ? >> ii;
	
	@ModuleInput
	CentralMoment30 m30;

	@Override
	public String name() {
		return "Normalized central moment p = 3 and q = 0";
	}

	@Override
	protected double calculateFeature() {
		int p = 3; int q = 0;
		double norm = Math.pow(ii.size(), (p + q + 2) / 2);
		return m30.value()/norm;
	}
}
