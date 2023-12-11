package br;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPConnectionFactory;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPPart;
import com.sun.xml.messaging.saaj.client.p2p.HttpSOAPConnectionFactory;

public class SoapClient {

    private String CAMPO1;
    private String id;

    public SoapClient(String CAMPO1, String id) {
        this.CAMPO1 = CAMPO1;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCAMPO1() {
        return CAMPO1;
    }

    public void setCAMPO1(String CAMPO1) {
        this.CAMPO1 = CAMPO1;
    }

    public void callSoapWebService_read(String soapEndpointUrl,
            String soapAction) {
        try {
            // Criar conexao SOAP
            SOAPConnectionFactory soapConnectionFactory
                    = HttpSOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection
                    = soapConnectionFactory.createConnection();
            // Enviar SOAP Message para o server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest_read(soapAction, 
                    CAMPO1, id), soapEndpointUrl);

            // Imprimir resposta
            System.out.println("Response SOAP Message:");
            soapResponse.writeTo(System.out);
            System.out.println();
            soapConnection.close();
        } catch (Exception e) {
            System.out.println("ERRO:");
            System.out.println(e.getMessage());
        }
    }

    private static SOAPMessage createSOAPRequest_read(String soapAction, String CAMPO1, String id) throws Exception {
        // Criar mensagem SOAP
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        // Criar envelope SOAP
        createSoapEnvelope_read(soapMessage, CAMPO1, id);
        soapMessage.saveChanges();
        // Exibir mensagem
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");
        return soapMessage;
    }

    private static void createSoapEnvelope_read(SOAPMessage soapMessage, String CAMPO1, String id) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();
        // Verificar no WSDL o namespace utilizado
        String myNamespace = "ns2";
        String myNamespaceURI = "http://ws.br/";
        // Preencher SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);
        // Preencher SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("consultarNomeFilme", myNamespace);
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("idFilme"); // O nome do elemento deve ser o esperado pelo serviço SOAP
        soapBodyElem1.addTextNode(CAMPO1);
        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("id"); // Adiciona o elemento do ID do cliente
        soapBodyElem2.addTextNode(id);
    }

    // ---------------------------------------------------
    public void callSoapWebService_write(String soapEndpointUrl,
            String soapAction) {
        try {
            // Criar conexao SOAP
            SOAPConnectionFactory soapConnectionFactory
                    = HttpSOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection
                    = soapConnectionFactory.createConnection();
            // Enviar SOAP Message para o server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest_write(soapAction, CAMPO1, id), soapEndpointUrl);

            // Imprimir resposta
            System.out.println("Response SOAP Message:");
            soapResponse.writeTo(System.out);
            System.out.println();
            soapConnection.close();
        } catch (Exception e) {
            System.out.println("ERRO:");
            System.out.println(e.getMessage());
        }
    }

    private static SOAPMessage createSOAPRequest_write(String soapAction, String CAMPO1, String id) throws Exception {
        // Criar mensagem SOAP
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        // Criar envelope SOAP
        createSoapEnvelope_write(soapMessage, CAMPO1, id);
        soapMessage.saveChanges();
        // Exibir mensagem
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");
        return soapMessage;
    }

    private static void createSoapEnvelope_write(SOAPMessage soapMessage, String CAMPO1, String id) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();
        // Verificar no WSDL o namespace utilizado
        String myNamespace = "ns2";
        String myNamespaceURI = "http://ws.br/";
        // Preencher SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);
        // Preencher SOAP Body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("alugarFilme", myNamespace);
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("idFilme"); // O nome do elemento deve ser o esperado pelo serviço SOAP
        soapBodyElem1.addTextNode(CAMPO1);
        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("id"); // Adiciona o elemento do ID do cliente
        soapBodyElem2.addTextNode(id);
    }
}
