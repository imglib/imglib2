package mpicbg.imglib.algorithm.gauss;

import mpicbg.imglib.algorithm.gauss.DifferenceOfGaussian.SpecialPoint;
import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.cursor.Localizable;

public class DifferenceOfGaussianPeak implements Localizable
{
	SpecialPoint specialPoint;
	final protected int[] pixelLocation;
	protected double value;
	
	public DifferenceOfGaussianPeak( final int[] pixelLocation, final double value, final SpecialPoint specialPoint )
	{
		this.specialPoint = specialPoint;
		this.pixelLocation = pixelLocation.clone();
		this.value = value;
	}
	
	public boolean isMin() { return specialPoint == SpecialPoint.MIN; }
	public boolean isMax() { return specialPoint == SpecialPoint.MAX; }
	public SpecialPoint getPeakType() { return specialPoint; }
	public double getValue() { return value; }
	
	public void setPeakType( final SpecialPoint specialPoint ) { this.specialPoint = specialPoint; }
	public void setPixelLocation( final int location, final int dim ) { pixelLocation[ dim ] = location; }
	public void setPixelLocation( final int[] pixelLocation )
	{
		for ( int d = 0; d < pixelLocation.length; ++d )
			this.pixelLocation[ d ] = pixelLocation[ d ];
	}
	public void setValue( final double value ) { this.value = value; }

	@Override
	public void getPosition( final int[] position )
	{
		for ( int d = 0; d < pixelLocation.length; ++d )
			position[ d ] = pixelLocation[ d ];
	}

	@Override
	public int[] getPosition() { return pixelLocation.clone(); }

	@Override
	public int getPosition( final int dim ) { return pixelLocation[ dim ]; }

	@Override
	public String getPositionAsString() { return MathLib.printCoordinates( pixelLocation );	}

	@Override
	public void fwd( final long steps ) {}

	@Override
	public void fwd() {}
}
