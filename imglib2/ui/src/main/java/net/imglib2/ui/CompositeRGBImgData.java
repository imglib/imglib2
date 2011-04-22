/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.imglib2.ui;

import java.awt.Color;
import java.util.ArrayList;
import net.imglib2.converter.Converter;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.ui.lut.LutBuilder;

/**
 *
 * @author GBH
 */
public class CompositeRGBImgData<T extends RealType<T> & NativeType<T>> {

	public String name;
	public Img<T> img;
	public ImageDisplay owner;
	public int width, height;
	public ARGBScreenImage screenImage;
	ArrayList<Converter<T, ARGBType>> converters = new ArrayList<Converter<T, ARGBType>>();
	//public RealLUTConverter<T> converter = new RealLUTConverter<T>[3];
	public CompositeXYProjector<T, ARGBType> projector;

	public CompositeRGBImgData(final String name, final Img<T> img, final ImageDisplay owner) {
		this.name = name;
		this.img = img;
		this.owner = owner;
		width = (int) img.dimension(0);
		height = (int) img.dimension(1);

		screenImage = new ARGBScreenImage(width, height);
		final int min = 0, max = 255;
		converters.add(new CompositeLUTConverter<T>(min, max, LutBuilder.getInstance().createLUT("red")));
		converters.add(new CompositeLUTConverter<T>(min, max, LutBuilder.getInstance().createLUT("green")));
		converters.add(new CompositeLUTConverter<T>(min, max, LutBuilder.getInstance().createLUT("blue")));


//		converters.add(new CompositeLUTConverter<T>(min, max, createRampLUT(0)));
//		converters.add(new CompositeLUTConverter<T>(min, max, createRampLUT(1)));
//		converters.add(new CompositeLUTConverter<T>(min, max, createRampLUT(2)));
		int channelDimIndex = 2;
		
		projector = new CompositeXYProjector<T, ARGBType>(img, screenImage, converters, channelDimIndex);
		projector.map();
	}

	protected Color[] createRampLUT(int c) {
		int numcolors = 256;
		Color[] colorTable = new Color[numcolors];
		for (int i = 0; i < numcolors; i++) {
			if (c == 0) {
				colorTable[i] = new Color(i, 0, 0);
			} else if (c == 1) {
				colorTable[i] = new Color(0, i, 0);
			} else if (c == 2) {
				colorTable[i] = new Color(0, 0, i);
			}

		}
		return colorTable;
	}

}