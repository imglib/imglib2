package net.imglib2.descriptors.moments.image.helper;

import net.imglib2.IterableInterval;
import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.type.numeric.RealType;

public class NormalizedCentralMoment11 extends AbstractFeatureModule
{
	@ModuleInput
	IterableInterval< ? extends RealType< ? >> ii;
	
	@ModuleInput
	CentralMoment11 m11;

	@Override
	public String name() {
		return "Normalized central moment p = 1 and q = 1";
	}

	@Override
	protected double calculateFeature() {
		int p = 1; int q = 1;
		double norm = Math.pow(ii.size(), (p + q + 2) / 2);
		return m11.value()/norm;
	}
}
