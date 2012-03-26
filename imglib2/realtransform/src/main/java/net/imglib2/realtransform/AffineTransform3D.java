package net.imglib2.realtransform;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealPositionable;
import net.imglib2.concatenate.Concatenable;


/**
 * 3d-affine transformation models to be applied to points in 3d-space.
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 * 
 */
public class AffineTransform3D implements AffineGet, AffineSet, Concatenable< AffineTransform3D >
{
	final static protected class AffineMatrix3D
	{
		public double
			m00 = 1.0, m01 = 0.0, m02 = 0.0, m03 = 0.0, 
			m10 = 0.0, m11 = 1.0, m12 = 0.0, m13 = 0.0, 
			m20 = 0.0, m21 = 0.0, m22 = 1.0, m23 = 0.0;
		
		public AffineMatrix3D copy()
		{
			final AffineMatrix3D copy = new AffineMatrix3D();
			
			copy.m00 = m00;
			copy.m01 = m01;
			copy.m02 = m02;
			copy.m03 = m03;
			
			copy.m10 = m10;
			copy.m11 = m11;
			copy.m12 = m12;
			copy.m13 = m13;
			
			copy.m20 = m20;
			copy.m21 = m11;
			copy.m22 = m22;
			copy.m23 = m23;
			
			return copy;
		}
		
		final protected double det()
		{
			return
				m00 * m11 * m22 +
				m10 * m21 * m02 +
				m20 * m01 * m12 -
				m02 * m11 * m20 -
				m12 * m21 * m00 -
				m22 * m01 * m10;
		}
	}
	
	
	final protected AffineMatrix3D a;
	
	final protected RealPoint d0;
	final protected RealPoint d1;
	final protected RealPoint d2;
	final protected RealPoint[] ds;
	
	final protected AffineTransform3D inverse;
	
	public AffineTransform3D()
	{
		this( new AffineMatrix3D() );
	}
	
	protected AffineTransform3D( final AffineMatrix3D a )
	{
		this.a = a;
		
		d0 = new RealPoint( 3 );
		d1 = new RealPoint( 3 );
		d2 = new RealPoint( 3 );
		ds = new RealPoint[]{ d0, d1, d2 };
		
		updateDs();
		
		inverse = new AffineTransform3D( this );
		invert();
		inverse.updateDs();
	}
	
	protected AffineTransform3D( final AffineTransform3D inverse )
	{
		this.inverse = inverse;
		
		a = new AffineMatrix3D();
		
		d0 = new RealPoint( 3 );
		d1 = new RealPoint( 3 );
		d2 = new RealPoint( 3 );
		ds = new RealPoint[]{ d0, d1, d2 };
	}
	
	protected void invert()
	{
		final double det = a.det();
		
		/* similar to Jama, throw a RunTimeException for singular matrices. */
		if ( det == 0 )
			throw new RuntimeException( "Matrix is singular." );
		
		final double idet = 1.0 / det;
		
		inverse.a.m00 = ( a.m11 * a.m22 - a.m12 * a.m21 ) * idet;
		inverse.a.m01 = ( a.m02 * a.m21 - a.m01 * a.m22 ) * idet;
		inverse.a.m02 = ( a.m01 * a.m12 - a.m02 * a.m11 ) * idet;
		inverse.a.m10 = ( a.m12 * a.m20 - a.m10 * a.m22 ) * idet;
		inverse.a.m11 = ( a.m00 * a.m22 - a.m02 * a.m20 ) * idet;
		inverse.a.m12 = ( a.m02 * a.m10 - a.m00 * a.m12 ) * idet;
		inverse.a.m20 = ( a.m10 * a.m21 - a.m11 * a.m20 ) * idet;
		inverse.a.m21 = ( a.m01 * a.m20 - a.m00 * a.m21 ) * idet;
		inverse.a.m22 = ( a.m00 * a.m11 - a.m01 * a.m10 ) * idet;
		
		inverse.a.m03 = -inverse.a.m00 * a.m03 - inverse.a.m01 * a.m13 - inverse.a.m02 * a.m23;
		inverse.a.m13 = -inverse.a.m10 * a.m03 - inverse.a.m11 * a.m13 - inverse.a.m12 * a.m23;
		inverse.a.m23 = -inverse.a.m20 * a.m03 - inverse.a.m21 * a.m13 - inverse.a.m22 * a.m23;
	}
	
