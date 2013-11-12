package net.imglib2.descriptors.moments.image.helper;

import net.imglib2.IterableInterval;
import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.type.numeric.RealType;

public class NormalizedCentralMoment03 extends AbstractFeatureModule
{
	@ModuleInput
	IterableInterval< ? extends RealType< ? >> ii;
	
	@ModuleInput
	CentralMoment03 m03;

	@Override
	public String name() {
		return "Normalized central moment p = 0 and q = 3";
	}

	@Override
	protected double calculateFeature() {
		int p = 0; int q = 3;
		double norm = Math.pow(ii.size(), (p + q + 2) / 2);
		return m03.value()/norm;
	}
}
