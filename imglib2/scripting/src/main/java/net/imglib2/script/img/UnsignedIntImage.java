/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.script.img;

import java.util.List;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.type.numeric.integer.UnsignedIntType;

/** Create a new n-dimensional image based on an array of float. */
/**
 * TODO
 *
 */
public class UnsignedIntImage extends ArrayImg<UnsignedIntType, IntArray>
{
	public UnsignedIntImage(final List<Number> dim) {
		this(AlgorithmUtil.asLongArray(dim));
	}

	public UnsignedIntImage(final long[] dim) {
		this(dim, new int[AlgorithmUtil.size(dim)]);
	}

	public UnsignedIntImage(final long[] dim, final int[] pixels) {
		super(new IntArray(pixels), dim, 1);
		setLinkedType(new UnsignedIntType(this));
	}
}
