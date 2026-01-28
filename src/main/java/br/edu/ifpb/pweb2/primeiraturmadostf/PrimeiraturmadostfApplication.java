package br.edu.ifpb.pweb2.primeiraturmadostf;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PrimeiraturmadostfApplication {

	public static void main(String[] args) {
		// Carrega variáveis do arquivo .env
		Dotenv.configure()
          .systemProperties() // Isso faz o Spring "enxergar" as variáveis antes de validar o banco
          .ignoreIfMissing() 
          .load();
		
		SpringApplication.run(PrimeiraturmadostfApplication.class, args);
	}

}
