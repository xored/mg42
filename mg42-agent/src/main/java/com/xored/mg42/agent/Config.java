package com.xored.mg42.agent;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Config {
	private static final String ARG_OUTPUT = "out";
	private static final String ARG_PORT = "port";
	private static final String ARG_TRACERS = "tracers";

	public Config(Map<String, String> args) throws IOException {
		output = args.containsKey(ARG_OUTPUT) ? URI
				.create(args.get(ARG_OUTPUT)) : null;
		port = args.containsKey(ARG_PORT) ? Integer
				.parseInt(args.get(ARG_PORT)) : -1;
		tracers = args.containsKey(ARG_TRACERS) ? readTracers(args
				.get(ARG_TRACERS)) : new HashMap<String, ClassTrace>();
	}

	private URI output;
	private int port;
	private Map<String, ClassTrace> tracers;

	public URI getOutput() {
		return output;
	}

	public int getPort() {
		return port;
	}

	public Map<String, ClassTrace> getTracers() {
		return tracers;
	}

	public static Config fromArgs(String argStr) throws IOException {
		if (argStr == null || argStr.length() == 0) {
			return new Config(new HashMap<String, String>());
		}
		String[] args = argStr.split("=");
		if (args.length % 2 == 1) {
			throw new IllegalArgumentException(
					"Invalid args for agent: use key1=val1=key2=val2");
		}

		Map<String, String> result = new HashMap<String, String>();
		for (int i = 0; i < args.length / 2; i++) {
			result.put(args[i * 2], args[i * 2 + 1]);
		}
		return new Config(result);
	}

	private static Map<String, ClassTrace> readTracers(String path)
			throws IOException {
		JsonObject object = new JsonParser().parse(
				CharStreams.toString(new InputStreamReader(new FileInputStream(
						path), Charsets.UTF_8))).getAsJsonObject();
		Map<String, ClassTrace> result = new HashMap<String, ClassTrace>();
		for (Entry<String, JsonElement> entry : object.entrySet()) {
			result.put(entry.getKey(), ClassTrace.fromJson(entry.getKey(),
					entry.getValue().getAsJsonArray()));
		}
		return result;
	}

}
