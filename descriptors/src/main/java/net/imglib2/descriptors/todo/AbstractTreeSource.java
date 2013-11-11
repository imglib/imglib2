package net.imglib2.descriptors.todo;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.descriptors.Module;
import net.imglib2.descriptors.TreeSource;
import net.imglib2.descriptors.TreeSourceListener;

public abstract class AbstractTreeSource< O > implements TreeSource< O >
{

	private O obj;

	private final List< TreeSourceListener > listeners = new ArrayList< TreeSourceListener >();

	void update( O obj )
	{
		this.obj = obj;
		notifyListeners();
	}

	@Override
	public O get()
	{
		return obj;
	}

	@Override
	public boolean isEquivalentModule( Module< ? > output )
	{
		return false;
	}

	@Override
	public void notifyListeners()
	{
		for ( TreeSourceListener listener : listeners )
		{
			listener.updated( this );
		}
	}

	@Override
	public void registerListener( TreeSourceListener listener )
	{
		listeners.add( listener );
	}

	@Override
	public boolean isRegistered( TreeSourceListener listener )
	{
		return listeners.contains( listener );
	}
}
