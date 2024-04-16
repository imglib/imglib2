/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.display;

import static org.junit.Assert.assertTrue;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.imglib2.display.screenimage.awt.ARGBScreenImage;

import org.junit.Test;

public class ARGBScreenImageExpectationChecking
{
	static public final void main( final String[] args )
	{
		System.out.println( "Painting on java.awt.Graphics alters original array: " + new ARGBScreenImageExpectationChecking().testFill2() );
		System.out.println( "After painting, the image shows a yellow pixel at 0,0: " + new ARGBScreenImageExpectationChecking().testFillAndGrabPixel2() );
		try
		{
			System.out.println( "After painting onto JPanel and capturing, the imageshows a red pixel at 100,100: " + new ARGBScreenImageExpectationChecking().testFillAndPaintPanelAndGrab2() );
		}
		catch ( final Exception e )
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testFill()
	{
		assertTrue( "Painting on java.awt.Graphics does not alter original array", testFill2() );
	}

	private boolean testFill2()
	{
		// Define an image
		final int width = 256;
		final int height = 256;
		final int[] pixels = new int[ width * height ];

		final ARGBScreenImage simg = new ARGBScreenImage( width, height, pixels );

		// Paint a yellow rectangle
		final Graphics g = simg.image().getGraphics();
		g.setColor( Color.yellow );
		g.fillRect( 0, 0, 100, 100 );
		g.dispose();

		// Test whether the original array has changed
		return 0 != pixels[ 0 ];
	}

	private int getPixel( final Image img, final int x, final int y )
	{
		final int[] pix = new int[ 1 ];
		final PixelGrabber pg = new PixelGrabber( img, x, y, 1, 1, pix, 0, 1 );
		try
		{
			pg.grabPixels();
		}
		catch ( final InterruptedException e )
		{
			e.printStackTrace();
		}
		return pix[ 0 ];
	}

	@Test
	public void testFillAndGrabPixel()
	{
		assertTrue( "After painting, the image does not show a yellow pixel at 0,0", testFillAndGrabPixel2() );
	}

	public boolean testFillAndGrabPixel2()
	{
		// Define an image
		final int width = 256;
		final int height = 256;
		final int[] pixels = new int[ width * height ];

		final ARGBScreenImage simg = new ARGBScreenImage( width, height, pixels );

		// Paint a yellow rectangle
		final Graphics g = simg.image().getGraphics();
		g.setColor( Color.yellow );
		g.fillRect( 0, 0, 100, 100 );
		g.dispose();

		// Paint the image, now that it has been altered, onto another image
		final BufferedImage bi = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
		final Graphics2D g2 = bi.createGraphics();
		g2.drawImage( simg.image(), 0, 0, null );
		g2.dispose();

		// Test if the first pixel, as seen from the image, is yellow
		return 0x00ffff00 == ( getPixel( bi, 0, 0 ) & 0x00ffffff );
	}

	@Test
	public void testFillAndPaintPanelAndGrab() throws InterruptedException, InvocationTargetException
	{
		assertTrue( "After painting onto JPanel and capturing, the image does not show a red pixel at 100,100", testFillAndPaintPanelAndGrab2() );
	}

	public boolean testFillAndPaintPanelAndGrab2() throws InterruptedException, InvocationTargetException
	{
		// Define an image
		final int width = 256;
		final int height = 256;
		final int[] pixels = new int[ width * height ];

		final ARGBScreenImage simg = new ARGBScreenImage( width, height, pixels );

		// Paint a yellow rectangle
		final Graphics g = simg.image().getGraphics();
		g.setColor( Color.yellow );
		g.fillRect( 0, 0, 100, 100 );
		g.dispose();

		final BufferedImage[] capture = new BufferedImage[ 1 ];
		final JFrame[] frame = new JFrame[ 2 ];

		// Show the image in a JFrame
		SwingUtilities.invokeAndWait( new Runnable()
		{
			@Override
			public void run()
			{
				frame[ 0 ] = frame( simg.image(), "Test ARGBScreenImage" );
				frame[ 0 ].setVisible( true );
			}
		} );

		// Wait for all sorts of asynchronous events
		Thread.sleep( 2000 );

		SwingUtilities.invokeAndWait( new Runnable()
		{
			@Override
			public void run()
			{
				// Paint into the image again
				final Graphics g2 = simg.image().getGraphics();
				g2.setColor( Color.red );
				g2.fillRect( 100, 100, 100, 100 );
				g2.dispose();
				final JPanel panel = ( JPanel ) frame[ 0 ].getContentPane().getComponent( 0 );
				panel.invalidate();
				panel.validate();
				panel.repaint();
			}
		} );

		// Wait for all sorts of asynchronous events
		Thread.sleep( 2000 );

		// Capture the image with a Robot
		SwingUtilities.invokeAndWait( new Runnable()
		{
			@Override
			public void run()
			{
				final Point panelLocation = frame[ 0 ].getContentPane().getComponent( 0 ).getLocationOnScreen();
				final Rectangle panelBounds = new Rectangle( panelLocation.x, panelLocation.y, width, height );
				Robot robot;
				try
				{
					robot = new Robot();
					capture[ 0 ] = robot.createScreenCapture( panelBounds );
					frame[ 1 ] = frame( capture[ 0 ], "Robot capture" );
					frame[ 1 ].setVisible( true );
				}
				catch ( final AWTException e )
				{
					e.printStackTrace();
				}

				frame[ 0 ].dispose();
				frame[ 1 ].dispose();
			}
		} );

		// Is red:
		return 0x00ff0000 == ( getPixel( capture[ 0 ], 100, 100 ) & 0x00ffffff );
	}

	private JFrame frame( final Image img, final String title )
	{
		final JPanel panel = new JPanel()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = -1605551958207115402L;

			@Override
			public void update( final Graphics g )
			{
				this.paint( g );
			}

			@Override
			public void paint( final Graphics g )
			{
				g.drawImage( img, 0, 0, null );
			}
		};
		panel.setPreferredSize( new Dimension( img.getWidth( null ), img.getHeight( null ) ) );
		final JFrame frame = new JFrame( title );
		frame.getContentPane().add( panel );
		frame.pack();
		return frame;
	}
}
