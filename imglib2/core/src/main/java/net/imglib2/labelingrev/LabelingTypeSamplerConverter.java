package net.imglib2.labelingrev;

import net.imglib2.Sampler;
import net.imglib2.converter.readwrite.SamplerConverter;
import net.imglib2.type.logic.BoolType;
import net.imglib2.type.numeric.IntegerType;

final class LabelingTypeSamplerConverter<L extends Comparable<L>> implements
		SamplerConverter<IntegerType<?>, LabelingType<L>> {

	private LabelingMapping<L> mapping = null;

	private BoolType dirtyCache = null;

	public LabelingTypeSamplerConverter(LabelingMapping<L> mapping) {
		this.mapping = mapping;
		this.dirtyCache = new BoolType();
	}

	@Override
	public LabelingType<L> convert(Sampler<IntegerType<?>> sampler) {
		return new LabelingType<L>(sampler, mapping, dirtyCache);
	}
}