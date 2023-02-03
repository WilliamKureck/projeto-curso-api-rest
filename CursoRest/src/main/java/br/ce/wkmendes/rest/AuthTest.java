package br.ce.wkmendes.rest;

import static io.restassured.RestAssured.*;

import org.hamcrest.Matchers;
import org.junit.Test;

public class AuthTest {

	@Test
	public void testValidarUsuarioSWAPI() {
		
		given()
			.log().all()
		.when()
			.get("https://swapi.dev/api/people/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", Matchers.is("Luke Skywalker"))
			;
	}
}
