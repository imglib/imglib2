package net.imglib2.ops.descriptors.tamura.features;

import net.imglib2.ops.descriptors.AbstractFeatureModule;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.firstorder.Mean;

public class Contrast extends AbstractFeatureModule {
	
	@ModuleInput
	Mean mean;

	@Override
	public String name() {
		return "Contrast";
	}

	@Override
	protected double calculateFeature() {
		return 0;
	}

}
