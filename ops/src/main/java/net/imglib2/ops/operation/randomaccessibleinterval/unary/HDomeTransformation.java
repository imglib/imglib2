/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * #L%
 */
package net.imglib2.ops.operation.randomaccessibleinterval.unary;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.ops.img.BinaryOperationAssignment;
import net.imglib2.ops.img.UnaryOperationAssignment;
import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.img.unary.ImgCopyOperation;
import net.imglib2.ops.operation.real.binary.RealSubtract;
import net.imglib2.ops.types.ConnectedType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * @author Clemens Muething (University of Konstanz)
 * 
 * @param <T>
 */
public class HDomeTransformation< T extends RealType< T >> implements UnaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< T >>
{

	private final ConnectedType m_type;

	private final double m_height;

	private final double m_substractBefore;

	private final ImgFactory< T > m_imgFactory;

	/**
	 * 
	 * @param type
	 * @param height
	 * @param substractBefore
	 * @deprecated Use the other constructor and specify an ImgFactory for the
	 *             Output instead.
	 */
	@Deprecated
	public HDomeTransformation( ConnectedType type, double height, double substractBefore )
	{
		m_type = type;
		m_height = height;
		m_substractBefore = substractBefore;
		m_imgFactory = new ListImgFactory< T >();
	}

	@SuppressWarnings( "unchecked" )
	public HDomeTransformation( ConnectedType type, double height, double substractBefore, @SuppressWarnings( "rawtypes" ) ImgFactory imgFactory )
	{
		m_type = type;
		m_height = height;
		m_substractBefore = substractBefore;
		m_imgFactory = imgFactory;
	}

	@Override
	public RandomAccessibleInterval< T > compute( RandomAccessibleInterval< T > input, RandomAccessibleInterval< T > output )
	{

		// calculate the regional maxima that should be
		// subtracted
		// before real run

		if ( m_substractBefore > 0.0 )
		{
			Img< T > noSingular = m_imgFactory.create( input, Views.iterable( input ).firstElement().createVariable() );
			getRegionalMaxima( input, m_substractBefore, noSingular );
			// subtract these maxima from the
			// original image
			input = subtract( input, noSingular );
		}

		// now calculate the desired regional maxima

		if ( m_height > 0.0 )
		{
			output = getRegionalMaxima( input, m_height, output );
		}
		else
		{
			new ImgCopyOperation< T >().compute( Views.iterable( input ), Views.iterable( output ) );
		}

		return output;
	}

	@Override
	public UnaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< T >> copy()
	{
		return new HDomeTransformation< T >( m_type, m_height, m_substractBefore, m_imgFactory );
	}

	private RandomAccessibleInterval< T > getRegionalMaxima( final RandomAccessibleInterval< T > img, double height, RandomAccessibleInterval< T > output )
	{

		// compute the marker image, i.e. subtract
		// height from image
		UnaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< T >> op = new SubstractConstantOp( height );

		op.compute( img, output );

		// reconstruct the image from the marker and the
		// original image
		GrayscaleReconstructionByDilation< T, T > op2 = new GrayscaleReconstructionByDilation< T, T >( m_type );

		output = op2.compute( img, output );

		// subtract the reconstructed image from the
		// original image to
		// obtain height
		return subtract( img, output );
	}

	/**
	 * ATTENTION: Subtrahend will be overwritten.
	 */
	private RandomAccessibleInterval< T > subtract( final RandomAccessibleInterval< T > minuend, final RandomAccessibleInterval< T > subtrahend )
	{

		BinaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< T >, RandomAccessibleInterval< T >> subtract = new SubstractImgFromImgOp();

		subtract.compute( minuend, subtrahend, subtrahend );
		return subtrahend;
	}

	private class SubstractConstantOp implements UnaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< T >>
	{

		private final double height;

		public SubstractConstantOp( double height )
		{
			this.height = height;
		}

		@Override
		public RandomAccessibleInterval< T > compute( RandomAccessibleInterval< T > input, RandomAccessibleInterval< T > output )
		{
			new UnaryOperationAssignment< T, T >( new RealSubtractConstantBounded< T >( height ) ).compute( Views.iterable( input ), Views.iterable( output ) );
			return output;
		}

		@Override
		public UnaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< T >> copy()
		{
			return new SubstractConstantOp( height );
		}

	}

	private class RealSubtractConstantBounded< I extends RealType< I >> implements UnaryOperation< I, I >
	{

		private final double constant;

		public RealSubtractConstantBounded( final double constant )
		{
			this.constant = constant;
		}

		@Override
		public I compute( I input, I output )
		{

			double val = Math.max( output.getMinValue(), input.getRealDouble() - this.constant );
			output.setReal( val );

			return output;
		}

		@Override
		public UnaryOperation< I, I > copy()
		{
			return new RealSubtractConstantBounded< I >( this.constant );
		}

	}

	private class SubstractImgFromImgOp implements BinaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< T >, RandomAccessibleInterval< T >>
	{

		@Override
		public RandomAccessibleInterval< T > compute( RandomAccessibleInterval< T > input1, RandomAccessibleInterval< T > input2, RandomAccessibleInterval< T > output )
		{
			new BinaryOperationAssignment< T, T, T >( new RealSubtract< T, T, T >() ).compute( Views.iterable( input1 ), Views.iterable( input2 ), Views.iterable( output ) );
			return output;
		}

		@Override
		public BinaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< T >, RandomAccessibleInterval< T >> copy()
		{
			return new SubstractImgFromImgOp();
		}

	}
}
