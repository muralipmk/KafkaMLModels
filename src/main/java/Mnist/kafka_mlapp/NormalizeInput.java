package Mnist.kafka_mlapp;

import org.datavec.image.loader.NativeImageLoader;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NormalizeInput {
    public int Height= 0;
    public int Width= 0;
    public int Channels= 0;
    public List<Integer> Lables= new ArrayList<Integer>();

    public static int getMaxIndex(INDArray array){
        float max= 0;
        int maxIdx= 0;
        for(int i= 0;i < array.length();i++){
            if(max < array.getFloat(i)) {
                max = array.getFloat(i);
                maxIdx = i;
            }
        }
        return maxIdx;
    }

    public NormalizeInput(int height, int width, int channels, List<Integer> lables) {
        Height = height;
        Width = width;
        Channels = channels;
        Lables = lables;
    }

    public INDArray normalizeInput(File input_File, int min, int max) throws Exception{
        NativeImageLoader imageLoader= new NativeImageLoader(Height, Width, Channels);

        //Create INDArray matrix.
        INDArray matrix=  imageLoader.asMatrix(input_File);

        //Scale the image from 0-255 to 0-1.
        DataNormalization dataNormalization= new ImagePreProcessingScaler(min,max);
        dataNormalization.transform(matrix);

        return  matrix;
    }
}
