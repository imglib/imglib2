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
package mpicbg.imglib.outside;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.type.Type;

public abstract class OutsideStrategy<T extends Type<T>>
{
	final Cursor<T> parentCursor;
	
	public OutsideStrategy( final Cursor<T> parentCursor )
	{
		this.parentCursor = parentCursor;
	}
	
	/*
	 * Returns a link to the parent Cursor of this Strategy
	 */
	public Cursor<T> getParentCursor() { return parentCursor; }

	/*
	 * This method is fired by the parent cursor in the case that it moves while being outside the image
	 */
	public abstract void notifyOutside();

	/*
	 * This method is fired by the parent cursor in the case that it moves while being outside the image
	 */
	public abstract void notifyOutside( int steps, int dim );

	/*
	 * This method is fired by the parent cursor in the case that it moves while being outside the image
	 */
	public abstract void notifyOutsideFwd( int dim );

	/*
	 * This method is fired by the parent cursor in the case that it moves while being outside the image
	 */
	public abstract void notifyOutsideBck( int dim );
	
	/*
	 * This method is fired by the parent cursor in the case that it leaves the image
	 */
	public abstract void initOutside();
	
	/*
	 * Returns the Type that stores the current value of the Outside Strategy
	 */
	public abstract T getType();
	
	/*
	 * Closed possibly created cursors or images
	 */
	public abstract void close();
}
