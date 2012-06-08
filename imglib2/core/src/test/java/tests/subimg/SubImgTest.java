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
package tests.subimg;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.subimg.SubImg;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

public class SubImgTest
{

	// Not very general test to check a {@link SubImg}
	@Test
	public void testSubImg()
	{
		// Random source img with pixel at (0,0,0) = 1!
		Img< UnsignedByteType > sourceImg = new ArrayImgFactory< UnsignedByteType >().create( new long[] { 10, 10, 10 }, new UnsignedByteType() );
		sourceImg.firstElement().set( 1 );

		// SubImg from [1,0,0] to [9,9,9] NOT including the first pixel
		SubImg< UnsignedByteType > subImg = new SubImg< UnsignedByteType >( sourceImg, new FinalInterval( new long[] { 1, 0, 0 }, new long[] { 9, 9, 9 } ), false );

		// Cursor over SUBIMG
		Cursor< UnsignedByteType > subCursor = subImg.cursor();

		long[] pos = new long[ subCursor.numDimensions() ];

		// Cursor position should clearly be 0, as this cursor should only
		// return values from [1,0,0] to [9,9,9] and only the first value is set
		assertTrue( subCursor.next().get() == 0 );

		subCursor.localize( pos );
		// Pos should be at [0,0,0] as the SubImg should act like an Img
		assertArrayEquals( pos, new long[ sourceImg.numDimensions() ] );

	}
}
