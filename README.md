# Padrão Saga Orquestrado

Este projeto tem a finalidade de ser um projeto de estudos para colocar em prática tudo que foi aprendido sobre o padrão Saga Orquestrado. Que é um padrão utilizado quando se trabalha com arquitetura de microsserviços, é um padrão que resolve o problema de inconsistência de dados quando utilizamos transação distribuída nos microsserviços.

O projeto foi pensado para simular o processo de compra e venda de ativos da bolsa de valores, onde toda compra ou venda de um ativo (ação ou fundo imobiliário) gera um novo pedido, e nesse projeto vamos simular esse processo utilizando a arquitetura de microsserviços e o padrão saga onde será garantido a consistência dos dados.

# Desenho da Solução

![Arquitetura](docs/arquitetura.png)


# Tecnologias

- Java 17
- Spring Boot
- Apache Kafka
- Docker
- PostgreSQL
- MongoDB
- Redpanda Console

# Microsserviços

**order-service:** Microsserviço responsável por receber as informações do pedido, criar o pedido, inicar e encerrar toda a saga.

**orchestrador-service:** Microsserviço responsável por realizar toda a orquestração da saga, além disso é responsável por conhecer os outros microsserviços.

**order-validation-service:** Nesse microsserviço é realizado algumas validações: verificar se o cliente tem saldo o suficiente para realizar a compra do ativo, ou se for uma operação de venda, verifica se o cliente tem a quantidade de ativos o suficiente para vender. Além disso bate na API externa para recuperar o preço atual do ativo e realizar o cálculo total do pedido. E por fim realiza a atualização da posição do cliente.

**order-register-service:** Aqui todos os detalhes do pedido será gravado e um e-mail será enviado para o cliente dizendo que o pedido foi executado com sucesso.

# Configurando o projeto

Antes de rodar os projetos será necessário realizar algumas configurações:

1. Entre no arquivo `application.properties` do projeto `ordervalidationservice` e altere a linha `api.finance.key` colocando o token que é criado ao se cadastrar e se inscrever no site [Real-Time Finance Data](https://rapidapi.com/letscrape-6bRBa3QguO5/api/real-time-finance-data/)

2. Entre no arquivo `application.properties` do projeto `orderregisterservice` e altere as linhas `spring.mail.username` e `spring.mail.password` colocando um e-mail e senha respectivamente.

# Rodar o projeto

> Para rodar o projeto é necessário ter o docker instalado

Para rodar o projeto siga os seguintes passos:

1. Execute o arquivo **docker-compose** que está na raiz do projeto para subir os serviços necessários:
```
docker-compose up --build -d zookeeper kafka redpanda-console order-db order-validation-db order-register-db
```

2. Execute cada microsserviço individualmente dentro da IDE que esteja utilizando ou execute o projeto através do **maven**. Para isso entra na pasta de cada serviço e execute o seguinte comando:
```
./mvnw spring-boot:run
```

# Testando a aplicação

Para criar um pedido e iniciar toda a saga é necessário realizar um **POST** para o seguinte endpoint: http://localhost:3000/api/order

Exemplo de requisição para passar no body:

``` json
{
	"customerDocument": "27242216052",
	"tickerSymbol": "PETR4",
	"tradeQuantity": 5,
	"operation": "BUY"
}
```

# API externa

API utilizada para consultar os preços dos ativos será a [Real-Time Finance Data](https://rapidapi.com/letscrape-6bRBa3QguO5/api/real-time-finance-data/). Ao se cadatrar na plataforma RapidApi podemos utilizar o plano grátis, onde podemos fazer 100 requisições por mês para a API.

# Autor

### Nicolas Rezende