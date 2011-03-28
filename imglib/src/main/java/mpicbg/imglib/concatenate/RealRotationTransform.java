package mpicbg.imglib.concatenate;

import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.RealPositionable;

public class RealRotationTransform implements RealRotation, Concatenable< RealRotation >, PreConcatenable< RealRotation >
{
	String name;

	RealRotationTransform( String name )
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public RealRotationTransform concatenate( RealRotation a )
	{
		System.out.println("Rotation2D.concatenate( " + a.toString() + " )" );
		return new RealRotationTransform( "(" + name + "x" + a.getName() + ")" );
	}

	@Override
	public RealRotationTransform preConcatenate( RealRotation a )
	{
		System.out.println("Rotation2D.preConcatenate( " + a.toString() + " )" );
		return new RealRotationTransform( "(" + a.getName() + "x" + name + ")" );
	}

	@Override
	public String toString()
	{
		return "Rotation2D_" + name;
	}

	@Override
	public Class< RealRotation > getConcatenableClass()
	{
		return RealRotation.class;
	}

	@Override
	public Class< RealRotation > getPreConcatenableClass()
	{
		return RealRotation.class;
	}

	@Override
	public int numSourceDimensions()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numTargetDimensions()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void apply( double[] source, double[] target )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void apply( float[] source, float[] target )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void apply( RealLocalizable source, RealPositionable target )
	{
		// TODO Auto-generated method stub
		
	}	
}
