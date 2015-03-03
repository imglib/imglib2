package net.imglib2.img.basictypeaccess.unsafe;

public interface UnsafeDataAccess< A >
{
	A createArray( int numEntities );
	Object getCurrentStorageArray();
}
