package net.imglib2.ui.lut;

/**
 * Adds Alpha to Lookup Table for 256 RGB 
 * 
 * @author GBH
 */
public class LutWithAlpha extends Lut {

	public final byte[] alpha = new byte[lutSize];
	
}
