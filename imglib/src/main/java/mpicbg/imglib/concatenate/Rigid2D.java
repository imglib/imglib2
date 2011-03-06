package mpicbg.imglib.concatenate;

public class Rigid2D implements Rigid, Concatenatable< Rigid >
{
	String name;

	Rigid2D( String name )
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void concatenate( Rigid a )
	{
		System.out.println("Rigid2D.concatenate( " + a.toString() + " )" );
		name = "(" + name + "x" + a.getName() + ")";
	}

	@Override
	public void preConcatenate( Rigid a )
	{
		System.out.println("Rigid2D.preConcatenate( " + a.toString() + " )" );
		name = "(" + a.getName() + "x" + name + ")";
	}

	@Override
	public String toString()
	{
		return "Rigid2D_" + name;
	}

	@Override
	public Class< Rigid > getConcatenatableClass()
	{
		return Rigid.class;
	}
}
