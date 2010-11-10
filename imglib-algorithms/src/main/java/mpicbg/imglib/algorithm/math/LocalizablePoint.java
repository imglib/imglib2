package mpicbg.imglib.algorithm.math;

import mpicbg.imglib.cursor.Localizable;
import mpicbg.imglib.util.Util;

public class LocalizablePoint implements Localizable 
{
	final int[] position;
	final int numDimensions;
	
	public LocalizablePoint ( final int[] position )
	{
		this.position = position;
		this.numDimensions = position.length;
	}

	public LocalizablePoint ( final float[] position )
	{
		this( position.length );
		
		for ( int d = 0; d < numDimensions; ++d )
			this.position[ d ] = Util.round( position[ d ] );
	}

	public LocalizablePoint ( final int numDimensions )
	{
		this.numDimensions = numDimensions;
		this.position = new int[ numDimensions ];
	}
	
	@Override
	public void fwd(long steps) {}

	@Override
	public void fwd() {}

	@Override
	public void getPosition( final int[] position ) 
	{
		for ( int d = 0; d < numDimensions; ++d )
			position[ d ] = this.position[ d ];
	}

	@Override
	public int[] getPosition() { return position; }

	@Override
	public int getPosition( final int dim ) { return position[ dim ]; }

	@Override
	public String getPositionAsString() { return Util.printCoordinates( position ); }
}
