package lab5;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Peer implements IMensagem {

    ArrayList<PeerLista> alocados;

    public Peer() {
        alocados = new ArrayList<>();
    }

    //Cliente: invoca o metodo remoto 'enviar'
    //Servidor: invoca o metodo local 'enviar'
    @Override
    public Mensagem enviar(Mensagem mensagem) throws RemoteException {
        Mensagem resposta;
        try {
            System.out.println("Mensagem recebida: " + mensagem.getMensagem());
            resposta = new Mensagem(parserJSON(mensagem.getMensagem()));
        } catch (Exception e) {
            e.printStackTrace();
            resposta = new Mensagem("{\n" + "\"result\": false\n" + "}");
        }
        return resposta;
    }

    public String parserJSON(String json) {
        String result = "false";

        String fortune = "-1";

        String[] v = json.split(":");
        System.out.println(">>>" + v[1]);
        String[] v1 = v[1].split("\"");
        System.out.println(">>>" + v1[1]);
        if (v1[1].equals("write")) {
            String[] p = json.split("\\[");
            System.out.println(p[1]);
            String[] p1 = p[1].split("]");
            System.out.println(p1[0]);
            String[] p2 = p1[0].split("\"");
            System.out.println(p2[1]);
            fortune = p2[1];

            // Write in file
            Principal pv2 = new Principal();
            pv2.write(fortune);
        } else if (v1[1].equals("read")) {
            // Read file
            Principal pv2 = new Principal();
            fortune = pv2.read();
        }

        result = "{\n" + "\"result\": \"" + fortune + "\"" + "}";
        System.out.println(result);

        return result;
    }

    public void iniciar() {

        try {
            //Adquire aleatoriamente um ID do PeerList
            List<PeerLista> listaPeers = new ArrayList<>();
            for (PeerLista peer : PeerLista.values()) {
                listaPeers.add(peer);
            }

            Registry servidorRegistro;
            try {
                servidorRegistro = LocateRegistry.createRegistry(1099);
            } catch (java.rmi.server.ExportException e) { //Registro jah iniciado 
                System.out.print("Registro jah iniciado. Usar o ativo.\n");
            }
            servidorRegistro = LocateRegistry.getRegistry(); //Registro eh unico para todos os peers
            String[] listaAlocados = servidorRegistro.list();
            for (int i = 0; i < listaAlocados.length; i++) {
                System.out.println(listaAlocados[i] + " ativo.");
            }

            SecureRandom sr = new SecureRandom();
            /*PeerLista peer = listaPeers.get(sr.nextInt(listaPeers.size()));

            int tentativas = 0;
            boolean repetido = true;
            boolean cheio = false;
            while (repetido && !cheio) {
                repetido = false;
                peer = listaPeers.get(sr.nextInt(listaPeers.size()));
                for (int i = 0; i < listaAlocados.length && !repetido; i++) {

                    if (listaAlocados[i].equals(peer.getNome())) {
                        System.out.println(peer.getNome() + " ativo. Tentando proximo...");
                        repetido = true;
                        tentativas = i + 1;
                    }

                }
                //System.out.println(tentativas+" "+listaAlocados.length);

                //Verifica se o registro estah cheio (todos alocados)
                if (listaAlocados.length > 0
                        && //Para o caso inicial em que nao ha servidor alocado,
                        //caso contrario, o teste abaixo sempre serah true
                        tentativas == listaPeers.size()) {
                    cheio = true;
                }
            }
             */
            Scanner scanner = new Scanner(System.in);
            PeerLista peer = listaPeers.get(sr.nextInt(listaPeers.size()));

            int escolhaUsuario;
            int contador = 0;
            boolean aux1 = true;
            boolean aux2 = false;
            while (aux1) {
                do {
                    System.out.println("Escolha um número de 1 a 5:");
                    escolhaUsuario = scanner.nextInt();
                    if (escolhaUsuario < 1 || escolhaUsuario > 5) {
                        System.out.println("Deve ser um numero de 1 a 5");
                    }
                } while (escolhaUsuario < 1 || escolhaUsuario > 5);

                peer = listaPeers.get(escolhaUsuario - 1);
                aux2 = false;
                contador = 0;
                for (int i = 0; i < listaAlocados.length; i++) {
                    if (listaAlocados[i].equals(peer.getNome())) {
                        contador += 1;
                        System.out.println(peer.getNome() + " ativo. Tente outro Peer");
                        aux2 = true;
                    }
                }
                if (!aux2) {
                    aux1 = false;
                }
            }

            if (contador == 5) {
                System.out.println("Sistema cheio. Tente mais tarde.");
                System.exit(1);
            }

            IMensagem skeleton = (IMensagem) UnicastRemoteObject.exportObject(this, 0); //0: sistema operacional indica a porta (porta anonima)            

            servidorRegistro.rebind(peer.getNome(), skeleton);
            System.out.print(peer.getNome() + " Servidor RMI: Aguardando conexoes...");

            //---Cliente RMI
            new ClienteRMI().iniciarCliente();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Peer servidor = new Peer();
        servidor.iniciar();
    }
}
