package com.mcef.arm64.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Localhost Bridge Relay for Android
 * Relays localhost:3876 (Create: Trackmaps) requests from WebView to Java
 */
public class LocalhostBridgeRelay {
    private static final Logger LOGGER = LoggerFactory.getLogger("LocalhostBridgeRelay");
    
    private final String targetHost;
    private final int targetPort;
    private ServerSocket serverSocket;
    private ExecutorService executor;
    private AtomicBoolean running = new AtomicBoolean(false);
    private Thread listenerThread;
    
    public LocalhostBridgeRelay(String targetHost, int targetPort) {
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.executor = Executors.newCachedThreadPool();
    }
    
    /**
     * Start the relay server
     */
    public void start() throws IOException {
        if (running.get()) {
            LOGGER.warn("Relay already running");
            return;
        }
        
        try {
            // Create server socket on localhost:3876
            serverSocket = new ServerSocket(targetPort, 50, java.net.InetAddress.getByName(targetHost));
            running.set(true);
            
            LOGGER.info("LocalhostBridgeRelay: Started on {}:{}", targetHost, targetPort);
            
            // Start listener thread
            listenerThread = new Thread(this::acceptConnections);
            listenerThread.setName("MCEF-BridgeRelay-Listener");
            listenerThread.setDaemon(true);
            listenerThread.start();
            
        } catch (IOException e) {
            LOGGER.error("Failed to start bridge relay", e);
            throw e;
        }
    }
    
    /**
     * Accept incoming connections from WebView
     */
    private void acceptConnections() {
        LOGGER.info("LocalhostBridgeRelay: Accepting connections");
        
        while (running.get()) {
            try {
                Socket clientSocket = serverSocket.accept();
                LOGGER.debug("LocalhostBridgeRelay: Accepted connection from {}", clientSocket.getInetAddress());
                
                // Handle connection in thread pool
                executor.submit(() -> handleConnection(clientSocket));
                
            } catch (IOException e) {
                if (running.get()) {
                    LOGGER.error("Error accepting connection", e);
                }
            }
        }
    }
    
    /**
     * Handle individual connection
     */
    private void handleConnection(Socket clientSocket) {
        try (Socket destSocket = new Socket(targetHost, targetPort)) {
            
            // Start forwarding threads
            Thread clientToServer = new Thread(() -> forward(clientSocket, destSocket));
            Thread serverToClient = new Thread(() -> forward(destSocket, clientSocket));
            
            clientToServer.setName("MCEF-BridgeRelay-C2S");
            serverToClient.setName("MCEF-BridgeRelay-S2C");
            clientToServer.setDaemon(true);
            serverToClient.setDaemon(true);
            
            clientToServer.start();
            serverToClient.start();
            
            clientToServer.join();
            
        } catch (Exception e) {
            LOGGER.error("Error handling connection", e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOGGER.error("Error closing client socket", e);
            }
        }
    }
    
    /**
     * Forward data between sockets
     */
    private void forward(Socket source, Socket destination) {
        try (
            InputStream input = source.getInputStream();
            OutputStream output = destination.getOutputStream()
        ) {
            byte[] buffer = new byte[65536];
            int bytesRead;
            
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
                output.flush();
            }
            
        } catch (IOException e) {
            // Connection closed, this is normal
            LOGGER.debug("Forward connection closed");
        }
    }
    
    /**
     * Stop the relay
     */
    public void stop() throws IOException {
        if (!running.getAndSet(false)) {
            LOGGER.warn("Relay not running");
            return;
        }
        
        LOGGER.info("LocalhostBridgeRelay: Stopping");
        
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        
        executor.shutdown();
        
        try {
            if (listenerThread != null) {
                listenerThread.join(5000);
            }
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted while waiting for listener thread", e);
        }
        
        LOGGER.info("LocalhostBridgeRelay: Stopped");
    }
    
    /**
     * Check if relay is running
     */
    public boolean isRunning() {
        return running.get();
    }
}
