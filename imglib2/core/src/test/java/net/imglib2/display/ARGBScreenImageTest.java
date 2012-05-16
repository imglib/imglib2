package net.imglib2.display;

import java.awt.Color;
import java.awt.Graphics;

import org.junit.Test;

public class ARGBScreenImageTest
{
	static public final void main(String[] args)
	{
		System.out.println("Painting on java.awt.Graphics alters original array: " + new ARGBScreenImageTest().testFill());
	}
	
	@Test
	public boolean testFill() {
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
		return 0 != pixels[0];
	}
}
