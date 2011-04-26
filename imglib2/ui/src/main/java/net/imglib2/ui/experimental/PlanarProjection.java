package net.imglib2.ui.experimental;

import net.imglib2.ui.*;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.XYProjector;
import net.imglib2.img.ImgPlus;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;

/**
 * This uses XYProjector and RealARGBConverter to create an ARGBScreenImage
 * Assumes a 3D Img and produces an XY plane for a given Z
 * 
 * TODO: Add sub-image access, (x,y) offsets,…

 *
 * @author GBH
 */
public class PlanarProjection<T extends RealType<T> & NativeType<T>> {

	public String name;
	public ImgPlus<T> imgPlus;
	public ImageDisplay owner;
	public int width, height;
	public ARGBScreenImage screenImage;
	public RealARGBConverter<T> converter;
	public XYProjector<T, ARGBType> projector;

	public PlanarProjection(final String name, final ImgPlus<T> imgPlus, final ImageDisplay owner) {
		this.name = name;
		this.imgPlus = imgPlus;
		this.owner = owner;
		width = (int) imgPlus.getImg().dimension(0);
		height = (int) imgPlus.getImg().dimension(1);
		screenImage = new ARGBScreenImage(width, height);
		final int min = 0, max = 255;
		converter = new RealARGBConverter<T>(min, max);
		projector = new XYProjector<T, ARGBType>(imgPlus.getImg(),
				screenImage, converter);
		projector.map();
	}

}
