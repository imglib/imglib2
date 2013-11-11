package net.imglib2.descriptors.geometric;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.geometric.area.Area;
import net.imglib2.descriptors.geometric.perimeter.Perimeter;

public class Circularity extends AbstractFeatureModule
{
	@ModuleInput
	private Perimeter perimeter;

	@ModuleInput
	private Area area;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Circularity";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double calculateFeature()
	{

		// circularity = 4pi(area/perimeter^2)
		// A circularity value of 1.0 indicates a perfect circle.
		// As the value approaches 0.0, it indicates an increasingly elongated
		// polygon.
		// http://rsbweb.nih.gov/ij/plugins/circularity.html
		return 4 * Math.PI * ( area.value() / Math.pow( perimeter.value(), 2 ) );
	}

}
