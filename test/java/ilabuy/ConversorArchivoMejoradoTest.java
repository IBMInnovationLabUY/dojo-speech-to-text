package ilabuy;

import junit.framework.TestCase;

import java.io.FileInputStream;

public class ConversorArchivoMejoradoTest extends TestCase {

    public void testConvertir() {
        final String USERNAME = "username";
        final String PASSWORD = "password";

        Conversor c = new ConversorArchivoMejorado(USERNAME, PASSWORD);

        try{
            FileInputStream fs = new FileInputStream("C:\\Users\\MarcosMacedo\\Desktop\\iralaplaza.wav");
            String resultado = c.convertir(fs);
            System.out.println(resultado);
            assertFalse(resultado.equals(""));
        }catch(Exception ex){
            fail(ex.getMessage());
        }
    }
}