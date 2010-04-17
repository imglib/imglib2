package mpicbg.imglib.cursor.link;

import mpicbg.imglib.cursor.RasterLocalizable;

final public class NullLink implements CursorLink
{	
	@Override
	final public void bck( final int dim ) {}

	@Override
	final public void fwd( final int dim ) {}

	@Override
	final public void move( final int steps, final int dim ) {}

	@Override
	final public void moveRel( final int[] position ) {}

	@Override
	final public void moveTo( final RasterLocalizable localizable ) {}

	@Override
	final public void moveTo( final int[] position ) {}

	@Override
	final public void setPosition( final RasterLocalizable localizable ) {}
	
	@Override
	final public void setPosition( final int[] position ) {}

	@Override
	final public void setPosition( final int position, final int dim ) {}

	@Override
	final public void localize( final int[] position ) {}

	@Override
	final public int[] getRasterLocation() { return null; }

	@Override
	final public int getRasterLocation( final int dim ) { return 0; }

	@Override
	final public String getLocationAsString() { return ""; }

	@Override
	final public void fwd( final long steps ) {}

	@Override
	final public void fwd() {}

	@Override
	public double[] getDoubleLocation(){ return null; }

	@Override
	public double getDoubleLocation( int dim ){ return 0; }
	
	@Override
	public float[] getFloatLocation(){ return null; }

	@Override
	public float getFloatLocation( int dim ){ return 0; }

	@Override
	public void localize( float[] location ){}

	@Override
	public void localize( double[] location ){}
}
