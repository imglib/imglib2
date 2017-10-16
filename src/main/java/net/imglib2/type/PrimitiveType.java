package net.imglib2.type;

import net.imglib2.img.basictypeaccess.AccessFlags;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.volatiles.array.DirtyVolatileByteArray;

/**
 * Enumeration of Java primitive types which can back {@link NativeType}s.
 * <p>
 * In conjunction with {@link AccessFlags} this describes a specific
 * {@link ArrayDataAccess}. For example, {@code BYTE} with flags {@code DIRTY}
 * and {@code VOLATILE} specifies {@link DirtyVolatileByteArray}.
 * </p>
 *
 * @author Tobias Pietzsch
 */
public enum PrimitiveType
{
	BYTE,
	CHAR,
	SHORT,
	INT,
	LONG,
	FLOAT,
	DOUBLE,
	UNDEFINED;
}
