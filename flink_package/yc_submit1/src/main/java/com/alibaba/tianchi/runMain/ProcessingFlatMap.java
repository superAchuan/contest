package com.alibaba.tianchi.runMain;

//import  com.alibaba.tianchi.runMain.ImageProcesser;
import com.alibaba.tianchi.garbage_image_util.IdLabel;
import com.alibaba.tianchi.garbage_image_util.ImageData;
import com.intel.analytics.bigdl.transform.vision.image.opencv.OpenCVMat;
import com.intel.analytics.bigdl.transform.vision.image.util.BoundingBox;
import com.intel.analytics.zoo.pipeline.inference.JTensor;
import com.intel.analytics.zoo.pipeline.nnframes.NNImageReader;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.alibaba.tianchi.runMain.ImageUtil.*;

public class ProcessingFlatMap
        extends RichFlatMapFunction<ImageData, Tuple2<String, JTensor>> {

    public void DD(String s) {
        System.out.println("************** FHP * " + s);
    }

    @Override
    public void flatMap(ImageData value, Collector<Tuple2<String, JTensor>> out) throws Exception {
        int image_size = 299;

//        BufferedImage image = bytesToBufferedImage(value.getImage());
//
//        int m = Math.min(image.getWidth(), image.getHeight());
//        float ratio = 224.0f / ((float)m) ;
//        image = ImageUtil.resize(image, (int)(image.getWidth()*ratio), (int)(image.getHeight()*ratio));
//
//        File toFile = new File("./2.jpg");
//        ImageIO.write(image, "jpg",toFile);
//
////        float [][][]a = image.getData();
//        System.out.println(image.getData());
//        //saveImageFile(image, "/tmp/" + value.getId()+"01.jpg");
//
//        ImageProcesser p = new ImageProcesser();
//        JTensor jt = p.preProcess(value.getImage(), 224, 224);

        BufferedImage image = bytesToBufferedImage(value.getImage());
        image = ImageUtil.resize(image, image_size, image_size);
        float[] image1D = getPixels(image, image_size, image_size);

        JTensor jt = new JTensor(image1D, new int[]{1, image_size, image_size, 3});

//        int width = image.getWidth();
//        int height = image.getHeight();
//        for (int y = 0; y < height; y++)
//        {
//            for (int x = 0; x < width; x++)
//            {
//                int p = image.getRGB(x,y);
//                int a = (p>>24)&0xff;
//                int r = (p>>16)&0xff;
//                p = (a<<24) | (r<<16) | (0<<8) | 0;
//                image.setRGB(x, y, p);
//            }
//        }
//        File toFile = new File("./2.jpg");
//        ImageIO.write(image, "jpg",toFile);




//        float[] read_1D = getPixels(read_1, 224, 224);
//        for(int i = 0; i < 224 ; i++){
//            for (int j = 0; j < 224; j++){
//                read_1.getRGB(i,j);
//            }
//
//        }

        out.collect(new Tuple2<>(value.getId(), jt));


    }

    private BufferedImage bytesToBufferedImage(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
