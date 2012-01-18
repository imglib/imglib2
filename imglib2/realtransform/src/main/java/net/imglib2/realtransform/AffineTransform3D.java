package net.imglib2.realtransform;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.concatenate.Concatenable;


/**
 * 3d-affine transformation models to be applied to points in 3d-space.
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 * 
 */
public class AffineTransform3D implements Affine, Concatenable< AffineTransform3D >
{
	protected double
		m00 = 1.0, m01 = 0.0, m02 = 0.0, m03 = 0.0, 
		m10 = 0.0, m11 = 1.0, m12 = 0.0, m13 = 0.0, 
		m20 = 0.0, m21 = 0.0, m22 = 1.0, m23 = 0.0;
	
	protected double
		i00 = 1.0f, i01 = 0.0f, i02 = 0.0f, i03 = 0.0f, 
		i10 = 0.0f, i11 = 1.0f, i12 = 0.0f, i13 = 0.0f, 
		i20 = 0.0f, i21 = 0.0f, i22 = 1.0f, i23 = 0.0f;
	
	@Override
	final public void apply( final double[] source, final double[] target )
	{
		assert source.length >= 3 && target.length >= 3 : "3d affine transformations can be applied to 3d coordinates only.";
		
		target[ 0 ] = source[ 0 ] * m00 + source[ 1 ] * m01 + source[ 2 ] * m02 + m03;
		target[ 1 ] = source[ 0 ] * m10 + source[ 1 ] * m11 + source[ 2 ] * m12 + m13;
		target[ 2 ] = source[ 0 ] * m20 + source[ 1 ] * m21 + source[ 2 ] * m22 + m23;
	}
	
	@Override
	final public void applyInverse( final double[] source, final double[] target )
	{
		assert source.length >= 3 && target.length >= 3 : "3d affine transformations can be applied to 3d coordinates only.";
		
		source[ 0 ] = target[ 0 ] * i00 + target[ 1 ] * i01 + target[ 2 ] * i02 + i03;
		source[ 1 ] = target[ 0 ] * i10 + target[ 1 ] * i11 + target[ 2 ] * i12 + i13;
		source[ 2 ] = target[ 0 ] * i20 + target[ 1 ] * i21 + target[ 2 ] * i22 + i23;
	}
	

	final public void set( final AffineTransform3D m )
	{
		m00 = m.m00;
		m10 = m.m10;
		m20 = m.m20;
		m01 = m.m01;
		m11 = m.m11;
		m21 = m.m21;
		m02 = m.m02;
		m12 = m.m12;
		m22 = m.m22;
		m03 = m.m03;
		m13 = m.m13;
		m23 = m.m23;
		
		invert();
	}

	public AffineTransform3D copy()
	{
		final AffineTransform3D m = new AffineTransform3D();
		m.m00 = m00;
		m.m10 = m10;
		m.m20 = m20;
		m.m01 = m01;
		m.m11 = m11;
		m.m21 = m21;
		m.m02 = m02;
		m.m12 = m12;
		m.m22 = m22;
		m.m03 = m03;
		m.m13 = m13;
		m.m23 = m23;
		
		m.invert();

		return m;
	}
	
	final static protected double det(
			final double m00, final double m01, final double m02,
			final double m10, final double m11, final double m12,
			final double m20, final double m21, final double m22 )
	{
		return
			m00 * m11 * m22 +
			m10 * m21 * m02 +
			m20 * m01 * m12 -
			m02 * m11 * m20 -
			m12 * m21 * m00 -
			m22 * m01 * m10;
	}
	
	protected void invert()
	{
		final double det = det( m00, m01, m02, m10, m11, m12, m20, m21, m22 );
		
		final double idet = 1f / det;
		
		i00 = ( m11 * m22 - m12 * m21 ) * idet;
		i01 = ( m02 * m21 - m01 * m22 ) * idet;
		i02 = ( m01 * m12 - m02 * m11 ) * idet;
		i10 = ( m12 * m20 - m10 * m22 ) * idet;
		i11 = ( m00 * m22 - m02 * m20 ) * idet;
		i12 = ( m02 * m10 - m00 * m12 ) * idet;
		i20 = ( m10 * m21 - m11 * m20 ) * idet;
		i21 = ( m01 * m20 - m00 * m21 ) * idet;
		i22 = ( m00 * m11 - m01 * m10 ) * idet;
		
		i03 = -i00 * m03 - i01 * m13 - i02 * m23;
		i13 = -i10 * m03 - i11 * m13 - i12 * m23;
		i23 = -i20 * m03 - i21 * m13 - i22 * m23;
	}
	
