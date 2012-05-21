package net.imglib2.display;

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

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class ARGBScreenImageExpectationChecking
{
	static public final void main(String[] args)
	{
		System.out.println("Painting on java.awt.Graphics alters original array: " + new ARGBScreenImageExpectationChecking().testFill2());
		System.out.println("After painting, the image shows a yellow pixel at 0,0: " + new ARGBScreenImageExpectationChecking().testFillAndGrabPixel2());
		try {
			System.out.println("After painting onto JPanel and capturing, the imageshows a red pixel at 100,100: " + new ARGBScreenImageExpectationChecking().testFillAndPaintPanelAndGrab2());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFill() {
		assertTrue("Painting on java.awt.Graphics does not alter original array", testFill2());
	}
	
	private boolean testFill2() {
		// Define an image
		int width = 256;
		int height = 256;
		int[] pixels = new int[width * height];
		
		ARGBScreenImage simg = new ARGBScreenImage(width, height, pixels);
		
		// Paint a yellow rectangle
		Graphics g = simg.image().getGraphics();
		g.setColor(Color.yellow);
		g.fillRect(0, 0, 100, 100);
		g.dispose();
		
		// Test whether the original array has changed
		return  0 != pixels[0];
	}
	
	private int getPixel(Image img, int x, int y) {
		int[] pix = new int[1];
		PixelGrabber pg = new PixelGrabber(img, x, y, 1, 1, pix, 0, 1);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return pix[0];
	}
	
	@Test
	public void testFillAndGrabPixel() {
		assertTrue("After painting, the image does not show a yellow pixel at 0,0", testFillAndGrabPixel2());
	}
	
	public boolean testFillAndGrabPixel2() {
		// Define an image
		int width = 256;
		int height = 256;
		int[] pixels = new int[width * height];
		
		ARGBScreenImage simg = new ARGBScreenImage(width, height, pixels);
		
		// Paint a yellow rectangle
		Graphics g = simg.image().getGraphics();
		g.setColor(Color.yellow);
		g.fillRect(0, 0, 100, 100);
		g.dispose();
		
		// Paint the image, now that it has been altered, onto another image
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bi.createGraphics();
		g2.drawImage(simg.image(), 0, 0, null);
		g2.dispose();
		
		// Test if the first pixel, as seen from the image, is yellow
		return 0x00ffff00 == (getPixel(bi, 0, 0) & 0x00ffffff);
	}
	
	@Test
	public void testFillAndPaintPanelAndGrab() throws InterruptedException, InvocationTargetException {
		assertTrue("After painting onto JPanel and capturing, the image does not show a red pixel at 100,100", testFillAndPaintPanelAndGrab2());
	}

	public boolean testFillAndPaintPanelAndGrab2() throws InterruptedException, InvocationTargetException {	
		// Define an image
		final int width = 256;
		final int height = 256;
		int[] pixels = new int[width * height];

		final ARGBScreenImage simg = new ARGBScreenImage(width, height, pixels);

		// Paint a yellow rectangle
		Graphics g = simg.image().getGraphics();
		g.setColor(Color.yellow);
		g.fillRect(0, 0, 100, 100);
		g.dispose();
		
		final BufferedImage[] capture = new BufferedImage[1];
		final JFrame[] frame = new JFrame[2];
		
		// Show the image in a JFrame
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				frame[0] = frame(simg.image(), "Test ARGBScreenImage");
				frame[0].setVisible(true);
			}
		});

		// Wait for all sorts of asynchronous events
		Thread.sleep(2000);
		
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				// Paint into the image again
				Graphics g2 = simg.image().getGraphics();
				g2.setColor(Color.red);
				g2.fillRect(100, 100, 100, 100);
				g2.dispose();
				JPanel panel = (JPanel) frame[0].getContentPane().getComponent(0);
				panel.invalidate();
				panel.validate();
				panel.repaint();
			}
		});
		
		// Wait for all sorts of asynchronous events
		Thread.sleep(2000);

		// Capture the image with a Robot
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				Point panelLocation = frame[0].getContentPane().getComponent(0).getLocationOnScreen();
				Rectangle panelBounds = new Rectangle(panelLocation.x, panelLocation.y, width, height);				
				Robot robot;
				try {
					robot = new Robot();
					capture[0] = robot.createScreenCapture(panelBounds);
					frame[1] = frame(capture[0], "Robot capture");
					frame[1].setVisible(true);
				} catch (AWTException e) {
					e.printStackTrace();
				}
				
				frame[0].dispose();
				frame[1].dispose();
			}
		});
		

		// Is red:
		return 0x00ff0000 == (getPixel(capture[0], 100, 100) & 0x00ffffff);
	}
	
	private JFrame frame(final Image img, String title) {
		JPanel panel = new JPanel() {
			@Override
			public void update(Graphics g) {
				this.paint(g);
			}
			@Override
			public void paint(Graphics g) {
				g.drawImage(img, 0, 0, null);
			}
		};
		panel.setPreferredSize(new Dimension(img.getWidth(null), img.getHeight(null)));
		JFrame frame = new JFrame(title);
		frame.getContentPane().add(panel);
		frame.pack();
		return frame;
	}
}
