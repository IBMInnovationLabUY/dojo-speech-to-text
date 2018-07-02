package ilabuy;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class ConversorArchivoMejorado implements Conversor {
    private String USERNAME;
    private String PASSWORD;

    public ConversorArchivoMejorado(String sttUsername, String sttPassword) {

        if (sttUsername.equals("") || sttPassword.equals("")) {
            throw new IllegalArgumentException("Hay argumentos vacios");
        }

        USERNAME = sttUsername;
        PASSWORD = sttPassword;
    }

    //PRE: Acepta un archivo de audio en formato WAV
    //POST: Convierte a texto e imprime en pantalla
    public String convertir(InputStream stream) {

        String transcript = "";

        SpeechToText service = new SpeechToText(USERNAME, PASSWORD);

        //Creamos el modelo para el lenguaje
        CreateLanguageModelOptions createOptions = new CreateLanguageModelOptions.Builder()
                .name("modelo-uruguayo")
                .baseModelName("es-ES_BroadbandModel")
                .description("Modelo para reconocer el vocabulario urbano uruguayo")
                .build();
        LanguageModel myModel = service.createLanguageModel(createOptions).execute();
        String id = myModel.getCustomizationId();

        try {

            //Creamos un nuevo corpus para el modelo y lo agregamos al mismo
            AddCorpusOptions addOptions = new AddCorpusOptions.Builder()
                    .customizationId(id)
                    .corpusName("corpus-1")
                    .corpusFile(new File("C:\\Users\\MarcosMacedo\\Desktop\\corpus.txt"))
                    .corpusFileContentType(HttpMediaType.TEXT_PLAIN)
                    .allowOverwrite(false)
                    .build();

            //Aplicamos el corpus al modelo creado en Watson
            service.addCorpus(addOptions).execute();

            //Hacemos polling para ver cuando terminó Watson de procesar y agregar al corpus al modelo
            GetCorpusOptions getOptions = new GetCorpusOptions.Builder()
                    .customizationId(id)
                    .corpusName("corpus-1")
                    .build();
            for (int x = 0; x < 30 && (service.getCorpus(getOptions).execute()).getStatus() != Corpus.Status.ANALYZED; x++) {
                Thread.sleep(5000);
            }

            // Get all corpora
            ListCorporaOptions listCorporaOptions = new ListCorporaOptions.Builder()
                    .customizationId(id)
                    .build();
            Corpora corpora = service.listCorpora(listCorporaOptions).execute();
            System.out.println(corpora);


            // Get specific corpus
            Corpus corpus = service.getCorpus(getOptions).execute();
            System.out.println(corpus);

            //Agregamos manualmente al corpus la palabra rateó
            service.addWord(new AddWordOptions.Builder()
                    .customizationId(id)
                    .wordName("rateó")
                    .word("rateó")
                    .displayAs("rateó")
                    .addSoundsLike("rateó")
                    .build()).execute();


            //Se entrena el modelo en watson
            TrainLanguageModelOptions trainOptions = new TrainLanguageModelOptions.Builder()
                    .customizationId(id)
                    .wordTypeToAdd(TrainLanguageModelOptions.WordTypeToAdd.ALL)
                    .build();
            service.trainLanguageModel(trainOptions).execute();

            //Se hace pooling para esperar que el modelo esté listo
            for (int x = 0; x < 30 && myModel.getStatus() != LanguageModel.Status.AVAILABLE; x++) {
                GetLanguageModelOptions optionsMdl = new GetLanguageModelOptions.Builder()
                        .customizationId(id)
                        .build();
                myModel = service.getLanguageModel(optionsMdl).execute();
                Thread.sleep(10000);
            }

            RecognizeOptions recognizeOptionsWithModel = new RecognizeOptions.Builder()
                    .model("es-ES_BroadbandModel")
                    .customizationId(id)
                    .audio(stream)
                    .contentType(HttpMediaType.AUDIO_WAV)
                    .build();


            //Ahora se utiliza el modelo que se creó para transcribir
            SpeechRecognitionResults tsResult = service.recognize(recognizeOptionsWithModel).execute();

            StringBuilder builder = new StringBuilder();

            //Vamos formando el string final con las palabras reconocidas
            for (SpeechRecognitionResult r : tsResult.getResults()) {

                for(SpeechRecognitionAlternative a : r.getAlternatives()){
                    builder.append(a.getTranscript() + "");
                    builder.append(System.getProperty( "line.separator" ));
                }

            }

            transcript = builder.toString();

        } catch (Exception ex) {
            System.out.println("Hola error");
        } finally {
            DeleteLanguageModelOptions deleteOptions = new DeleteLanguageModelOptions.Builder()
                    .customizationId(id)
                    .build();
            service.deleteLanguageModel(deleteOptions).execute();
        }

        StringBuilder builder = new StringBuilder();

        return transcript;

    }

}
