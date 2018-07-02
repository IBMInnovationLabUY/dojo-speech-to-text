package ilabuy;

import junit.framework.TestCase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConversorArchivoTest extends TestCase {

    public void testConvertir() {
        final String USERNAME = "username";
        final String PASSWORD = "password";

        Conversor c = new ConversorArchivo(USERNAME, PASSWORD);

        try{
            FileInputStream fs = new FileInputStream("C:\\Users\\MarcosMacedo\\Desktop\\iralaplaza.wav");
            String resultado = c.convertir(fs);
            System.out.println(resultado);
            assertTrue(true);
        }catch(Exception ex){
            fail(ex.getMessage());
        }
    }

}