	final public AffineTransform3D preConcatenate( final AffineTransform3D affine )
	{
		final double a00 = affine.m00 * m00 + affine.m01 * m10 + affine.m02 * m20;
		final double a01 = affine.m00 * m01 + affine.m01 * m11 + affine.m02 * m21;
		final double a02 = affine.m00 * m02 + affine.m01 * m12 + affine.m02 * m22;
		final double a03 = affine.m00 * m03 + affine.m01 * m13 + affine.m02 * m23 + affine.m03;
		
		final double a10 = affine.m10 * m00 + affine.m11 * m10 + affine.m12 * m20;
		final double a11 = affine.m10 * m01 + affine.m11 * m11 + affine.m12 * m21;
		final double a12 = affine.m10 * m02 + affine.m11 * m12 + affine.m12 * m22;
		final double a13 = affine.m10 * m03 + affine.m11 * m13 + affine.m12 * m23 + affine.m13;
		
		final double a20 = affine.m20 * m00 + affine.m21 * m10 + affine.m22 * m20;
		final double a21 = affine.m20 * m01 + affine.m21 * m11 + affine.m22 * m21;
		final double a22 = affine.m20 * m02 + affine.m21 * m12 + affine.m22 * m22;
		final double a23 = affine.m20 * m03 + affine.m21 * m13 + affine.m22 * m23 + affine.m23;
		
		m00 = a00;
		m01 = a01;
		m02 = a02;
		m03 = a03;
		
		m10 = a10;
		m11 = a11;
		m12 = a12;
		m13 = a13;
		
		m20 = a20;
		m21 = a21;
		m22 = a22;
		m23 = a23;
		
		invert();
		
		return this;
	}
	
	@Override
	final public AffineTransform3D concatenate( final AffineTransform3D model )
	{
		final double a00 = m00 * model.m00 + m01 * model.m10 + m02 * model.m20;
		final double a01 = m00 * model.m01 + m01 * model.m11 + m02 * model.m21;
		final double a02 = m00 * model.m02 + m01 * model.m12 + m02 * model.m22;
		final double a03 = m00 * model.m03 + m01 * model.m13 + m02 * model.m23 + m03;
		
		final double a10 = m10 * model.m00 + m11 * model.m10 + m12 * model.m20;
		final double a11 = m10 * model.m01 + m11 * model.m11 + m12 * model.m21;
		final double a12 = m10 * model.m02 + m11 * model.m12 + m12 * model.m22;
		final double a13 = m10 * model.m03 + m11 * model.m13 + m12 * model.m23 + m13;
		
		final double a20 = m20 * model.m00 + m21 * model.m10 + m22 * model.m20;
		final double a21 = m20 * model.m01 + m21 * model.m11 + m22 * model.m21;
		final double a22 = m20 * model.m02 + m21 * model.m12 + m22 * model.m22;
		final double a23 = m20 * model.m03 + m21 * model.m13 + m22 * model.m23 + m23;
		
		m00 = a00;
		m01 = a01;
		m02 = a02;
		m03 = a03;
		
		m10 = a10;
		m11 = a11;
		m12 = a12;
		m13 = a13;
		
		m20 = a20;
		m21 = a21;
		m22 = a22;
		m23 = a23;
		
		invert();
		
		return this;
	}
	
	
	/**
	 * Initialize the model such that the respective affine transform is:
	 * 
	 * <pre>
	 * m00 m01 m02 m03
	 * m10 m11 m12 m13
	 * m20 m21 m22 m23
	 * 0   0   0   1
	 * </pre>
	 * 
	 * @param m00
	 * @param m01
	 * @param m02
	 * @param m03
	 * 
	 * @param m10
	 * @param m11
	 * @param m12
	 * @param m13
	 * 
	 * @param m20
	 * @param m21
	 * @param m22
	 * @param m23
	 */
	final public void set(
			final double m00, final double m01, final double m02, final double m03,
			final double m10, final double m11, final double m12, final double m13,
			final double m20, final double m21, final double m22, final double m23 )
	{
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m03 = m03;
		
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;

		invert();
	}
	
	@Override
	final public String toString()
	{
		return
			"3d-affine: (" +
			m00 + ", " + m01 + ", " + m02 + ", " + m03 + ", " +
			m10 + ", " + m11 + ", " + m12 + ", " + m13 + ", " +
			m20 + ", " + m21 + ", " + m22 + ", " + m23 + ")";
	}
	
