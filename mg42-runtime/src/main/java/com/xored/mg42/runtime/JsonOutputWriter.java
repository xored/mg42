package com.xored.mg42.runtime;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonOutputWriter {

	private enum OutputType {
		STDOUT, TCP
	};

	private OutputType type;
	private BufferedWriter writer;

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public JsonOutputWriter(URI output) {
		type = OutputType.STDOUT;
		if ("tcp".equals(output.getScheme())) {
			try {
				initTcpWriter();
				type = OutputType.TCP;
			} catch (IOException e) {
				System.out.println("Could not output to: "
						+ TracerConfig.getOutput() + ". " + e.getMessage());
				System.out.println("Output redirected to stdout");
			}
		}
	}

	public void write(Object data) {
		String jsonData = gson.toJson(data);
		switch (type) {
		case TCP:
			try {
				writer.write(jsonData);
				writer.flush();
			} catch (IOException e) {
				System.out.println("Error when trying output to: "
						+ TracerConfig.getOutput() + ". " + e.getMessage());
				System.out.println("Output redirected to stdout");
				type = OutputType.STDOUT;
				System.out.println(jsonData);
			}
			break;
		default:
			System.out.println(jsonData);
		}
	}

	private void initTcpWriter() throws IOException {
		if (writer == null) {
			SocketAddress sockaddr = new InetSocketAddress(TracerConfig
					.getOutput().getHost(), TracerConfig.getOutput().getPort());
			Socket sock = new Socket();
			int timeoutMs = 3000;
			sock.connect(sockaddr, timeoutMs);
			writer = new BufferedWriter(new OutputStreamWriter(
					sock.getOutputStream()));
		}
	}
}
