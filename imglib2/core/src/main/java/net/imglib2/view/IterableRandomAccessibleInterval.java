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

package net.imglib2.view;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPositionable;

/**
 * Generates {@link Cursor Cursors} that iterate a
 * {@link RandomAccessibleInterval} in flat order, that is: row by row, plane
 * by plane, cube by cube, ...
 *
 * @author Stephan Saalfeld
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class IterableRandomAccessibleInterval< T > implements IterableInterval< T >, RandomAccessibleInterval< T >
{
	final protected RandomAccessibleInterval< T > interval;
	final long size;

	public static < T > IterableRandomAccessibleInterval< T > create( final RandomAccessibleInterval< T > interval )
	{
		return new IterableRandomAccessibleInterval< T >( interval );
	}

	public IterableRandomAccessibleInterval( final RandomAccessibleInterval< T > interval )
	{
		this.interval = interval;
		final int n = interval.numDimensions();
		long s = interval.dimension( 0 );
		for ( int d = 1; d < n; ++d )
			s *= interval.dimension( d );
		size = s;
	}

	@Override
	public long size()
	{
		return size;
	}

	@Override
	public T firstElement()
	{
		// we cannot simply create an randomaccessible on interval
		// this does not ensure it will be placed at the first element
		return cursor().next();
	}

	@Override
	public FlatIterationOrder iterationOrder()
	{
		return new FlatIterationOrder( interval );
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		return iterationOrder().equals( f.iterationOrder() );
	}

	@Override
	public double realMin( final int d )
	{
		return interval.realMin( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		interval.realMin( min );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		interval.realMin( min );
	}

	@Override
	public double realMax( final int d )
	{
		return interval.realMax( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		interval.realMax( max );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		interval.realMax( max );
	}

	@Override
	public int numDimensions()
	{
		return interval.numDimensions();
	}

	@Override
	public Iterator< T > iterator()
	{
		return cursor();
	}

	@Override
	public long min( final int d )
	{
		return interval.min( d );
	}

	@Override
	public void min( final long[] min )
	{
		interval.min( min );
	}

	@Override
	public void min( final Positionable min )
	{
		interval.min( min );
	}

	@Override
	public long max( final int d )
	{
		return interval.max( d );
	}

	@Override
	public void max( final long[] max )
	{
		interval.max( max );
	}

	@Override
	public void max( final Positionable max )
	{
		interval.max( max );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		interval.dimensions( dimensions );
	}

	@Override
	public long dimension( final int d )
	{
		return interval.dimension( d );
	}

	@Override
	public Cursor< T > cursor()
	{
		return new RandomAccessibleIntervalCursor< T >( interval );
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return cursor();
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		return interval.randomAccess();
	}

	@Override
	public RandomAccess< T > randomAccess( final Interval i )
	{
		return interval.randomAccess( i );
	}
}
