/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
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

package mpicbg.imglib.util;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.container.basictypecontainer.ByteAccess;
import mpicbg.imglib.container.basictypecontainer.DoubleAccess;
import mpicbg.imglib.container.basictypecontainer.ShortAccess;
import mpicbg.imglib.container.basictypecontainer.array.ByteArray;
import mpicbg.imglib.container.basictypecontainer.array.DoubleArray;
import mpicbg.imglib.container.basictypecontainer.array.ShortArray;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;
import mpicbg.imglib.type.numeric.integer.UnsignedShortType;
import mpicbg.imglib.type.numeric.real.DoubleType;

/**
 * TODO
 *
 */
final public class DevUtil
{	
	private DevUtil() {}
	
	final public static Image<UnsignedByteType> createImageFromArray( final byte[] data, final int[] dim )
	{
		final ByteAccess byteAccess = new ByteArray( data );
		final Array<UnsignedByteType, ByteAccess> array = 
			new Array<UnsignedByteType, ByteAccess>(new ArrayContainerFactory(), byteAccess, dim, 1 );
			
		// create a Type that is linked to the container
		final UnsignedByteType linkedType = new UnsignedByteType( array );
		
		// pass it to the DirectAccessContainer
		array.setLinkedType( linkedType );
		
		return new Image<UnsignedByteType>(array, new UnsignedByteType());
	}
	
	final public static Image<UnsignedShortType> createImageFromArray( final short[] data, final int[] dim )
	{
		final ShortAccess access = new ShortArray( data );
		final Array<UnsignedShortType, ShortAccess> array = 
			new Array<UnsignedShortType, ShortAccess>(new ArrayContainerFactory(), access, dim, 1 );
			
		// create a Type that is linked to the container
		final UnsignedShortType linkedType = new UnsignedShortType( array );
		
		// pass it to the DirectAccessContainer
		array.setLinkedType( linkedType );
		
		return new Image<UnsignedShortType>(array, new UnsignedShortType());
	}
	

	final public static Image<DoubleType> createImageFromArray( final double[] data, final int[] dim )
	{
		final DoubleAccess access = new DoubleArray( data );
		final Array<DoubleType, DoubleAccess> array = 
			new Array<DoubleType, DoubleAccess>(new ArrayContainerFactory(), access, dim, 1 );
			
		// create a Type that is linked to the container
		final DoubleType linkedType = new DoubleType( array );
		
		// pass it to the DirectAccessContainer
		array.setLinkedType( linkedType );
		
		return new Image<DoubleType>(array, new DoubleType());
	}
}
