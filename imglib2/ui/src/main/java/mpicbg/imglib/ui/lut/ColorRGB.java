package mpicbg.imglib.ui.lut;

//==============================================================================
// ColorRGB Class
//
// Red, Green, and Blue values
//   all in [0,1]
// 
// AGG - Alexander Gee
//
// 041497 - code converted to Java
//==============================================================================
public class ColorRGB 
{
	private float R;
	private float G;
	private float B;

	public ColorRGB()
	{
		R = (float)0.0;
		G = (float)0.0;
		B = (float)0.0;
	}

	public ColorRGB( int r, int g, int b )
	{
		R = (float)r/(float)255.0;
		G = (float)g/(float)255.0;
		B = (float)b/(float)255.0;
	}

	public ColorRGB( float r, float g, float b )
	{
		R = r;
		G = g;
		B = b;
	}

	public void setColorRGB( int r, int g, int b )
	{
		R = (float)r/(float)255.0;
		G = (float)g/(float)255.0;
		B = (float)b/(float)255.0;
	}

	public void setColorRGB( float r, float g, float b )
	{
		R = r;
		G = g;
		B = b;
	}

	public float getR()
	{
		return R;
	}

	public float getG()
	{
		return G;
	}

	public float getB()
	{
		return B;
	}
}

