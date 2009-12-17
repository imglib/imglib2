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
package mpicbg.imglib.type.label;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.basictypecontainer.CharContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.BasePairTypeDisplay;
import mpicbg.imglib.type.BasePairType;
import mpicbg.imglib.type.TypeImpl;
import mpicbg.imglib.type.label.BasePairBitType.Base;

public class BasePairCharType extends TypeImpl<BasePairCharType> implements BasePairType<BasePairCharType>
{
	final CharContainer<BasePairCharType> charStorage;
	char[] v;
	
	// this is the constructor if you want it to read from an array
	public BasePairCharType( CharContainer<BasePairCharType> charStorage )
	{
		this.charStorage = charStorage;
	}
	
	// this is BasePairCharType constructor if you want it to be a variable
	public BasePairCharType( final char value )
	{
		charStorage = null;
		v = new char[ 1 ];
		v[ 0 ] = value;
		i = 0;
	}

	// this is the constructor if you want it to be a variable
	public BasePairCharType() { this( 'N' ); }

	@Override
	public CharContainer<BasePairCharType> createSuitableContainer( final ContainerFactory storageFactory, final int dim[] )
	{
		return storageFactory.createCharInstance( dim, 1 );	
	}

	@Override
	public BasePairTypeDisplay<BasePairCharType> getDefaultDisplay( final Image<BasePairCharType> image )
	{
		return new BasePairTypeDisplay<BasePairCharType>( image );
	}
	
	@Override
	public void updateDataArray( final Cursor<?> c ) 
	{ 
		v = charStorage.getCurrentStorageArray( c ); 
	}
	
	public char getChar() { return v[ i ]; }
	public void setChar( final char f ) { v[ i ] = f; }

	public void set( final Base base ) 
	{
		switch ( base )
		{
			case A: setChar('A'); return;
			case T: setChar('T'); return;
			case G: setChar('G'); return;
			case C: setChar('C'); return;
			case gap: setChar(' '); return;
			default: setChar('N'); return;
		}
	}
	
	public Base get() 
	{
		final char value = getChar();
		
		switch ( value )
		{
			case 'A': return Base.A;
			case 'T': return Base.T;
			case 'G': return Base.G;
			case 'C': return Base.C;
			case ' ': return Base.gap;
			default: return Base.N;
		}
	}
	
	@Override
	public void set( final BasePairCharType c ) { v[ i ] = c.getChar(); }

	@Override
	public int compareTo( final BasePairCharType c ) 
	{ 
		final char input = getChar();
		final char compare = c.getChar();
		
		if ( input == compare )
		{
			return 0;
		}
		else
		{
			switch ( input )
			{
				case ' ': return -1; 
				case 'N': if ( compare == ' ' ) return 1; else return -1;
				case 'A': if ( compare == ' ' || compare == 'N' ) return 1; else return -1;
				case 'T': if ( compare == 'G' || compare == 'C' ) return -1; else return 1;
				case 'G': if ( compare == 'C' ) return -1; else return 1;
				default: return 1;
			}
		}
	}

	@Override
	public void complement() 
	{
		final char base = getChar();
		switch ( base )
		{
			case 'A': setChar( 'T' ); break;
			case 'T': setChar( 'A' ); break;
			case 'G': setChar( 'C' ); break;
			case 'C': setChar( 'G' ); break;
		}
	}

	@Override
	public byte baseToValue()
	{
		final char base = getChar();
		
		switch ( base )
		{
			case 'N': return 1;
			case 'A': return 2;
			case 'T': return 3;
			case 'G': return 4;
			case 'C': return 5;
			default: return 0;
		}		
	}
	
	@Override
	public BasePairCharType[] createArray1D(int size1){ return new BasePairCharType[ size1 ]; }

	@Override
	public BasePairCharType[][] createArray2D(int size1, int size2){ return new BasePairCharType[ size1 ][ size2 ]; }

	@Override
	public BasePairCharType[][][] createArray3D(int size1, int size2, int size3) { return new BasePairCharType[ size1 ][ size2 ][ size3 ]; }

	@Override
	public BasePairCharType getType() { return this; }

	@Override
	public BasePairCharType createType( Container<BasePairCharType> container )
	{
		return new BasePairCharType( (CharContainer<BasePairCharType>)container );
	}
	
	@Override
	public BasePairCharType createVariable(){ return new BasePairCharType( 'N' ); }
	
	@Override
	public BasePairCharType copyVariable(){ return new BasePairCharType( v[ i ] ); }
	
	@Override
	public String toString() { return "" + v[i]; }
}
