package mpicbg.imglib.concatenate;

public interface Concatenatable< A >
{
	public void concatenate( A a );
	public void preConcatenate( A a );

	public Class<A> getConcatenatableClass();
}
