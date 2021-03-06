package com.ratata.socketRestServer;

import java.io.File;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratata.socketRestServer.Threads.Server;
import com.ratata.socketRestServer.Threads.ServerMirror;

public class Launcher {

	public static void main(String[] args) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		for (String config : args) {
			JsonNode configNode = mapper.readTree(new File(config));
			if (configNode.has("botName")) {
				ServerMirror serverMirror = new ServerMirror();
				serverMirror.startServer(configNode.get("openPort").asInt(), configNode.get("proxyUrl").asText(),
						configNode.get("botName").asText(), configNode.get("destHost").asText(),
						configNode.get("destPort").asText());
			} else {
				Server server = new Server();
				server.startServer(configNode.get("openPort").asInt(), configNode.get("proxyUrl").asText(),
						configNode.get("destHost").asText(), configNode.get("destPort").asText());
			}
		}
	}
}
