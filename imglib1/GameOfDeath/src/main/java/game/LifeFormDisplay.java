/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package game;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.Display;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class LifeFormDisplay extends Display<LifeForm>
{
	public LifeFormDisplay( final Image<LifeForm> img)
	{
		super(img);
		this.min = 0;
		this.max = 1;
	}	
	
	@Override
	public void setMinMax()
	{
		final Cursor<LifeForm> c = img.createCursor();
		
		if ( !c.hasNext() )
		{
			min = -Float.MAX_VALUE;
			max = Float.MAX_VALUE;
			return;
		}
		
		c.fwd();
		min = max = c.getType().getWeight();

		while ( c.hasNext() )
		{
			c.fwd();

			final double value = c.getType().getWeight();
			
			if ( value > max )
				max = value;			
			else if ( value < min )
				min = value;
		}
		
		c.close();
	}
	
	@Override
	public float get32Bit( LifeForm c ) { return c.getWeight(); }
	@Override
	public float get32BitNormed( LifeForm c ) { return normFloat( c.getWeight() ); }
	
	@Override
	public byte get8BitSigned( final LifeForm c ) { return (byte)Math.round( normFloat( c.getWeight() ) * 255 ); }
	@Override
	public short get8BitUnsigned( final LifeForm c ) 
	{
		return (short)Math.round( normFloat( c.getWeight() ) * 255 ); 
	}		
	
	@Override
	public int get8BitARGB( final LifeForm c ) 
	{ 
		final int col = get8BitUnsigned( c );
		
		if( c.getName() == 0 )
			return (col<<16);
		else if ( c.getName() == 1 )
			return (col<<8);
		else
			return col;
	}	
}
