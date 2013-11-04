package net.imglib2.ops.descriptors;

public interface TreeSource< O > extends Module< O >
{
	void notifyListeners();

	void registerListener( TreeSourceListener listener );

	boolean isRegistered( TreeSourceListener listener );
}