	protected void updateDs()
	{
		d0.setPosition( a.m00, 0 );
		d0.setPosition( a.m10, 1 );
		d0.setPosition( a.m20, 2 );
		
		d1.setPosition( a.m01, 0 );
		d1.setPosition( a.m11, 1 );
		d1.setPosition( a.m21, 2 );
		
		d2.setPosition( a.m02, 0 );
		d2.setPosition( a.m12, 1 );
		d2.setPosition( a.m22, 2 );
	}
	
	@Override
	final public void apply( final double[] source, final double[] target )
	{
		assert source.length >= 3 && target.length >= 3 : "3d affine transformations can be applied to 3d coordinates only.";
		
		target[ 0 ] = source[ 0 ] * a.m00 + source[ 1 ] * a.m01 + source[ 2 ] * a.m02 + a.m03;
		target[ 1 ] = source[ 0 ] * a.m10 + source[ 1 ] * a.m11 + source[ 2 ] * a.m12 + a.m13;
		target[ 2 ] = source[ 0 ] * a.m20 + source[ 1 ] * a.m21 + source[ 2 ] * a.m22 + a.m23;
	}
	
	@Override
	public void apply( final float[] source, final float[] target )
	{
		assert source.length >= 3 && target.length >= 3 : "3d affine transformations can be applied to 3d coordinates only.";
		
		target[ 0 ] = ( float )( source[ 0 ] * a.m00 + source[ 1 ] * a.m01 + source[ 2 ] * a.m02 + a.m03 );
		target[ 1 ] = ( float )( source[ 0 ] * a.m10 + source[ 1 ] * a.m11 + source[ 2 ] * a.m12 + a.m13 );
		target[ 2 ] = ( float )( source[ 0 ] * a.m20 + source[ 1 ] * a.m21 + source[ 2 ] * a.m22 + a.m23 );
	}
	
	@Override
	public void apply( final RealLocalizable source, final RealPositionable target )
	{
		assert source.numDimensions() >= 3 && target.numDimensions() >= 3 : "3d affine transformations can be applied to 3d coordinates only.";
		
		target.setPosition( source.getDoublePosition( 0 ) * a.m00 + source.getDoublePosition( 1 ) * a.m01 + source.getDoublePosition( 2 ) * a.m02 + a.m03, 0 );
		target.setPosition( source.getDoublePosition( 0 ) * a.m10 + source.getDoublePosition( 1 ) * a.m11 + source.getDoublePosition( 2 ) * a.m12 + a.m13, 1 );
		target.setPosition( source.getDoublePosition( 0 ) * a.m20 + source.getDoublePosition( 1 ) * a.m21 + source.getDoublePosition( 2 ) * a.m22 + a.m23, 2 );
	}
	
	@Override
	final public void applyInverse( final double[] source, final double[] target )
	{
		assert source.length >= 3 && target.length >= 3 : "3d affine transformations can be applied to 3d coordinates only.";
		
		source[ 0 ] = target[ 0 ] * inverse.a.m00 + target[ 1 ] * inverse.a.m01 + target[ 2 ] * inverse.a.m02 + inverse.a.m03;
		source[ 1 ] = target[ 0 ] * inverse.a.m10 + target[ 1 ] * inverse.a.m11 + target[ 2 ] * inverse.a.m12 + inverse.a.m13;
		source[ 2 ] = target[ 0 ] * inverse.a.m20 + target[ 1 ] * inverse.a.m21 + target[ 2 ] * inverse.a.m22 + inverse.a.m23;
	}
	
