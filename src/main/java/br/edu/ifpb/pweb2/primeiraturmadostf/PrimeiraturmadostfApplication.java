package br.edu.ifpb.pweb2.primeiraturmadostf;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PrimeiraturmadostfApplication {

	public static void main(String[] args) {
		// Carrega variáveis do arquivo .env
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing() 
				.load();
		
		// Adiciona as variáveis do .env ao System.getenv()
		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});
		
		SpringApplication.run(PrimeiraturmadostfApplication.class, args);
	}

}
