package com.socket.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class ChatServer {
	private HashMap<String, DataOutputStream> clients;
	private ServerSocket serverSocket;
	
	public static void main(String[] args) {
		new ChatServer().start();
	}
	
	public ChatServer() {
		clients = new HashMap<String, DataOutputStream>();
		
		Collections.synchronizedMap(clients);
	}
	
	public void start() {
		try {
			Socket socket;
			
			serverSocket = new ServerSocket(7777);
			
			while(true) {
				socket = serverSocket.accept();
				ServerReceiver receiver = new ServerReceiver(socket);
				new Thread(receiver).start();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class ServerReceiver implements Runnable {
		Socket socket;
		DataInputStream input;
		DataOutputStream output;
		
		public ServerReceiver(Socket socket) {
			this.socket = socket;
			try {
				input = new DataInputStream(socket.getInputStream());
				output = new DataOutputStream(socket.getOutputStream());
			}catch (IOException e) {
				
			}
		}

		@Override
		public void run() {
			String name = "";
			try {
				name = input.readUTF();
				sendToAll("#" + name + "[" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "���� ��ȭ�濡 �����Ͽ����ϴ�.");
				
				clients.put(name, output);
				System.out.println(name + "[" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "���� ��ȭ�濡 �����Ͽ����ϴ�.");
				
				System.out.println("���� " + clients.size() + "���� ��ȭ�濡 ���� ���Դϴ�.");
				
				// �޽��� ����
				while( input != null ) {
					sendToAll(input.readUTF());
				}
			}catch (IOException e) {
			}finally {
				clients.remove(name);
			}
		}
		
		public void sendToAll(String message) {
			Iterator<String> it = clients.keySet().iterator();
			
			while(it.hasNext()) {
				try {
					DataOutputStream dos = clients.get(it.next());
					dos.writeUTF(message);
				}catch (Exception e) {
					
				}
			}
		}
	}
}
