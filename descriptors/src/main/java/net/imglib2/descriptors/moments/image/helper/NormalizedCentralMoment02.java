package net.imglib2.descriptors.moments.image.helper;

import net.imglib2.IterableInterval;
import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.type.numeric.RealType;

public class NormalizedCentralMoment02 extends AbstractFeatureModule
{
	@ModuleInput
	CentralMoment02 m02;
	
	@ModuleInput
	IterableInterval< ? extends RealType< ? >> ii;

	@Override
	public String name() {
		return "Normalized central moment p = 0 and q = 2";
	}

	@Override
	protected double calculateFeature() {
		int p = 0; int q = 2;
		double norm = Math.pow(ii.size(), (p + q + 2) / 2);
		return m02.value()/norm;
	}
}
