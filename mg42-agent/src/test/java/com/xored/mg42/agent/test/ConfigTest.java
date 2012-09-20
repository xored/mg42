package com.xored.mg42.agent.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import com.xored.mg42.agent.ClassTrace;
import com.xored.mg42.agent.Config;
import com.xored.mg42.agent.MethodTrace;

public class ConfigTest {
	@Test
	public void testEmpty() throws IOException {
		Config config = Config.fromArgs("");
		assertNull(config.getOutput());
		assertEquals(-1, config.getPort());
		assertTrue(config.getTracers().isEmpty());
	}

	@Test
	public void testSimple() throws IOException {
		Config config = Config
				.fromArgs("port=123=out=file:/Users/ivaninozemtsev/Temp");
		assertTrue(config.getTracers().isEmpty());
		assertEquals(123, config.getPort());
		assertEquals(URI.create("file:/Users/ivaninozemtsev/Temp"),
				config.getOutput());
	}

	@Test
	public void testTracers() throws IOException, URISyntaxException {
		URL myTestURL = ConfigTest.class.getClassLoader().getResource(
				"tracers.config");
		File myFile = new File(myTestURL.toURI());
		String args = String.format("tracers=%s", myFile.getAbsolutePath());
		Config config = Config.fromArgs(args);
		assertEquals(1, config.getTracers().size());
		ClassTrace trace = config.getTracers().entrySet().iterator().next()
				.getValue();
		assertEquals(trace.name, "com.xored.program.CommandProcessor");
		assertEquals(1, trace.methods.length);
		MethodTrace mt = trace.methods[0];
		assertEquals("process", mt.method);
		assertEquals(1, mt.args.length);
		assertEquals("java.lang.String", mt.args[0]);
		assertEquals("com.xored.programclient.CommandProcessorTracer",
				mt.onEnter);
		assertNull(mt.onExit);

	}
}
