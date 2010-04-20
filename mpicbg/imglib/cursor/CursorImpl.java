/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.cursor;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.location.Iterator;
import mpicbg.imglib.location.VoidIterator;
import mpicbg.imglib.type.Type;

/**
 * We use the class {@link CursorImpl} instead of implementing methods here so that other classes can
 * only implement {@link Cursor} and extend other classes instead. As each {@link CursorImpl} is also
 * a {@link Cursor} there are no disadvantages for the {@link Cursor} implementations.
 * 
 * @author Stephan
 *
 * @param <T>
 */
public abstract class CursorImpl<T extends Type<T>> implements Cursor<T>
{
	final protected Image<T> image;
	final protected Container<T> container;
	protected boolean isClosed = false;
	
	protected Iterator< ? > linkedIterator = VoidIterator.getInstance();
	
	public CursorImpl( final Container<T> container, final Image<T> image )
	{
		this.image = image;
		this.container = container;
	}

	@Override
	/* TODO Cursors are Iterable?  Shouldn't the Image/Containers be Iterable? */
	public Iterator< T > iterator() 
	{
		reset();
		return this;
	}
	
	@Override
	@Deprecated
	final public T getType(){ return type(); } 
	
	@Override
	public int getArrayIndex() { return type().getIndex(); }
	@Override
	public Image<T> getImage() { return image; }
	@Override
	public Container<T> getStorageContainer() { return container; }
	@Override
	public boolean isActive() { return !isClosed; }
	
	@Override
	public void remove() {}
	
	@Override
	public T next(){ fwd(); return type(); }

	@Override
	public void fwd( final long steps )
	{ 
		for ( long j = 0; j < steps; ++j )
			fwd();
	}

	@Override
	public int[] createPositionArray() { return new int[ image.getNumDimensions() ]; }	
	
	@Override
	public int getNumDimensions() { return image.getNumDimensions(); }
	
	@Override
	public int[] getDimensions() { return image.getDimensions(); }
	
	@Override
	public void getDimensions( int[] position ) { image.getDimensions( position ); }
	
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
