
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Cliente {
    
    private static Socket socket;
    private static DataInputStream entrada;
    private static DataOutputStream saida;
    
    private int porta=1025;
    
    public void iniciar(){
    	System.out.println("Cliente iniciado na porta: "+porta);
    	
    	try {
            
            socket = new Socket("127.0.0.1", porta);
            
            entrada = new DataInputStream(socket.getInputStream());
            saida = new DataOutputStream(socket.getOutputStream());
            
            //Recebe do usuario algum valor
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            
            while (true) {
                System.out.println("Escolha uma opção:\n1 - Ler uma fortuna\n2 - Escrever uma nova fortuna\n3 - Sair");
                String input = br.readLine(); 

	            int opcao = Integer.parseInt(input);

                if (opcao == 1) {
                    String mensagem = "{\"method\":\"read\",\"args\":\"\"}";
                    saida.writeUTF(mensagem);
                    
                } else if (opcao == 2) {
                	System.out.println("Digite a nova fortuna:");
                    String novaFortuna = br.readLine();
                    String mensagem = "{\"method\":\"write\",\"args\":\"" + novaFortuna + "\"}";
                    saida.writeUTF(mensagem);
                } else if (opcao == 3) {
                    break;
                } else {
                    System.out.println("Opção inválida.");
                    continue;
                }
            }
            System.out.println(entrada.readUTF());
			socket.close();

            
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new Cliente().iniciar();
    }
    
}
