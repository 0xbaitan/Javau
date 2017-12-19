

import java.awt.image.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;
import java.util.*;
import javax.imageio.stream.*;

/**
 * This specifically class is made for conversions of images to and from Base64
 * Code. Its subsidiary functions help in resizing and making a color
 * transparent in an Image. Modification to this code will be done in the future.
 * 
 * @see java.awt.Image
 * @see java.awt.image.BufferedImage
 * @see java.awt.image.ImageFilter
 * @author Tanish Baidya
 * @version v1.0
 */
public class ByteImage {

	/**
	 * Extracts a bytes from an Image file and then stores it in a byte array.
	 * 
	 * @param imageFile
	 *            the image file from which the bytes are to be extracted.
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @return the byte array containing the bits of the image.
	 */

	@SuppressWarnings("null")
	public byte[] getBytes(File imageFile) throws IOException {
		// byteList of type LinkedList<Byte> which will hold the temporary bits
		// and then is converted to the array
		java.util.List<Byte> byteList = new LinkedList<Byte>();

		ImageInputStream stream = ImageIO.createImageInputStream(imageFile);
		// Individual bits in the ImageInputStream
		byte bits;
		Byte bit;
		if (stream != null)
			while ((bits = stream.readByte()) != -1) {
				// bits in the form of Byte
				bit = new Byte(bits);
				// Bytes being stored in the list
				byteList.add(bit);
			}
		else {
			stream.flush();
			stream.close();
		}
		// Length of the LinkedList<Byte> and byte[], increasing efficiency
		int length = byteList.size();

		// byte[] to store all the bits of the image
		byte imageBytes[] = new byte[length];
		int index = 0;
		for (index = 0; index < length; index++) {
			imageBytes[index] = byteList.get(index);
		}
		return imageBytes;
	}

	/**
	 * Produces a BufferedImage from this given array of bytes. If the byte
	 * array is corrupted or the image is unreadable or null, a message will be
	 * displayed on the console.
	 * 
	 * @param imageBytes
	 *            the array containing the bytes.
	 * 
	 * @return the BufferedImage produced from this given array of bytes.
	 */
	public BufferedImage getImage(byte[] imageBytes) {
		BufferedImage byteImage = null;
		try {
			// Gets the BufferedImage
			byteImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

		} catch (IOException exception) {
			// If I/O error occurs a message will be displayed
			System.err.println(exception.getMessage());
		}
		if (byteImage == null)
			// If for some reason the image is null, this message will be
			// displayed
			System.err.println("Image cannot be produced from this byte array / or no byte array found");
		return byteImage;
	}

	/**
	 * Extracts bytes in the form of an array from an image, by creating a
	 * temporary file in System. Though advised not to use this function as all
	 * images will be in PNG format only.
	 * 
	 * @param image
	 *            the image from which the bytes are to be extracted.
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @return the byte array containing the bits of the image.
	 */
	public byte[] getBytes(Image image) throws IOException {
		// Creating a temporary file of PNG format in the system from which the
		// ByteImage.getBytes(File imageFile) function can be used on it
		File file = File.createTempFile("Temporary", ".png");
		ImageIO.write((RenderedImage) image, "png", file);
		// Extraction of bytes from the image file
		byte imageBytes[] = this.getBytes(file);
		// After the extraction is done, the image file is no more needed and is
		// deleted from the system
		file.delete();
		return imageBytes;
	}

	/**
	 * Encodes the specified image file into a String using the Base64 encoding
	 * scheme.
	 * 
	 * @param imageFile
	 *            the image file to encode
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @return A String containing the resulting Base64 encoded charactacters
	 *         from the image file.
	 */
	public String getEncodedImage(File imageFile) throws IOException {
		return Base64.getEncoder().encodeToString(this.getBytes(imageFile));

	}

	/**
	 * Encodes the specified image into a String using the Base64 encoding
	 * scheme.
	 * 
	 * @param image
	 *            the image to encode
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @return A String containing the resulting Base64 encoded charactacters
	 *         from the image.
	 */
	public String getEncodedImage(Image image) throws IOException {
		return Base64.getEncoder().encodeToString(this.getBytes(image));

	}

	/**
	 * Morphs the image into a shape and returns a BufferedImage. The image's
	 * alpha bits beyond the specified shape will become 0, in other words the
	 * color in those coordinates become transparent.
	 * 
	 * @param image
	 *            the image whose shape is to be changed.
	 * @param shape
	 *            The specified shape to be implemented on the image
	 * 
	 * 
	 * @return The new BufferedImage with the specified shape implemented.
	 * @see java.awt.Shape
	 */
	public BufferedImage setShape(Image image, final Shape shape) {
		// ImageFilter to create the image after implementing the shape
		ImageFilter filter = new RGBImageFilter() {
			public final int filterRGB(int x, int y, int rgb) {

				// If the current coordinates of the image does not lie in the
				// shape's boundaries, the rbg returned has 0 alpha bits. Else
				// the rgb originally present is returned
				if (!shape.contains(x, y)) {
					return 0x00FFFFFF & rgb;
				} else {
					return rgb;
				}
			}
		};
		ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
		Image imageNew = Toolkit.getDefaultToolkit().createImage(ip);
		return this.getBufferedImage(imageNew);
	}

