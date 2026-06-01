# OrbitCast API Contract

Base local:

```text
http://localhost:8080
```

Base de produção:

```text
https://orbitcast.onrender.com
```

Formato padrão:

```text
Content-Type: application/json
```

## Avisos Para Integração Com O Front-End

- A API não usa prefixo `/api`. Use `/clientes`, `/canais`, `/campanhas` e demais rotas diretamente na base da API.
- A rota `/health` retorna `text/plain`, não JSON. Se o front chamar essa rota, use `Accept: */*` ou `Accept: text/plain`.
- As demais rotas principais retornam JSON e podem usar `Accept: application/json`.
- Respostas `DELETE` com status `204` não possuem corpo. O front não deve tentar executar `response.json()` nessas respostas.
- A rota `POST /campanhas/{id}/regioes/{regiaoId}` retorna status `201` sem corpo. O front deve validar pelo status.
- O Render pode demorar ou falhar em uma primeira chamada depois de ficar inativo. Se acontecer erro isolado logo ao abrir o sistema, tente repetir a chamada.
- O CORS já está habilitado para origens locais como `http://localhost:3000` e `http://localhost:5173`.

## Health

```text
GET /health
```

Resposta:

```text
OrbitCast API online
```

## Clientes

```text
GET /clientes
GET /clientes/{id}
POST /clientes
PUT /clientes/{id}
DELETE /clientes/{id}
```

Exemplo para `POST /clientes`:

```json
{
  "nome": "Instituto Conexao Remota",
  "documento": "77700700000113",
  "email": "contato@conexaoremota.org",
  "telefone": "(11) 4000-7000",
  "segmento": "Educacao",
  "ativo": true
}
```

## Canais

```text
GET /canais
GET /canais/{id}
POST /canais
PUT /canais/{id}
DELETE /canais/{id}
```

Exemplo para `POST /canais`:

```json
{
  "clienteId": 1,
  "nome": "Aulas Orbitais",
  "tipoConteudo": "Educacional",
  "publicoAlvo": "Estudantes de regioes remotas",
  "classificacaoIndicativa": "Livre",
  "ativo": true
}
```

## Regioes

```text
GET /regioes
GET /regioes/{id}
POST /regioes
PUT /regioes/{id}
DELETE /regioes/{id}
```

Exemplo para `POST /regioes`:

```json
{
  "nome": "Comunidade Ribeirinha Norte",
  "estado": "AM",
  "pais": "Brasil",
  "populacaoEstimada": 45000,
  "indiceConectividade": 22.5,
  "latitude": -3.1019,
  "longitude": -60.025,
  "areaKm2": 12000.0,
  "prioridadeSocial": 5
}
```

## Campanhas

```text
GET /campanhas
GET /campanhas/{id}
POST /campanhas
PUT /campanhas/{id}
DELETE /campanhas/{id}
GET /campanhas/{id}/regioes
POST /campanhas/{id}/regioes/{regiaoId}
DELETE /campanhas/{id}/regioes/{regiaoId}
GET /campanhas/{id}/simulacoes
POST /campanhas/{id}/simulacoes
GET /campanhas/{id}/planos-cobertura
```

Valores aceitos em `qualidadeDesejada`:

```text
SD
HD
FULL_HD
4K
```

Valores aceitos em `status`:

```text
PLANEJADA
EM_ANALISE
APROVADA
CANCELADA
FINALIZADA
```

Exemplo para `POST /campanhas`:

```json
{
  "clienteId": 1,
  "canalId": 1,
  "nome": "Aulas via Satelite",
  "descricao": "Campanha educacional para regioes com baixa conectividade.",
  "dataInicio": "2026-06-15",
  "dataFim": "2026-06-20",
  "duracaoHoras": 40,
  "qualidadeDesejada": "HD",
  "orcamento": 450000.0,
  "status": "PLANEJADA"
}
```

Exemplo para `POST /campanhas/{id}/regioes/{regiaoId}`:

```json
{
  "prioridade": 5,
  "observacao": "Regiao prioritaria pela baixa conectividade."
}
```

Exemplo para `POST /campanhas/1/simulacoes`:

```text
Sem corpo na requisicao.
```

Resposta de simulacao:

```json
{
  "id": 9,
  "campanhaId": 1,
  "custoEstimado": 1202000.0,
  "alcanceEstimado": 727200,
  "qualidadeSinal": 80.67,
  "viabilidade": "MEDIA",
  "recomendacao": "Revisar plano: considere reduzir regioes, ajustar qualidade ou ampliar o orcamento antes da aprovacao.",
  "dataSimulacao": "2026-05-26T14:48:02.329351"
}
```

## Planos De Cobertura

```text
GET /planos-cobertura
GET /planos-cobertura/{id}
POST /planos-cobertura
PUT /planos-cobertura/{id}
DELETE /planos-cobertura/{id}
GET /campanhas/{id}/planos-cobertura
```

Valores aceitos em `viabilidadeGeral`:

```text
BAIXA
MEDIA
ALTA
```

Exemplo para `POST /planos-cobertura`:

```json
{
  "campanhaId": 1,
  "nome": "Plano Educacional Reforcado",
  "descricao": "Prioriza regioes com baixa conectividade e alto impacto educacional.",
  "custoTotal": 430000.0,
  "alcanceTotal": 535000,
  "viabilidadeGeral": "ALTA"
}
```

## Simulacoes

```text
GET /simulacoes
GET /simulacoes/{id}
```

## Dashboard

```text
GET /dashboard/resumo
```

Resposta:

```json
{
  "totalClientes": 6,
  "totalCanais": 7,
  "totalRegioes": 10,
  "totalCampanhas": 6,
  "totalSimulacoes": 8,
  "alcanceEstimadoTotal": 3575000,
  "custoMedioSimulacoes": 382500.0,
  "qualidadeMediaSinal": 81.2625,
  "campanhasPorStatus": {
    "APROVADA": 2,
    "EM_ANALISE": 2,
    "PLANEJADA": 2
  },
  "simulacoesPorViabilidade": {
    "ALTA": 4,
    "MEDIA": 4
  }
}
```

## Erros

Formato de erro:

```json
{
  "status": 400,
  "erro": "Regra de negocio violada",
  "mensagem": "Nome do cliente e obrigatorio.",
  "timestamp": "2026-05-26T15:00:00"
}
```
