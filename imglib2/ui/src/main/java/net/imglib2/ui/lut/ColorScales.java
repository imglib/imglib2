package net.imglib2.ui.lut;

//******************************************************************************
// ColorScales.java:	Applet
//
// Alexander Gee
// April 17, 1997
//
//******************************************************************************
import java.applet.*;
import java.awt.*;

//==============================================================================
// Main Class for applet ColorScales
//
//==============================================================================
public class ColorScales extends Applet
{
	private ColorCanvas        colorcanvas;

	private GridBagLayout      gridbag;
	private GridBagConstraints gridc;

	private Label              l_scales;
	private CheckboxGroup      cmpgroup;
	private Checkbox           c_btc;
	private Checkbox           c_bty;
	private Checkbox           c_gray;
	private Checkbox           c_htobj;
	private Checkbox           c_lingray;
	private Checkbox           c_locs;
	private Checkbox           c_magenta;
	private Checkbox           c_ocs;
	private Checkbox           c_rainbow;


	// ColorScales Class Constructor
	//--------------------------------------------------------------------------
	public ColorScales()
	{
	}

	// APPLET INFO SUPPORT:
	//		The getAppletInfo() method returns a string describing the applet's
	// author, copyright date, or miscellaneous information.
    //--------------------------------------------------------------------------
	public String getAppletInfo()
	{
		return "Name: ColorScales\r\n" +
		       "Author: Alexander Gee\r\n" +
		       "Created with Microsoft Visual J++ Version 1.0";
	}


	// The init() method is called by the AWT when an applet is first loaded or
	// reloaded.  Override this method to perform whatever initialization your
	// applet needs, such as initializing data structures, loading images or
	// fonts, creating frame windows, setting the layout manager, or adding UI
	// components.
    //--------------------------------------------------------------------------
	public void init()
	{
        // If you use a ResourceWizard-generated "control creator" class to
        // arrange controls in your applet, you may want to call its
        // CreateControls() method from within this method. Remove the following
        // call to resize() before adding the call to CreateControls();
        // CreateControls() does its own resizing.
        //----------------------------------------------------------------------
		resize( 340, 320 );

		gridbag     = new GridBagLayout();
		gridc       = new GridBagConstraints();
		setLayout( gridbag );

		gridc.gridx      = 0;
		gridc.gridheight = 10;
		gridc.fill       = GridBagConstraints.HORIZONTAL;
		gridc.anchor     = GridBagConstraints.NORTHWEST;
		colorcanvas      = new ColorCanvas( 3 );
		gridbag.setConstraints( colorcanvas, gridc );
		add( colorcanvas );
		gridc.gridx      = 1;
		gridc.gridheight = 1; 
		gridc.anchor     = GridBagConstraints.NORTH;
		l_scales   = new Label( "Scales" );
		gridbag.setConstraints( l_scales, gridc );
		add( l_scales );
		gridc.anchor     = GridBagConstraints.NORTHWEST;
		cmpgroup   = new CheckboxGroup();
		c_btc      = new Checkbox( "BTC",           cmpgroup, false );
		gridbag.setConstraints( c_btc, gridc );
		add( c_btc );
		c_bty      = new Checkbox( "BTY",           cmpgroup, false );
		gridbag.setConstraints( c_bty, gridc );
		add( c_bty );
		c_gray     = new Checkbox( "Gray",          cmpgroup,  true );
		gridbag.setConstraints( c_gray, gridc );
		add( c_gray );
		c_htobj    = new Checkbox( "heated Object", cmpgroup, false );
		gridbag.setConstraints( c_htobj, gridc );
		add( c_htobj );
		c_lingray  = new Checkbox( "Linear Gray",   cmpgroup, false );
		gridbag.setConstraints( c_lingray, gridc );
		add( c_lingray );
		c_locs     = new Checkbox( "LOCS",          cmpgroup, false );
		gridbag.setConstraints( c_locs, gridc );
		add( c_locs );
		c_magenta  = new Checkbox( "Magenta",       cmpgroup, false );
		gridbag.setConstraints( c_magenta, gridc );
		add( c_magenta );
		c_ocs      = new Checkbox( "OCS",           cmpgroup, false );
		gridbag.setConstraints( c_ocs, gridc );
		add( c_ocs );
		c_rainbow  = new Checkbox( "Rainbow",       cmpgroup, false );
		gridbag.setConstraints( c_rainbow, gridc );
		add( c_rainbow );
	}

	// Place additional applet clean up code here.  destroy() is called when
	// when you applet is terminating and being unloaded.
	//-------------------------------------------------------------------------
	public void destroy()
	{
	}

	// ColorScales Paint Handler
	//--------------------------------------------------------------------------
	public void paint(Graphics g)
	{
	}

	//		The start() method is called when the page containing the applet
	// first appears on the screen. The AppletWizard's initial implementation
	// of this method starts execution of the applet's thread.
	//--------------------------------------------------------------------------
	public void start()
	{
	}
	
	//		The stop() method is called when the page containing the applet is
	// no longer on the screen. The AppletWizard's initial implementation of
	// this method stops execution of the applet's thread.
	//--------------------------------------------------------------------------
	public void stop()
	{
	}

	// Handle Event Actions
	//--------------------------------------------------------
	public boolean action( Event e, Object o )
	{
		if( e.target == c_btc ) {
			colorcanvas.setColors( 1 );
			return true;
		}
		if( e.target == c_bty ) {
			colorcanvas.setColors( 2 );
			return true;
		}
		if( e.target == c_gray ) {
			colorcanvas.setColors( 3 );
			return true;
		}
		if( e.target == c_htobj ) {
			colorcanvas.setColors( 4 );
			return true;
		}
		if( e.target == c_lingray ) {
			colorcanvas.setColors( 5 );
			return true;
		}
		if( e.target == c_locs ) {
			colorcanvas.setColors( 6 );
			return true;
		}
		if( e.target == c_magenta ) {
			colorcanvas.setColors( 7 );
			return true;
		}
		if( e.target == c_ocs ) {
			colorcanvas.setColors( 8 );
			return true;
		}
		if( e.target == c_rainbow ) {
			colorcanvas.setColors( 9 );
			return true;
		}

		return false;
	}
}