	/**
	 * TODO Not yet tested
	 */
	@Override
	public AffineTransform3D inverse()
	{
		final AffineTransform3D ict = new AffineTransform3D();
		
		ict.m00 = i00;
		ict.m10 = i10;
		ict.m20 = i20;
		ict.m01 = i01;
		ict.m11 = i11;
		ict.m21 = i21;
		ict.m02 = i02;
		ict.m12 = i12;
		ict.m22 = i22;
		ict.m03 = i03;
		ict.m13 = i13;
		ict.m23 = i23;
		
		ict.i00 = m00;
		ict.i10 = m10;
		ict.i20 = m20;
		ict.i01 = m01;
		ict.i11 = m11;
		ict.i21 = m21;
		ict.i02 = m02;
		ict.i12 = m12;
		ict.i22 = m22;
		ict.i03 = m03;
		ict.i13 = m13;
		ict.i23 = m23;
		
		return ict;
	}
	
	/**
	 * Rotate
	 * 
	 * @param axis 0=x, 1=y, 2=z
	 * @param d angle in radians
	 * 
	 * TODO Don't be lazy and do it directly on the values instead of creating another transform
	 */
	public void rotate( final int axis, final float d )
	{
		final float dcos = ( float )Math.cos( d ); 
		final float dsin = ( float )Math.sin( d );
		final AffineTransform3D dR = new AffineTransform3D();
		
		switch ( axis )
		{
		case 0:
			dR.set(
					1.0f, 0.0f, 0.0f, 0.0f,
					0.0f, dcos, -dsin, 0.0f,
					0.0f, dsin, dcos, 0.0f );
			break;
		case 1:
			dR.set(
					dcos, 0.0f, dsin, 0.0f,
					0.0f, 1.0f, 0.0f, 0.0f,
					-dsin, 0.0f, dcos, 0.0f );
			break;
		default:
			dR.set(
					dcos, -dsin, 0.0f, 0.0f,
					dsin, dcos, 0.0f, 0.0f,
					0.0f, 0.0f, 1.0f, 0.0f );
			break;
		}
		
		preConcatenate( dR );
	}

	public void toArray( final double[] data )
	{
		data[ 0 ] = m00;
		data[ 1 ] = m10;
		data[ 2 ] = m20;
		data[ 3 ] = m01;
		data[ 4 ] = m11;
		data[ 5 ] = m21;
		data[ 6 ] = m02;
		data[ 7 ] = m12;
		data[ 8 ] = m22;
		data[ 9 ] = m03;
		data[ 10 ] = m13;
		data[ 11 ] = m23;
	}

	public void toMatrix( final double[][] data )
	{
		data[ 0 ][ 0 ] = m00;
		data[ 0 ][ 1 ] = m01;
		data[ 0 ][ 2 ] = m02;
		data[ 0 ][ 3 ] = m03;
		data[ 1 ][ 0 ] = m10;
		data[ 1 ][ 1 ] = m11;
		data[ 1 ][ 2 ] = m12;
		data[ 1 ][ 3 ] = m13;
		data[ 2 ][ 0 ] = m20;
		data[ 2 ][ 1 ] = m21;
		data[ 2 ][ 2 ] = m22;
		data[ 2 ][ 3 ] = m23;
	}

	@Override
	public void applyInverse( final float[] source, final float[] target )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyInverse( final RealPositionable source, final RealLocalizable target )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int numSourceDimensions()
	{
		return 3;
	}

	@Override
	public int numTargetDimensions()
	{
		return 3;
	}

	@Override
	public void apply( final float[] source, final float[] target )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void apply( final RealLocalizable source, final RealPositionable target )
	{
		assert source.numDimensions() >= 3 && target.numDimensions() >= 3 : "3d affine transformations can be applied to 3d coordinates only.";
		
		target.setPosition( source.getDoublePosition( 0 ) * m00 + source.getDoublePosition( 1 ) * m01 + source.getDoublePosition( 2 ) * m02 + m03, 0 );
		target.setPosition( source.getDoublePosition( 0 ) * m10 + source.getDoublePosition( 1 ) * m11 + source.getDoublePosition( 2 ) * m12 + m13, 1 );
		target.setPosition( source.getDoublePosition( 0 ) * m20 + source.getDoublePosition( 1 ) * m21 + source.getDoublePosition( 2 ) * m22 + m23, 2 );
	}

	@Override
	public Class< AffineTransform3D > getConcatenableClass()
	{
		return AffineTransform3D.class;
	}

	@Override
	public double get( final int row, final int column )
	{
		return 0;
	}
}
