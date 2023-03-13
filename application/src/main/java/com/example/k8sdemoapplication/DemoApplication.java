package com.example.k8sdemoapplication;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@RestController
public class DemoApplication {
	private Boolean liveness = Boolean.TRUE;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@GetMapping(value = "/")
	public String hostname() throws UnknownHostException {
		return getHostname();
	}

	/**
	 * Demonstrate liveness and force a service to restart
	 * @return
	 */
	@GetMapping(value = "/liveness")
	public ResponseEntity<Void> getLiveness() {
		return liveness ? ResponseEntity.ok().build() : ResponseEntity.internalServerError().build();
	}

	@PostMapping(value = "/liveness")
	public ResponseEntity<String> changeLiveness() {
		this.liveness = ! this.liveness;
		return ResponseEntity.ok(getHostname());
	}

	@SneakyThrows
	static String getHostname() {
		return InetAddress.getLocalHost().getHostName();
	}
}
