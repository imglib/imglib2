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
package mpicbg.imglib.cursor.vector;

public class Vector4d extends AbstractVector<Vector4d>
{
	int a, b, c, d;
	
	public Vector4d()
	{
		super( 4 );
		a = b = c = d = 0;
	}
	
	public int getPosition( final int dimension ) 
	{ 
		if ( dimension == 0 )
			return a;
		else if ( dimension == 1 )
			return b;
		else if ( dimension == 2 )
			return c;
		else
			return d;
	}
	
	public void setPosition( final int dimension, final int value ) 
	{ 
		if ( dimension == 0 )
			a = value;
		else if ( dimension == 1 )
			b = value;
		else if ( dimension == 2 )
			c = value;
		else
			d = value;
	}
	
	public void add( final Vector4d vector2 )
	{
		a += vector2.a;
		b += vector2.b;
		c += vector2.c;
		d += vector2.d;
	}

	public void add( final int value )
	{
		a += value;
		b += value;
		c += value;
		d += value;
	}

	public void sub( final Vector4d vector2 )
	{
		a -= vector2.a;
		b -= vector2.b;
		c -= vector2.c;
		d -= vector2.d;
	}

	public void sub( final int value )
	{
		a -= value;
		b -= value;
		c -= value;
		d -= value;
	}

}
