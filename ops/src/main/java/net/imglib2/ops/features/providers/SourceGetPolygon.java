package net.imglib2.ops.features.providers;

import java.awt.Polygon;

import net.imglib2.ops.features.Source;
import net.imglib2.ops.features.providers.sources.GetPolygon;

public class SourceGetPolygon extends GetPolygon implements Source< Polygon >
{
	private Polygon m_polygon;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Source Polygon";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SourceGetPolygon copy()
	{
		return new SourceGetPolygon();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Polygon recompute()
	{
		return m_polygon;
	}

	@Override
	public void update( Polygon obj )
	{
		m_polygon = obj;
	}
}
