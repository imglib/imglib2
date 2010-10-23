package mpicbg.imglib.cursor.link;

import mpicbg.imglib.cursor.Localizable;

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
	final public void moveTo( final Localizable localizable ) {}

	@Override
	final public void moveTo( final int[] position ) {}

	@Override
	final public void setPosition( final Localizable localizable ) {}
	
	@Override
	final public void setPosition( final int[] position ) {}

	@Override
	final public void setPosition( final int position, final int dim ) {}

	@Override
	final public void getPosition( final int[] position ) {}

	@Override
	final public int[] getPosition() { return null; }

	@Override
	final public int getPosition( final int dim ) { return 0; }

	@Override
	final public String getPositionAsString() { return ""; }

	@Override
	final public void fwd( final long steps ) {}

	@Override
	final public void fwd() {}
}
