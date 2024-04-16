/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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
 * #L%
 */

package net.imglib2.type.numeric;

import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.util.Fraction;

/**
 *
 * @author Stephan Saalfeld
 */
public class NativeARGBDoubleType extends AbstractARGBDoubleType< NativeARGBDoubleType > implements NativeType< NativeARGBDoubleType >
{
	private final Index i;

	final protected NativeImg< ?, ? extends DoubleAccess > img;

	protected DoubleAccess dataAccess;

	public NativeARGBDoubleType( final NativeImg< ?, ? extends DoubleAccess > img )
	{
		i = new Index();
		this.img = img;
	}

	public NativeARGBDoubleType( final double a, final double r, final double g, final double b )
	{
		i = new Index();
		img = null;
		dataAccess = new DoubleArray( 4 );
		set( a, r, g, b );
	}

	public NativeARGBDoubleType( final DoubleAccess access )
	{
		i = new Index();
		img = null;
		dataAccess = access;
	}

	public NativeARGBDoubleType()
	{
		this( 0, 0, 0, 0 );
	}

	@Override
	public void updateContainer( final Object c )
	{
		dataAccess = img.update( c );
	}

	@Override
	public Index index()
	{
		return i;
	}

	@Override
	public NativeARGBDoubleType duplicateTypeOnSameNativeImg()
	{
		return new NativeARGBDoubleType( img );
	}

	private static final NativeTypeFactory< NativeARGBDoubleType, DoubleAccess > typeFactory = NativeTypeFactory.DOUBLE( NativeARGBDoubleType::new );

	@Override
	public NativeTypeFactory< NativeARGBDoubleType, DoubleAccess > getNativeTypeFactory()
	{
		return typeFactory;
	}

	@Override
	public double getA()
	{
		final int ai = i.get() << 2;
		return dataAccess.getValue( ai );
	}

	@Override
	public double getR()
	{
		final int ai = i.get() << 2;
		return dataAccess.getValue( ai + 1 );
	}

	@Override
	public double getG()
	{
		final int ai = i.get() << 2;
		return dataAccess.getValue( ai + 2 );
	}

	@Override
	public double getB()
	{
		final int ai = i.get() << 2;
		return dataAccess.getValue( ai + 3 );
	}

	@Override
	public void setA( final double a )
	{
		final int ai = i.get() << 2;
		dataAccess.setValue( ai, a );
	}

	@Override
	public void setR( final double r )
	{
		final int ai = i.get() << 2;
		dataAccess.setValue( ai + 1, r );
	}

	@Override
	public void setG( final double g )
	{
		final int ai = i.get() << 2;
		dataAccess.setValue( ai + 2, g );
	}

	@Override
	public void setB( final double b )
	{
		final int ai = i.get() << 2;
		dataAccess.setValue( ai + 3, b );
	}

	@Override
	public void set( final double a, final double r, final double g, final double b )
	{
		final int ai = i.get() << 2;
		dataAccess.setValue( ai, a );
		dataAccess.setValue( ai + 1, r );
		dataAccess.setValue( ai + 2, g );
		dataAccess.setValue( ai + 3, b );
	}

	public void set( final ARGBDoubleType c )
	{
		set( c.getA(), c.getR(), c.getG(), c.getB() );
	}

	@Override
	public NativeARGBDoubleType createVariable()
	{
		return new NativeARGBDoubleType();
	}

	@Override
	public NativeARGBDoubleType copy()
	{
		return new NativeARGBDoubleType( getA(), getR(), getG(), getB() );
	}

	@Override
	public Fraction getEntitiesPerPixel()
	{
		return new Fraction( 4, 1 );
	}
}
