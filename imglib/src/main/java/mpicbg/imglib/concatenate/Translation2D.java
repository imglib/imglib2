package mpicbg.imglib.concatenate;

public class Translation2D implements Translation, Concatenatable< Translation >
{
	String name;

	Translation2D( String name )
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void concatenate( Translation a )
	{
		System.out.println("Translation2D.concatenate( " + a.toString() + " )" );
		name = "(" + name + "x" + a.getName() + ")";
	}

	@Override
	public void preConcatenate( Translation a )
	{
		System.out.println("Translation2D.preConcatenate( " + a.toString() + " )" );
		name = "(" + a.getName() + "x" + name + ")";
	}

	@Override
	public String toString()
	{
		return "Translation2D_" + name;
	}

	@Override
	public Class< Translation > getConcatenatableClass()
	{
		return Translation.class;
	}	
}
