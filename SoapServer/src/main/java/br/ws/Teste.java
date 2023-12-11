
package br.ws;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPConnectionFactory;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPPart;
import java.io.ByteArrayOutputStream;

public class Teste {
    public String getFilmesJson() {
        String soapEndpointUrl = "http://localhost:8000/MovieService?wsdl";
        String soapAction = "http://localhost:8000/MovieService/get_movie_data";

        try {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            SOAPMessage soapRequest = createSOAPRequest(soapAction);
            SOAPMessage soapResponse = soapConnection.call(soapRequest, soapEndpointUrl);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapResponse.writeTo(out);
            String filmesJson = new String(out.toByteArray());

            soapConnection.close();

            return filmesJson;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private SOAPMessage createSOAPRequest(String soapAction) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        envelope.addNamespaceDeclaration("ns1", "http://ws.br/");

        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapElement = soapBody.addChildElement("get_movie_data", "ns1");

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();

        return soapMessage;
    }
    
        public static void main(String[] args) {
        // Chamar getFilmesJson assim que o servidor for iniciado
        Teste soapServer = new Teste();
        String filmesJson = soapServer.getFilmesJson();
        System.out.println("Filmes JSON: " + filmesJson);

        // Iniciar o servidor
        System.out.println("Iniciando o servidor SOAP...");
        // Adicione o código de inicialização do servidor aqui
    }
}
