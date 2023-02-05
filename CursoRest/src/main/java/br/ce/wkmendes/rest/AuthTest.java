package br.ce.wkmendes.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import groovy.util.XmlParser;
import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class AuthTest {

	@Test
	public void testValidarUsuarioSWAPISemAutenticacao() {
		
		given()
			.log().all()
		.when()
			.get("https://swapi.dev/api/people/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("Luke Skywalker"))
			;
	}
	
	@Test
	public void testValidarClima() {
		
		given()
			.log().all()
			.queryParam("q", "Fortaleza,BR")
			.queryParam("appid", "c54a7416adb37f79f2b65684b46c7fea")
			.queryParam("units", "metric")
		.when()
			.get("https://api.openweathermap.org/data/2.5/weather")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("Fortaleza"))
			.body("coord.lon", is(-38.5247f))
			.body("main.temp", greaterThan(25f))
			;
	}
	
	@Test
	public void testNaoDeveAcessarSemSenha() {
		
		given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(401)
			;
	}
	
	@Test
	public void testDeveFazerAutenticacaoBasica() {
		
		given()
			.log().all()
		.when()
			.get("https://admin:senha@restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
			;
	}
	
	@Test
	public void testDeveFazerAutenticacaoBasicaEnviandoUsuarioGiven() {
		
		given()
			.log().all()
			.auth().basic("admin", "senha")
		.when()
			.get("https://restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
			;
	}
	
	@Test
	public void testDeveFazerAutenticacaoBasicaChallenge() {
		
		given()
			.log().all()
			.auth().preemptive().basic("admin", "senha")
		.when()
			.get("https://restapi.wcaquino.me/basicauth2")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
			;
	}
	
	@Test
	public void testDeveFazerAutenticacaoComToken() {
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "emailtestewilliam@emailteste.com");
		login.put("senha", "0123456789");
		
		//Login na api e obtenção do token
		String token = given()
			.log().all()
			.body(login)   
			.contentType(ContentType.JSON)
		.when()
			.post("http://barrigarest.wcaquino.me/signin")
		.then()
			.log().all()
			.statusCode(200)
			.extract().path("token")
			;
		
		// Conferir a conta criada no usuario 
		given()
			.log().all()
			.header("Authorization", "JWT " + token)
		.when()
			.get("http://barrigarest.wcaquino.me/contas")
		.then()
			.log().all()
			.statusCode(200)
			.body("nome", hasItem("Conta teste"))
			;
	}
	
	@Test
	public void testAcessarAplicacaoWebDiretamente() {
		//Login pela aplicação web e obtenção do cookie de login
		String cookie = given()
			.log().all()
			.formParam("email", "emailtestewilliam@emailteste.com")
			.formParam("senha", "0123456789")
			.contentType(ContentType.URLENC.withCharset("UTF-8"))
		.when()
			.post("http://seubarriga.wcaquino.me/logar")
		.then()
			.log().all()
			.statusCode(200)
			.extract().header("set-cookie")
			;
		
		cookie = cookie.split("=")[1].split(";")[0];
		System.out.print(cookie);
		
		// Conferir a conta criada no usuario enviando o cookie na requisição
		String body = given()
			.log().all()
			.cookie("connect.sid", cookie)
		.when()
			.get("http://seubarriga.wcaquino.me/contas")
		.then()
			.log().all()
			.statusCode(200)
			.body("hyml.body.table.tbody.tr[0].td[0]", is("Conta teste"))
			.extract().body().asString()
			;
		
		System.out.println("-----------------------------");
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, body);
		System.out.println( xmlPath.getString("hyml.body.table.tbody.tr[0].td[0]"));
	}
}
