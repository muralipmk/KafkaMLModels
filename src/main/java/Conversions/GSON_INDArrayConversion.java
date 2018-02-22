package Conversions;

import com.google.gson.Gson;
import org.datavec.image.loader.NativeImageLoader;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.util.ArrayUtil;

import java.io.File;
import java.util.ArrayList;

public class GSON_INDArrayConversion {

    public static void main(String[] args) throws Exception {
        NativeImageLoader imageLoader= new NativeImageLoader(28, 28, 1);
        File file= new File("/home/murali/Desktop/mnist_png/testing/0/3.png");

        //Create INDArray matrix.
        INDArray matrix=  imageLoader.asMatrix(file);

        System.out.println("INDArray Original: " + matrix);
        DataBuffer buff= matrix.data();
        double[] array= buff.asDouble();

        Gson gson= new Gson();
        String imageStr= gson.toJson(array);
        ArrayList<Double> result= gson.fromJson(imageStr, ArrayList.class);

        double[] r= ArrayUtil.toArrayDouble(result);

        int[] shape = matrix.shape();
        INDArray final_array= Nd4j.create(r, shape, 'c');
        System.out.println("After Java to INDarray: " + final_array.toString().getBytes().length);
    }
}
