package mpicbg.imglib.cursor.link;

import mpicbg.imglib.cursor.RasterLocalizable;
import mpicbg.imglib.cursor.PositionableCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

final public class GenericCursorLink< T extends Type<T> > implements CursorLink
{
	final PositionableCursor<T> linkedCursor;
	
	public GenericCursorLink( final PositionableCursor<T> linkedCursor ) { this.linkedCursor = linkedCursor; }
	
	public GenericCursorLink( final Image<T> img ) { this.linkedCursor = img.createPositionableCursor(); }
	
	public LocalizableCursor<T> getLinkedCursor() { return linkedCursor; }
	
	@Override
	final public void fwd() { linkedCursor.fwd(); }

	@Override
	final public void fwd( final int dim ) { linkedCursor.fwd( dim ); }

	@Override
	final public void fwd( final long steps ) { linkedCursor.fwd( steps ); }

	@Override
	final public void bck( final int dim ) { linkedCursor.bck( dim ); }

	@Override
	final public void move( final int steps, final int dim ) { linkedCursor.move( steps, dim); }

	@Override
	final public void move( final long steps, final int dim ) { linkedCursor.move( steps, dim); }
	
	@Override
	final public void moveTo( final RasterLocalizable localizable ) { linkedCursor.moveTo( localizable ); }

	@Override
	final public void moveTo( final int[] position ) { linkedCursor.moveTo( position ); }

	@Override
	final public void moveTo( final long[] position ) { linkedCursor.moveTo( position ); }

	@Override
	final public void setPosition( final RasterLocalizable localizable ) { linkedCursor.setPosition( localizable ); }
	
	@Override
	final public void setPosition( final int[] position ) { linkedCursor.setPosition( position ); }

	@Override
	final public void setPosition( final long[] position ) { linkedCursor.setPosition( position ); }

	@Override
	final public void setPosition( final int position, final int dim ) { linkedCursor.setPosition( position, dim ); }

	@Override
	final public void setPosition( final long position, final int dim ) { linkedCursor.setPosition( position, dim ); }

	@Override
	final public void localize( final float[] location ){ linkedCursor.localize( location ); }

	@Override
	final public void localize( final double[] location ){ linkedCursor.localize( location ); }
	
	@Override
	final public void localize( final int[] position ) { linkedCursor.localize( position ); }

	@Override
	final public void localize( final long[] position ) { linkedCursor.localize( position ); }

	@Override
	final public float getFloatPosition( final int dim ){ return linkedCursor.getFloatPosition( dim ); }

	@Override
	public double getDoublePosition( final int dim ){ return linkedCursor.getDoublePosition( dim ); }

	@Override
	final public int getIntPosition( final int dim ) { return linkedCursor.getIntPosition( dim ); }

	@Override
	final public long getLongPosition( final int dim ) { return linkedCursor.getLongPosition( dim ); }

	@Override
	final public String getLocationAsString() { return linkedCursor.getLocationAsString(); }
}
