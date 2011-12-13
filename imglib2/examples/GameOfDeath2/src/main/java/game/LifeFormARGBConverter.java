/**
 * Copyright (c) 2011, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
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
package game;

import net.imglib2.converter.Converter;
import net.imglib2.display.AbstractLinearRange;
import net.imglib2.type.numeric.ARGBType;

/**
 * 
 *
 * @author Stephan Preibisch <preibisch@mpi-cbg.de>
 */
public class LifeFormARGBConverter extends AbstractLinearRange implements Converter< LifeForm, ARGBType >
{
	public LifeFormARGBConverter()
	{
		super();
	}
	
	public LifeFormARGBConverter( final double min, final double max )
	{
		super( min, max );
	}
	
	@Override
	public void convert( final LifeForm input, final ARGBType output )
	{
		final int col = (short)Math.round( normFloat( input.getWeight() ) * 255 );
		
		if( input.getName() == 0 )
			output.set( col<<16 );
		else if ( input.getName() == 1 )
			output.set( col<<8 );
		else
			output.set( col );
	}
	
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
