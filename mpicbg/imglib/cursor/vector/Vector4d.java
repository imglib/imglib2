package mpicbg.imglib.cursor.vector;

public class Vector4d extends AbstractVector<Vector4d>
{
	int a, b, c, d;
	
	public Vector4d()
	{
		super( 4 );
		a = b = c = d = 0;
	}
	
	public int getPosition( final int dimension ) 
	{ 
		if ( dimension == 0 )
			return a;
		else if ( dimension == 1 )
			return b;
		else if ( dimension == 2 )
			return c;
		else
			return d;
	}
	
	public void setPosition( final int dimension, final int value ) 
	{ 
		if ( dimension == 0 )
			a = value;
		else if ( dimension == 1 )
			b = value;
		else if ( dimension == 2 )
			c = value;
		else
			d = value;
	}
	
	public void add( final Vector4d vector2 )
	{
		a += vector2.a;
		b += vector2.b;
		c += vector2.c;
		d += vector2.d;
	}

	public void add( final int value )
	{
		a += value;
		b += value;
		c += value;
		d += value;
	}

	public void sub( final Vector4d vector2 )
	{
		a -= vector2.a;
		b -= vector2.b;
		c -= vector2.c;
		d -= vector2.d;
	}

	public void sub( final int value )
	{
		a -= value;
		b -= value;
		c -= value;
		d -= value;
	}

}
