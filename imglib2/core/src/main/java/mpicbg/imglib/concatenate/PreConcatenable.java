package mpicbg.imglib.concatenate;

public interface PreConcatenable< A >
{
	public Concatenable< A > preConcatenate( A a );

	public Class< A > getPreConcatenableClass();
}
