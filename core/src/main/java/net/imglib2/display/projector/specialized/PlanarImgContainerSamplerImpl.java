package net.imglib2.display.projector.specialized;

import net.imglib2.img.planar.PlanarImg.PlanarContainerSampler;

/**
 * Helps to sample a planar image by keeping track of the current slice.
 * 
 * @author Michael Zinsmaier, Martin Horn, Christian Dietz
 */
public class PlanarImgContainerSamplerImpl implements PlanarContainerSampler
{

	private int m_currentSliceIndex = -1;

	public PlanarImgContainerSamplerImpl( int startIndex )
	{
		m_currentSliceIndex = startIndex;
	}

	@Override
	public int getCurrentSliceIndex()
	{
		return m_currentSliceIndex;
	}

	public int fwd()
	{
		return m_currentSliceIndex++;
	}

	public void setCurrentSlice( int slice )
	{
		m_currentSliceIndex = slice;
	}

}
