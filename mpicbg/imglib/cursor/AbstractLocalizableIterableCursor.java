package mpicbg.imglib.cursor;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.location.Iterator;
import mpicbg.imglib.location.VoidIterator;
import mpicbg.imglib.type.Type;

public abstract class AbstractLocalizableIterableCursor<T extends Type<T>> extends AbstractLocalizableCursor<T> implements LocalizableIterableCursor<T>
{
	protected Iterator< ? > linkedIterator = VoidIterator.getInstance();

	public AbstractLocalizableIterableCursor( final Container<T> container, final Image<T> image )
	{
		super( container, image );
	}
	
	@Override
	public void remove() {}
	
	@Override
	public T next(){ fwd(); return type(); }

	@Override
	public void jumpFwd( final long steps )
	{ 
		for ( long j = 0; j < steps; ++j )
			fwd();
	}
	
	@Override
	public boolean hasNextLinked(){ return hasNext() && linkedIterator.hasNext(); }
	
	@Override
	final public void linkIterator( final Iterator< ? > iterable ){ linkedIterator = iterable; }
	
	@Override
	final public Iterator< ? > unlinkIterator()
	{
		final Iterator< ? > iterable = linkedIterator;
		linkedIterator = VoidIterator.getInstance();
		return iterable;
	}
}
