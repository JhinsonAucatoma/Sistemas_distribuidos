package ec.edu.uteq.distribuidas.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class ClienteTCP {

    private static final String HOST = "localhost";
    private static final int PUERTO = 9000;

    public static void main(String[] args) {

        System.out.println("=== Cliente TCP -- Aplicaciones Distribuidas ===");

        try (
                Socket socket = new Socket(HOST, PUERTO);
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter salida = new PrintWriter(
                        socket.getOutputStream(), true);
                Scanner teclado = new Scanner(System.in)
        ) {

            System.out.println("Conectado a " + HOST + ":" + PUERTO);
            System.out.println("Comandos : HORA | SALIR | cualquier texto");
            System.out.println("----------------------------------------");

            while (true) {

                System.out.print("> ");
                String mensaje = teclado.nextLine();

                salida.println(mensaje); // enviar al servidor

                String respuesta = entrada.readLine(); // esperar respuesta
                System.out.println("Servidor : " + respuesta);

                if (respuesta != null && respuesta.startsWith("ADIOS")) {
                    System.out.println("Desconectando...");
                    break;
                }
            }

        } catch (ConnectException e) {

            System.err.println(
                    "ERROR: No se pudo conectar a "
                            + HOST
                            + ":"
                            + PUERTO
                            + ". ¿Está el servidor corriendo?"
            );

        } catch (IOException e) {

            System.err.println(
                    "Error de comunicación: "
                            + e.getMessage()
            );
        }
    }
}