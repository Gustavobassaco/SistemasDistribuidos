/**
 * Laboratorio 3  
 * Autor: Lucio Agostinho Rocha
 * Ultima atualizacao: 04/04/2023
 */

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Principal {

	public final static Path path = Paths
			.get("src/fortune-br.txt");
	private int NUM_FORTUNES = 0;
	private FileReader fr; 
	
	public class FileReader {

		public int countFortunes() throws FileNotFoundException {

			int lineCount = 0;

			InputStream is = new BufferedInputStream(new FileInputStream(
					path.toString()));
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
					is))) {

				String line = "";
				while (!(line == null)) {

					if (line.equals("%"))
						lineCount++;

					line = br.readLine();

				}// fim while

				System.out.println(lineCount);
			} catch (IOException e) {
				System.out.println("Excecao na leitura do arquivo.");
			}
			return lineCount;
		}

		public void parser(HashMap<Integer, String> hm) throws FileNotFoundException {
		    // Abre um InputStream para o arquivo de fortunas
		    InputStream is = new BufferedInputStream(new FileInputStream(path.toString()));

		    // Usa um BufferedReader para facilitar a leitura do arquivo
		    try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
		        // Inicializa o contador de linhas
		        int lineCount = 0;

		        // Inicializa uma string para armazenar cada linha lida do arquivo
		        String line = "";

		        // Loop principal para ler todas as linhas do arquivo
		        while (!(line == null)) {
		            // Incrementa o contador se a linha � um delimitador "%"
		            if (line.equals("%"))
		                lineCount++;

		            // L� a pr�xima linha do arquivo
		            line = br.readLine();

		            // Inicializa uma StringBuffer para construir a fortuna
		            StringBuffer fortune = new StringBuffer();

		            // Loop interno para ler todas as linhas da fortuna at� encontrar outro delimitador "%"
		            while (!(line == null) && !line.equals("%")) {
		                // Adiciona a linha � fortuna
		                fortune.append(line + "\n");

		                // L� a pr�xima linha
		                line = br.readLine();
		                // System.out.print(lineCount + ".");
		            }

		            // Adiciona a fortuna ao HashMap usando o contador de linhas como chave
		            hm.put(lineCount, fortune.toString());

		            // Imprime a fortuna
		            System.out.println(fortune.toString());

		            // System.out.println(lineCount);
		        }// fim while

		    } catch (IOException e) {
		        // Em caso de exce��o de leitura do arquivo, imprime uma mensagem de erro
		        System.out.println("SHOW: Excecao na leitura do arquivo.");
		    }
		}
		
		public String read(HashMap<Integer, String> hm) throws FileNotFoundException {
		    // Inicializa a vari�vel result com um valor padr�o de erro
		    String result = "-2";

		    // Abre um InputStream para o arquivo de fortunas
		    InputStream is = new BufferedInputStream(new FileInputStream(path.toString()));

		    // Usa um BufferedReader para facilitar a leitura do arquivo
		    try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
		        // Cria um SecureRandom para gerar um �ndice aleat�rio
		        SecureRandom sr = new SecureRandom();

		        // Gera um �ndice aleat�rio com base no n�mero total de fortunas
		        int lineSelected = sr.nextInt(NUM_FORTUNES);

		        // Imprime o �ndice selecionado (pode ser removido em produ��o)
		        System.out.println(lineSelected);

		        // Imprime a fortuna correspondente ao �ndice selecionado (pode ser removido em produ��o)
		        System.out.println(hm.get(lineSelected));

		        // Atribui a fortuna correspondente ao �ndice selecionado � vari�vel result
		        result = hm.get(lineSelected);

		    } catch (IOException e) {
		        // Em caso de exce��o de leitura do arquivo, imprime uma mensagem de erro
		        System.out.println("SHOW: Excecao na leitura do arquivo.");
		    }

		    // Retorna o resultado, que pode ser uma fortuna ou o valor padr�o de erro
		    return result;
		}
		
		
		public void write(HashMap<Integer, String> hm, String fortune) throws FileNotFoundException {
		    // Abre um OutputStream para o arquivo de fortunas em modo de ap�ndice
		    OutputStream os = new BufferedOutputStream(new FileOutputStream(path.toString(), true));

		    // Usa um BufferedWriter para facilitar a escrita no arquivo
		    try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os))) {
		        // Cria um Scanner para receber a entrada do usu�rio 
		        Scanner input = new Scanner(System.in);

		        // Incrementa o contador de fortunas
		        NUM_FORTUNES++;

		        // Adiciona a nova fortuna ao HashMap usando o contador de fortunas como chave
		        hm.put(NUM_FORTUNES, fortune);

		        // Imprime a nova fortuna
		        System.out.println(hm.get(NUM_FORTUNES));

		        // Adiciona a nova fortuna ao final do arquivo com um delimitador
		        bw.append("\n%\n" + fortune);

		    } catch (IOException e) {
		        // Em caso de exce��o durante a escrita no arquivo, imprime uma mensagem de erro
		        System.out.println("SHOW: Excecao na leitura do arquivo.");
		    }
		}
	}
	
	public void write(String fortune){
	    // Cria uma inst�ncia de FileReader para manipular o arquivo de fortunas
	    fr = new FileReader();

	    try {
	        // Obt�m o n�mero total de fortunas no arquivo
	        NUM_FORTUNES = fr.countFortunes();

	        // Cria um HashMap para armazenar as fortunas
	        HashMap hm = new HashMap<Integer, String>();

	        // Preenche o HashMap com as fortunas do arquivo
	        fr.parser(hm);

	        // L� as fortunas do arquivo
	        fr.read(hm);

	        // Adiciona a nova fortuna ao arquivo e atualiza o HashMap
	        fr.write(hm, fortune);

	    } catch (FileNotFoundException e) {
	        // Em caso de exce��o de arquivo n�o encontrado, imprime o rastreamento da pilha
	        e.printStackTrace();
	    }
	}

	// M�todo para ler uma fortuna aleat�ria do arquivo
	public String read(){
	    // Inicializa a vari�vel result com um valor padr�o de erro ("-1")
	    String result = "-1";

	    // Cria uma inst�ncia de FileReader para manipular o arquivo de fortunas
	    fr = new FileReader();

	    try {
	        // Obt�m o n�mero total de fortunas no arquivo
	        NUM_FORTUNES = fr.countFortunes();

	        // Cria um HashMap para armazenar as fortunas
	        HashMap hm = new HashMap<Integer, String>();

	        // Preenche o HashMap com as fortunas do arquivo
	        fr.parser(hm);

	        // L� uma fortuna aleat�ria do arquivo
	        result = fr.read(hm);

	    } catch (FileNotFoundException e) {
	        // Em caso de exce��o de arquivo n�o encontrado, imprime o rastreamento da pilha
	        e.printStackTrace();
	    }

	    // Retorna o resultado, que pode ser uma fortuna ou o valor padr�o de erro ("-1")
	    return result;
	}
	
}
