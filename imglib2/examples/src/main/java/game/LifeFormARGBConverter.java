/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package game;

import net.imglib2.converter.Converter;
import net.imglib2.display.AbstractLinearRange;
import net.imglib2.type.numeric.ARGBType;

/**
 * The LifeFormARGBConverter creates an ARGB representation of a LifeForm by implementing the interface 
 * Converter< LifeForm, ARGBType >. Currently, up to seven different races can be mapped into seven different
 * colors for display. Note that the simulation itself supports an arbitrary number of Life Forms. The weight 
 * of a LifeForm will represent its intensity by mapping its float value to a value between 0...255, based on
 * the minimal and maximal weight of the entire Arena.
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class LifeFormARGBConverter extends AbstractLinearRange implements Converter< LifeForm, ARGBType >
{
	/**
	 * Instantiate a new LifeFormARGBConverter where min=0 and max=1
	 */
	public LifeFormARGBConverter()
	{
		super();
	}
	
	/**
	 * Instantiate a new LifeFormARGBConverter
	 * @param min - the minimal weight for display (will map to intensity 0)
	 * @param max - the maximal weight for display (will map to intensity 255)
	 */
	public LifeFormARGBConverter( final double min, final double max )
	{
		super( min, max );
	}
	
	/** 
	 * Convert the LifeForm to an ARGB value
	 * @param input - the LifeForm to convert
	 * @param output - the ARGBType that will contain the RGB representation 
	 */
	@Override
	public void convert( final LifeForm input, final ARGBType output )
	{
		final int col = (short)Math.round( normFloat( input.getWeight() ) * 255 );
		
		final int name = input.getName();
		
		if ( name == 0 )
			output.set( col<<16 );
		else if ( name == 1 )
			output.set( col<<8 );
		else if ( name == 2 )
			output.set( col );
		else if ( name == 3 )
			output.set( (col<<16) + (col<<8) );
		else if ( name == 4 )
			output.set( (col<<16) + (col<<8) + col );
		else if ( name == 5 )
			output.set( (col<<16) + col );
		else if ( name == 6 )
			output.set( (col<<8) + col );
	}
	
	/**
	 * norm the weight of the LifeForm to 0...255 using min and max
	 * @param c
	 * @return
	 */
	public float normFloat( final float c )
	{
		double value = ( c - min ) / ( max - min );
		
		if ( value < 0 )
			value = 0;
		else if ( value > 1 )
			value = 1;
		
		return (float)value;
	}

}
