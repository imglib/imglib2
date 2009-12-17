/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.cursor;

public interface LocalizableByDim3D extends Localizable3D
{
	public void fwdX();
	public void fwdY();
	public void fwdZ();

	public void bckX();
	public void bckY();
	public void bckZ();
	
	public void moveX( int steps );
	public void moveY( int steps );
	public void moveZ( int steps );

	public void moveTo( int x, int y, int z );
	public void moveRel( int x, int y, int z );
	
	public void setPosition( int posX, int posY, int posZ );
	public void setPositionX( int pos );
	public void setPositionY( int pos );
	public void setPositionZ( int pos );
}
