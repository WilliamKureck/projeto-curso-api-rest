package br.ce.wkmendes.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.Assert;

import io.restassured.http.ContentType;


public class VerbosTest {

	@Test
	public void testSalvaUsuario() { 
		given()
			.log().all()
			.contentType("application/json")
			.body("{ \"name\": \"jose\", \"age\" : 50}")
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("jose"))
			.body("age", is(50))
			;
	}
	
	@Test
	public void testNaoDeveSalvarUsuarioSemNome() { 
		given()
			.log().all()
//			.contentType("application/json")
			.contentType(ContentType.JSON)
			.body("{ \"age\" : 50}")
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(400)
			.body("id", is(nullValue()))
			.body("error", is("Name é um atributo obrigatório"))
			;
	}
	
	@Test
	public void testSalvarUsuarioViaXML() { 
		given()
			.log().all()
			.contentType(ContentType.XML)
			.body("<user><name>Jose</name><age>50</age></user>")
		.when()
			.post("https://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Jose"))
			.body("user.age", is("50"))
			;
	}
	
	@Test
	public void testAlterarUsuarioComPUT() { 
		given()
			.log().all()
			.contentType("application/json")
			.body("{ \"name\": \"Usuario Alterado\", \"age\" : 80}")
		.when()
				.put("https://restapi.wcaquino.me/users/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Usuario Alterado"))
			.body("age", is(80))
			.body("salary", is(1234.5678f))
			;
	}
	
	@Test
	public void testCustomizarURL() { 
		given()
			.log().all()
			.contentType("application/json")
			.body("{ \"name\": \"Usuario Alterado\", \"age\" : 80}")
			.pathParam("entidade", "users")
			.pathParam("userId", "1")
		.when()
			.put("https://restapi.wcaquino.me/{entidade}/{userId}")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Usuario Alterado"))
			.body("age", is(80))
			.body("salary", is(1234.5678f))
			;
	}
	
	@Test
	public void testRemoverUsuario() { 
		given()
			.log().all()
		.when()
			.delete("https://restapi.wcaquino.me/users/1")
		.then()
			.log().all()
			.statusCode(204)
			;
	}
	
	@Test
	public void testTentarRemoverUsuarioInexistente() { 
		given()
			.log().all()
		.when()
			.delete("https://restapi.wcaquino.me/users/1000")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Registro inexistente"))
			;
	}
	
	@Test
	public void testSalvarUsuarioComMap() { 
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", "Usuario via map");
		params.put("age", 25);
		
		given()
			.log().all()
			.contentType("application/json")
			.body(params)
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.body("id", is(notNullValue()))
			.body("name", is("Usuario via map"))
			.body("age", is(25))
			;
	}
	
	@Test
	public void testSalvarUsuarioUsandoObjeto() { 
		User user = new User("Usuario via objeto", 35);
		
		given()
			.log().all()
			.contentType("application/json")
			.body(user)
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.body("id", is(notNullValue()))
			.body("name", is("Usuario via objeto"))
			.body("age", is(35))
			;
	}	
	
	@Test
	public void testDeserializarObjetoAoSalvarUsuario() { 
		User user = new User("Usuario deserializado", 35);
		
		User usuarioInserido = 
			given()
				.log().all()
				.contentType("application/json")
				.body(user)
			.when()
				.post("https://restapi.wcaquino.me/users")
			.then()
				.log().all()
				.extract().body().as(User.class)
			;
		
		System.out.println(usuarioInserido);
		Assert.assertThat(usuarioInserido.getId(), notNullValue());
		Assert.assertEquals("Usuario deserializado", usuarioInserido.getName());
		Assert.assertThat(usuarioInserido.getAge(), is(35));
	}
	
	@Test
	public void testSalvarUsuarioViaXMLUsandoObjeto() { 
		User user = new User("Usuario XML", 40);
		
		given()
			.log().all()
			.contentType(ContentType.XML)
			.body(user)
		.when()
			.post("https://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Usuario XML"))
			.body("user.age", is("40"))
			;
	}
	
	@Test
	public void testDeserializandoXMLUsuarioViaXMLUsandoObjeto() { 
		User user = new User("Usuario deserializado", 60);
		
		User usuarioInserido = 
				given()
					.log().all()
					.contentType(ContentType.XML)
					.body(user)
				.when()
					.post("https://restapi.wcaquino.me/usersXML")
				.then()
					.log().all()
					.extract().body().as(User.class)
				;
			
			System.out.println(usuarioInserido);
			Assert.assertThat(usuarioInserido.getId(), notNullValue());
			Assert.assertThat(usuarioInserido.getName(), is("Usuario deserializado"));
			Assert.assertThat(usuarioInserido.getAge(), is(60));
			Assert.assertThat(usuarioInserido.getSalary(), nullValue());
		}
}
