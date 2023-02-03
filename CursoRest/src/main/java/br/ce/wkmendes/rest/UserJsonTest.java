package br.ce.wkmendes.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class UserJsonTest {

	@Test
	public void testVerificarJsonPrimeiroNivel() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users/1")
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("name", containsString("Silva"))
			.body("age", greaterThan(18))
			;
			
	}
	
	@Test
	public void testVerificarJsonPrimeiroNivelOutrasFormas() {
		 Response response = RestAssured.request(Method.GET, "https://restapi.wcaquino.me/users/1");
		 
		 //path
		 Assert.assertEquals(new Integer(1), response.path("id"));
		 Assert.assertEquals(new Integer(1), response.path("%s", "id"));
		 
		 //jsonpath
		 JsonPath jsonpath = new JsonPath(response.asString());
		 Assert.assertEquals(1, jsonpath.getInt("id"));
		
		 //from
		 int id = JsonPath.from(response.asString()).getInt("id");
		 Assert.assertEquals(1, id);
	}
	
	@Test
	public void testVerificarJsonSegundoNivel() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users/2")
		.then()
			.statusCode(200)
			.body("name", containsString("Joaquina"))
			.body("age", greaterThan(18))
			.body("endereco.rua", is("Rua dos bobos"))
			;
			
	}
	
	@Test
	public void testVerificarJsonComListas() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users/3")
		.then()
			.statusCode(200)
			.body("name", containsString("Júlia"))
			.body("age", greaterThan(19))
			.body("filhos", hasSize(2))
			.body("filhos[0].name", is("Zezinho"))
			.body("filhos[1].name", is("Luizinho"))
			.body("filhos.name", hasItem("Zezinho"))
			.body("filhos.name", hasItems("Zezinho", "Luizinho"))
			;
			
	}
	
	@Test
	public void testVerificarRetornoComErro() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users/4")
		.then()
			.statusCode(404)
			.body("error", is("Usuário inexistente"))
			;
			
	}
	
	@Test
	public void testVerificarJsonComListasNaRaiz() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			.body("$", hasSize(3)) // No lugar do cifrão pode ser vazio
			.body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
			.body("age[1]", is(25))
			.body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho"))) // Lista na raiz busca por todos os registros e não por um especifico
			.body("salary", contains(1234.5678f, 2500, null))
			;
			
	}
	
	@Test
	public void testVerificacoesAvancadas() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			.body("$", hasSize(3))
			.body("age.findAll{it <= 25}.size()", is(2))
			.body("age.findAll{it <= 25 && it > 20}.size()", is(1))
			.body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina")) // Usei o hasItem pois essa verificação retorna uma lista e não um objeto
			.body("findAll{it.age <= 25}[0].name", is("Maria Joaquina")) // Aqui eu transformei a lista em um objeto pegando apenas o primeiro registro dela e assim eu consigo validar utilizando o is
			.body("findAll{it.age <= 25}[-1].name", is("Ana Júlia"))
			.body("find{it.age <= 25}.name", is("Maria Joaquina")) // Aqui o find busca todos os registros mas tras apenas o primeiro, por isso não preciso apontar qual da lista eu quero
			.body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana Júlia")) // Aqui ele busca todos os registros da lista que contem a letra N e pega o nome deles
			.body("findAll{it.name.length() > 10}.name", hasItems("João da Silva", "Maria Joaquina")) // Aqui ele busca todos os registros da lista que contem mais de 10 caracteres e pega o nome deles
			.body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA")) // Passando o nome para maiusculo
			.body("name.findAll{it.startsWith('João')}.collect{it.toUpperCase()}", hasItem("JOÃO DA SILVA")) // Procura todos os nomes que começam com joao e coloca para maiusculo
			.body("name.findAll{it.startsWith('João')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("JOÃO DA SILVA"), arrayWithSize(1))) // Procura todos os nomes que começam com joao, coloca pra maiusculo e cria um array com ele, tendo o array ele cerifica no array o tamanho e se contem o nome do joao
			.body("age.collect{it *2}", hasItems(60, 50, 40)) // pega as idades, multiplica por 2 e então valida se estão como esperado
			.body("id.max()", is(3))
			.body("salary.min()", is(1234.5678f))
			.body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001))) // Pegando os salarios diferentes de nulo e somando, e validando se ele esta igual ao esperado com uma margem de erro
			.body("salary.findAll{it != null}.sum()", allOf(greaterThan(3000d), lessThan(5000d))) // Pegando os salarios diferentes de nulo e somando, e validando se ele esta entre 3 e 5mil
			;
	}
	
	@Test
	public void testVerificacoesAvancadasMaisSimplificadas() {
		ArrayList<String> names = 
			given()
			.when()
				.get("https://restapi.wcaquino.me/users")
			.then()
				.statusCode(200)
				.extract().path("name.findAll{it.startsWith('João')}") // Vai extrair uma lista de strigs no given dos nomes que começam com João
				;
		Assert.assertEquals(1, names.size()); // Pega para validar o tamanho do array names
		Assert.assertTrue(names.get(0).equalsIgnoreCase("João da Silva")); // Faz a validação do nome que esta no array names e ignora se esta maiusculo ou minusculo
		Assert.assertEquals(names.get(0).toUpperCase(), "João da Silva".toUpperCase()); // Faz a validação do nome que esta no array names e tranforma o texto a ser validado em maiusculo
	}
}
