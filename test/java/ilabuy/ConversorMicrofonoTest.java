package ilabuy;

import junit.framework.TestCase;

public class ConversorMicrofonoTest extends TestCase {
    final String USERNAME = "username";
    final String PASSWORD = "password";

    public void testConvertir() {

        Conversor c = new ConversorMicrofono(USERNAME, PASSWORD);

        try{
            //Se le pasa null ya que no necesita un archivo de audio
            c.convertir(null);
            assertTrue(true);
        }catch(Exception ex){
            fail(ex.getMessage());
        }

    }
}