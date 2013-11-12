package net.imglib2.descriptors.moments.image.helper;

import net.imglib2.IterableInterval;
import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.type.numeric.RealType;

public class NormalizedCentralMoment12 extends AbstractFeatureModule
{
	@ModuleInput
	IterableInterval< ? extends RealType< ? >> ii;
	
	@ModuleInput
	CentralMoment12 m12;

	@Override
	public String name() {
		return "Normalized central moment p = 1 and q = 2";
	}

	@Override
	protected double calculateFeature() {
		int p = 1; int q = 2;
		double norm = Math.pow(ii.size(), (p + q + 2) / 2);
		return m12.value()/norm;
	}
}
