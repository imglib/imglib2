/**
 * Copyright (c) 2009--2010, Cardona, Preibisch & Saalfeld
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
 */
package mpicbg.imglib.sampler.shapelist;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.basictypecontainer.FakeAccess;
import mpicbg.imglib.container.basictypecontainer.array.FakeArray;
import mpicbg.imglib.container.shapelist.ShapeListCached;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.label.FakeType;

/**
 * 
 * @param <T>
 *
 * @author Cardona, Preibisch and Saalfeld
 */
public abstract class ShapeListCache< T extends Type< T > >
{
	final protected Array< FakeType, FakeAccess > fakeArray;
	final protected ShapeListCached< T > container;
	final protected int cacheSize;

	public ShapeListCache( final int cacheSize, final ShapeListCached< T > container )
	{		
		this.container = container;
		this.cacheSize = cacheSize;
		
		fakeArray = new Array< FakeType, FakeAccess >( null, new FakeArray(), container.getDimensions(), 1 );
	}
	
	public abstract T lookUp( final int[] position );	
	public abstract ShapeListCache< T > createInstance();
}
