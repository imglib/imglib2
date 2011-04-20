package mpicbg.imglib.concatenate;

public interface Concatenable< A >
{
	public Concatenable< A > concatenate( A a );

	public Class< A > getConcatenableClass();
}