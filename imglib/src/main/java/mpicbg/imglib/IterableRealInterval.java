/**
 * Copyright (c) 2010, Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package mpicbg.imglib;

import mpicbg.imglib.container.Img;

/**
 * <p><em>f</em>:R<sup><em>n</em></sup>&isin;[0,<em>s</em>]&rarr;T</em></p>
 * 
 * <p>A function over real space and a finite set of elements in the
 * target domain <em>T</em>.  All target elements <em>T</em> can be accessed
 * through Iterators.  There is an iterator that tracks its source location
 * at each move and one that calculates it on request only.  Depending on
 * the frequency of requesting the source location, either of them is
 * optimal in terms of speed.</p>
 * 
 * @param <T>
 * @param <F>
 * @param <I>
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public interface IterableRealInterval< T > extends RealInterval, Iterable< T >
{
	/**
	 * <p>Returns a {@link RealCursor} that iterates with
	 * optimal speed without calculating the location at each iteration step.
	 * Localization is performed on demand.</p>
	 * 
	 * <p>Use this where localization is required rarely/ not for each
	 * iteration.</p>
	 * 
	 * @return I fast iterating iterator
	 */
	public RealCursor< T > cursor();

	/**
	 * <p>Returns a {@link RealLocalizable} {@link Iterator} that calculates its
	 * location at each iteration step.  That is, localization is performed
	 * with optimal speed.</p>
	 * 
	 * <p>Use this where localization is required often/ for each
	 * iteration.</p>
	 * 
	 * @return I fast localizing iterator
	 */
	public RealCursor< T > localizingCursor();
	
	/**
	 * <p>Returns the number of elements in this
	 * {@link IterableRealInterval Function}.</p>
	 * 
	 * @return number of elements
	 */
	public long size();

	/**
	 * Get the first element of this {@IterableRealInterval}.
	 * This is a shortcut for <code>cursor().next()</code>.
	 * 
	 * This can be used to create a new variable of type T
	 * using <code>firstElement().createVariable()</code>,
	 * which is useful in generic methods to store temporary
	 * results, e.g., a running sum over pixels in the {@IterableRealInterval}.
	 * 
	 * @return the first element in iteration order.
	 */
	public T firstElement();
	
	/**
	 * <p>Returns <tt>true</tt> if this {@link IterableRealInterval} and
	 * <em>f</em> can be copied by synchronous iteration.  That is, having
	 * an {@link Iterator} on this and another {@link Iterator} on <em>f</em>,
	 * moving both in synchrony will point both of them to corresponding
	 * locations in their source domain.  In other words, this and <em>f</em>
	 * have the same iteration order and means and the same number of
	 * elements.</p>
	 * 
	 * <p><em>Note</em> that returning <tt>false</tt> does not mean that
	 * copying by synchronous iteration is not possible but only that the
	 * method cannot decide if the required conditions are met.</p>
	 * 
	 * @param f the {@link Img} to be tested
	 * 
	 * @return <tt>true</tt> if copy by iteration is definitely possible,
	 *   <tt>false</tt> otherwise
	 */
	public boolean equalIterationOrder( final IterableRealInterval< ? > f );
}