	@Override
	public void applyInverse( final float[] source, final float[] target )
	{
		assert source.length >= 3 && target.length >= 3 : "3d affine transformations can be applied to 3d coordinates only.";
		
		source[ 0 ] = ( float )( target[ 0 ] * inverse.a.m00 + target[ 1 ] * inverse.a.m01 + target[ 2 ] * inverse.a.m02 + inverse.a.m03 );
		source[ 1 ] = ( float )( target[ 0 ] * inverse.a.m10 + target[ 1 ] * inverse.a.m11 + target[ 2 ] * inverse.a.m12 + inverse.a.m13 );
		source[ 2 ] = ( float )( target[ 0 ] * inverse.a.m20 + target[ 1 ] * inverse.a.m21 + target[ 2 ] * inverse.a.m22 + inverse.a.m23 );
	}
	

	@Override
	public void applyInverse( final RealPositionable source, final RealLocalizable target )
	{
		assert source.numDimensions() >= 3 && target.numDimensions() >= 3 : "3d affine transformations can be applied to 3d coordinates only.";
		
		source.setPosition( target.getDoublePosition( 0 ) * inverse.a.m00 + target.getDoublePosition( 1 ) * inverse.a.m01 + target.getDoublePosition( 2 ) * inverse.a.m02 + inverse.a.m03, 0 );
		source.setPosition( target.getDoublePosition( 0 ) * inverse.a.m10 + target.getDoublePosition( 1 ) * inverse.a.m11 + target.getDoublePosition( 2 ) * inverse.a.m12 + inverse.a.m13, 1 );
		source.setPosition( target.getDoublePosition( 0 ) * inverse.a.m20 + target.getDoublePosition( 1 ) * inverse.a.m21 + target.getDoublePosition( 2 ) * inverse.a.m22 + inverse.a.m23, 2 );
	}

	@Override
	final public AffineTransform3D concatenate( final AffineTransform3D model )
	{
		final double a00 = a.m00 * model.a.m00 + a.m01 * model.a.m10 + a.m02 * model.a.m20;
		final double a01 = a.m00 * model.a.m01 + a.m01 * model.a.m11 + a.m02 * model.a.m21;
		final double a02 = a.m00 * model.a.m02 + a.m01 * model.a.m12 + a.m02 * model.a.m22;
		final double a03 = a.m00 * model.a.m03 + a.m01 * model.a.m13 + a.m02 * model.a.m23 + a.m03;
		
		final double a10 = a.m10 * model.a.m00 + a.m11 * model.a.m10 + a.m12 * model.a.m20;
		final double a11 = a.m10 * model.a.m01 + a.m11 * model.a.m11 + a.m12 * model.a.m21;
		final double a12 = a.m10 * model.a.m02 + a.m11 * model.a.m12 + a.m12 * model.a.m22;
		final double a13 = a.m10 * model.a.m03 + a.m11 * model.a.m13 + a.m12 * model.a.m23 + a.m13;
		
		final double a20 = a.m20 * model.a.m00 + a.m21 * model.a.m10 + a.m22 * model.a.m20;
		final double a21 = a.m20 * model.a.m01 + a.m21 * model.a.m11 + a.m22 * model.a.m21;
		final double a22 = a.m20 * model.a.m02 + a.m21 * model.a.m12 + a.m22 * model.a.m22;
		final double a23 = a.m20 * model.a.m03 + a.m21 * model.a.m13 + a.m22 * model.a.m23 + a.m23;
		
		a.m00 = a00;
		a.m01 = a01;
		a.m02 = a02;
		a.m03 = a03;
		
		a.m10 = a10;
		a.m11 = a11;
		a.m12 = a12;
		a.m13 = a13;
		
		a.m20 = a20;
		a.m21 = a21;
		a.m22 = a22;
		a.m23 = a23;
		
		invert();
		updateDs();
		inverse.updateDs();
		
		return this;
	}
	
