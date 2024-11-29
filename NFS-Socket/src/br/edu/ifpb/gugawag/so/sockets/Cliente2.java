package br.edu.ifpb.gugawag.so.sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente2 {

  public static void main(String[] args) throws IOException {
    System.out.println("== Cliente ==");

    // Configurando o socket
    try (Socket socket = new Socket("127.0.0.1", 7001);
         DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
         DataInputStream dis = new DataInputStream(socket.getInputStream());
         Scanner teclado = new Scanner(System.in)) {

      System.out.println("Conectado ao servidor.");

      while (true) {
        menu(); // Exibe o menu
        System.out.print("Digite o comando: ");
        String comando = teclado.nextLine();

        // Permitir sair do programa
        if (comando.equalsIgnoreCase("sair")) {
          System.out.println("Encerrando o cliente...");
          break;
        }

        // Envia o comando ao servidor
        dos.writeUTF(comando);

        // Recebe a resposta do servidor
        String resposta = dis.readUTF();
        System.out.println("Servidor respondeu: " + resposta);
      }
    } catch (IOException e) {
      System.err.println("Erro ao se comunicar com o servidor: " + e.getMessage());
    }
  }

  // Método para exibir o menu
  public static void menu() {
    System.out.println("\n=== Operações disponíveis ===");
    System.out.println("readdir - devolve a lista de nomes (Ex.: readdir)");
    System.out.println("rename - renomeia um arquivo (Ex.: rename casa ap)");
    System.out.println("create - cria um arquivo (Ex.: create casa)");
    System.out.println("remove - remove um arquivo (Ex.: remove casa)");
    System.out.println("sair - encerra o cliente");
  }
}
