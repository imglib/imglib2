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
 * An execption is the 1D FFT implementation of Dave Hale which we use as a
 * library, wich is released under the terms of the Common Public License -
 * v1.0, which is available at http://www.eclipse.org/legal/cpl-v10.html  
 *
 * @author Stephan Preibisch
 */
package net.imglib2.algorithm.fft;

import net.imglib2.util.Util;

public class PhaseCorrelationPeak implements Comparable<PhaseCorrelationPeak>
{
	long[] position = null;
	long[] originalInvPCMPosition = null;
	float phaseCorrelationPeak = 0, crossCorrelationPeak = 0;
	long numPixels = 0;
	boolean sortPhaseCorrelation = true;
	
	public PhaseCorrelationPeak( final long[] position, final float phaseCorrelationPeak, final float crossCorrelationPeak )
	{
		this.position = position.clone();
		this.phaseCorrelationPeak = phaseCorrelationPeak;
		this.crossCorrelationPeak = crossCorrelationPeak;
	}
	
	public PhaseCorrelationPeak( final long[] position, final float phaseCorrelationPeak )
	{
		this ( position, phaseCorrelationPeak, 0 );
	}

	public PhaseCorrelationPeak( final long[] position )
	{
		this ( position, 0, 0 );
	}
	
	public PhaseCorrelationPeak()
	{
		this ( null, 0, 0 );
	}
	
	public void setPosition( final long[] position ) { this.position = position.clone(); }
	public void setOriginalInvPCMPosition( final long[] originalInvPCMPosition ) { this.originalInvPCMPosition = originalInvPCMPosition; }
	public void setPhaseCorrelationPeak( final float phaseCorrelationPeak ) { this.phaseCorrelationPeak = phaseCorrelationPeak; }
	public void setCrossCorrelationPeak( final float crossCorrelationPeak ) { this.crossCorrelationPeak = crossCorrelationPeak; }
	public void setSortPhaseCorrelation( final boolean sortPhaseCorrelation ) { this.sortPhaseCorrelation = sortPhaseCorrelation; }
	public void setNumPixels( final long numPixels ) { this.numPixels = numPixels; }
	
	public long[] getPosition() { return position.clone(); }
	public long[] getOriginalInvPCMPosition() { return originalInvPCMPosition; }
	public float getPhaseCorrelationPeak() { return phaseCorrelationPeak; }
	public float getCrossCorrelationPeak() { return crossCorrelationPeak; }
	public boolean getSortPhaseCorrelation() { return sortPhaseCorrelation; }
	public long getNumPixels() { return numPixels; }

	@Override
	public int compareTo( final PhaseCorrelationPeak o )
	{
		if ( sortPhaseCorrelation )
		{
			if ( this.phaseCorrelationPeak > o.phaseCorrelationPeak )
				return 1;
			else if ( this.phaseCorrelationPeak == o.phaseCorrelationPeak )
				return 0;
			else
				return -1;
		}
		else
		{		
			if ( this.crossCorrelationPeak > o.crossCorrelationPeak )
			{
				return 1;
			}
			else if ( this.crossCorrelationPeak == o.crossCorrelationPeak )
			{
				if ( this.numPixels >= o.numPixels )
					return 1;
				else
					return 0;
			}
			else
			{
				return -1;
			}
		}
	}
	
	@Override
	public String toString()
	{
		if ( originalInvPCMPosition == null)
			return Util.printCoordinates( position ) + ", phaseCorrelationPeak = " + phaseCorrelationPeak + ", crossCorrelationPeak = " + crossCorrelationPeak;
		else
			return Util.printCoordinates( position ) + " [" + Util.printCoordinates( originalInvPCMPosition ) + "], phaseCorrelationPeak = " + phaseCorrelationPeak + ", crossCorrelationPeak = " + crossCorrelationPeak; 
	}
}
