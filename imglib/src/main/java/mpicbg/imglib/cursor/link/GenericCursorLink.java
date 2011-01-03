package mpicbg.imglib.cursor.link;

import mpicbg.imglib.cursor.Localizable;
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
	final public void moveTo( final Localizable localizable ) { linkedCursor.moveTo( localizable ); }

	@Override
	final public void moveTo( final int[] position ) { linkedCursor.moveTo( position ); }

	@Override
	final public void setPosition( final Localizable localizable ) { linkedCursor.setPosition( localizable ); }
	
	@Override
	final public void setPosition( final int[] position ) { linkedCursor.setPosition( position ); }

	@Override
	final public void setPosition( final int position, final int dim ) { linkedCursor.setPosition( position, dim ); }

	@Override
	final public void getPosition( final int[] position ) { linkedCursor.getPosition( position ); }

	@Override
	final public int[] getPosition() { return linkedCursor.getPosition(); }

	@Override
	final public int getPosition( final int dim ) { return linkedCursor.getPosition( dim ); }

	@Override
	final public String getPositionAsString() { return linkedCursor.getPositionAsString(); }

	@Override
	final public void fwd( final long steps ) { linkedCursor.fwd( steps ); }

	@Override
	final public void fwd() { linkedCursor.fwd(); }
}
