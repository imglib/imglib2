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

public interface LocalizableByDim extends Localizable
{
	public void fwd( int dim );
	public void bck( int dim );
	
	public void move( int steps, int dim );

	public void moveTo( LocalizableCursor<?> cursor );
	public void moveTo( int position[] );
	public void moveRel( int position[] );
	
	public void setPosition( LocalizableCursor<?> cursor );
	public void setPosition( int position[] );
	public void setPosition( int position, int dim );		
}
