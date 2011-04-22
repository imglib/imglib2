package net.imglib2.ui;

import java.util.ArrayList;
import net.imglib2.converter.Converter;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.Projector;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.XYProjector;
import net.imglib2.img.ImgPlus;
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
public class ImgProjector<T extends RealType<T> & NativeType<T>> {

	private ImgPlus<T> img;
	private ImageDisplay owner;
	private ARGBScreenImage screenImage;
	ArrayList<Converter<T, ARGBType>> converters = new ArrayList<Converter<T, ARGBType>>();
	private XYProjector<T, ARGBType> projector;

	public ImgPlus<T> getImg() {
		return img;
	}

	public ImageDisplay getOwner() {
		return owner;
	}

	public XYProjector<T, ARGBType> getProjector() {
		return projector;
	}

	public ARGBScreenImage getScreenImage() {
		return screenImage;
	}

	public ImgProjector(final String name, final ImgPlus<T> img,
			int channelDimIndex, ArrayList<Lut> luts,
			final ImageDisplay owner) {

		this.img = img;
		this.owner = owner;

		screenImage = new ARGBScreenImage((int) img.dimension(0), (int) img.dimension(1));
		final int min = 0, max = 255;
		if (channelDimIndex < 0) {
			if (luts != null) {
				projector = new XYProjector<T, ARGBType>(img, screenImage, new RealLUTConverter<T>(min, max, luts.get(0)));
			} else {
				projector = new XYProjector<T, ARGBType>(img, screenImage, new RealARGBConverter<T>(min, max));
			}
		} else {
			for (int i = 0; i < luts.size(); i++) {
				Lut lut = luts.get(i);
				converters.add(new CompositeLUTConverter<T>(min, max, lut));
			}
			projector = new CompositeXYProjector<T, ARGBType>(img, screenImage, converters, channelDimIndex);
		}
		projector.map();
	}

}