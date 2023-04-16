package net.imglib2.blocks;

import java.util.function.Supplier;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.img.NativeImg;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.type.NativeType;
import net.imglib2.view.TransformBuilder;

/**
 * Data that describes {@code RandomAccessible} View that can be copied from using
 * {@link ViewPrimitiveBlocks}.
 * <p>
 * Use {@link ViewAnalyzer#getViewProperties(RandomAccessible)} to (try to)
 * extract {@code ViewProperties} for a given {@code RandomAccessible}.
 *
 * @param <T>
 * 		type of the view {@code RandomAccessible}
 * @param <R>
 * 		type of the root {@code NativeImg}
 */
class ViewProperties< T extends NativeType< T >, R extends NativeType< R > >
{
	private final T viewType;

	private final NativeImg< R, ? > root;

	private final R rootType;

	private final Extension extension;

	private final MixedTransform transform;

	private final boolean hasTransform;

	private final MixedTransform permuteInvertTransform;

	private final boolean hasPermuteInvertTransform;

	private final Supplier< Converter< R, T > > converterSupplier;

	/**
	 * Create {@code ViewProperties}.
	 *
	 * @param viewType pixel type of the View to copy from
	 * @param root the {@code NativeImg} at the root of the View chain
	 * @param rootType pixel type of the root {@code NativeImg}
	 * @param extension out-of-bounds extension to apply to the root
	 * @param transform the concatenated transform from the final View to the root.
	 * @param permuteInvertTransform captures axis permutation and inversion part in {@code transform}.
	 * @param converterSupplier creates {@code Converter} from {@code rootType} to {@code viewType}.
	 */
	ViewProperties(
			final T viewType,
			final NativeImg< R, ? > root,
			final R rootType,
			final Extension extension,
			final MixedTransform transform,
			final MixedTransform permuteInvertTransform,
			final Supplier< ? extends Converter< ?, ? > > converterSupplier )
	{
		this.viewType = viewType;
		this.root = root;
		this.rootType = rootType;
		this.extension = extension;
		this.transform = transform;
		hasTransform = !TransformBuilder.isIdentity( transform );
		this.permuteInvertTransform = permuteInvertTransform;
		hasPermuteInvertTransform = !TransformBuilder.isIdentity( permuteInvertTransform );
		this.converterSupplier = converterSupplier == null ? null : () -> ( Converter< R, T > ) converterSupplier.get();
	}

	@Override
	public String toString()
	{
		return "ViewProperties{" +
				"viewType=" + viewType.getClass().getSimpleName() +
				", root=" + root +
				", rootType=" + rootType.getClass().getSimpleName() +
				", extension=" + extension +
				", transform=" + transform +
				", hasPermuteInvertTransform=" + hasPermuteInvertTransform +
				", permuteInvertTransform=" + permuteInvertTransform +
				", converterSupplier=" + converterSupplier +
				'}';
	}

	public T getViewType()
	{
		return viewType;
	}

	public NativeImg< R, ? > getRoot()
	{
		return root;
	}

	public R getRootType()
	{
		return rootType;
	}

	public Extension getExtension()
	{
		return extension;
	}

	/**
	 * Returns {@code true} if there is a non-identity {@link #getTransform() transform}.
	 *
	 * @return {@code true} iff the {@link #getTransform() transform} is not identity.
	 */
	public boolean hasTransform()
	{
		return hasTransform;
	}

	public MixedTransform getTransform()
	{
		return transform;
	}

	/**
	 * Returns {@code true} if there is a non-identity {@link
	 * #getPermuteInvertTransform() permute-invert} transform.
	 *
	 * @return {@code true} iff the {@link #getPermuteInvertTransform()
	 * permute-invert} transform is not identity.
	 */
	public boolean hasPermuteInvertTransform()
	{
		return hasPermuteInvertTransform;
	}

	public MixedTransform getPermuteInvertTransform()
	{
		return permuteInvertTransform;
	}

	public boolean hasConverterSupplier()
	{
		return converterSupplier != null;
	}

	public Supplier< Converter< R, T > > getConverterSupplier()
	{
		return converterSupplier;
	}
}
