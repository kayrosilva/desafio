package com.github.kayrosilva.desafio;

import com.github.kayrosilva.desafio.model.entity.Cliente;
import com.github.kayrosilva.desafio.model.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class DesafioApplication {




	public static void main(String[] args) {
		SpringApplication.run(DesafioApplication.class, args);
	}



	}

