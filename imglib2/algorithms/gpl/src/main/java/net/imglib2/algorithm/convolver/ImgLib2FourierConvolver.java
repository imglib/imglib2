package net.imglib2.algorithm.convolver;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.fft2.FFTConvolution;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;

/**
 * Convolution, using ImgLib2Fourier implementation
 * 
 * @author Christian Dietz (University of Konstanz)
 * @author Martin Horn (University of Konstanz)
 */
public class ImgLib2FourierConvolver< T extends RealType< T >, K extends RealType< K >, O extends RealType< O >> implements Convolver< T, K, O >
{

	private RandomAccessible< T > m_last = null;

	private FFTConvolution< T, K, O > m_fc = null;

	// Empty constructor for extension point
	public ImgLib2FourierConvolver()
	{}

	@Override
	public ImgLib2FourierConvolver< T, K, O > copy()
	{
		return new ImgLib2FourierConvolver< T, K, O >();
	}

	@Override
	public RandomAccessibleInterval< O > compute( final RandomAccessible< T > in, final RandomAccessibleInterval< K > kernel, final RandomAccessibleInterval< O > out )
	{

		if ( in.numDimensions() != kernel.numDimensions() ) { throw new IllegalStateException( "Kernel dimensions do not match to Img dimensions in ImgLibImageConvolver!" ); }

		if ( m_last != in )
		{
			m_last = in;
			m_fc = FFTConvolution.create( m_last, out, kernel, kernel, out, new ArrayImgFactory< ComplexFloatType >() );
			m_fc.setKernel( kernel );
			m_fc.setKeepImgFFT( true );
		}
		else
		{
			m_fc.setKernel( kernel );
			m_fc.setOutput( out );
		}

		m_fc.run();

		return out;
	}
}
