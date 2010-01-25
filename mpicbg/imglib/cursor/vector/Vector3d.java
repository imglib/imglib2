package mpicbg.imglib.cursor.vector;

public class Vector3d extends AbstractVector<Vector3d>
{
	int x, y, z;
	
	public Vector3d()
	{
		super( 3 );
		x = y = z = 0;
	}
	
	public int getPosition( final int dimension ) 
	{ 
		if ( dimension == 0 )
			return x;
		else if ( dimension == 1 )
			return y;
		else
			return z;
	}
	public void setPosition( final int dimension, final int value ) 
	{ 
		if ( dimension == 0 )
			x = value;
		else if ( dimension == 1 )
			y = value;
		else 
			z = value;
	}
	
	public void add( final Vector3d vector2 )
	{
		x += vector2.x;
		y += vector2.y;
		z += vector2.z;
	}

	public void add( final int value )
	{
		x += value;
		y += value;
		z += value;
	}

	public void sub( final Vector3d vector2 )
	{
		x -= vector2.x;
		y -= vector2.y;
		z -= vector2.z;
	}

	public void sub( final int value )
	{
		x -= value;
		y -= value;
		z -= value;
	}

}
