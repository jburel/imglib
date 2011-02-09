/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
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
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib;


/**
 * An element that can be positioned in n-dimensional discrete space.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de> and Stephan Preibisch
 */
public interface Positionable extends EuclideanSpace
{
	/**
	 * Move by 1 in one dimension.
	 * 
	 * @param dim
	 */
	public void fwd( int dim );
	
	/**
	 * Move by -1 in one dimension.
	 * 
	 * @param dim
	 */
	public void bck( int dim );
	
	/**
	 * Move the element in one dimension for some distance.
	 *  
	 * @param distance
	 * @param dim
	 */
	public void move( int distance, int dim );
	
	/**
	 * Move the element in one dimension for some distance.
	 *  
	 * @param distance
	 * @param dim
	 */
	public void move( long distance, int dim );

	/**
	 * Move the element relative to its current location using an
	 * {@link Localizable} as distance vector.
	 * 
	 * @param localizable
	 */
	public void move( Localizable localizable );
	
	/**
	 * Move the element relative to its current location using an int[] as
	 * distance vector.
	 * 
	 * @param position
	 */
	public void move( int[] position );
	
	/**
	 * Move the element relative to its current location using a long[] as
	 * distance vector.
	 * 
	 * @param position
	 */
	public void move( long[] position );
	
	/**
	 * Place the element at the same location as a given {@link Localizable}
	 * 
	 * @param localizable
	 */
	public void setPosition( Localizable localizable );
	
	/**
	 * Set the position of the element.
	 * 
	 * @param position
	 */
	public void setPosition( int[] position );
	
	/**
	 * Set the position of the element.
	 * 
	 * @param position
	 */
	public void setPosition( long[] position );
	
	/**
	 * Set the position of the element for one dimension.
	 * 
	 * @param position
	 * @param dim
	 */
	public void setPosition( int position, int dim );		
	
	/**
	 * Set the position of the element for one dimension.
	 * 
	 * @param position
	 * @param dim
	 */
	public void setPosition( long position, int dim );
}