	public AffineTransform3D copy()
	{
		final AffineMatrix3D ma = new AffineMatrix3D();
		ma.m00 = a.m00;
		ma.m10 = a.m10;
		ma.m20 = a.m20;
		ma.m01 = a.m01;
		ma.m11 = a.m11;
		ma.m21 = a.m21;
		ma.m02 = a.m02;
		ma.m12 = a.m12;
		ma.m22 = a.m22;
		ma.m03 = a.m03;
		ma.m13 = a.m13;
		ma.m23 = a.m23;
		
		return new AffineTransform3D( ma );
	}
	
	@Override
	public RealLocalizable d( final int d )
	{
		return ds[ d ];
	}
	
	
	@Override
	public double get( final int row, final int column )
	{
		assert row >= 0 && row < 3 && column >= 0 && column < 4 : "Index out of bounds, a 3d affine matrix is a 3x4 matrix.";
		
		switch( row )
		{
		case 0:
			switch( column )
			{
			case 0:
				return a.m00;
			case 1:
				return a.m01;
			case 2:
				return a.m02;
			default:
				return a.m03;
			}
		case 1:
			switch( column )
			{
			case 0:
				return a.m10;
			case 1:
				return a.m11;
			case 2:
				return a.m12;
			default:
				return a.m13;
			}
		default:
			switch( column )
			{
			case 0:
				return a.m20;
			case 1:
				return a.m21;
			case 2:
				return a.m22;
			default:
				return a.m23;
			}
		}
	}
	
	@Override
	public double[] getRowPackedCopy()
	{
		return new double[]{
				a.m00, a.m01, a.m02, a.m03,
				a.m10, a.m11, a.m12, a.m13,
				a.m20, a.m21, a.m22, a.m23
		};
	}
	
	@Override
	public Class< AffineTransform3D > getConcatenableClass()
	{
		return AffineTransform3D.class;
	}
	
	@Override
	public AffineTransform3D inverse()
	{	
		return inverse;
	}
	
