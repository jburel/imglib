package mpicbg.imglib.algorithm;

import java.util.Arrays;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.cursor.special.StructuringElementCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyValueFactory;
import mpicbg.imglib.type.Type;

/**
 * ROIAlgorithm implements a framework against which to build operations of one image against
 * another, like convolution, cross-correlation, or morphological operations.  It operates by
 * creating a {@link RegionOfInterestCursor} from the input image, and an output image, which is
 * assumed to be of the same size as the input.  The process() method curses over the output image,
 * and at each step calls the abstract method patchOperation(), giving it the current location in 
 * the output and input images, as well as the RegionOfInterestCursor, which will curse over a 
 * patch of a given size in the input image.  patchOperation() is responsible for setting the
 * value of the pixel at the given position in the output image.
 * 
 * @author Larry Lindsey
 *
 * @param <T>
 * @param <S>
 */
public abstract class ROIAlgorithm <T extends Type<T>, S extends Type<S>>
	implements OutputAlgorithm<S>, Benchmark
{

	private final Image<T> inputImage;
	private Image<S> outputImage;
	private StructuringElementCursor<T> strelCursor;
	private final ImageFactory<S> imageFactory;		
	private String errorMsg;
	private String name;
	private long pTime;

	protected ROIAlgorithm(final ImageFactory<S> imFactory,
            final Image<T> inputImage,
            final int[] patchSize)
	{
	    this(imFactory, inputImage, patchSize,
	            new OutOfBoundsStrategyValueFactory<T>(
	                    inputImage.createType()));
	}
	
	protected ROIAlgorithm(final ImageFactory<S> imFactory,
	        final Image<T> inputImage, final int[] patchSize, 
	        final OutOfBoundsStrategyFactory<T> oobFactory)
	{
	    this(imFactory, new StructuringElementCursor<T>(
	            inputImage.createLocalizableByDimCursor(oobFactory),
	            patchSize));
	}
	
	protected ROIAlgorithm(final ImageFactory<S> imFactory,
	        StructuringElementCursor<T> strelCursor)
	{		
		int nd = strelCursor.getImage().getNumDimensions();
		
		final int[] initPos = new int[nd];
		
		pTime = 0;
		inputImage = strelCursor.getImage();
		
		outputImage = null;
		imageFactory = imFactory;
		errorMsg = "";
		name = null;
		Arrays.fill(initPos, 0);
		
		this.strelCursor = strelCursor;
		
	}

	
	protected abstract boolean patchOperation(
			final StructuringElementCursor<T> cursor,
			final S outputType);


    protected StructuringElementCursor<T> getStrelCursor()
    {
        return strelCursor;
    }

	/**
	 * Set the name given to the output image.
	 * @param inName the name to give to the output image.
	 */
	public void setName(final String inName)
	{
		name = inName;
	}
		
	public String getName()
	{
		return name;
	}
	


	/**
	 * Returns the {@link Image} that will eventually become the result of this
	 * {@link OutputAlgorithm}, and creates it if it has not yet been created.
	 * @return the {@link Image} that will eventually become the result of this
	 * {@link OutputAlgorithm}.
	 */
	protected Image<S> getOutputImage()
	{		
		if (outputImage == null)
		{
			if (name == null)
			{
				outputImage = imageFactory.createImage(inputImage.getDimensions());
			}
			else
			{
				outputImage = imageFactory.createImage(inputImage.getDimensions(), name);				
			}
		}
		return outputImage;
	}
	
	@Override
	public Image<S> getResult()
	{		
		return outputImage;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMsg;
	}
	
	protected void setErrorMessage(String message)
	{
		errorMsg = message;
	}
	
	@Override
	public boolean process()
	{
		final LocalizableByDimCursor<S> outputCursor =
		    getOutputImage().createLocalizableByDimCursor();
		final long sTime = System.currentTimeMillis();
		strelCursor.patchReset();
		
		try
		{
		
		while (strelCursor.patchHasNext())
		{
		    strelCursor.patchFwd();
		    outputCursor.setPosition(strelCursor.getPatchCenterCursor());

		    if (!patchOperation(strelCursor, outputCursor.getType()))
			{
				outputCursor.close();
				strelCursor.patchReset();
				return false;
			}
		}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
		    System.out.println("Found error, strel at : " + strelCursor.getPositionAsString());
		    System.out.println("Whereas outputcursor at : " + outputCursor.getPositionAsString());
		    throw e;
		}
		outputCursor.close();
		
		pTime = System.currentTimeMillis() - sTime;
		return true;		
	}
	
	@Override
	public boolean checkInput()
	{
		if (strelCursor.isActive())
		{
		    return true;
		}
		else
		{
		    setErrorMessage("StructuringElementCursor is inactive");
		    return false;
		}
	}
	
	public void close()
	{
		strelCursor.close();
	}
	
	public void closeAll()
	{
	    strelCursor.closeAll();
	    close();	    
	}

	@Override
	public long getProcessingTime()
	{
		return pTime;
	}

}