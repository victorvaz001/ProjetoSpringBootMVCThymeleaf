package curso.springboot.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EntityScan(basePackages="curso.springboot.model") //escaneamento das entidades no pacote model
@ComponentScan(basePackages = {"curso.*"}) //força o mapeamento, todos os pacotes
@EnableJpaRepositories(basePackages = {"curso.springboot.repository"}) //habilitando o crudRepository no pacote
@EnableTransactionManagement //habilitanto as transações com banco de dados
public class SpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
		
		/*BCryptPasswordEncoder encoder =  new BCryptPasswordEncoder();
		  String result = encoder.encode("123");
		  System.out.println(result);*/
		
	}
}
