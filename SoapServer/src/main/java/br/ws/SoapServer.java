package br.ws;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.FileNotFoundException;

import java.io.FileReader;
import java.io.IOException;
import org.json.simple.parser.ParseException;

@WebService(serviceName = "Locadora")
public class SoapServer  {

    private static final String FILE_PATH = "C:/Users/gusta/OneDrive/Documents/NetBeansProjects/SoapServer/src/main/java/br/ws/filmes.json";
    private ControleConcorrencia controle = new ControleConcorrencia();
    public JSONObject filmesJson;

    public SoapServer() throws FileNotFoundException, IOException, ParseException {
        String filePath = "C:/Users/gusta/OneDrive/Documents/NetBeansProjects/SoapServer/src/main/java/br/ws/filmes.json";
        JSONParser parser = new JSONParser();
        FileReader reader = new FileReader(filePath);
        filmesJson = (JSONObject) parser.parse(reader);
        controle.iniciarAgendamentoLiberacaoTrancas();
        controle.iniciarAgendamentoVerificacaoFilasEspera();

        // Adiciona a propriedade "estado" com valor true a cada elemento do JSON
        for (Object key : filmesJson.keySet()) {
            String filmeId = (String) key;
            String filmeNome = (String) filmesJson.get(filmeId);

            JSONObject filmeObj = new JSONObject();
            filmeObj.put("nome", filmeNome);
            filmeObj.put("estado", false);
            filmesJson.put(filmeId, filmeObj);
        }
        int count = 1;
        for (Object key : filmesJson.keySet()) {
            String filmeId = (String) key;
            JSONObject filmeObj = (JSONObject) filmesJson.get(filmeId);
            String filmeNome = (String) filmeObj.get("nome");
            count++;
            if (count == 3) {
                break;
            }
        }
        // Adicione a inicialização do controle de concorrência
        controle = new ControleConcorrencia();
    }


    @WebMethod(operationName = "consultarNomeFilme")
    public String consultarNomeFilme(
            @WebParam(name = "idFilme") String idFilme,
            @WebParam(name = "id") String id) {
        try {
            String nomeFilme = "-1";
            // Antes de iniciar a operação, adquira o lock
            if (controle.adquirirLock(idFilme, id, "r")) {
//                System.out.print("Lock: r" + id + "[" + idFilme + "]   ");

                // Obter o nome do filme pelo ID
                if (filmesJson.containsKey(idFilme)) {
                    JSONObject filmeObj = (JSONObject) filmesJson.get(idFilme);
                    nomeFilme = (String) filmeObj.get("nome");
//                    System.out.print("Execute: r" + id + "[" + idFilme + "]");
                } else {
                    nomeFilme = "Null";
                }

                return nomeFilme;
            } 
            return nomeFilme;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @WebMethod(operationName = "alugarFilme")
    public boolean alugarFilme(
            @WebParam(name = "idFilme") String idFilme,
            @WebParam(name = "id") String id) {
        try {
            if (filmesJson.containsKey(idFilme)) {
                JSONObject filmeObj = (JSONObject) filmesJson.get(idFilme);

                // Verificar o estado atual do filme
                boolean estadoAtual = (boolean) filmeObj.get("estado");

                // Se o filme estiver disponível, marcá-lo como alugado (estado = true)
                if (!estadoAtual && controle.adquirirLock(idFilme, id, "w")) {
                    filmeObj.put("estado", true);

//                    aguardarTempo(350);
                    filmeObj.put("estado", false);
//                    System.out.print("Execute: w" + id + "[" + idFilme + "]  ");

                } else {
                }

            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void aguardarTempo(int milissegundos) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                Thread.sleep(milissegundos);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        executorService.shutdown();

        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @WebMethod(operationName = "somar")
    public int somar(@WebParam(name = "campo1") String CAMPO1, @WebParam(name = "campo2") String CAMPO2) {
        return Integer.parseInt(CAMPO1) + Integer.parseInt(CAMPO2);
    }

}
