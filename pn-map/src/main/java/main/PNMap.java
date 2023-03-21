package main;

import ckafka.data.Swap;
import ckafka.data.SwapData;
import com.fasterxml.jackson.databind.ObjectMapper;

import auxiliar.StaticLibrary;
import main.java.application.ModelApplication;
import mapa.GeographicMap;
import model.Region;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marcelle Dantas
 */

public class PNMap extends ModelApplication {

    /** Array of regions */
    private ArrayList<Region> regionList;

    /** JMapViewer-based map */
    private GeographicMap map;

    private Swap swap;
    private ObjectMapper objectMapper;


    public PNMap() {
        this.objectMapper = new ObjectMapper();
        this.swap = new Swap(objectMapper);


        /**
         * Getting regions
         */
        String workDir = System.getProperty("user.dir");
        System.out.println("Working Directory = " + workDir);
        String fullFilename = workDir + "/Bairros/RioDeJaneiro.txt";
        
		List<String> lines = StaticLibrary.readFilenamesFile(fullFilename);
		// reads each region file
		this.regionList = new ArrayList<Region>();	// region list
		for(String line : lines) {
			int regionNumber = Integer.parseInt(line.substring(0, line.indexOf(",")).trim());
			String filename = line.substring(line.indexOf(",")+1).trim();
			Region region = StaticLibrary.readRegion(filename, regionNumber);
			this.regionList.add(region);
		}

        

    }

    //record passa a conter as informações de contexto do nó móvel
    @Override
    public void recordReceived(ConsumerRecord record) {
        System.out.println(String.format("Mensagem recebida de %s", record.key()));
        try {

            SwapData data = swap.SwapDataDeserialization((byte[]) record.value());
            String text = new String(data.getMessage(), StandardCharsets.UTF_8);
            System.out.println("Mensagem recebida = " + text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        //Unicast messages: set environment variables
        Map<String, String> env = new HashMap<String, String>();
        env.putAll(System.getenv());
//        if (System.getenv("app.consumer.topics") == null)
//            env.put("app.consumer.topics", "GroupReportTopic");
        if (System.getenv("gd.one.consumer.topics") == null)
            env.put("gd.one.consumer.topics", "GroupReportTopic");
//        if (System.getenv("app.consumer.auto.offset.reset") == null)
//            env.put("app.consumer.auto.offset.reset", "latest");
//        if (System.getenv("app.consumer.bootstrap.servers") == null)
//            env.put("app.consumer.bootstrap.servers", "127.0.0.1:9092");
//        if (System.getenv("app.consumer.group.id") == null)
//            env.put("app.consumer.group.id", "gw-consumer");
//        if (System.getenv("app.producer.retries") == null)
//            env.put("app.producer.retries", "3");
//        if (System.getenv("app.producer.enable.idempotence") == null)
//            env.put("app.producer.enable.idempotence", "true");
//        if (System.getenv("app.producer.linger.ms") == null)
//            env.put("app.producer.linger.ms", "1");
//        if (System.getenv("app.producer.acks") == null)
//            env.put("app.producer.acks", "all");
        try {
            setEnv(env);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setEnv(Map<String, String> newenv) throws Exception {
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
            env.putAll(newenv);
            Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
            cienv.putAll(newenv);
        } catch (NoSuchFieldException e) {
            Class[] classes = Collections.class.getDeclaredClasses();
            Map<String, String> env = System.getenv();
            for (Class cl : classes) {
                if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                    Field field = cl.getDeclaredField("m");
                    field.setAccessible(true);
                    Object obj = field.get(env);
                    Map<String, String> map = (Map<String, String>) obj;
                    map.clear();
                    map.putAll(newenv);
                }
            }
        }
    }

}
