/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.postman.tests;

import co.edu.uniandes.csw.postman.utils.CollectionBuilder;
import co.edu.uniandes.csw.postman.utils.PathBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    private CollectionBuilder cb;
    private Process process;
    private InputStream inputStream;
    private BufferedReader bf;
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

    public PostmanTestBuilder() {
        path = new PathBuilder();
        env = " -e ";
        coll = "newman run ";
        options = " -r json-summary --reporter-summary-json-export ";
        line = "";
        ln = null;
        prerequest_scripts_failed = null;
        test_scripts_failed = null;
        assertions_failed = null;
        iterations_failed = null;
        requests_failed = null;
    }

    public void setTestWithoutLogin(String collectionName) throws IOException {
        int fileCount = path.getFiles().length;

        if (path.validateDir() && fileCount > 0) {
            for (File f : path.getFiles()) {

                cb = new CollectionBuilder(f);
                if (cb.isCollection(collectionName)) {
                    coll = coll.concat(path.getPATH().concat("\\").concat(cb.getOriginalName()));
                    System.out.println("comando y ruta de ejecucion");
                    System.out.println(coll);
                    fileCount--;
                } else {
                    if (fileCount == 0) {
                        try {
                            throw new IOException();
                        } catch (IOException ie) {
                            System.out.println(ie.getMessage() + " no se encontro archivo: " + coll);
                        }
                    }
                }
            }
        }
        tmp = File.createTempFile(collectionName, ".bat");
        bw = new BufferedWriter(new FileWriter(tmp));
        bw.write(coll.concat(" --disable-unicode"));
        bw.close();
        tmp.setExecutable(true);
        startProcess();

    }

    public void setTestWithoutLogin(String collectionName, String environmentName) throws IOException {

        if (path.validateDir()) {
            for (File f : path.getFiles()) {
                cb = new CollectionBuilder(f);

                if (cb.isEnvironment(environmentName)) {
                    env = env.concat(path.getPATH().concat("\\").concat(cb.getOriginalName() + " --disable-unicode"));
                }

                if (cb.isCollection(collectionName)) {
                    coll = coll.concat(path.getPATH().concat("\\").concat(cb.getOriginalName()));
                }
            }
        }
        tmp = File.createTempFile(collectionName, ".bat");
        output = File.createTempFile("output", ".json");
        bw = new BufferedWriter(new FileWriter(tmp));
        bw.write(coll.concat(options).concat(output.getAbsolutePath()).concat(env));
        bw.close();
        tmp.setExecutable(true);
        startProcess();
    }

    private void startProcess() {

        try {
            System.out.println("ELeavin.." + output.getAbsolutePath());
            process = Runtime.getRuntime().exec(tmp.getAbsolutePath());
            process.waitFor();

            JSONParser parser = new JSONParser();
            try {
                Object obj = parser.parse(new FileReader(output.getAbsolutePath()));
                JSONObject jsonObject = (JSONObject) obj;
                JSONObject run = (JSONObject) jsonObject.get("Run");
                JSONObject stats = (JSONObject) run.get("Stats");
                JSONObject requests = (JSONObject) stats.get("Requests");
                JSONObject assertions = (JSONObject) stats.get("Assertions");
                requests_failed = String.valueOf((long) requests.get("failed"));
                assertions_failed = String.valueOf((long) assertions.get("failed"));
                System.out.println(requests_failed);
                System.out.println(assertions_failed);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
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
