package com.xored.mg42.agent;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
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
		handlers = args.containsKey(ARG_TRACERS) ? readTracers(args
				.get(ARG_TRACERS)) : new HandlerGroups(
				new HashMap<String, HandlerGroup>());
		sources = handlers.calcSources();
	}

	private URI output;
	private int port;
	public final HandlerGroups handlers;
	public final SourceClasses sources;

	public URI getOutput() {
		return output;
	}

	public int getPort() {
		return port;
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

	private static HandlerGroups readTracers(String path) throws IOException {
		return HandlerGroups.fromJson(new JsonParser()
				.parse(CharStreams.toString(new InputStreamReader(
						new FileInputStream(path), Charsets.UTF_8)))
				.getAsJsonObject().get("tracerGroups").getAsJsonArray());
	}

}
