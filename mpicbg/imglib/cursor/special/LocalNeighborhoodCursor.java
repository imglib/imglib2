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
package mpicbg.imglib.cursor.special;

import mpicbg.imglib.cursor.AbstractCursor;
import mpicbg.imglib.cursor.PositionableCursor;
import mpicbg.imglib.cursor.array.ArrayLocalizableCursor;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.label.FakeType;

public class LocalNeighborhoodCursor<T extends Type<T>> extends AbstractCursor<T>
{
	/**
	 * Here we "misuse" a ArrayLocalizableCursor to iterate over cells,
	 * it always gives us the location of the current cell we are instantiating 
	 */
	final ArrayLocalizableCursor<FakeType> neigborhoodCursor;

	final PositionableCursor<T> cursor;
	final int[] position, tmp;
	final int numDimensions, centralPositionIndex;
	boolean isActive, debug = false;
	
	public LocalNeighborhoodCursor( final PositionableCursor<T> cursor )
	{
		super( cursor.getStorageContainer(), cursor.getImage() );
		
		numDimensions = cursor.getImage().getNumDimensions();
		
		this.cursor = cursor;
		position = new int[ numDimensions ];
		cursor.localize( position );
		
		tmp = new int[ numDimensions ];
				
		int[] dim = new int[ numDimensions ];
		for ( int d = 0; d < numDimensions; ++d )
			dim[ d ] = 3;

		neigborhoodCursor = ArrayLocalizableCursor.createLinearCursor( dim );
		isActive = true;

		for ( int d = 0; d < numDimensions; ++d )
			dim[ d ] = 1;

		centralPositionIndex = neigborhoodCursor.getStorageContainer().getPos( dim );
	}
	
	@Override
	public boolean hasNext() { return neigborhoodCursor.hasNext(); }
	
	@Override
	public void close() 
	{
		neigborhoodCursor.close();
		isActive = false;
	}

	public void update()
	{
		cursor.localize( position );
		this.neigborhoodCursor.reset();		
	}

	@Override
	public T type() { return cursor.type(); }
	
	@Override
	public void reset()
	{
		cursor.setPosition( position );
		this.neigborhoodCursor.reset();
		
		linkedIterator.reset();
	}

	@Override
	public void fwd()
	{
		neigborhoodCursor.fwd();
		
		if ( neigborhoodCursor.type().getIndex() == centralPositionIndex )
			neigborhoodCursor.fwd();
		
		neigborhoodCursor.localize( tmp );

		for ( int d = 0; d < numDimensions; ++d )
			tmp[ d ] = position[ d ] + ( tmp[d] - 1 );
		
		cursor.moveTo( tmp );
		
		linkedIterator.fwd();
	}
	
	public int getRelativePosition( final int d ) { return neigborhoodCursor.getIntPosition( d ); }
	
	@Override
	public int getArrayIndex() { return cursor.getArrayIndex(); }

	@Override
	public int getStorageIndex() { return cursor.getStorageIndex();	}

	@Override
	public boolean isActive() { return cursor.isActive() && isActive; }
}
