/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class SingleSourceOfTruth {

    // Quản lý danh sách Client online (Key: username, Value: ClientHandler)
    private final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();

    // Singleton Instance
    private static final SingleSourceOfTruth instance = new SingleSourceOfTruth();

    private SingleSourceOfTruth() {
    }

    public static SingleSourceOfTruth getInstance() {
        return instance;
    }

    // ================= MỤC QUẢN LÝ CLIENT =================
    public void addClient(String username, ClientHandler handler) {
        clients.put(username, handler);
    }

    public void removeClient(String username) {
        if (username != null) {
            clients.remove(username);
        }
    }

    public ClientHandler getClient(String username) {
        return clients.get(username);
    }

    public Collection<ClientHandler> getAllClients() {
        return clients.values();
    }

}