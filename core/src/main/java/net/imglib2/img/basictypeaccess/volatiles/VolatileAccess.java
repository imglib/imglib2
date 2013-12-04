package net.imglib2.img.basictypeaccess.volatiles;

/**
 * A basic type access that can contain valid or invalid data.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface VolatileAccess
{
	boolean isValid();
}
