package mpicbg.imglib.concatenate;

import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.RealPositionable;

public class RealRigidTransform implements RealRigid, Concatenable< RealRigid >, PreConcatenable< RealRigid >
{
	String name;

	RealRigidTransform( String name )
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public RealRigidTransform concatenate( RealRigid a )
	{
		System.out.println("Rigid2D.concatenate( " + a.toString() + " )" );
		return new RealRigidTransform( "(" + name + "x" + a.getName() + ")" );
	}

	@Override
	public RealRigidTransform preConcatenate( RealRigid a )
	{
		System.out.println("Rigid2D.preConcatenate( " + a.toString() + " )" );
		return new RealRigidTransform( "(" + a.getName() + "x" + name + ")" );
	}

	@Override
	public String toString()
	{
		return "Rigid2D_" + name;
	}

	@Override
	public Class< RealRigid > getConcatenableClass()
	{
		return RealRigid.class;
	}

	@Override
	public Class< RealRigid > getPreConcatenableClass()
	{
		return RealRigid.class;
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
