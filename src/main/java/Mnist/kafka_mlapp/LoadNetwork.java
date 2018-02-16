package Mnist.kafka_mlapp;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

import java.io.File;

/**
 * This class loads the pretrained model from the specified path.
 */
public class LoadNetwork {

    public static MultiLayerNetwork model= null;

    /**
     * This method returns the pretrained model. If the model is null then model is not yet load then it loads the model
     * and returns reference to pretrained model.
     * @param trainedModel_Path
     * @return
     * @throws Exception
     */
    public static MultiLayerNetwork getModel(String trainedModel_Path) throws Exception{
        if(model == null)
            model= ModelSerializer.restoreMultiLayerNetwork(new File(trainedModel_Path));
        return model;
    }

    /**
     * This method returs the reference to update model.
     * @param updateTrainedModel_Path
     * @return
     * @throws Exception
     */
    public static MultiLayerNetwork updateModel(String updateTrainedModel_Path) throws Exception{
        model= ModelSerializer.restoreMultiLayerNetwork(new File(updateTrainedModel_Path));
        return model;
    }
}
