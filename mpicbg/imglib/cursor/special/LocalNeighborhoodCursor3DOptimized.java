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

import mpicbg.imglib.cursor.LocalizableByDimCursor3D;
import mpicbg.imglib.type.Type;

public class LocalNeighborhoodCursor3DOptimized<T extends Type<T>> extends LocalNeighborhoodCursor3D<T>
{
	final LocalizableByDimCursor3D<T> cursor;
	int x, y, z;
	
	public LocalNeighborhoodCursor3DOptimized( final LocalizableByDimCursor3D<T> cursor )
	{
		super( cursor );
		
		this.cursor = cursor;
		this.x = cursor.getX();
		this.y = cursor.getY();
		this.z = cursor.getZ();
	}
	
	@Override
	public void reset()
	{
		if (i == 25)
			cursor.bckZ();
		else
			cursor.setPosition( x, y, z );
		
		i = -1;
	}
	
	@Override
	public void update()
	{
		this.x = cursor.getX();
		this.y = cursor.getY();
		this.z = cursor.getZ();
		
		i = -1;
	}
	
	@Override
	public boolean hasNext()
	{
		if (i < 25)
			return true;
		else 
			return false;		
	}
	
	@Override
	public void fwd()
	{
		// acces plan for neighborhood, starting at the 
		// center position (x)
		//
		// upper z plane (z-1)
		// -------------
		// | 2 | 1 | 8 |
		// |------------
		// | 3 | 0 | 7 |
		// |------------
		// | 4 | 5 | 6 |
		// -------------
		//
		// mid z plane (z=0)
		// -------------
		// | 11| 10| 9 |
		// |------------
		// | 12| x | 16|
		// |------------
		// | 13| 14| 15|
		// -------------
		//
		// lower z plane(z+1)
		// -------------
		// | 20| 19| 18|
		// |------------
		// | 21| 25| 17|
		// |------------
		// | 22| 23| 24|
		// -------------
		//
		
		switch( i )		
		{
			case -1: cursor.bckZ(); break;
			case  0: cursor.bckY(); break;
			case  1: cursor.bckX(); break;
			case  2: cursor.fwdY(); break;
			case  3: cursor.fwdY(); break;
			case  4: cursor.fwdX(); break;
			case  5: cursor.fwdX(); break;
			case  6: cursor.bckY(); break;
			case  7: cursor.bckY(); break;			
			case  8: cursor.fwdZ(); break;
			
			case  9: cursor.bckX(); break;
			case 10: cursor.bckX(); break;
			case 11: cursor.fwdY(); break;
			case 12: cursor.fwdY(); break;
			case 13: cursor.fwdX(); break;
			case 14: cursor.fwdX(); break;
			case 15: cursor.bckY(); break;
			case 16: cursor.fwdZ(); break;

			case 17: cursor.bckY(); break;
			case 18: cursor.bckX(); break;
			case 19: cursor.bckX(); break;
			case 20: cursor.fwdY(); break;
			case 21: cursor.fwdY(); break;
			case 22: cursor.fwdX(); break;
			case 23: cursor.fwdX(); break;
			case 24: cursor.bckX(); cursor.bckY(); break;
		}
		
		++i;
	}
}
