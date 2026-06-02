package ec.edu.uteq.distribuidas.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class ServidorTCP {

    private static final int PUERTO = 9000;
    private static final int MAX_CLIENTES = 50;
    private static final Logger logger = Logger.getLogger(ServidorTCP.class.getName());
    private static final AtomicInteger contadorClientes = new AtomicInteger(0);

    public static void main(String[] args) {

        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTES);

        logger.info("Iniciando servidor en puerto " + PUERTO);

        try (ServerSocket servidor = new ServerSocket(PUERTO)) {

            servidor.setReuseAddress(true);

            System.out.println("[" + timestamp() + "] Servidor listo en :" + PUERTO);

            while (!Thread.currentThread().isInterrupted()) {

                Socket clienteSocket = servidor.accept();
                int id = contadorClientes.incrementAndGet();

                System.out.printf("[%s] Cliente #%d conectado desde %s %n",
                        timestamp(),
                        id,
                        clienteSocket.getRemoteSocketAddress());

                pool.submit(new ManejadorCliente(clienteSocket, id));
            }

        } catch (IOException e) {

            logger.severe("Error en servidor : " + e.getMessage());

        } finally {

            pool.shutdown();
        }
    }

    private static String timestamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    }

    static class ManejadorCliente implements Runnable {

        private final Socket socket;
        private final int idCliente;

        ManejadorCliente(Socket socket, int idCliente) {
            this.socket = socket;
            this.idCliente = idCliente;
        }

        @Override
        public void run() {

            String nombreHilo = "Cliente-" + idCliente;
            Thread.currentThread().setName(nombreHilo);

            try (
                    BufferedReader entrada = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    PrintWriter salida = new PrintWriter(
                            socket.getOutputStream(), true)
            ) {

                String linea;

                while ((linea = entrada.readLine()) != null) {

                    System.out.printf("[%s][%s] Recibido : %s %n",
                            ServidorTCP.timestamp(),
                            nombreHilo,
                            linea);

                    String respuesta = procesarMensaje(linea);
                    salida.println(respuesta);

                    if ("SALIR".equalsIgnoreCase(linea.trim())) {
                        break;
                    }
                }

            } catch (IOException e) {

                System.err.println("[" + nombreHilo + "] Desconectado : " + e.getMessage());

            } finally {

                cerrarSocket();
            }
        }

        private String procesarMensaje(String mensaje) {

            if ("HORA".equalsIgnoreCase(mensaje.trim())) {
                return "HORA_ACTUAL :" + LocalDateTime.now();
            }

            if ("SALIR".equalsIgnoreCase(mensaje.trim())) {
                return "ADIOS : hasta luego cliente #" + idCliente;
            }

            return "ECO [#" + idCliente + "]:" + mensaje;
        }

        private void cerrarSocket() {

            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}