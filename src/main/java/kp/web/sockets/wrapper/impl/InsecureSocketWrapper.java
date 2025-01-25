package kp.web.sockets.wrapper.impl;

import kp.utils.Printer;
import kp.utils.Utils;
import kp.web.sockets.wrapper.SocketWrapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.Phaser;

/**
 * The insecure implementation of the {@link SocketWrapper}.
 */
public class InsecureSocketWrapper extends SocketWrapper {

    /**
     * Constructs an InsecureSocketWrapper.
     * Initializes the insecure socket wrapper and logs the creation time.
     */
    public InsecureSocketWrapper() {

        final Instant start = Instant.now();
        initializeInsecureSocketWrapper();
        final Instant finish = Instant.now();
        Printer.printf("InsecureSocketWrapper(): server socket created, host[%s], port[%d], %s", HOST, PORT,
                Utils.formatElapsed(start, finish));
    }

    /**
     * Initializes the insecure socket wrapper.
     */
    private void initializeInsecureSocketWrapper() {

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runServer(Phaser phaser, String content, int number) {

        Printer.printf("runServer(): number[%d], start", number);
        final ArrayList<Instant> instantList = new ArrayList<>();
        instantList.add(Instant.now());// ◄ place '0'
        try (Socket socket = serverSocket.accept();
             PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            instantList.add(Instant.now());// ◄ place '1'
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Printer.printf("runServer(): number[%d], content...[%s], server received from client", number,
                        line.substring(line.length() - 15));
            }
            writer.println(content);
            writer.flush();
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        instantList.add(Instant.now());// ◄ place '2'
        Printer.printf("runServer(): number[%d], %s", number, Utils.calculateElapsedTimes(instantList));
        phaser.arriveAndDeregister();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runClient(Phaser phaser, String content, int number) {

        Printer.printf("runClient(): number[%d], start", number);
        final ArrayList<Instant> instantList = new ArrayList<>();
        instantList.add(Instant.now());// ◄ place '0'
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            instantList.add(Instant.now());// ◄ place '1'
            writer.printf("%s%n%n", content);
            writer.flush();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Printer.printf("runClient(): number[%d], content...[%s], client received from server", number,
                        line.substring(line.length() - 15));
            }
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        instantList.add(Instant.now());// ◄ place '2'
        Printer.printf("runClient(): number[%d], %s", number, Utils.calculateElapsedTimes(instantList));
        phaser.arriveAndDeregister();
    }

}
