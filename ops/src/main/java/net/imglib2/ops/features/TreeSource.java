package net.imglib2.ops.features;

public interface TreeSource< O > extends Module< O >
{
	void notifyListeners();

	void registerListener( TreeSourceListener listener );

	boolean isRegistered( TreeSourceListener listener );
}
