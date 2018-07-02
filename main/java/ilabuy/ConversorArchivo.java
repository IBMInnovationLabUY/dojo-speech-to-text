package ilabuy;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.*;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.List;

public class ConversorArchivo implements Conversor {
    private String USERNAME;
    private String PASSWORD;

    public ConversorArchivo(String sttUsername, String sttPassword){

        if(sttUsername.equals("") || sttPassword.equals("")){
            throw new IllegalArgumentException("Hay argumentos vacios");
        }

        USERNAME = sttUsername;
        PASSWORD = sttPassword;
    }

    //PRE: Acepta un archivo de audio en formato WAV
    //POST: Convierte a texto e imprime en pantalla
    public String convertir(InputStream stream) {

        //Se crea una instancia del servicio
        SpeechToText stt = new SpeechToText(USERNAME, PASSWORD);

        //Se crea un objeto con las opciones del audio a convertir
        RecognizeOptions options = new RecognizeOptions.Builder()
                .audio(stream)
                .model("es-ES_BroadbandModel")
                .contentType(RecognizeOptions.ContentType.AUDIO_WAV)
                .speakerLabels(true)
                .build();

        //Ejecutamos el reconocimiento de manera síncrona y con las opciones especificadas
        SpeechRecognitionResults as = stt.recognize(options).execute();

        StringBuilder builder = new StringBuilder();

        //Vamos formando el string final con las palabras reconocidas
        for (SpeechRecognitionResult r : as.getResults()) {

            for(SpeechRecognitionAlternative a : r.getAlternatives()){
                builder.append(a.getTranscript() + " ");
            }

        }

        return builder.toString();
    }

}
