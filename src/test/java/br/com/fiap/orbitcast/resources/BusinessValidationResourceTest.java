package br.com.fiap.orbitcast.resources;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class BusinessValidationResourceTest {

    @Test
    void deveRecusarClienteComDocumentoInvalido() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "nome": "Cliente Documento Invalido",
                          "documento": "99999999999",
                          "email": "documento.invalido@orbitcast.com",
                          "telefone": "(11) 4000-9000",
                          "segmento": "Educacao",
                          "ativo": true
                        }
                        """)
                .when()
                .post("/clientes")
                .then()
                .statusCode(400)
                .body("mensagem", containsString("Documento"));
    }

    @Test
    void deveRecusarClienteComEmailInvalido() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "nome": "Cliente Email Invalido",
                          "documento": "88800800000138",
                          "email": "email-invalido",
                          "telefone": "(11) 4000-9001",
                          "segmento": "Educacao",
                          "ativo": true
                        }
                        """)
                .when()
                .post("/clientes")
                .then()
                .statusCode(400)
                .body("mensagem", containsString("Email"));
    }

    @Test
    void deveRecusarCanalParaClienteInexistente() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "clienteId": 9999,
                          "nome": "Canal Cliente Inexistente",
                          "tipoConteudo": "Educacional",
                          "publicoAlvo": "Estudantes",
                          "classificacaoIndicativa": "Livre",
                          "ativo": true
                        }
                        """)
                .when()
                .post("/canais")
                .then()
                .statusCode(400)
                .body("mensagem", containsString("Cliente informado para o canal nao existe"));
    }

    @Test
    void deveRecusarCanalDuplicadoParaCliente() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "clienteId": 1,
                          "nome": "Aulas Sem Fronteiras",
                          "tipoConteudo": "Educacional",
                          "publicoAlvo": "Estudantes",
                          "classificacaoIndicativa": "Livre",
                          "ativo": true
                        }
                        """)
                .when()
                .post("/canais")
                .then()
                .statusCode(400)
                .body("mensagem", containsString("Cliente ja possui um canal"));
    }

    @Test
    void deveRecusarRegiaoComCoordenadaIncompleta() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "nome": "Regiao Coordenada Incompleta",
                          "estado": "AM",
                          "pais": "Brasil",
                          "populacaoEstimada": 10000,
                          "indiceConectividade": 30.5,
                          "latitude": -3.1019,
                          "areaKm2": 1200.0,
                          "prioridadeSocial": 4
                        }
                        """)
                .when()
                .post("/regioes")
                .then()
                .statusCode(400)
                .body("mensagem", containsString("Latitude e longitude"));
    }

    @Test
    void deveRecusarCampanhaComCanalDeOutroCliente() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "clienteId": 1,
                          "canalId": 2,
                          "nome": "Campanha Canal Invalido",
                          "descricao": "Tentativa de usar canal de outro cliente.",
                          "dataInicio": "2026-10-01",
                          "dataFim": "2026-10-02",
                          "duracaoHoras": 8,
                          "qualidadeDesejada": "HD",
                          "orcamento": 100000.0,
                          "status": "PLANEJADA"
                        }
                        """)
                .when()
                .post("/campanhas")
                .then()
                .statusCode(400)
                .body("mensagem", containsString("nao pertence ao cliente"));
    }

    @Test
    void deveRecusarCampanhaComDuracaoMaiorQuePeriodo() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "clienteId": 1,
                          "canalId": 1,
                          "nome": "Campanha Duracao Invalida",
                          "descricao": "Duracao maior que a janela disponivel.",
                          "dataInicio": "2026-10-01",
                          "dataFim": "2026-10-01",
                          "duracaoHoras": 25,
                          "qualidadeDesejada": "HD",
                          "orcamento": 100000.0,
                          "status": "PLANEJADA"
                        }
                        """)
                .when()
                .post("/campanhas")
                .then()
                .statusCode(400)
                .body("mensagem", containsString("periodo da campanha"));
    }

    @Test
    void deveRecusarCampanhaNovaEncerrada() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "clienteId": 1,
                          "canalId": 1,
                          "nome": "Campanha Ja Finalizada",
                          "descricao": "Campanha criada em estado encerrado.",
                          "dataInicio": "2026-10-01",
                          "dataFim": "2026-10-02",
                          "duracaoHoras": 8,
                          "qualidadeDesejada": "HD",
                          "orcamento": 100000.0,
                          "status": "FINALIZADA"
                        }
                        """)
                .when()
                .post("/campanhas")
                .then()
                .statusCode(400)
                .body("mensagem", containsString("nao pode iniciar"));
    }

    @Test
    void deveRecusarRemocaoDeClienteComVinculos() {
        given()
                .when()
                .delete("/clientes/1")
                .then()
                .statusCode(400)
                .body("mensagem", containsString("vinculadas"));
    }

    @Test
    void deveRecusarSimulacaoDeCampanhaSemRegiao() {
        Integer campanhaId = given()
                .contentType("application/json")
                .body("""
                        {
                          "clienteId": 1,
                          "canalId": 1,
                          "nome": "Campanha Sem Regiao",
                          "descricao": "Campanha ainda sem cobertura definida.",
                          "dataInicio": "2026-11-01",
                          "dataFim": "2026-11-01",
                          "duracaoHoras": 2,
                          "qualidadeDesejada": "HD",
                          "orcamento": 100000.0,
                          "status": "PLANEJADA"
                        }
                        """)
                .when()
                .post("/campanhas")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("status", is("PLANEJADA"))
                .extract()
                .path("id");

        given()
                .when()
                .post("/campanhas/{id}/simulacoes", campanhaId)
                .then()
                .statusCode(400)
                .body("mensagem", containsString("ao menos uma regiao"));
    }

    @Test
    void deveExecutarCrudDePlanoCobertura() {
        Integer planoId = given()
                .contentType("application/json")
                .body("""
                        {
                          "campanhaId": 2,
                          "nome": "Plano Teste Cobertura",
                          "descricao": "Plano criado para validar o CRUD.",
                          "custoTotal": 250000.0,
                          "alcanceTotal": 300000,
                          "viabilidadeGeral": "MEDIA"
                        }
                        """)
                .when()
                .post("/planos-cobertura")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("campanhaId", is(2))
                .body("viabilidadeGeral", is("MEDIA"))
                .extract()
                .path("id");

        given()
                .when()
                .get("/planos-cobertura/{id}", planoId)
                .then()
                .statusCode(200)
                .body("nome", is("Plano Teste Cobertura"));

        given()
                .when()
                .get("/campanhas/2/planos-cobertura")
                .then()
                .statusCode(200)
                .body("find { it.id == " + planoId + " }.nome", is("Plano Teste Cobertura"));

        given()
                .contentType("application/json")
                .body("""
                        {
                          "campanhaId": 2,
                          "nome": "Plano Teste Cobertura Atualizado",
                          "descricao": "Plano atualizado para validar o CRUD.",
                          "custoTotal": 260000.0,
                          "alcanceTotal": 310000,
                          "viabilidadeGeral": "ALTA"
                        }
                        """)
                .when()
                .put("/planos-cobertura/{id}", planoId)
                .then()
                .statusCode(200)
                .body("nome", is("Plano Teste Cobertura Atualizado"))
                .body("viabilidadeGeral", is("ALTA"));

        given()
                .when()
                .delete("/planos-cobertura/{id}", planoId)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/planos-cobertura/{id}", planoId)
                .then()
                .statusCode(404);
    }

    @Test
    void deveRecusarPlanoCoberturaSemRegiaoNaCampanha() {
        Integer campanhaId = given()
                .contentType("application/json")
                .body("""
                        {
                          "clienteId": 1,
                          "canalId": 1,
                          "nome": "Campanha Plano Sem Regiao",
                          "descricao": "Campanha sem regioes para validar plano.",
                          "dataInicio": "2026-12-01",
                          "dataFim": "2026-12-02",
                          "duracaoHoras": 4,
                          "qualidadeDesejada": "HD",
                          "orcamento": 120000.0,
                          "status": "PLANEJADA"
                        }
                        """)
                .when()
                .post("/campanhas")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .contentType("application/json")
                .body("""
                        {
                          "campanhaId": %d,
                          "nome": "Plano Sem Regiao",
                          "descricao": "Plano invalido para campanha sem regiao.",
                          "custoTotal": 100000.0,
                          "alcanceTotal": 200000,
                          "viabilidadeGeral": "MEDIA"
                        }
                        """.formatted(campanhaId))
                .when()
                .post("/planos-cobertura")
                .then()
                .statusCode(400)
                .body("mensagem", containsString("ao menos uma regiao"));
    }
}
