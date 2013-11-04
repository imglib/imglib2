package net.imglib2.ops.features.geometric;

import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.ModuleInput;
import net.imglib2.ops.features.geometric.area.Area;
import net.imglib2.ops.features.geometric.perimeter.Perimeter;
import net.imglib2.type.numeric.real.DoubleType;

public class Circularity extends AbstractFeature
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
	public Circularity copy()
	{
		return new Circularity();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DoubleType compute()
	{

		// circularity = 4pi(area/perimeter^2)
		// A circularity value of 1.0 indicates a perfect circle.
		// As the value approaches 0.0, it indicates an increasingly elongated
		// polygon.
		// http://rsbweb.nih.gov/ij/plugins/circularity.html
		return new DoubleType( 4 * Math.PI * ( area.get().get() / Math.pow( perimeter.get().get(), 2 ) ) );
	}

}
