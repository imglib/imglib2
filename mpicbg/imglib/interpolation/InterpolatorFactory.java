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
package mpicbg.imglib.interpolation;

import mpicbg.imglib.Factory;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.Type;

public abstract class InterpolatorFactory<T extends Type<T>> implements Factory
{	
	protected OutsideStrategyFactory<T> outsideStrategyFactory;
	
	public InterpolatorFactory( final OutsideStrategyFactory<T> outsideStrategyFactory )
	{
		this.outsideStrategyFactory = outsideStrategyFactory;
	}
	
	public void setOutsideStrategyFactory( final OutsideStrategyFactory<T> outsideStrategyFactory ) 
	{ 
		this.outsideStrategyFactory = outsideStrategyFactory; 
	}
	
	public OutsideStrategyFactory<T> getOutsideStrategyFactory() 
	{ 
		return outsideStrategyFactory; 
	}
		
	public abstract Interpolator<T> createInterpolator( final Image<T> img );
	
	@Override
	public String getErrorMessage()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void printProperties()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setParameters(String configuration)
	{
		// TODO Auto-generated method stub
		
	}
	
}
