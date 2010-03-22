package mpicbg.imglib.exception;

public class ImgLibException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ImgLibException( String message )
	{
		super( message );
	}

	public ImgLibException( Object obj, String message )
	{
		super( obj.getClass().getCanonicalName() + ": " + message );
	}

	public ImgLibException()
	{
		
	}
}
