/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.algorithm.region.hypersphere;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;

/**
 * Iterate over all pixels in an n-dimensional sphere.
 *  
 * @param <T>
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Stephan Preibisch <preibisch@mpi-cbg.de>
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */

public class HyperSphereCursor< T > implements Cursor< T >
{
	final RandomAccessible< T > source;
	final protected long[] center;
	final protected RandomAccess< T > randomAccess;
	
	final protected long radius;
	final int numDimensions, maxDim;
	
	// the current radius in each dimension we are at
	final long[] r;
	
	// the remaining number of steps in each dimension we still have to go
	final long[] s;
	
	public HyperSphereCursor( final RandomAccessible< T > source, final long[] center, final long radius )
	{
		this.source = source;
		this.center = center;
		this.radius = radius;
		this.numDimensions = source.numDimensions();
		this.maxDim = numDimensions - 1;
		this.r = new long[ numDimensions ];
		this.s = new long[ numDimensions ];
		this.randomAccess = source.randomAccess();
		
		reset();
	}
	
	public HyperSphereCursor( final HyperSphereCursor< T > cursor )
	{
		this.source = cursor.source;
		this.center = cursor.center;
		this.radius = cursor.radius;
		this.numDimensions = cursor.numDimensions();
		this.maxDim = cursor.maxDim;

		this.r = cursor.r.clone();
		this.s = cursor.s.clone();
		
		this.randomAccess = source.randomAccess();
		this.randomAccess.setPosition( cursor.randomAccess );
	}

	@Override
	public boolean hasNext()
	{
		return s[ maxDim ] > 0; 
	}

	@Override
	public void fwd()
	{
		int d;
		for ( d = 0; d < numDimensions; ++d )
		{
			if ( --s[ d ] >= 0 )
			{
				randomAccess.fwd( d );
				break;
			}
			else
			{
				s[ d ] = r[ d ] = 0;
				randomAccess.setPosition( center[ d ], d );
			}
		}

		if ( d > 0 )
		{
			final int e = d - 1;
			final long rd = r[ d ];
			final long pd = rd - s[ d ];
			
			final long rad = (long)( Math.sqrt( rd * rd - pd * pd ) );
			s[ e ] = 2 * rad;
			r[ e ] = rad;
			
			randomAccess.setPosition( center[ e ] - rad, e );
		}
	}

	@Override
	public void reset()
	{		
		final int maxDim = numDimensions - 1;
		
		for ( int d = 0; d < maxDim; ++d )
		{
			r[ d ] = s[ d ] = 0;
			randomAccess.setPosition( center[ d ], d ); 
		}
		
		randomAccess.setPosition( center[ maxDim ] - radius - 1, maxDim  );
		
		r[ maxDim ] = radius;
		s[ maxDim ] = 1 + 2 * radius;			
	}

	@Override
	public void jumpFwd( final long steps )
	{
		for ( long j = 0; j < steps; ++j )
			fwd();
	}

	@Override
	public void localize( final float[] position ) { randomAccess.localize( position ); }

	@Override
	public void localize( final double[] position ) { randomAccess.localize( position ); }

	@Override
	public float getFloatPosition( final int d ) { return randomAccess.getFloatPosition( d ); }

	@Override
	public double getDoublePosition( final int d ) { return randomAccess.getDoublePosition( d ); }

	@Override
	public int numDimensions() { return numDimensions; }

	@Override
	public T get() { return randomAccess.get(); }

	@Override
	public T next() 
	{
		fwd();
		return get();
	}

	@Override
	public void remove() {}

	@Override
	public void localize( final int[] position ) { randomAccess.localize( position ); }

	@Override
	public void localize( final long[] position ) { randomAccess.localize( position ); }

	@Override
	public int getIntPosition( final int d ) { return randomAccess.getIntPosition( d ); }

	@Override
	public long getLongPosition( final int d )  { return randomAccess.getLongPosition( d ); }

	@Override
	public HyperSphereCursor< T > copyCursor() { return new HyperSphereCursor< T >( this ); }
	
	@Override
	public HyperSphereCursor< T > copy() { return copyCursor(); }
}