	/**
	 * Converts the image to BufferedImage.
	 * 
	 * @param image
	 *            the image to be converted into BufferedImage.
	 * 
	 * 
	 * 
	 * @return The BufferedImage after conversion.
	 *
	 */
	public BufferedImage getBufferedImage(Image im) {
		// A new BufferedImage is created which shares the same dimensions and
		// properties of the image
		BufferedImage bi = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		// The graphics of the null BufferedImage
		Graphics bg = bi.getGraphics();
		// The original image is drawn over the null BufferedImage, hence
		// becoming the exact copy
		bg.drawImage(im, 0, 0, null);
		// The graphics is no more required and is disposed
		bg.dispose();
		return bi;
	}

	/**
	 * Decodes the Base64 code into an image (BufferedImage).
	 * 
	 * @param encodedString
	 *            the encoded string which the Base64 code of the image.
	 * 
	 * 
	 * 
	 * @return The BufferedImage after decoding the Base64 code.
	 *
	 */
	public BufferedImage setDecodedImage(String encodedString) {
		return this.getImage(Base64.getDecoder().decode(encodedString));
	}

	/**
	 * Resizes the specified image with the specified height and width.
	 * 
	 * @param width
	 *            the new width of the image.
	 * @param height
	 *            the new height of the image.
	 * 
	 * @param image
	 *            the image which is to be resized
	 * 
	 * @return The BufferedImage after resizing the image to its specified width
	 *         and height.
	 * @see java.awt.image.ReplicateScaleFilter
	 *
	 */
	public BufferedImage resize(int width, int height, Image image) {

		ImageFilter filter = new ReplicateScaleFilter(width, height);
		ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
		return this.getBufferedImage(Toolkit.getDefaultToolkit().createImage(ip));
	}

	/**
	 * Recolors the image to specified color of equivalent saturation and hue.
	 * 
	 * @param image
	 *            the image to be recolored.
	 * @param singleShade
	 *            The color to which the image will be recolored .
	 * 
	 * 
	 * @return The BufferedImage after recoloring the image.
	 * @see java.awt.image.RGBImageFilter
	 *
	 */

	public BufferedImage singleShadeRecolor(Image image, Color singleShade) {
		float vals[] = new float[3];
		vals = Color.RGBtoHSB(singleShade.getRed(), singleShade.getGreen(), singleShade.getBlue(), vals);
		// Hue of the specified color
		float shadeHue = vals[0];
		// Saturation of the specified color
		float shadeSat = vals[1];

		ImageFilter filter = new RGBImageFilter() {

			public final int filterRGB(int x, int y, int rgb) {

				Color pixelColor = new Color(rgb);
				float pixelVals[] = new float[3];
				pixelVals = Color.RGBtoHSB(pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue(), pixelVals);
				// Lum of the current rgb of x and y coordinates of the image
				float pixelLum = pixelVals[2];
				// The rgb returned of the shade of the specified color
				return Color.getHSBColor(shadeHue, shadeSat, pixelLum).getRGB();

			}

		};
		ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
		Image imageNew = Toolkit.getDefaultToolkit().createImage(ip);
		return this.getBufferedImage(imageNew);
	}

	/**
	 * The colors in the image which match the specified color turns
	 * transparent.
	 * 
	 * @param image
	 *            the image to be undergone changes.
	 * @param color
	 *            The color which is to be made transparent in the image.
	 * 
	 * 
	 * @return The BufferedImage after recoloring the image.
	 * @see java.awt.image.RGBImageFilter
	 *
	 */

	public BufferedImage makeTransparent(Image image, Color color) {
		ImageFilter filter = new RGBImageFilter() {
			public final int filterRGB(int x, int y, int rgb) {
				// rgb of the specified color
				int markerRGB = color.getRGB() | 0xFF000000;
				// if the rgb of the image in coordinates x and y match
				// markerRGB, color become transparent else original rgb is
				// returned
				if ((rgb | 0xFF000000) == markerRGB) {
					// Alpha bits become 0
					return 0x00FFFFFF & rgb;
				} else {
					return rgb;
				}

			}

		};
		ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
		Image imageNew = Toolkit.getDefaultToolkit().createImage(ip);
		return this.getBufferedImage(imageNew);
	}

}