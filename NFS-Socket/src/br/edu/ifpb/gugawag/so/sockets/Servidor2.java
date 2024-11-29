package br.edu.ifpb.gugawag.so.sockets;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor2 {

  // Lista de arquivos simulando o sistema de arquivos
  private static List<String> arquivos = new ArrayList<>();
  private static final String ARQUIVO_DADOS = "arquivos.txt";

  public static void main(String[] args) throws IOException {
    System.out.println("== Servidor ==");

    // Carregar a lista de arquivos do arquivo
    carregarArquivos();

    // Configurando o socket
    ServerSocket serverSocket = new ServerSocket(7001);
    System.out.println("Aguardando conexões na porta 7001...");
    Socket socket = serverSocket.accept();

    // Criando os fluxos de entrada e saída para comunicação
    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
    DataInputStream dis = new DataInputStream(socket.getInputStream());

    System.out.println("Cliente conectado: " + socket.getInetAddress());

    // Loop para processar comandos
    while (true) {
      try {
        // Lendo o comando do cliente
        String mensagem = dis.readUTF();
        System.out.println("Comando recebido: " + mensagem);

        // Processando o comando e obtendo a resposta
        String resposta = processarComando(mensagem);

        // Enviando a resposta ao cliente
        dos.writeUTF(resposta);
      } catch (IOException e) {
        System.err.println("Erro na comunicação com o cliente: " + e.getMessage());
        break;
      }
    }

    // Fechando os recursos
    socket.close();
    serverSocket.close();
  }

  /**
   * Processa os comandos recebidos do cliente e realiza as operações na lista de arquivos.
   *
   * @param comando Comando enviado pelo cliente.
   * @return Resposta para o cliente.
   */
  private static String processarComando(String comando) {
    // Separando o comando e os argumentos
    String[] partes = comando.split(" ", 2);
    String operacao = partes[0];

    switch (operacao.toLowerCase()) {
      case "readdir":
        // Retorna a lista de arquivos como uma string
        return arquivos.isEmpty() ? "Nenhum arquivo disponível." : String.join(", ", arquivos);

      case "create":
        // Criar um novo arquivo
        if (partes.length < 2) return "Erro: Nome do arquivo não especificado.";
        String novoArquivo = partes[1];
        if (arquivos.contains(novoArquivo)) {
          return "Erro: Arquivo já existe.";
        }
        arquivos.add(novoArquivo);
        salvarArquivos();
        return "Arquivo '" + novoArquivo + "' criado com sucesso.";

      case "rename":
        // Renomear um arquivo existente
        if (partes.length < 2) return "Erro: Parâmetros insuficientes.";
        String[] nomes = partes[1].split(" ", 2);
        if (nomes.length < 2) return "Erro: Parâmetros insuficientes.";
        String antigo = nomes[0];
        String novo = nomes[1];
        if (!arquivos.contains(antigo)) {
          return "Erro: Arquivo '" + antigo + "' não encontrado.";
        }
        arquivos.remove(antigo);
        arquivos.add(novo);
        salvarArquivos();
        return "Arquivo renomeado de '" + antigo + "' para '" + novo + "'.";

      case "remove":
        // Remover um arquivo existente
        if (partes.length < 2) return "Erro: Nome do arquivo não especificado.";
        String arquivoRemover = partes[1];
        if (!arquivos.contains(arquivoRemover)) {
          return "Erro: Arquivo '" + arquivoRemover + "' não encontrado.";
        }
        arquivos.remove(arquivoRemover);
        salvarArquivos();
        return "Arquivo '" + arquivoRemover + "' removido com sucesso.";

      default:
        // Comando desconhecido
        return "Erro: Comando inválido.";
    }
  }

  /**
   * Carrega os arquivos do arquivo de persistência.
   */
  private static void carregarArquivos() {
    try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_DADOS))) {
      String linha;
      while ((linha = reader.readLine()) != null) {
        arquivos.add(linha);
      }
      System.out.println("Arquivos carregados do arquivo: " + arquivos);
    } catch (IOException e) {
      System.out.println("Nenhum arquivo existente encontrado. Iniciando com uma lista vazia.");
    }
  }

  /**
   * Salva os arquivos no arquivo de persistência.
   */
  private static void salvarArquivos() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_DADOS))) {
      for (String arquivo : arquivos) {
        writer.write(arquivo);
        writer.newLine();
      }
      System.out.println("Lista de arquivos salva no arquivo.");
    } catch (IOException e) {
      System.err.println("Erro ao salvar arquivos: " + e.getMessage());
    }
  }
}

