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

package net.imglib2.type.numeric.complex;

import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.FloatingType;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ComplexDoubleType extends AbstractComplexType<ComplexDoubleType>
	implements NativeType<ComplexDoubleType>, FloatingType<ComplexDoubleType>
{

	// Note - FloatingType declarations added by Barry DeZonia

	private int i = 0;

	// the indices for real and imaginary value
	private int realI = 0, imaginaryI = 1;

	final protected NativeImg<ComplexDoubleType, ? extends DoubleAccess> img;

	// the DataAccess that holds the information
	protected DoubleAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public ComplexDoubleType(
		final NativeImg<ComplexDoubleType, ? extends DoubleAccess> complexfloatStorage)
	{
		img = complexfloatStorage;
	}

	// this is the constructor if you want it to be a variable
	public ComplexDoubleType( final double r, final double i )
	{
		img = null;
		dataAccess = new DoubleArray( 2 );
		set( r, i );
	}

	// this is the constructor if you want to specify the dataAccess
	public ComplexDoubleType( final DoubleAccess access )
	{
		img = null;
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public ComplexDoubleType() { this( 0, 0 ); }

	@Override
	public NativeImg<ComplexDoubleType, ? extends DoubleAccess>
		createSuitableNativeImg(
			final NativeImgFactory<ComplexDoubleType> storageFactory,
			final long dim[])
	{
		// create the container
		final NativeImg<ComplexDoubleType, ? extends DoubleAccess> container =
			storageFactory.createDoubleInstance(dim, 2);

		// create a Type that is linked to the container
		final ComplexDoubleType linkedType = new ComplexDoubleType( container );

		// pass it to the NativeContainer
		container.setLinkedType( linkedType );

		return container;
	}

	@Override
	public void updateContainer(final Object c) {
		dataAccess = img.update(c);
	}

	@Override
	public ComplexDoubleType duplicateTypeOnSameNativeImg() {
		return new ComplexDoubleType(img);
	}

	@Override
	public float getRealFloat() { return (float)dataAccess.getValue( realI ); }
	@Override
	public double getRealDouble() { return dataAccess.getValue( realI ); }
	@Override
	public float getImaginaryFloat() {
		return (float) dataAccess.getValue(imaginaryI);
	}
	@Override
	public double getImaginaryDouble() {
		return dataAccess.getValue(imaginaryI);
	}

	@Override
	public void setReal( final float r ){ dataAccess.setValue( realI, r ); }
	@Override
	public void setReal( final double r ){ dataAccess.setValue( realI, r ); }
	@Override
	public void setImaginary(final float i) {
		dataAccess.setValue(imaginaryI, i);
	}
	@Override
	public void setImaginary(final double i) {
		dataAccess.setValue(imaginaryI, i);
	}
	
	public void set( final double r, final double i ) 
	{ 
		dataAccess.setValue( realI, r );
		dataAccess.setValue( imaginaryI, i );
	}

	@Override
	public void set( final ComplexDoubleType c )
	{
		setReal( c.getRealDouble() );
		setImaginary( c.getImaginaryDouble() );
	}

	@Override
	public ComplexDoubleType createVariable() {
		return new ComplexDoubleType(0, 0);
	}

	@Override
	public ComplexDoubleType copy() {
		return new ComplexDoubleType(getRealFloat(), getImaginaryFloat());
	}

	@Override
	public int getEntitiesPerPixel() { return 2; }

	@Override
	public void updateIndex( final int index )
	{
		this.i = index;
		realI = index * 2;
		imaginaryI = index * 2 + 1;
	}

	@Override
	public void incIndex()
	{
		++i;
		realI += 2;
		imaginaryI += 2;
	}
	@Override
	public void incIndex( final int increment )
	{
		i += increment;

		final int inc2 = 2 * increment;
		realI += inc2;
		imaginaryI += inc2;
	}
	@Override
	public void decIndex()
	{
		--i;
		realI -= 2;
		imaginaryI -= 2;
	}
	@Override
	public void decIndex( final int decrement )
	{
		i -= decrement;
		final int dec2 = 2 * decrement;
		realI -= dec2;
		imaginaryI -= dec2;
	}

	@Override
	public int getIndex() { return i; }

	@Override
	public void PI(ComplexDoubleType result) {
		result.setComplexNumber(Math.PI, 0);
	}

	@Override
	public void E(ComplexDoubleType result) {
		result.setComplexNumber(Math.E, 0);
	}

	@Override
	public void exp(ComplexDoubleType result) {
		ComplexMath.exp(this, result);
	}

	@Override
	public void sqrt(ComplexDoubleType result) {
		ComplexMath.sqrt(this, result);
	}

	@Override
	public void log(ComplexDoubleType result) {
		ComplexMath.log(this, result);
	}

	@Override
	public void pow(ComplexDoubleType p, ComplexDoubleType result) {
		ComplexMath.pow(this, p, result);
	}

	@Override
	public void logBase(ComplexDoubleType b, ComplexDoubleType result) {
		ComplexMath.logBase(this, b, result);
	}

	@Override
	public void sin(ComplexDoubleType result) {
		ComplexMath.sin(this, result);
	}

	@Override
	public void cos(ComplexDoubleType result) {
		ComplexMath.cos(this, result);
	}

	@Override
	public void tan(ComplexDoubleType result) {
		ComplexMath.tan(this, result);
	}

	@Override
	public void asin(ComplexDoubleType result) {
		ComplexMath.asin(this, result);
	}

	@Override
	public void acos(ComplexDoubleType result) {
		ComplexMath.acos(this, result);
	}

	@Override
	public void atan(ComplexDoubleType result) {
		ComplexMath.atan(this, result);
	}

	@Override
	public void sinh(ComplexDoubleType result) {
		ComplexMath.sinh(this, result);
	}

	@Override
	public void cosh(ComplexDoubleType result) {
		ComplexMath.cosh(this, result);
	}

	@Override
	public void tanh(ComplexDoubleType result) {
		ComplexMath.tanh(this, result);
	}

	@Override
	public void asinh(ComplexDoubleType result) {
		ComplexMath.asinh(this, result);
	}

	@Override
	public void acosh(ComplexDoubleType result) {
		ComplexMath.acosh(this, result);
	}

	@Override
	public void atanh(ComplexDoubleType result) {
		ComplexMath.atanh(this, result);
	}

}
