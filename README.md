# API REST de Clima - Belo Horizonte/MG

API REST feita com Java 21 e Spring Boot que consome a API externa
[Open-Meteo](https://open-meteo.com/), processa a resposta e devolve as informações
meteorológicas de Belo Horizonte - MG (e de outras cidades) em JSON.

Atividade 01 - Desenvolvimento e Integração de Aplicações Web.

## Autores

| Nome | Matrícula |
|---|---|
| _(preencher)_ | _(preencher)_ |
| _(preencher)_ | _(preencher)_ |

## Dependências

| Dependência | Versão |
|---|---|
| Java (JDK) | 21 |
| Spring Boot | 3.5.16 |
| spring-boot-starter-web | herdada do parent |
| spring-boot-starter-validation | herdada do parent |
| springdoc-openapi-starter-webmvc-ui | 2.8.17 |
| spring-boot-starter-test | herdada do parent |
| Maven Wrapper | 3.9.16 |

As chamadas HTTP usam o `RestClient` do próprio Spring, sem biblioteca externa.

A API escolhida foi a Open-Meteo por ser gratuita, não exigir API Key e fornecer todos os
dados pedidos no enunciado.

## Como executar

Pré-requisitos: JDK 21 ou superior e conexão com a internet. Não é preciso instalar o
Maven, o projeto usa o Maven Wrapper.

Clonar o repositório:

```bash
git clone https://github.com/SEU-USUARIO/clima-rest-api.git
```

```bash
cd clima-rest-api
```

Executar (Linux, macOS ou Git Bash):

```bash
./mvnw spring-boot:run
```

Executar (Windows, PowerShell ou CMD):

```bash
mvnw.cmd spring-boot:run
```

A aplicação sobe em `http://localhost:8080`. Para testar:

```bash
curl http://localhost:8080/clima
```

Gerar o jar executável:

```bash
./mvnw clean package
```

```bash
java -jar target/clima-rest-api-0.0.1-SNAPSHOT.jar
```

Para usar outra porta:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=9090
```

## Configuração da API Key

A Open-Meteo é gratuita e não exige API Key, então a aplicação roda sem configuração
adicional. Mesmo assim o projeto já está preparado para receber uma chave sem expô-la no
código. Em `application.properties`:

```properties
open-meteo.api-key=${OPEN_METEO_API_KEY:}
```

A sintaxe `${VARIAVEL:padrao}` lê a chave da variável de ambiente `OPEN_METEO_API_KEY` e
usa vazio quando ela não existe. Quando preenchida, o `OpenMeteoClient` anexa o parâmetro
`apikey` às chamadas.

Definir a variável no Linux, macOS ou Git Bash:

```bash
export OPEN_METEO_API_KEY=sua-chave-aqui
```

No Windows (PowerShell):

```bash
$env:OPEN_METEO_API_KEY = "sua-chave-aqui"
```

No Windows (CMD):

```bash
set OPEN_METEO_API_KEY=sua-chave-aqui
```

A chave nunca é escrita no código nem no `application.properties`. O `.gitignore` bloqueia
`.env`, `application-local.properties` e `application-secret*.properties` para evitar envio
acidental de credenciais.

Para trocar por uma API que exija chave (WeatherAPI, OpenWeather, Tomorrow.io), basta
ajustar as URLs no `application.properties` e definir a variável de ambiente.

## Endpoints

Base: `http://localhost:8080`

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/clima` | Clima atual e previsão de Belo Horizonte - MG |
| GET | `/clima/belo-horizonte` | Mesma resposta, com a cidade explícita na rota |
| GET | `/clima/cidade/{cidade}` | Clima de outra cidade |
| GET | `/swagger-ui.html` | Documentação interativa |
| GET | `/v3/api-docs` | Especificação OpenAPI em JSON |

Parâmetros:

| Parâmetro | Tipo | Onde | Obrigatório | Padrão | Regras |
|---|---|---|---|---|---|
| `dias` | inteiro | query | não | 7 | entre 1 e 16 |
| `cidade` | texto | path | sim | - | entre 2 e 80 caracteres |

Exemplos:

```bash
curl http://localhost:8080/clima
```

```bash
curl "http://localhost:8080/clima?dias=1"
```

```bash
curl "http://localhost:8080/clima/belo-horizonte?dias=3"
```

```bash
curl "http://localhost:8080/clima/cidade/Ouro%20Preto"
```

## Exemplo de resposta

`GET /clima?dias=2` retorna 200 OK:

```json
{
  "localizacao": {
    "cidade": "Belo Horizonte",
    "estado": "Minas Gerais",
    "pais": "Brasil",
    "latitude": -19.9297,
    "longitude": -43.966034,
    "altitudeMetros": 869.0,
    "fusoHorario": "America/Sao_Paulo"
  },
  "consultadoEm": "2026-08-24T07:50:26.725-03:00",
  "fonte": "Open-Meteo",
  "atual": {
    "temperatura": 17.8,
    "sensacaoTermica": 17.1,
    "temperaturaMaxima": 26.7,
    "temperaturaMinima": 16.3,
    "umidade": 80,
    "pressao": 923.3,
    "precipitacao": 0.0,
    "ventoVelocidade": 14.6,
    "ventoDirecaoGraus": 106,
    "ventoDirecao": "ESE",
    "ventoDirecaoExtenso": "Lés-sudeste",
    "codigoCondicao": 1,
    "condicao": "PREDOMINANTEMENTE_LIMPO",
    "descricao": "Predominantemente limpo",
    "diurno": true,
    "observadoEm": "2026-08-24T07:45:00",
    "unidades": {
      "temperatura": "Celsius",
      "umidade": "%",
      "pressao": "hPa",
      "precipitacao": "mm",
      "ventoVelocidade": "km/h",
      "ventoDirecao": "graus"
    }
  },
  "previsao": [
    {
      "data": "2026-08-24",
      "temperaturaMaxima": 26.7,
      "temperaturaMinima": 16.3,
      "precipitacaoTotal": 3.2,
      "probabilidadePrecipitacao": 73,
      "ventoVelocidadeMaxima": 15.0,
      "codigoCondicao": 80,
      "condicao": "PANCADAS_DE_CHUVA_FRACAS",
      "descricao": "Pancadas de chuva fracas",
      "nascerDoSol": "06:10:00",
      "porDoSol": "17:45:00"
    }
  ]
}
```

Onde estão as informações pedidas no enunciado:

| Informação | Campo |
|---|---|
| Temperatura atual | `atual.temperatura` |
| Umidade do ar | `atual.umidade` |
| Velocidade do vento | `atual.ventoVelocidade` |
| Direção do vento | `atual.ventoDirecaoGraus`, `ventoDirecao`, `ventoDirecaoExtenso` |
| Condição climática | `atual.condicao`, `atual.codigoCondicao` |
| Temperatura máxima e mínima | `atual.temperaturaMaxima`, `atual.temperaturaMinima` |
| Descrição do tempo | `atual.descricao` |
| Localização da cidade | `localizacao` |
| Data e horário da consulta | `consultadoEm` |

## Tratamento de erros

Toda falha vira JSON, tratada de forma centralizada no `GlobalExceptionHandler`.

| Situação | HTTP |
|---|---|
| Parâmetro fora do intervalo (`dias=0`) | 400 |
| Parâmetro com tipo inválido (`dias=abc`) | 400 |
| Cidade não encontrada | 404 |
| Rota inexistente | 404 |
| Verbo HTTP não suportado | 405 |
| API externa recusou a requisição (4xx) | 502 |
| API externa fora do ar ou em timeout | 503 |
| Erro não previsto | 500 |

Corpo do erro:

```json
{
  "timestamp": "2026-08-24T07:52:11.735-03:00",
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Nenhuma cidade encontrada com o nome 'zzzqqq'.",
  "caminho": "/clima/cidade/zzzqqq"
}
```

Erros de validação trazem também a lista `detalhes`:

```json
{
  "timestamp": "2026-08-24T07:52:11.790-03:00",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Um ou mais parametros informados sao invalidos.",
  "caminho": "/clima",
  "detalhes": ["O parametro dias deve ser no minimo 1."]
}
```

O timeout de comunicação com a API externa é de 10 segundos e pode ser alterado em
`open-meteo.timeout-segundos`.

## Estrutura do projeto

```text
src/
├── main/
│   ├── java/br/com/pucminas/clima/
│   │   ├── ClimaRestApiApplication.java
│   │   ├── controller/
│   │   │   └── ClimaController.java
│   │   ├── service/
│   │   │   └── ClimaService.java
│   │   ├── client/
│   │   │   ├── OpenMeteoClient.java
│   │   │   └── dto/
│   │   │       ├── OpenMeteoForecastResponse.java
│   │   │       └── OpenMeteoGeocodingResponse.java
│   │   ├── dto/
│   │   │   ├── ClimaResponse.java
│   │   │   ├── LocalizacaoDto.java
│   │   │   ├── CondicaoAtualDto.java
│   │   │   ├── PrevisaoDiaDto.java
│   │   │   └── ErroResponse.java
│   │   ├── config/
│   │   │   ├── OpenMeteoProperties.java
│   │   │   ├── RestClientConfig.java
│   │   │   └── OpenApiConfig.java
│   │   ├── exception/
│   │   │   ├── ClimaIndisponivelException.java
│   │   │   ├── CidadeNaoEncontradaException.java
│   │   │   ├── RequisicaoInvalidaException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   └── util/
│   │       ├── CondicaoClimatica.java
│   │       └── DirecaoVento.java
│   └── resources/
│       └── application.properties
└── test/java/br/com/pucminas/clima/
```

O fluxo de uma requisição segue sempre a mesma direção:

```text
Cliente -> Controller -> Service -> Client -> API Open-Meteo
```

O `ClimaController` valida os parâmetros e delega, sem conhecer a Open-Meteo. O
`ClimaService` converte os dados e não faz chamadas HTTP. O `OpenMeteoClient` fala HTTP e
trata as falhas de comunicação, sem conhecer os DTOs da nossa API.

Os DTOs externos (`client/dto`) são separados dos DTOs próprios (`dto`) de propósito: se a
Open-Meteo mudar o formato dela, o impacto fica contido no `ClimaService` e o contrato da
nossa API continua o mesmo.

O `CondicaoClimatica` traduz o código WMO devolvido pela Open-Meteo para uma descrição em
português, e o `DirecaoVento` converte a direção em graus para os 16 pontos da rosa dos
ventos.

## Testes

```bash
./mvnw test
```

São 29 testes e nenhum depende de internet, a API externa é simulada com Mockito.

| Classe | O que cobre |
|---|---|
| `ClimaControllerTest` | Rotas, formato do JSON, validação e códigos HTTP de erro |
| `ClimaServiceTest` | Conversão da resposta externa e tolerância a campos ausentes |
| `CondicaoClimaticaTest` | Tradução dos códigos WMO |
| `DirecaoVentoTest` | Conversão de graus para a rosa dos ventos |
| `ClimaRestApiApplicationTests` | Carga do contexto Spring |

## Extras

Além do endpoint obrigatório, foram implementados:

- Consulta de outras cidades em `/clima/cidade/{cidade}`, usando a API de geocodificação da
  Open-Meteo para traduzir o nome em coordenadas.
- Previsão para os próximos dias pelo parâmetro `dias`, com máxima, mínima, probabilidade
  de chuva, vento máximo, nascer e pôr do sol.
- Conversão dos dados externos para objetos próprios da aplicação.
- Tratamento de erros cobrindo timeout, indisponibilidade da API externa, erros 4xx e 5xx
  do provedor, cidade inexistente, parâmetros inválidos e rotas inexistentes.
- Documentação interativa em `/swagger-ui.html`.

## Créditos

Dados meteorológicos fornecidos pela [Open-Meteo](https://open-meteo.com/),
sob licença [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/).
