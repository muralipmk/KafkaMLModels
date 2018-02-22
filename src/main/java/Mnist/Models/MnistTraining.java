package Mnist.Models;

import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Random;

public class MnistTraining {

        private static Logger log = LoggerFactory.getLogger(MnistTraining.class);

        public static void main(String[] args) throws Exception {
            // image information
            // 28 * 28 grayscale
            // grayscale implies single channel
            int height = 28;
            int width = 28;
            int channels = 1;
            int rngseed = 123;
            Random randNumGen = new Random(rngseed);
            int batchSize = 128;
            int outputNum = 10;
            int numEpochs = 40;

            // Define the File Paths
            File trainData = new File( "/home/murali/Desktop/mnist_png/training");
            File testData = new File( "/home/murali/Desktop/mnist_png/testing");

            // Define the FileSplit(PATH, ALLOWED FORMATS,random)
            FileSplit train = new FileSplit(trainData, NativeImageLoader.ALLOWED_FORMATS, randNumGen);
            FileSplit test = new FileSplit(testData, NativeImageLoader.ALLOWED_FORMATS, randNumGen);

            // Extract the parent path as the image label
            ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();

            ImageRecordReader recordReader = new ImageRecordReader(height, width, channels, labelMaker);

            // Initialize the record reader
            // add a listener, to extract the name
            recordReader.initialize(train);
            //recordReader.setListeners(new LogRecordListener());
            System.out.println(recordReader.getLabels());
            // DataSet Iterator
            DataSetIterator dataIter = new RecordReaderDataSetIterator(recordReader, batchSize, 1, outputNum);

            // Scale pixel values to 0-1
            DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
            scaler.fit(dataIter);
            dataIter.setPreProcessor(scaler);


            System.out.println("******Training the model*****");
            // Build Our Neural Network
            log.info("BUILD MODEL");
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                    .seed(rngseed)
                    .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                    .iterations(1)
                    .learningRate(0.006)
                    .updater(Updater.NESTEROVS)
                    .regularization(true).l2(1e-4)
                    .list()
                    .layer(0, new DenseLayer.Builder()
                            .nIn(height * width)
                            .nOut(100)
                            .activation(Activation.RELU)
                            .weightInit(WeightInit.XAVIER)
                            .build())
                    .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                            .nIn(100)
                            .nOut(outputNum)
                            .activation(Activation.SOFTMAX)
                            .weightInit(WeightInit.XAVIER)
                            .build())
                    .pretrain(false).backprop(true)
                    .setInputType(InputType.convolutional(height, width, channels))
                    .build();

            MultiLayerNetwork model = new MultiLayerNetwork(conf);

            // The Score iteration Listener will log
            // output to show how well the network is training
            model.setListeners(new ScoreIterationListener(10));

            log.info("TRAIN MODEL");
            for (int i = 0; i < numEpochs; i++) {
                model.fit(dataIter);
            }

            log.info("EVALUATE MODEL");
            recordReader.reset();

            // The model trained on the training dataset split
            // now that it has trained we evaluate against the
            // test data of images the network has not seen

            recordReader.initialize(test);
            DataSetIterator testIter = new RecordReaderDataSetIterator(recordReader, batchSize, 1, outputNum);
            scaler.fit(testIter);
            testIter.setPreProcessor(scaler);

            log.info(recordReader.getLabels().toString());

            // Create Eval object with 10 possible classes
            Evaluation eval = new Evaluation(outputNum);

            // Evaluate the network
            while (testIter.hasNext()) {
                DataSet next = testIter.next();
                INDArray output = model.output(next.getFeatureMatrix());
                // Compare the Feature Matrix from the model
                // with the labels from the RecordReader
                eval.eval(next.getLabels(), output);
            }

            System.out.println(eval.stats().toString());
            log.info(eval.stats());

            File file= new File("/home/murali/mnist_model.zip");

            boolean saveUpdater= false;

            ModelSerializer.writeModel(model,file,saveUpdater);
        }
}
