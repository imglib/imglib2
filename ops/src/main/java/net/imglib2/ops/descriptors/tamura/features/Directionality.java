package net.imglib2.ops.descriptors.tamura.features;

import net.imglib2.ops.descriptors.AbstractFeatureModule;

public class Directionality extends AbstractFeatureModule
{

	@Override
	public String name() {
		return "Directionality";
	}

	@Override
	protected double calculateFeature() {
		return 0;
	}

}
