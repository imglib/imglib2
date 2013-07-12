package net.imglib2.img.basictypeaccess.buffer;

public interface BufferDataAccess< A >
{
	A createArray( int numEntities );
	Object getCurrentStorageArray();
}
