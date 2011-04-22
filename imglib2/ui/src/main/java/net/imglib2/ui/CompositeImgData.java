package net.imglib2.ui;

import java.util.ArrayList;
import net.imglib2.converter.Converter;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.ui.lut.Lut;

/**
 *
 * Composite color image with arbitrary number of channels, each with a Lut
 * 
 * @author GBH
 */
public class CompositeImgData<T extends RealType<T> & NativeType<T>> {

	public String name;
	public Img<T> img;
	public CompositeImgPanel owner;
	public int width, height;
	public ARGBScreenImage screenImage;
	ArrayList<Converter<T, ARGBType>> converters = new ArrayList<Converter<T, ARGBType>>();
	public CompositeXYProjector<T, ARGBType> projector;

	public CompositeImgData(final String name, final Img<T> img, int channelDimIndex, ArrayList<Lut> luts, final CompositeImgPanel owner) {
		this.name = name;
		this.img = img;
		this.owner = owner;
		this.width = (int) img.dimension(0);
		height = (int) img.dimension(1);

		screenImage = new ARGBScreenImage(width, height);
		final int min = 0, max = 255;

		for (int i = 0; i < luts.size(); i++) {
			Lut lut = luts.get(i);
			converters.add(new CompositeLUTConverter<T>(min, max, lut));
		}
		projector = new CompositeXYProjector<T, ARGBType>(img, screenImage, converters, channelDimIndex);
		projector.map();
	}

}