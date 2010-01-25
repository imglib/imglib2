package mpicbg.imglib.cursor.vector;

public class Vector2d extends AbstractVector<Vector2d>
{
	int x, y;
	
	public Vector2d()
	{
		super( 2 );
		x = y = 0;
	}
	
	public int getPosition( final int dimension ) 
	{ 
		if ( dimension == 0 )
			return x;
		else
			return y;
	}
	public void setPosition( final int dimension, final int value ) 
	{ 
		if ( dimension == 0 )
			x = value;
		else
			y = value;
	}
	
	public void add( final Vector2d vector2 )
	{
		x += vector2.x;
		y += vector2.y;
	}

	public void add( final int value )
	{
		x += value;
		y += value;
	}

	public void sub( final Vector2d vector2 )
	{
		x -= vector2.x;
		y -= vector2.y;
	}

	public void sub( final int value )
	{
		x -= value;
		y -= value;
	}

}
