/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.unittests;

import co.edu.uniandes.csw.postman.tests.PostmanTestBuilder;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author estudiante
 */
public class PostmanTestBuilderTest {
   
    public PostmanTestBuilderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void testPostmanTestBuilder() throws IOException {
        PostmanTestBuilder tp = new PostmanTestBuilder();
        tp.setTestWithoutLogin("Author-Tests-Paso5.postman_collection", "Entorno-Colecciones-Book.postman_environment");
        assertEquals(tp.getAssertions_failed(), "0");
        assertEquals(tp.getRequests_failed(), "0");
                
    }
}
