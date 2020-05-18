/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.postman.tests;

import co.edu.uniandes.csw.postman.utils.OSValidator;
import co.edu.uniandes.csw.postman.utils.PathBuilder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Asistente
 */
public class PostmanTestBuilder {

    private String env;
    private String coll;
    private String options;
    private PathBuilder path;
    private String line;
    private String ln;
    private String requests_failed;
    private String iterations_failed;
    private String assertions_failed;
    private String test_scripts_failed;
    private String prerequest_scripts_failed;
    private File tmp;
    private File output;
    private BufferedWriter bw;
    private String command;

    private static final Logger LOGGER = Logger.getLogger(PostmanTestBuilder.class.getName());

    public PostmanTestBuilder() {
        path = new PathBuilder();
        env = " -e ";
        coll = "newman run ";
        options = " -r json-summary --reporter-summary-json-export ";
        prerequest_scripts_failed = null;
        test_scripts_failed = null;
        assertions_failed = null;
        iterations_failed = null;
        requests_failed = null;
    }

    public void setTestWithoutLogin(String collectionName, String environmentName) throws IOException {
        String path = System.getProperty("user.dir").concat(File.separator).concat("collections");

        //environment
        env = env.concat(path).concat(File.separator).concat(environmentName + ".json");

        //collection
        coll = coll.concat(path).concat(File.separator).concat(collectionName + ".json");

        tmp = File.createTempFile(collectionName, ".bat");
        output = File.createTempFile("output", ".json");
        bw = new BufferedWriter(new FileWriter(tmp));
        command = coll.concat(options).concat(output.getAbsolutePath()).concat(env);
        bw.write(coll.concat(options).concat(output.getAbsolutePath()).concat(env));
        bw.close();
        tmp.setExecutable(true);

        LOGGER.log(Level.INFO, "Collection name: {0}", path.concat(File.separator).concat(collectionName + ".json"));
        LOGGER.log(Level.INFO, "Environment name: {0}", path.concat(File.separator).concat(environmentName + ".json"));
        LOGGER.log(Level.INFO, "Output file name: {0}", output.getAbsolutePath());
        LOGGER.log(Level.INFO, "Command for newman: {0}", command);

        startProcess();
    }

    private void startProcess() {
        try {
            ProcessBuilder processBuilder;
            if (OSValidator.isWindows()) {
                processBuilder = new ProcessBuilder(tmp.getAbsolutePath());
            } else {
                processBuilder = new ProcessBuilder("bash", "-c", tmp.getAbsolutePath());
                Map<String, String> environment = processBuilder.environment();
                processBuilder.directory(new File(System.getProperty("user.home")));
                String e = "/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin:" + System.getProperty("user.home") + "/.npm-global/bin:/Library/TeX/texbin:/opt/X11/bin";
                environment.put("PATH", e);
            }

            processBuilder.redirectInput(Redirect.INHERIT);
            processBuilder.redirectOutput(Redirect.INHERIT);
            processBuilder.redirectError(Redirect.INHERIT);

            try {
                processBuilder.start().waitFor();
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
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            Logger.getLogger(PostmanTestBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        tmp.deleteOnExit();
        output.deleteOnExit();
    }

    /**
     * @return the requests_failed
     */
    public String getRequests_failed() {
        return requests_failed;
    }

    /**
     * @return the iterations_failed
     */
    public String getIterations_failed() {
        return iterations_failed;
    }

    /**
     * @return the assertions_failed
     */
    public String getAssertions_failed() {
        return assertions_failed;
    }

    /**
     * @return the test_scripts_failed
     */
    public String getTest_scripts_failed() {
        return test_scripts_failed;
    }

    /**
     * @return the prerequest_scripts_failed
     */
    public String getPrerequest_scripts_failed() {
        return prerequest_scripts_failed;
    }
}
