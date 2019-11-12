package com.alibaba.tianchi.runMain;

import com.alibaba.tianchi.garbage_image_util.ConfigConstant;
import com.alibaba.tianchi.garbage_image_util.ImageClassSink;
import com.alibaba.tianchi.garbage_image_util.ImageDirSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ZooMain {

    public static void main(String[] args) throws Exception {
        /*
        String savedModelTarPath = System.getenv(ConfigConstant.IMAGE_MODEL_PACKAGE_PATH);
        long fileSize = new File(savedModelTarPath).length();
        InputStream is = new FileInputStream(savedModelTarPath);
        byte[] savedModelTarBytes = new byte[(int)fileSize];
        is.read(savedModelTarBytes);
         */

        String modelPath = System.getenv("IMAGE_MODEL_PATH");
        int image_size = 299;

        boolean ifReverseInputChannels = true;
        int[] inputShape = {1, image_size, image_size, 3};
        float[] meanValues = {0f, 0f, 0f};
        float scale = 1.0f;
        String input = "input_1";

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        ImageDirSource src = new ImageDirSource();

        env.addSource(src).setParallelism(1)
                .flatMap(new ProcessingFlatMap()).setParallelism(1)
                .flatMap(new PredictionMapFunction(modelPath, inputShape, ifReverseInputChannels, meanValues, scale, input))
                .setParallelism(1)
                .addSink(new ImageClassSink()).setParallelism(1);
        env.execute();
    }
}
