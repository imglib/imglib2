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
 * @author Rick Lentz
 */
package mpicbg.imglib.container.basictypecontainer.array;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;

import mpicbg.imglib.container.basictypecontainer.DoubleAccess;

public class NIODoubleArray implements ArrayDataAccess<NIODoubleArray>, DoubleAccess
{
	protected DoubleBuffer data;

	public NIODoubleArray( final int numEntities )
	{
		this.data = ByteBuffer.allocateDirect( numEntities * 8 ).order( ByteOrder.nativeOrder() ).asDoubleBuffer();
	}
    		
	public NIODoubleArray( final double[] data )
	{
		DoubleBuffer bufferIn = DoubleBuffer.wrap( data );
		DoubleBuffer copy = ByteBuffer.allocateDirect( bufferIn.capacity() ).order( ByteOrder.nativeOrder() ).asDoubleBuffer();
		this.data = copy.put( bufferIn );
	}

	@Override
	public void close() { data = null; }

	@Override
	public double getValue( final int index )
	{
		return data.get( index );
	}

	@Override
	public void setValue( final int index, final double value )
	{
		data.put(index, value);		
	}
	
	@Override
	public double[] getCurrentStorageArray()
	{
		double[] outData = new double[ data.capacity() ];
		data.get( outData );
		return outData;
	}
	
	@Override
	public NIODoubleArray createArray( final int numEntities ) { return new NIODoubleArray( numEntities ); }
	
}
