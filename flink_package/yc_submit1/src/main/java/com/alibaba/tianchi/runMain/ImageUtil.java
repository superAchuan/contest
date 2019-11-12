package com.alibaba.tianchi.runMain;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ComponentSampleModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ImageUtil {

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    /**
     * @param image
     * @param bandOffset 用于判断通道顺序
     * @return
     */
    private static boolean equalBandOffsetWith3Byte(BufferedImage image, int[] bandOffset) {
        if (image.getType() == BufferedImage.TYPE_3BYTE_BGR) {
            if (image.getData().getSampleModel() instanceof ComponentSampleModel) {
                ComponentSampleModel sampleModel = (ComponentSampleModel) image.getData().getSampleModel();
                if (Arrays.equals(sampleModel.getBandOffsets(), bandOffset)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断图像是否为BGR格式
     *
     * @return
     */
    public static boolean isBGR3Byte(BufferedImage image) {
        return equalBandOffsetWith3Byte(image, new int[]{0, 1, 2});
    }

    /**
     * 判断图像是否为RGB格式
     *
     * @return
     */
    public static boolean isRGB3Byte(BufferedImage image) {
        return equalBandOffsetWith3Byte(image, new int[]{2, 1, 0});
    }

    /**
     * 对图像解码返回RGB格式矩阵数据
     *
     * @param image
     * @return
     */
    public static byte[] getMatrixRGB(BufferedImage image) {
        if (null == image)
            throw new NullPointerException();
        byte[] matrixRGB;
        if (isRGB3Byte(image)) {
            matrixRGB = (byte[]) image.getData().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
        } else {
            // 转RGB格式
            BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(),
                    BufferedImage.TYPE_3BYTE_BGR);
            new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null).filter(image, rgbImage);
            matrixRGB = (byte[]) rgbImage.getData().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
        }
        return matrixRGB;
    }

    /**
     * 对图像解码返回BGR格式矩阵数据
     *
     * @param image
     * @return
     */
    public static byte[] getMatrixBGR(BufferedImage image) {
        if (null == image)
            throw new NullPointerException();
        byte[] matrixBGR;
        if (isBGR3Byte(image)) {
            matrixBGR = (byte[]) image.getData().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
        } else {
            // ARGB格式图像数据
            int intrgb[] = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
            matrixBGR = new byte[image.getWidth() * image.getHeight() * 3];
            // ARGB转BGR格式
            for (int i = 0, j = 0; i < intrgb.length; ++i, j += 3) {
                matrixBGR[j] = (byte) (intrgb[i] & 0xff);
                matrixBGR[j + 1] = (byte) ((intrgb[i] >> 8) & 0xff);
                matrixBGR[j + 2] = (byte) ((intrgb[i] >> 16) & 0xff);
            }
        }
        return matrixBGR;
    }


    public static byte[] imageToBytes(BufferedImage bImage, String format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, format, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    public static float[] getPixels(BufferedImage image, int height, int width) {
        float[] result = new float [height*width*3];
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color c = new Color(image.getRGB(x, y), true);
//                result[3*width*y + 3*x+1] = (float) c.getRed();
//                result[3*width*y + 3*x + 2] = (float)c.getGreen();
//                result[3*width*y + 3*x + 0] = (float)c.getBlue();
//                result[width*y + x] = (float) c.getRed();
//                result[height*width +  width*y + x] = (float)c.getGreen();
//                result[2*height*width +width*y + x] = (float)c.getBlue();
                result[width*y + x] = (float) c.getGreen()/255.0f;
                result[height*width +  width*y + x] = (float)c.getBlue()/255.0f;
                result[2*height*width +width*y + x] = (float)c.getRed()/255.0f;
            }
        }
        return result;
    }

    public static float[] ByteArrayToFloatArray(byte[] data)

    {

        float[] result = new float[data.length / 4];

        int temp = 0;

        for (int i = 0; i < data.length; i += 4)

        {

            temp = temp | (data[i] & 0xff) << 0;

            temp = temp | (data[i+1] & 0xff) << 8;

            temp = temp | (data[i+2] & 0xff) << 16;

            temp = temp | (data[i+3] & 0xff) << 24;

            result[i / 4] = Float.intBitsToFloat(temp);

        }

        return result;

    }

}
