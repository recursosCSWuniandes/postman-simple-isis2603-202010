/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.postman.tests;

import co.edu.uniandes.csw.postman.utils.OSValidator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Asistente
 */
public class PostmanTestBuilder {

    private String environment;
    private String collection;
    private final String options;
    private String requests_failed;
    private String assertions_failed;
    private File tmpGeneral, tmpNewman;
    private File output;
    private BufferedWriter bwGeneral, bwNewman;
    private String command;

    private static final Logger LOGGER = Logger.getLogger(PostmanTestBuilder.class.getName());

    public PostmanTestBuilder() {
        //path = new PathBuilder();
        environment = " -e ";
        collection = "newman run ";
        options = " -r json-summary --reporter-summary-json-export ";
        assertions_failed = null;
        requests_failed = null;
    }

    public void setTestWithoutLogin(String collectionName, String environmentName) throws IOException {
        String path = System.getProperty("user.dir").concat(File.separator).concat("collections");

        //environment
        environment = environment.concat(path).concat(File.separator).concat(environmentName + ".json");

        //collection
        collection = collection.concat(path).concat(File.separator).concat(collectionName + ".json");

        tmpGeneral = File.createTempFile(collectionName, ".bat");
        output = File.createTempFile("output", ".json");
        bwGeneral = new BufferedWriter(new FileWriter(tmpGeneral));
        command = collection.concat(options).concat(output.getAbsolutePath()).concat(environment);
        bwGeneral.write(collection.concat(options).concat(output.getAbsolutePath()).concat(environment));
        bwGeneral.close();
        tmpGeneral.setExecutable(true);

        tmpNewman = File.createTempFile("tmpNewman", ".bat");
        bwNewman = new BufferedWriter(new FileWriter(tmpNewman));
        bwNewman.write(collection.concat(environment));
        bwNewman.close();
        tmpNewman.setExecutable(true);

        LOGGER.log(Level.INFO, "Collection name: {0}", path.concat(File.separator).concat(collectionName + ".json"));
        LOGGER.log(Level.INFO, "Environment name: {0}", path.concat(File.separator).concat(environmentName + ".json"));
        LOGGER.log(Level.INFO, "Output file name: {0}", output.getAbsolutePath());
        LOGGER.log(Level.INFO, "Command for newman: {0}", command);

        startProcess();
    }

    private void startProcess() {
        try {
            ProcessBuilder processBuilderGeneral;
            ProcessBuilder processBuilderNewman;
            if (OSValidator.isWindows()) {
                processBuilderGeneral = new ProcessBuilder(tmpGeneral.getAbsolutePath());
                processBuilderNewman = new ProcessBuilder(tmpNewman.getAbsolutePath());
            } else {
                processBuilderGeneral = new ProcessBuilder("bash", "-c", tmpGeneral.getAbsolutePath());
                Map<String, String> environmentGeneral = processBuilderGeneral.environment();
                processBuilderGeneral.directory(new File(System.getProperty("user.home")));
                String e = "/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin:" + System.getProperty("user.home") + "/.npm-global/bin:/Library/TeX/texbin:/opt/X11/bin";
                environmentGeneral.put("PATH", e);

                processBuilderNewman = new ProcessBuilder("bash", "-c", tmpNewman.getAbsolutePath());
                Map<String, String> environmentNewman = processBuilderNewman.environment();
                processBuilderNewman.directory(new File(System.getProperty("user.home")));
                environmentNewman.put("PATH", e);
            }

            processBuilderGeneral.redirectInput(Redirect.INHERIT);
            processBuilderGeneral.redirectOutput(Redirect.INHERIT);
            processBuilderGeneral.redirectError(Redirect.INHERIT);

            processBuilderNewman.redirectInput(Redirect.INHERIT);
            processBuilderNewman.redirectOutput(Redirect.INHERIT);
            processBuilderNewman.redirectError(Redirect.INHERIT);

            try {
                processBuilderGeneral.start().waitFor();
                processBuilderNewman.start().waitFor();
            } catch (InterruptedException ex) {
                Logger.getLogger(PostmanTestBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(new FileReader(output.getAbsolutePath()));
                JSONObject jsonObject = (JSONObject) obj;
                JSONObject run = (JSONObject) jsonObject.get("Run");
                JSONObject stats = (JSONObject) run.get("Stats");
                JSONObject requests = (JSONObject) stats.get("Requests");
                JSONObject assertions = (JSONObject) stats.get("Assertions");
                requests_failed = String.valueOf((long) requests.get("failed"));
                assertions_failed = String.valueOf((long) assertions.get("failed"));

            } catch (FileNotFoundException ex) {
                Logger.getLogger(PostmanTestBuilder.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | ParseException ex) {
                Logger.getLogger(PostmanTestBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(PostmanTestBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        tmpGeneral.deleteOnExit();
        tmpNewman.deleteOnExit();
        output.deleteOnExit();
    }

    /**
     * @return the requests_failed
     */
    public String getRequests_failed() {
        return requests_failed;
    }

    /**
     * @return the assertions_failed
     */
    public String getAssertions_failed() {
        return assertions_failed;
    }
}
