package br.ce.wkmendes.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;



public class FileTest {
	
	@Test
	public void testDeveObrigarEnvioArquivo() {
		given()
			.log().all()
		.when()
			.post("https://restapi.wcaquino.me/upload")
		.then()
			.log().all()
			.statusCode(404) //Deveria ser 400
			.body("error", is("Arquivo não enviado"))
			;

	}
	
	@Test
	public void testDeveFazerUploadArquivo() {
		given()
			.log().all()
			.multiPart("arquivo", new File("src/main/resources/users.pdf"))
		.when()
			.post("https://restapi.wcaquino.me/upload")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("users.pdf"))
			;

	}
	
	@Test
	public void testDeveFazerUploadArquivoGrandeDemais() {
		given()
			.log().all()
			.multiPart("arquivo", new File("src/main/resources/aquivo_teste.bmp"))
		.when()
			.post("https://restapi.wcaquino.me/upload")
		.then()
			.log().all()
			.time(lessThan(5000L))
			.statusCode(413)
			;

	}
	
	@Test
	public void testDeveBaixarArquivo() throws IOException {
		byte[] image = given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/download")
		.then()
//			.log().all()
			.statusCode(200)
			.extract().asByteArray();
		;
			
		File imagem = new File("src/main/resources/file.jpg");
		OutputStream out = new FileOutputStream(imagem);
		out.write(image);
		out.close();
		
		Assert.assertThat(imagem.length(), lessThan(100000L));
		
	}
}
