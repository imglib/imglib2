package mpicbg.imglib.concatenate;

public class Rotation2D implements Rotation, Concatenatable< Rotation >
{
	String name;

	Rotation2D( String name )
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void concatenate( Rotation a )
	{
		System.out.println("Rotation2D.concatenate( " + a.toString() + " )" );
		name = "(" + name + "x" + a.getName() + ")";
	}

	@Override
	public void preConcatenate( Rotation a )
	{
		System.out.println("Rotation2D.preConcatenate( " + a.toString() + " )" );
		name = "(" + a.getName() + "x" + name + ")";
	}

	@Override
	public String toString()
	{
		return "Rotation2D_" + name;
	}

	@Override
	public Class< Rotation > getConcatenatableClass()
	{
		return Rotation.class;
	}	
}
