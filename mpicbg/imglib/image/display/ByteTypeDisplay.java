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
package mpicbg.imglib.image.display;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.integer.ByteType;

public class ByteTypeDisplay extends Display<ByteType>
{
	public ByteTypeDisplay( final Image<ByteType> img)
	{
		super(img);
		this.min = 0;
		this.max = 255;
	}	

	@Override
	public void setMinMax()
	{
		final Cursor<ByteType> c = img.createCursor();
		final ByteType t = c.getType();

		
		if ( !c.hasNext() )
		{
			min = 0;
			max = 255;
			return;
		}
		
		c.fwd();
		min = max = t.get() & 0xff;

		while ( c.hasNext() )
		{
			c.fwd();
			
			final int value = t.get() & 0xff;
			
			if ( value > max )
				max = value;			
			else if ( value < min )
				min = value;
		}
		
		c.close();
	}
	
	@Override
	public float get32Bit( ByteType c ) { return (c.get() & 0xff); }
	@Override
	public float get32BitNormed( ByteType c ) { return normFloat( c.get() & 0xff ); }
	
	@Override
	public byte get8BitSigned( final ByteType c ) { return c.get(); }
	@Override
	public short get8BitUnsigned( final ByteType c ) { return (short)( c.get() & 0xff ); }
}

