package net.imglib2.blocks;

import net.imglib2.type.NativeType;

class ViewPropertiesOrError< T extends NativeType< T >, R extends NativeType< R > >
{
	private final ViewProperties< T, R > viewProperties;

	private final FallbackProperties< T > fallbackProperties;

	private final String errorMessage;

	ViewPropertiesOrError(
			final ViewProperties< T, R > viewProperties,
			final FallbackProperties< T > fallbackProperties,
			final String errorMessage )
	{
		this.viewProperties = viewProperties;
		this.fallbackProperties = fallbackProperties;
		this.errorMessage = errorMessage;
	}

	/**
	 * Whether {@code PrimitiveBlocks} copying from the view is supported, at
	 * all, either {@link #isFullySupported() fully} or via the fall-back implementation.
	 *
	 * @return {@code true}, if {@code PrimitiveBlocks} copying from the view is supported, at all.
	 */
	public boolean isSupported()
	{
		return isFullySupported() || fallbackProperties != null;
	}

	/**
	 * Whether optimized {@code PrimitiveBlocks} copying from the view is supported.
	 *
	 * @return {@code true}, if optimized {@code PrimitiveBlocks} copying from the view is supported.
	 */
	public boolean isFullySupported()
	{
		return viewProperties != null;
	}

	public ViewProperties< T, R > getViewProperties()
	{
		// TODO: null-check, throw Exception (which type?) with errorMessage
		return viewProperties;
	}

	public FallbackProperties< T > getFallbackProperties()
	{
		// TODO: null-check, throw Exception (which type?) with errorMessage
		return fallbackProperties;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}
}
