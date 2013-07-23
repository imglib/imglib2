/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

package net.imglib2.type.numeric.real;

import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ExponentialMathType;
import net.imglib2.type.numeric.FloatingType;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class DoubleType extends AbstractRealType<DoubleType> implements
	ExponentialMathType<DoubleType>, NativeType<DoubleType>,
	FloatingType<DoubleType>
{

	// Note - FloatingType declarations added by Barry DeZonia

	private int i = 0;

	final protected NativeImg<DoubleType, ? extends DoubleAccess> img;
	
	// the DataAccess that holds the information 
	protected DoubleAccess dataAccess;
	
	// this is the constructor if you want it to read from an array
	public DoubleType(NativeImg<DoubleType, ? extends DoubleAccess> doubleStorage)
	{
		img = doubleStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public DoubleType( final double value )
	{
		img = null;
		dataAccess = new DoubleArray( 1 );
		set( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public DoubleType( final DoubleAccess access )
	{
		img = null;
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public DoubleType() { this( 0 ); }

	@Override
	public NativeImg<DoubleType, ? extends DoubleAccess> createSuitableNativeImg(
		final NativeImgFactory<DoubleType> storageFactory, final long dim[])
	{
		// create the container
		final NativeImg<DoubleType, ? extends DoubleAccess> container =
			storageFactory.createDoubleInstance(dim, 1);
		
		// create a Type that is linked to the container
		final DoubleType linkedType = new DoubleType( container );
		
		// pass it to the NativeContainer
		container.setLinkedType( linkedType );
		
		return container;
	}
	
	@Override
	public void updateContainer(final Object c) {
		dataAccess = img.update(c);
	}

	@Override
	public DoubleType duplicateTypeOnSameNativeImg() {
		return new DoubleType(img);
	}
	
	public double get(){ return dataAccess.getValue( i ); }
	public void set( final double f ){ dataAccess.setValue( i, f ); }
	
	@Override
	public float getRealFloat() { return (float)get(); }
	@Override
	public double getRealDouble() { return get(); }
	
	@Override
	public void setReal( final float real ){ set( real ); }
	@Override
	public void setReal( final double real ){ set( real ); }
	
	@Override
	public double getMaxValue() { return Double.MAX_VALUE; }
	@Override
	public double getMinValue()  { return -Double.MAX_VALUE; }
	@Override
	public double getMinIncrement()  { return Double.MIN_VALUE; }
	
	@Override
	public DoubleType createVariable(){ return new DoubleType( 0 ); }
	
	@Override
	public DoubleType copy(){ return new DoubleType( get() ); }

	@Override
	public void exp() { set( Math.exp( get() ) ); }

	@Override
	public void round() { set( Util.round( get() ) ); }
	
	@Override
	public int getEntitiesPerPixel() { return 1; }
	
	@Override
	public void updateIndex( final int index ) { i = index; }
	@Override
	public int getIndex() { return i; }
	
	@Override
	public void incIndex() { ++i; }
	@Override
	public void incIndex( final int increment ) { i += increment; }
	@Override
	public void decIndex() { --i; }
	@Override
	public void decIndex( final int decrement ) { i -= decrement; }

	@Override
	public int getBitsPerPixel() { return 64; }

	@Override
	public void PI() {
		set(Math.PI);
	}

	@Override
	public void E() {
		set(Math.E);
	}

	@Override
	public void exp(DoubleType input) {
		set(Math.exp(input.get()));
	}

	@Override
	public void sqrt(DoubleType input) {
		set(Math.sqrt(input.get()));
	}

	@Override
	public void log(DoubleType input) {
		set(Math.log(input.get()));
	}

	@Override
	public void pow(DoubleType input, DoubleType power) {
		set(Math.pow(input.get(), power.get()));
	}

	@Override
	public void logBase(DoubleType input, DoubleType base) {
		set(Math.log(input.get()) / Math.log(base.get()));
	}

	@Override
	public void sin(DoubleType input) {
		set(Math.sin(input.get()));
	}

	@Override
	public void cos(DoubleType input) {
		set(Math.cos(input.get()));
	}

	@Override
	public void tan(DoubleType input) {
		set(Math.tan(input.get()));
	}

	@Override
	public void asin(DoubleType input) {
		set(Math.asin(input.get()));
	}

	@Override
	public void acos(DoubleType input) {
		set(Math.acos(input.get()));
	}

	@Override
	public void atan(DoubleType input) {
		set(Math.atan(input.get()));
	}

	@Override
	public void sinh(DoubleType input) {
		set(Math.sinh(input.get()));
	}

	@Override
	public void cosh(DoubleType input) {
		set(Math.cosh(input.get()));
	}

	@Override
	public void tanh(DoubleType input) {
		set(Math.tanh(input.get()));
	}

	// Handbook of Mathematics and Computational Science, Harris & Stocker,
	// Springer, 2006
	@Override
	public void asinh(DoubleType input) {
		double xt = input.get();
		double delta = Math.sqrt(xt * xt + 1);
		double value = Math.log(xt + delta);
		set(value);
	}

	// Handbook of Mathematics and Computational Science, Harris & Stocker,
	// Springer, 2006
	@Override
	public void acosh(DoubleType input) {
		double xt = input.get();
		double delta = Math.sqrt(xt * xt - 1);
		if (xt <= -1) delta = -delta;
		double value = Math.log(xt + delta);
		set(value);
	}

	// Handbook of Mathematics and Computational Science, Harris & Stocker,
	// Springer, 2006
	@Override
	public void atanh(DoubleType input) {
		double xt = input.get();
		double value = 0.5 * Math.log((1 + xt) / (1 - xt));
		set(value);
	}

	/**
	 * Fills result by taking the atan2 of the given y and x values.
	 */
	public void atan2(DoubleType y, DoubleType x) {
		set(Math.atan2(y.get(), x.get()));
	}
}
