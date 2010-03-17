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
package mpicbg.imglib.container.cube;

import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.basictypecontainer.FloatContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.type.Type;

public class FloatCube<T extends Type<T>> extends Cube<FloatCubeElement<T>, FloatCube<T>,T> implements FloatContainer<T>
{
	float[] cache = null;
	
	public FloatCube(ContainerFactory factory, int[] dim, int[] cubeSize, int entitiesPerPixel)
	{
		super(factory, dim, cubeSize, entitiesPerPixel);
	}
	
	@Override
	public FloatCubeElement<T> createCubeElementInstance( final int cubeId, final int[] dim, final int offset[], final int entitiesPerPixel )
	{
		return new FloatCubeElement<T>( this, cubeId, dim, offset, entitiesPerPixel );
	}

	@Override
	public void close() 
	{
		super.close();
		cache = null; 
	}
	
	@Override
	public float getValue( final int index )  { return cache[ index ]; }

	@Override
	public void setValue( final int index, final float value ) { cache[ index ] = value; }
	
	@Override
	public void update( final Cursor<?> c ) { cache = data.get( c.getStorageIndex() ).data;	}

	public float[] getCurrentStorageArray(Cursor<?> c) { return data.get( c.getStorageIndex() ).getCurrentStorageArray( c ); }
}
