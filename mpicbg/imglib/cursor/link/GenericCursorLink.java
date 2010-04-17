package mpicbg.imglib.cursor.link;

import mpicbg.imglib.cursor.RasterLocalizable;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

final public class GenericCursorLink< T extends Type<T> > implements CursorLink
{
	final LocalizableByDimCursor<T> linkedCursor;
	
	public GenericCursorLink( final LocalizableByDimCursor<T> linkedCursor ) { this.linkedCursor = linkedCursor; }
	
	public GenericCursorLink( final Image<T> img ) { this.linkedCursor = img.createLocalizableByDimCursor(); }
	
	public LocalizableCursor<T> getLinkedCursor() { return linkedCursor; }
	
	@Override
	final public void bck( final int dim ) { linkedCursor.bck( dim ); }

	@Override
	final public void fwd( final int dim ) { linkedCursor.fwd( dim ); }

	@Override
	final public void move( final int steps, final int dim ) { linkedCursor.move( steps, dim); }

	@Override
	final public void moveRel( final int[] position ) { linkedCursor.moveRel( position ); }

	@Override
	final public void moveTo( final RasterLocalizable localizable ) { linkedCursor.moveTo( localizable ); }

	@Override
	final public void moveTo( final int[] position ) { linkedCursor.moveTo( position ); }

	@Override
	final public void setPosition( final RasterLocalizable localizable ) { linkedCursor.setPosition( localizable ); }
	
	@Override
	final public void setPosition( final int[] position ) { linkedCursor.setPosition( position ); }

	@Override
	final public void setPosition( final int position, final int dim ) { linkedCursor.setPosition( position, dim ); }

	@Override
	final public void localize( final int[] position ) { linkedCursor.localize( position ); }

	@Override
	final public int[] getRasterLocation() { return linkedCursor.getRasterLocation(); }

	@Override
	final public int getRasterLocation( final int dim ) { return linkedCursor.getRasterLocation( dim ); }

	@Override
	final public String getLocationAsString() { return linkedCursor.getLocationAsString(); }

	@Override
	final public void fwd( final long steps ) { linkedCursor.fwd( steps ); }

	@Override
	final public void fwd() { linkedCursor.fwd(); }

	@Override
	public double[] getDoubleLocation(){ return linkedCursor.getDoubleLocation(); }

	@Override
	public double getDoubleLocation( int dim ){ return linkedCursor.getDoubleLocation( dim ); }

	@Override
	public float[] getFloatLocation(){ return linkedCursor.getFloatLocation(); }

	@Override
	public float getFloatLocation( int dim ){ return linkedCursor.getFloatLocation( dim ); }

	@Override
	public void localize( float[] location ){ linkedCursor.localize( location ); }

	@Override
	public void localize( double[] location ){ linkedCursor.localize( location ); }
}
