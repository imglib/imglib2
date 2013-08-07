package net.imglib2.ui.util;

import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.ui.RenderSource;

public class FinalSource< T, A > implements RenderSource< T, A >
{
	protected final RealRandomAccessible< T > source;

	protected final A sourceTransform;

	protected final Converter< ? super T, ARGBType > converter;

	public FinalSource( final RealRandomAccessible< T > source, final A sourceTransform, final Converter< ? super T, ARGBType > converter )
	{
		this.source = source;
		this.sourceTransform = sourceTransform;
		this.converter = converter;
	}

	@Override
	public RealRandomAccessible< T > getInterpolatedSource()
	{
		return source;
	}

	@Override
	public A getSourceTransform()
	{
		return sourceTransform;
	}

	@Override
	public Converter< ? super T, ARGBType > getConverter()
	{
		return converter;
	}

}
