package net.imglib2.ui.lut;

import java.awt.*;

//==============================================================================
// ColorCanvas Class
//
// AGG - Alexander Gee
//
// 041797 - code converted to Java
//==============================================================================
public class ColorCanvas extends Canvas
{
	final static int mBTC     = 1;
	final static int mBTY     = 2;
	final static int mGray    = 3;
	final static int mHO      = 4;
	final static int mLinGray = 5;
	final static int mLOCS    = 6;
	final static int mMagenta = 7;
	final static int mOCS     = 8;
	final static int mRainbow = 9;


	private Dimension    offsize;
	private Graphics     offscreen;
	private Image        offimage;
	private boolean      redrawall;

	private BTC          btc;
	private BTY          bty;
	private Gray         gray;
	private HeatedObject ho;
	private LinGray      lingray;
	private LOCS         locs;
	private Magenta      magenta;
	private OCS          ocs;
	private Rainbow      rainbow;

	private int          numcolors;
	private Color[]      colors;


	public ColorCanvas()
	{
		resize( 256, 306 );

		btc       = new BTC();
		bty       = new BTY();
		gray      = new Gray();
		ho        = new HeatedObject();
		lingray   = new LinGray();
		locs      = new LOCS();
		magenta   = new Magenta();
		ocs       = new OCS();
		rainbow   = new Rainbow();

		numcolors = 256;
		colors    = new Color[numcolors];

		for( int i = 0; i < numcolors; i++ )
		{
			colors[i] = new Color( i, i, i );
		}

		redrawall = true;
	}

	public ColorCanvas( int colormap )
	{
		resize( 256, 306 );

		btc       = new BTC();
		bty       = new BTY();
		gray      = new Gray();
		ho        = new HeatedObject();
		lingray   = new LinGray();
		locs      = new LOCS();
		magenta   = new Magenta();
		ocs       = new OCS();
		rainbow   = new Rainbow();

		setColors( colormap );

		redrawall = true;
	}


	// ColorCanvas Update Handler
	//--------------------------------------------------------------------------
	public void update( Graphics g )
	{
		// override clearing canvas
		paint( g );
	}


	// ColorScales Paint Handler
	//--------------------------------------------------------------------------
	public void paint( Graphics g )
	{
		if( offscreen == null ) {
			offsize   = size();
			offimage  = createImage( offsize.width, offsize.height );
			offscreen = offimage.getGraphics();
		}

		if( redrawall ) {
			int c = 0;
			offscreen.setColor( Color.black );
			offscreen.fillRect( 0, 0, offsize.width, offsize.height );
			for( int i = 0; i < numcolors; i++ )
			{
				offscreen.setColor( colors[i] );
				offscreen.drawLine( i, 256, i, 306 );
				offscreen.setColor( Color.red );
				c = colors[i].getRed();
				offscreen.drawLine( i, 255 - c, i, 255 - c );
				offscreen.setColor( Color.green );
				c = colors[i].getGreen();
				offscreen.drawLine( i, 255 - c, i, 255 - c );
				offscreen.setColor( Color.blue );
				c = colors[i].getBlue();
				offscreen.drawLine( i, 255 - c, i, 255 - c );
			}
			redrawall = false;
		}

		g.drawImage( offimage, 0, 0, this );
	}

	public boolean setColors( int colormap )
	{
		switch( colormap )
		{
		case mBTC:
			numcolors = btc.size;
			colors    = new Color[numcolors];
			for( int i = 0; i < numcolors; i++ )
			{
				colors[i] = new Color(
					btc.rgb[i].getR(),
					btc.rgb[i].getG(),
					btc.rgb[i].getB() );
			}
			break;
		case mBTY:
			numcolors = bty.size;
			colors    = new Color[numcolors];
			for( int i = 0; i < numcolors; i++ )
			{
				colors[i] = new Color(
					bty.rgb[i].getR(),
					bty.rgb[i].getG(),
					bty.rgb[i].getB() );
			}
			break;
		case mGray:
			numcolors = gray.size;
			colors    = new Color[numcolors];
			for( int i = 0; i < numcolors; i++ )
			{
				colors[i] = new Color(
					gray.rgb[i].getR(),
					gray.rgb[i].getG(),
					gray.rgb[i].getB() );
			}
			break;
		case mHO:
			numcolors = ho.size;
			colors    = new Color[numcolors];
			for( int i = 0; i < numcolors; i++ )
			{
				colors[i] = new Color(
					ho.rgb[i].getR(),
					ho.rgb[i].getG(),
					ho.rgb[i].getB() );
			}
			break;
		case mLinGray:
			numcolors = lingray.size;
			colors    = new Color[numcolors];
			for( int i = 0; i < numcolors; i++ )
			{
				colors[i] = new Color(
					lingray.rgb[i].getR(),
					lingray.rgb[i].getG(),
					lingray.rgb[i].getB() );
			}
			break;
		case mLOCS:
			numcolors = locs.size;
			colors    = new Color[numcolors];
			for( int i = 0; i < numcolors; i++ )
			{
				colors[i] = new Color(
					locs.rgb[i].getR(),
					locs.rgb[i].getG(),
					locs.rgb[i].getB() );
			}
			break;
		case mMagenta:
			numcolors = magenta.size;
			colors    = new Color[numcolors];
			for( int i = 0; i < numcolors; i++ )
			{
				colors[i] = new Color(
					magenta.rgb[i].getR(),
					magenta.rgb[i].getG(),
					magenta.rgb[i].getB() );
			}
			break;
		case mOCS:
			numcolors = ocs.size;
			colors    = new Color[numcolors];
			for( int i = 0; i < numcolors; i++ )
			{
				colors[i] = new Color(
					ocs.rgb[i].getR(),
					ocs.rgb[i].getG(),
					ocs.rgb[i].getB() );
			}
			break;
		case mRainbow:
			numcolors = rainbow.size;
			colors    = new Color[numcolors];
			for( int i = 0; i < numcolors; i++ )
			{
				colors[i] = new Color(
					rainbow.rgb[i].getR(),
					rainbow.rgb[i].getG(),
					rainbow.rgb[i].getB() );
			}
			break;
		default:
			return false;
		}

		redrawall = true;
		repaint();
		return true;
	}
}