	@Override
	public AffineTransform3D inverseAffine()
	{
		return inverse();
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

	final public AffineTransform3D preConcatenate( final AffineTransform3D affine )
	{
		final double a00 = affine.a.m00 * a.m00 + affine.a.m01 * a.m10 + affine.a.m02 * a.m20;
		final double a01 = affine.a.m00 * a.m01 + affine.a.m01 * a.m11 + affine.a.m02 * a.m21;
		final double a02 = affine.a.m00 * a.m02 + affine.a.m01 * a.m12 + affine.a.m02 * a.m22;
		final double a03 = affine.a.m00 * a.m03 + affine.a.m01 * a.m13 + affine.a.m02 * a.m23 + affine.a.m03;
		
		final double a10 = affine.a.m10 * a.m00 + affine.a.m11 * a.m10 + affine.a.m12 * a.m20;
		final double a11 = affine.a.m10 * a.m01 + affine.a.m11 * a.m11 + affine.a.m12 * a.m21;
		final double a12 = affine.a.m10 * a.m02 + affine.a.m11 * a.m12 + affine.a.m12 * a.m22;
		final double a13 = affine.a.m10 * a.m03 + affine.a.m11 * a.m13 + affine.a.m12 * a.m23 + affine.a.m13;
		
		final double a20 = affine.a.m20 * a.m00 + affine.a.m21 * a.m10 + affine.a.m22 * a.m20;
		final double a21 = affine.a.m20 * a.m01 + affine.a.m21 * a.m11 + affine.a.m22 * a.m21;
		final double a22 = affine.a.m20 * a.m02 + affine.a.m21 * a.m12 + affine.a.m22 * a.m22;
		final double a23 = affine.a.m20 * a.m03 + affine.a.m21 * a.m13 + affine.a.m22 * a.m23 + affine.a.m23;
		
		a.m00 = a00;
		a.m01 = a01;
		a.m02 = a02;
		a.m03 = a03;
		
		a.m10 = a10;
		a.m11 = a11;
		a.m12 = a12;
		a.m13 = a13;
		
		a.m20 = a20;
		a.m21 = a21;
		a.m22 = a22;
		a.m23 = a23;
		
		invert();
		updateDs();
		inverse.updateDs();
		
		return this;
	}

	/**
	 * Rotate
	 * 
	 * @param axis 0=x, 1=y, 2=z
	 * @param d angle in radians
	 * 
	 * TODO Don't be lazy and do it directly on the values instead of creating another transform
	 */
	public void rotate( final int axis, final double d )
	{
		final double dcos = Math.cos( d ); 
		final double dsin = Math.sin( d );
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
	
	
	/**
	 * Scale
	 * 
	 * @param d scale factor
	 * 
	 * TODO Don't be lazy and do it directly on the values instead of creating another transform
	 */
	public void scale( final double d )
	{
		final AffineTransform3D dR = new AffineTransform3D();
		dR.set(
				d, 0.0, 0.0, 0.0,
				0.0, d, 0.0, 0.0,
				0.0, 0.0, d, 0.0 );
		
		preConcatenate( dR );
	}

	final public void set( final AffineTransform3D m )
	{
		a.m00 = m.a.m00;
		a.m10 = m.a.m10;
		a.m20 = m.a.m20;
		a.m01 = m.a.m01;
		a.m11 = m.a.m11;
		a.m21 = m.a.m21;
		a.m02 = m.a.m02;
		a.m12 = m.a.m12;
		a.m22 = m.a.m22;
		a.m03 = m.a.m03;
		a.m13 = m.a.m13;
		a.m23 = m.a.m23;
		
		inverse.a.m00 = m.inverse.a.m00;
		inverse.a.m10 = m.inverse.a.m10;
		inverse.a.m20 = m.inverse.a.m20;
		inverse.a.m01 = m.inverse.a.m01;
		inverse.a.m11 = m.inverse.a.m11;
		inverse.a.m21 = m.inverse.a.m21;
		inverse.a.m02 = m.inverse.a.m02;
		inverse.a.m12 = m.inverse.a.m12;
		inverse.a.m22 = m.inverse.a.m22;
		inverse.a.m03 = m.inverse.a.m03;
		inverse.a.m13 = m.inverse.a.m13;
		inverse.a.m23 = m.inverse.a.m23;
		
		updateDs();
		inverse.updateDs();
	}

	/**
	 * Set the affine matrix to:
	 * 
	 * <pre>
	 * m00 m01 m02 m03
	 * m10 m11 m12 m13
	 * m20 m21 m22 m23
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
		a.m00 = m00;
		a.m01 = m01;
		a.m02 = m02;
		a.m03 = m03;
		
		a.m10 = m10;
		a.m11 = m11;
		a.m12 = m12;
		a.m13 = m13;
		
		a.m20 = m20;
		a.m21 = m21;
		a.m22 = m22;
		a.m23 = m23;

		invert();
		updateDs();
		inverse.updateDs();
	}

	public void toArray( final double[] data )
	{
		data[ 0 ] = a.m00;
		data[ 1 ] = a.m10;
		data[ 2 ] = a.m20;
		data[ 3 ] = a.m01;
		data[ 4 ] = a.m11;
		data[ 5 ] = a.m21;
		data[ 6 ] = a.m02;
		data[ 7 ] = a.m12;
		data[ 8 ] = a.m22;
		data[ 9 ] = a.m03;
		data[ 10 ] = a.m13;
		data[ 11 ] = a.m23;
	}

	public void toMatrix( final double[][] data )
	{
		data[ 0 ][ 0 ] = a.m00;
		data[ 0 ][ 1 ] = a.m01;
		data[ 0 ][ 2 ] = a.m02;
		data[ 0 ][ 3 ] = a.m03;
		data[ 1 ][ 0 ] = a.m10;
		data[ 1 ][ 1 ] = a.m11;
		data[ 1 ][ 2 ] = a.m12;
		data[ 1 ][ 3 ] = a.m13;
		data[ 2 ][ 0 ] = a.m20;
		data[ 2 ][ 1 ] = a.m21;
		data[ 2 ][ 2 ] = a.m22;
		data[ 2 ][ 3 ] = a.m23;
	}

	@Override
	final public String toString()
	{
		return
			"3d-affine: (" +
			a.m00 + ", " + a.m01 + ", " + a.m02 + ", " + a.m03 + ", " +
			a.m10 + ", " + a.m11 + ", " + a.m12 + ", " + a.m13 + ", " +
			a.m20 + ", " + a.m21 + ", " + a.m22 + ", " + a.m23 + ")";
	}

	@Override
	public void set( final double value, final int row, final int column )
	{
		assert row >= 0 && row < 3 && column >= 0 && column < 4 : "Index out of bounds, a 3d affine matrix is a 3x4 matrix.";
		
		switch( row )
		{
		case 0:
			switch( column )
			{
			case 0:
				a.m00 = value;
				break;
			case 1:
				a.m01 = value;
				break;
			case 2:
				a.m02 = value;
				break;
			default:
				a.m03 = value;
			}
			break;
		case 1:
			switch( column )
			{
			case 0:
				a.m10 = value;
				break;
			case 1:
				a.m11 = value;
				break;
			case 2:
				a.m12 = value;
				break;
			default:
				a.m13 = value;
			}
			break;
		default:
			switch( column )
			{
			case 0:
				a.m20 = value;
				break;
			case 1:
				a.m21 = value;
				break;
			case 2:
				a.m22 = value;
				break;
			default:
				a.m23 = value;
			}
			break;
		}
		
		updateDs();
		invert();
		inverse.updateDs();
	}

	@Override
	public void set( final double... values )
	{
		assert values.length == 12 : "Input dimensions do not match.  A 3d affine matrix is a 3x4 matrix.";
		
		a.m00 = values[ 0 ];
		a.m01 = values[ 1 ];
		a.m02 = values[ 2 ];
		a.m03 = values[ 3 ];
		
		a.m10 = values[ 4 ];
		a.m11 = values[ 5 ];
		a.m12 = values[ 6 ];
		a.m13 = values[ 7 ];
		
		a.m20 = values[ 8 ];
		a.m21 = values[ 9 ];
		a.m22 = values[ 10 ];
		a.m23 = values[ 11 ];
		
		updateDs();
		invert();
		inverse.updateDs();
	}

	@Override
	public void set( final double[][] values )
	{
		assert values.length == 3 && values[ 0 ].length == 4 && values[ 1 ].length == 4 && values[ 2 ].length == 4: "Input dimensions do not match.  A 3d affine matrix is a 3x4 matrix.";
		
		a.m00 = values[ 0 ][ 0 ];
		a.m01 = values[ 0 ][ 1 ];
		a.m02 = values[ 0 ][ 2 ];
		a.m03 = values[ 0 ][ 3 ];
		
		a.m10 = values[ 1 ][ 0 ];
		a.m11 = values[ 1 ][ 1 ];
		a.m12 = values[ 1 ][ 2 ];
		a.m13 = values[ 1 ][ 3 ];
		
		a.m20 = values[ 2 ][ 0 ];
		a.m21 = values[ 2 ][ 1 ];
		a.m22 = values[ 2 ][ 2 ];
		a.m23 = values[ 2 ][ 3 ];
		
		updateDs();
		invert();
		inverse.updateDs();
	}
}
