package kafka_mlapp;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class KafkaMLApp {

    static List<Integer> labelList = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

    public static Properties setProperties(){

        Properties properties= new Properties();

        //set bootstrap server
        properties.setProperty("bootstrap.servers","10.0.3.58:9092");
        //set deserialization for key
        properties.setProperty("key.deserializer", StringDeserializer.class.getName());
        //set deserialization for value
        properties.setProperty("value.deserializer", StringDeserializer.class.getName());

        properties.setProperty("group.id","ml_app1");
        //set auto commit flag to true (though it is default true for specification i am  setting it.
        properties.setProperty("enable.auto.commit","true");
        //set auto commit interval in milli second. 1000= 1second
        properties.setProperty("auto.commit.interval.ms","1000");
        //set auto offset reset for the consumer
        // properties.setProperty("auto.offset.reset","");

        return properties;
    }


    public static void main(String[] args) throws Exception{
        //initialize the properties.
        Properties properties= setProperties();

        KafkaConsumer<String, String> consumer= new KafkaConsumer<String, String>(properties);

        consumer.subscribe(Arrays.asList("mnist_topic"));

        //Load the Network.
        while(true){
            ConsumerRecords<String,String> records= consumer.poll(10);
            for(ConsumerRecord<String,String> r: records){
                NormalizeInput input= new NormalizeInput(28,28,1, labelList);
                INDArray input_Matrix= input.normalizeInput(new File(r.value().toString()), 0, 1);
                INDArray result= LoadNetwork.getModel("/home/murali/mnist_model.zip").output(input_Matrix);

                System.out.println("Number detected as: " + labelList.get(NormalizeInput.getMaxIndex(result)) + " : Actual Label: " + new File(r.value().toString()).getParent().toString());
            }
        }
    }
}
