package com.alibaba.tianchi.runMain;

import com.alibaba.tianchi.garbage_image_util.IdLabel;
import com.alibaba.tianchi.garbage_image_util.ImageData;
import com.intel.analytics.zoo.models.common.ZooModel;
import com.intel.analytics.zoo.pipeline.inference.AbstractInferenceModel;
import com.intel.analytics.zoo.pipeline.inference.JTensor;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PredictionMapFunction extends RichFlatMapFunction<Tuple2<String, JTensor>, IdLabel> {


    private enum MODEL_TYPE {BYTES, FILE};
    private MODEL_TYPE modelType;

    private byte[] savedModelTarBytes;
    private int[] inputShape;
    private boolean ifReverseInputChannels;
    private float[] meanValues;
    private float scale;
    private String input;

    private String modelPath;
    private AbstractInferenceModel model;
//    private int count = 0;
    public PredictionMapFunction(String modelPath,
                                 int[] inputShape,
                                 boolean ifReverseInputChannels,
                                 float[] meanValues,
                                 float scale,
                                 String input) {
        super();
        this.modelType = MODEL_TYPE.FILE;
        this.modelPath = modelPath;

        this.savedModelTarBytes = savedModelTarBytes;
        this.inputShape = inputShape;
        this.ifReverseInputChannels = ifReverseInputChannels;
        this.meanValues = meanValues;
        this.scale = scale;
        this.input = input;
    }

    public PredictionMapFunction(byte[] savedModelTarBytes,
                                 int[] inputShape,
                                 boolean ifReverseInputChannels,
                                 float[] meanValues,
                                 float scale,
                                 String input) {
        super();
        this.modelType = MODEL_TYPE.BYTES;
        this.savedModelTarBytes = savedModelTarBytes;

        this.inputShape = inputShape;
        this.ifReverseInputChannels = ifReverseInputChannels;
        this.meanValues = meanValues;
        this.scale = scale;
        this.input = input;
    }

    @Override
    public void flatMap(Tuple2<String, JTensor> value, Collector<IdLabel> out) throws Exception {
        List<List<JTensor>> inputs = new ArrayList<>();
        List<JTensor> data = Arrays.asList(value.f1);
        inputs.add(data);

        //System.out.println("\n********* FHP - PredictionMapFunction.flatMap, string = " + value.f0);

        float[] outputData = model.doPredict(inputs).get(0).get(0).getData();
//        System.out.println("\n********* FHP - predict result: " + outputData);

        int index = indexOfMax(outputData);
        String label = Labels[index];
//        System.out.println("\n********* FHP - label = " + label);
//        //计算准确个数
//        String imageName = value.f0;
//        imageName = imageName.split("_")[0];
//        if(imageName.equals(label)){
//            count++;
//        }
//        System.out.println("!!!count:"+count);
        IdLabel idLablel = new IdLabel(value.f0, label);
        out.collect(idLablel);
    }

   @Override
    public void open(Configuration parameters) throws Exception {
        //System.out.println("\n********* FHP - PredictionMapFunction open()");
        model = new GarbgeClassificationInferenceModel();
        if (this.modelType == MODEL_TYPE.BYTES) {
//            System.out.println("\n********* FHP - load 1");
            model.loadTF(savedModelTarBytes, inputShape, ifReverseInputChannels, meanValues, scale, input);
        } else {
//            System.out.println("\n********* FHP - load 2");
            model.loadTF(modelPath, inputShape, ifReverseInputChannels, meanValues, scale, input);
        }
//        System.out.println("\n********* FHP - model = " + model.toString());
    }

    @Override
    public void close() throws Exception {
        model.release();
    }

    private int indexOfMax(float[] data) {
        if (data.length < 1) {
            throw new InvalidParameterException("cannot corresponding index.");
        }

        float max = data[0];
        int maxIndex = 0;
        for (int i = 0; i < data.length; i++) {
            //System.out.print(f + ", ");
            if (data[i] > max) {
                max = data[i];
                maxIndex = i;
            }
        }
//        System.out.println("\n********* FHP - max = [" + max + "] = " + (int)maxIndex);
        return maxIndex;
    }

    private int indexOfMax(List<List<JTensor>> results) {
        float max = 0 ;
        int classed = 0;
        for(List<JTensor> result : results) {
            float[] data = result.get(0).getData();
            for(int i = 0; i< data.length; i++){
                if(data[i] > max) {
                    max = data[i];
                    classed = i;
                }
            }
            //System.out.println("\n********* FHP - class " + classed);
        }
        return classed;
    }

    private static String[] Labels = {
            "PET塑料瓶",
            "一次性塑料手套",
            "一次性筷子",
            "一次性纸杯",
            "中性笔",
            "作业本",
            "信封",
            "充电器",
            "充电宝",
            "充电电池",
            "充电线",
            "剃须刀",
            "剪刀",
            "化妆品瓶",
            "医用棉签",
            "口服液瓶",
            "吹风机",
            "土豆",
            "塑料包装",
            "塑料桶",
            "塑料玩具",
            "塑料盆",
            "塑料盖子",
            "塑料袋",
            "外卖餐盒",
            "头饰",
            "奶粉",
            "姜",
            "干电池",
            "废弃衣服",
            "废弃食用油",
            "快递盒",
            "手表",
            "打火机",
            "扫把",
            "护手霜",
            "护肤品玻璃罐",
            "抱枕",
            "抹布",
            "拖把",
            "指甲油瓶子",
            "指甲钳",
            "插座",
            "无纺布手提袋",
            "旧帽子",
            "旧玩偶",
            "旧镜子",
            "暖宝宝贴",
            "杀虫剂",
            "杏核",
            "杯子",
            "果皮",
            "棉签",
            "椅子",
            "毛毯",
            "水彩笔",
            "水龙头",
            "泡沫盒子",
            "洗面奶瓶",
            "海绵",
            "消毒液瓶",
            "烟盒",
            "牙刷",
            "牙签",
            "牙膏皮",
            "牛奶盒",
            "瓶盖",
            "电视机",
            "电风扇",
            "白糖_盐",
            "空调机",
            "糖果",
            "红豆",
            "纸尿裤",
            "纸巾_卷纸_抽纸",
            "纸箱",
            "纽扣",
            "耳机",
            "胶带",
            "胶水",
            "自行车",
            "菜刀",
            "菜板",
            "葡萄干",
            "蒜头",
            "蒜皮",
            "蚊香",
            "蛋_蛋壳",
            "衣架",
            "袜子",
            "辣椒",
            "过期化妆品",
            "退热贴",
            "酸奶盒",
            "铅笔屑",
            "陶瓷碗碟",
            "青椒",
            "面膜",
            "香烟",
            "鼠标"
    };

//    private static String[] Labels = {
//            "一次性快餐盒",
//            "充电宝",
//            "剩饭剩菜",
//            "包",
//            "化妆品瓶",
//            "塑料玩具",
//            "塑料碗盆",
//            "塑料衣架",
//            "大骨头",
//            "干电池",
//            "快递纸袋",
//            "插头电线",
//            "旧衣服",
//            "易拉罐",
//            "枕头",
//            "毛绒玩具",
//            "水果果皮",
//            "水果果肉",
//            "污损塑料",
//            "洗发水瓶",
//            "烟蒂",
//            "牙签",
//            "玻璃杯",
//            "皮鞋",
//            "砧板",
//            "破碎花盆及碟碗",
//            "竹筷",
//            "纸板箱",
//            "茶叶渣",
//            "菜叶菜根",
//            "蛋壳",
//            "调料瓶",
//            "软膏",
//            "过期药物",
//            "酒瓶",
//            "金属食品罐",
//            "锅",
//            "食用油桶",
//            "饮料瓶",
//            "鱼骨"
//    };
}
