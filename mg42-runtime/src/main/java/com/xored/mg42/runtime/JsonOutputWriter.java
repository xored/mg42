package com.xored.mg42.runtime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonOutputWriter {

	private enum OutputType {
		STDOUT, FILE, TCP
	};

	private static final String lineDelimeter = System
			.getProperty("line.separator");

	private final URI output;
	private final OutputType type;

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private final Queue<String> messageQueue = new ConcurrentLinkedQueue<String>();
	private final Thread parentThread = Thread.currentThread();

	public JsonOutputWriter(URI output) {
		this.output = output;
		if (output != null && "tcp".equals(output.getScheme())) {
			initTcpSender();
			type = OutputType.TCP;
		} else if (output != null && "file".equals(output.getScheme())) {
			initFileSender();
			type = OutputType.FILE;
		} else {
			type = OutputType.STDOUT;
		}
	}

	public void write(Object data) {
		String jsonData = gson.toJson(data);
		if (OutputType.STDOUT.equals(type)) {
			System.out.println(jsonData);
		} else {
			messageQueue.add(jsonData + lineDelimeter);
		}
	}

	private void initTcpSender() {
		Thread tcpSender = new Thread(new Runnable() {
			private Socket socket = new Socket();
			private Writer writer;

			private void tryToConnect() {
				try {
					SocketAddress sockaddr = new InetSocketAddress(output
							.getHost(), output.getPort());
					socket = new Socket();
					socket.connect(sockaddr, 3000);
					writer = new BufferedWriter(new OutputStreamWriter(socket
							.getOutputStream()));
				} catch (IOException e) {
					System.out.println("Could not output to: " + output
							+ ". Reason: " + e.getMessage());
				}
			}

			public void run() {
				try {
					while (!Thread.State.TERMINATED.equals(parentThread
							.getState())
							|| (!messageQueue.isEmpty() && socket.isConnected())) {
						if (!socket.isConnected()) {
							tryToConnect();
						} else if (!messageQueue.isEmpty()) {
							try {
								String msg = messageQueue.poll();
								writer.write(msg);
								writer.flush();
							} catch (IOException e) {
								System.out.println("Error while output to: "
										+ output + ". " + e.getMessage());
							}
						}
					}
				} finally {
					try {
						socket.close();
					} catch (IOException e) {
						// Do nothing
					}
				}
			}
		});
		tcpSender.start();
	}

	private void initFileSender() {
		Thread fileSender = new Thread(new Runnable() {

			private Writer writer;

			private Writer getWriter() {
				if (writer == null) {
					try {
						File outputFile = new File(output);
						writer = new BufferedWriter(new FileWriter(outputFile));
					} catch (IOException e) {
						System.out.println("Could not output to: " + output
								+ ". Reason: " + e.getMessage());
						return null;
					}
				}
				return writer;
			}

			public void run() {
				try {
					if (getWriter() != null) {
						while (!Thread.State.TERMINATED.equals(parentThread
								.getState()) || !messageQueue.isEmpty()) {
							if (!messageQueue.isEmpty()) {
								try {
									String msg = messageQueue.poll();
									writer.write(msg);
									writer.flush();
								} catch (IOException e) {
									System.out
											.println("Error while output to: "
													+ output + ". "
													+ e.getMessage());
								}
							}
						}
					}
				} finally {
					try {
						if (writer != null) {
							writer.close();
						}
					} catch (IOException e) {
						// Do nothing
					}
				}
			}
		});
		fileSender.start();
	}
}
