package com.jarcila.pokedex;

import org.springframework.boot.SpringApplication;

public class TestPokedexApplication {

	public static void main(String[] args) {
		SpringApplication.from(PokedexApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
