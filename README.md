<div align="center">

# OrbitCast API

Backend para planejamento e simulação de transmissões via satélite em regiões remotas ou com baixa conectividade.

[![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge)](https://www.oracle.com/java/)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.35.4-4695EB?style=for-the-badge)](https://quarkus.io/)
[![Oracle](https://img.shields.io/badge/Oracle-Database-C74634?style=for-the-badge)](https://www.oracle.com/database/)
[![Render](https://img.shields.io/badge/Deploy-Render-46E3B7?style=for-the-badge)](https://render.com/)

</div>

## Visão geral

O **OrbitCast** centraliza cadastros e análises para campanhas de transmissão. A API permite gerenciar clientes, canais, regiões, campanhas, simulações e planos de cobertura, apoiando decisões sobre custo, alcance, qualidade de sinal e viabilidade.

| Item | Valor |
| --- | --- |
| Produção | `https://orbitcast.onrender.com` |
| Health check | `GET /health` |
| Dashboard | `GET /dashboard/resumo` |
| Banco local | H2 em memória |
| Banco externo | Oracle |

A rota raiz `/` não possui endpoint e pode retornar `404`. Para verificar se a API está online, use `/health`.

## Equipe

| Integrante | RM |
| --- | --- |
| Gabriel Stuani | 566682 |
| Guilherme Soares | 568227 |
| Erick Ramos | 567837 |
| Matheus Carneiro Maciel | 567753 |

Turma: `1TDSPB`

## Tecnologias

| Categoria | Ferramentas |
| --- | --- |
| Linguagem | Java 21 |
| Framework | Quarkus 3.35.4, Jakarta REST |
| Banco | H2, Oracle Database |
| Persistência | JDBC manual |
| Build | Maven |
| Deploy | Docker, Render |

## Estrutura

```text
src/main/java/br/com/fiap/orbitcast
  bo
  connection
  dao
  dto
  entities
  exceptions
  resources
```

Scripts de banco:

```text
src/main/resources/db
src/main/resources/db/oracle
```

## Rodando localmente

```shell
mvn quarkus:dev
```

Base local:

```text
http://localhost:8080
```

Teste rápido:

```shell
curl http://localhost:8080/health
```

Resposta esperada:

```text
OrbitCast API online
```

## Oracle

Para rodar com Oracle, configure as variáveis de ambiente e inicie a aplicação com o perfil `oracle`.

```powershell
$env:ORACLE_JDBC_URL="jdbc:oracle:thin:@//host:porta/service_name"
$env:ORACLE_USERNAME="seu_usuario"
$env:ORACLE_PASSWORD="sua_senha"
$env:ORBITCAST_DATABASE_INITIALIZE="false"
mvn quarkus:dev -Dquarkus.profile=oracle
```

Use `ORBITCAST_DATABASE_INITIALIZE=true` apenas com banco vazio.

Documentação completa do Oracle: [ORACLE_SETUP.md](ORACLE_SETUP.md)

## Deploy

O projeto está pronto para deploy no Render como **Web Service** usando Docker.

| Campo | Valor |
| --- | --- |
| Runtime | Docker |
| Branch | `main` |
| Root Directory | vazio |
| Dockerfile Path | `./Dockerfile` |

Variáveis no Render:

```text
QUARKUS_PROFILE=oracle
ORACLE_JDBC_URL=jdbc:oracle:thin:@//host:porta/service_name
ORACLE_USERNAME=seu_usuario
ORACLE_PASSWORD=sua_senha
ORBITCAST_DATABASE_INITIALIZE=false
```

Não configure `PORT` manualmente no Render. A plataforma fornece essa variável automaticamente.

## Endpoints

| Recurso | Endpoints |
| --- | --- |
| Health | `GET /health` |
| Dashboard | `GET /dashboard/resumo` |
| Clientes | `GET /clientes`, `GET /clientes/{id}`, `POST /clientes`, `PUT /clientes/{id}`, `DELETE /clientes/{id}` |
| Canais | `GET /canais`, `GET /canais/{id}`, `POST /canais`, `PUT /canais/{id}`, `DELETE /canais/{id}` |
| Regiões | `GET /regioes`, `GET /regioes/{id}`, `POST /regioes`, `PUT /regioes/{id}`, `DELETE /regioes/{id}` |
| Campanhas | `GET /campanhas`, `GET /campanhas/{id}`, `POST /campanhas`, `PUT /campanhas/{id}`, `DELETE /campanhas/{id}` |
| Campanha e regiões | `GET /campanhas/{id}/regioes`, `POST /campanhas/{id}/regioes/{regiaoId}`, `DELETE /campanhas/{id}/regioes/{regiaoId}` |
| Simulações | `GET /simulacoes`, `GET /simulacoes/{id}`, `GET /campanhas/{id}/simulacoes`, `POST /campanhas/{id}/simulacoes` |
| Planos de cobertura | `GET /planos-cobertura`, `GET /planos-cobertura/{id}`, `POST /planos-cobertura`, `PUT /planos-cobertura/{id}`, `DELETE /planos-cobertura/{id}` |

O endpoint `POST /campanhas/{id}/simulacoes` não recebe corpo. A simulação é calculada com base na campanha e nas regiões associadas.

Contrato completo com exemplos de JSON: [API_CONTRACT.md](API_CONTRACT.md)

## Validação

Testes automatizados:

```shell
mvn test
```

Validação em produção:

```text
GET https://orbitcast.onrender.com/health
GET https://orbitcast.onrender.com/dashboard/resumo
GET https://orbitcast.onrender.com/clientes
```

## Formato de erro

```json
{
  "status": 400,
  "erro": "Regra de negocio violada",
  "mensagem": "Nome do cliente e obrigatorio.",
  "timestamp": "2026-05-26T15:00:00"
}
```
