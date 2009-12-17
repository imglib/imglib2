package mpicbg.imglib.image.display.imagej;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.type.Type;
import mpicbg.models.InvertibleCoordinateTransform;

public class InverseTransformDescription<T extends Type<T>> 
{
	final InvertibleCoordinateTransform transform;
	final InterpolatorFactory<T> factory;
	final Image<T> image;
	final float[] offset;
	final int numDimensions;
	
	public InverseTransformDescription( final InvertibleCoordinateTransform transform, final InterpolatorFactory<T> factory, final Image<T> image )
	{
		this.transform = transform;
		this.factory = factory;
		this.image = image;
		this.numDimensions = image.getNumDimensions();
		this.offset = new float[ numDimensions ];
	}
	
	public InvertibleCoordinateTransform getTransform() { return transform; }
	public InterpolatorFactory<T> getInterpolatorFactory() { return factory; }
	public Image<T> getImage() { return image; }
	
	public void setOffset( final float[] offset )
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.offset[ d ] = offset[ d ];
	}
	
	public float[] getOffset() { return offset.clone(); }
	public void getOffset( final float[] offset )
	{
		for ( int d = 0; d < numDimensions; ++d )
			offset[ d ] = this.offset[ d ];		
	}
}
