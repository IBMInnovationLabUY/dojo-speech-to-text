package ilabuy;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionAlternative;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResult;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

import javax.sound.sampled.*;
import java.io.InputStream;

public class ConversorMicrofono implements Conversor {
    private String USERNAME;
    private String PASSWORD;

    public ConversorMicrofono(String sttUsername, String sttPassword){

        if(sttUsername.equals("") || sttPassword.equals("")){
            throw new IllegalArgumentException("Hay argumentos vacios");
        }

        USERNAME = sttUsername;
        PASSWORD = sttPassword;
    }

    public String convertir(InputStream stream) {

        SpeechToText service = new SpeechToText(USERNAME, PASSWORD);

        // Signed PCM AudioFormat with 16kHz, 16 bit sample size, mono
        int sampleRate = 16000;
        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("No hay un dispositivo de entrada disponible");
            System.exit(0);
        }

        try {
            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            AudioInputStream audio = new AudioInputStream(line);

            RecognizeOptions options = new RecognizeOptions.Builder()
                    .audio(audio)
                    .interimResults(true)
                    .timestamps(true)
                    .wordConfidence(true)
                    .model("es-ES_BroadbandModel")
                    //.inactivityTimeout(5) // use this to stop listening when the speaker pauses, i.e. for 5s
                    .contentType(HttpMediaType.AUDIO_RAW + ";rate=" + sampleRate)
                    .build();



            service.recognizeUsingWebSocket(options, new BaseRecognizeCallback() {

                @Override
                //Se llama cada vez que se transcribe audio en tiempo real
                public void onTranscription(SpeechRecognitionResults speechResults) {
                    SpeechRecognitionResult result = speechResults.getResults().get(0);
                    StringBuilder builder = new StringBuilder();

                    //Vamos formando el string final con las palabras reconocidas
                    for (SpeechRecognitionAlternative a : result.getAlternatives()) {

                        builder.append(a.getTranscript() + " ");
                    }

                    System.out.println(builder.toString());
                }

                @Override
                //Se llama cuando el servicio de Watson fue inicializado y esta esperando recibir bytes
                public void onListening() {
                    System.out.println("Escuchando");
                }

            });

            System.out.println("Este programa se ejecutará durante 30sec...");
            Thread.sleep(30 * 1000);

            //Cuando cerramos el InputStream que utiliza el WebSocket también se cierra el WebSocket (termina el reconocimiento)
            line.stop();
            line.close();

            return "exito";

        }catch (Exception ex){
            ex.printStackTrace();
            return "";
        }

    }
}
