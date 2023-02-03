package br.ce.wkmendes.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.hamcrest.Matchers;
import org.junit.Test;

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
			.body("coord.lon", is(-38.52f))
			.body("main.temp", greaterThan(25f))
			;
	}
	//c54a7416adb37f79f2b65684b46c7fea
	// https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}
}
