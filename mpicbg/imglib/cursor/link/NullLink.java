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
	final public int getIntPosition( final int dim ) { return 0; }

	@Override
	final public String getLocationAsString() { return ""; }

	@Override
	final public void fwd( final long steps ) {}

	@Override
	final public void fwd() {}

	@Override
	public double getDoublePosition( final int dim ){ return 0; }
	
	@Override
	public float getFloatPosition( final int dim ){ return 0; }

	@Override
	public void localize( final float[] location ){}

	@Override
	public void localize( final double[] location ){}

	@Override
	public void move( long distance, int dim ){}

	@Override
	public void moveTo( long[] position ){}

	@Override
	public void setPosition( long[] position ){}

	@Override
	public void setPosition( long position, int dim ){}

	@Override
	public long getLongPosition( int dim ){ return 0; }
	
	@Override
	public void localize( long[] location ){}
}
