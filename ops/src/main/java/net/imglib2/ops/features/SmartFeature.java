package net.imglib2.ops.features;

public interface SmartFeature< V > extends Feature< V >
{
	Feature< V > instantiate( FeatureProcessorBuilder< ?, ? > processor );
}
