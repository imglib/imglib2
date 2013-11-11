package net.imglib2.descriptors.tamura.features;

import net.imglib2.descriptors.AbstractFeatureModule;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.firstorder.Mean;
import net.imglib2.descriptors.geometric.area.Area;
import net.imglib2.descriptors.tamura.GreyValueMatrix;

/**
 * Implementation of Contrast Tamura feature done by Marko Keuschnig & Christian
 * Penz as used in jfeaturelib
 * 
 */
public class Contrast extends AbstractFeatureModule
{
	@ModuleInput
	GreyValueMatrix greyValueMatrix;

	@ModuleInput
	Mean mean;

	@ModuleInput
	Area area;

	private int[][] greyValues;

	@Override
	public String name()
	{
		return "Contrast Tamura";
	}

	@Override
	protected double calculateFeature()
	{
		greyValues = greyValueMatrix.get();

		double result = 0, sigma, my4 = 0, alpha4 = 0;

		sigma = this.calculateSigma();

		for ( int x = 0; x < greyValues.length; x++ )
		{
			for ( int y = 0; y < greyValues[ x ].length; y++ )
			{
				my4 = my4 + Math.pow( greyValues[ x ][ y ] - mean.value(), 4 );
			}
		}

		alpha4 = my4 / ( Math.pow( sigma, 4 ) );
		result = sigma / ( Math.pow( alpha4, 0.25 ) );
		return result;
	}

	private final double calculateSigma()
	{
		double result = 0;

		for ( int x = 0; x < greyValues.length; x++ )
		{
			for ( int y = 0; y < greyValues[ x ].length; y++ )
			{
				result = result + Math.pow( greyValues[ x ][ y ] - mean.value(), 2 );

			}
		}

		result = result / area.value();
		return Math.sqrt( result );
	}

}
