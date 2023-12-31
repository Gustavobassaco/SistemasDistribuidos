package br;

public class Principal {

    public static void main(String[] args) {
        System.out.println("Principal: envia mensagem para o servidor");
        String soapEndpointUrl
                = "http://localhost:8080/SoapServer/Locadora?wsdl";
        String soapAction
                = "http://localhost:8080/SoapServer/Locadora";

        String id1 = String.valueOf(1);
        String id2 = String.valueOf(2);

        String inx = String.valueOf(1);
        String iny = String.valueOf(2);
        String inz = String.valueOf(3);
        String inu = String.valueOf(4);

        //String CAMPO2 = String.valueOf(c2);
        SoapClient sc1 = new SoapClient(iny, id1);
        SoapClient sc2 = new SoapClient(inx, id2);

        sc1.callSoapWebService_read(soapEndpointUrl, soapAction);
        
        sc1.setCAMPO1(iny);
        sc1.callSoapWebService_write(soapEndpointUrl, soapAction);
  
        sc2.setCAMPO1(iny);
        sc2.callSoapWebService_write(soapEndpointUrl, soapAction);
        
        sc2.setCAMPO1(inx);
        sc2.callSoapWebService_read(soapEndpointUrl, soapAction);

        sc1.setCAMPO1(inz);
        sc1.callSoapWebService_read(soapEndpointUrl, soapAction);

        
        System.out.println("Fim!");
    }
}
