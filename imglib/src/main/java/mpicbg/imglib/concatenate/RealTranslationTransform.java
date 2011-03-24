package mpicbg.imglib.concatenate;

import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.RealPositionable;

public class RealTranslationTransform implements RealTranslation, Concatenable< RealTranslation >, PreConcatenable< RealTranslation >
{
	String name;

	RealTranslationTransform( String name )
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public RealTranslationTransform concatenate( RealTranslation a )
	{
		System.out.println("Translation2D.concatenate( " + a.toString() + " )" );
		return new RealTranslationTransform( "(" + name + "x" + a.getName() + ")" );
	}

	@Override
	public RealTranslationTransform preConcatenate( RealTranslation a )
	{
		System.out.println("Translation2D.preConcatenate( " + a.toString() + " )" );
		return new RealTranslationTransform( "(" + a.getName() + "x" + name + ")" );
	}

	@Override
	public String toString()
	{
		return "Translation2D_" + name;
	}

	@Override
	public Class< RealTranslation > getConcatenableClass()
	{
		return RealTranslation.class;
	}

	@Override
	public Class< RealTranslation > getPreConcatenableClass()
	{
		return RealTranslation.class;
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
