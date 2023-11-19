# Padrão Saga Orquestrado

Este projeto tem a finalidade de ser um projeto de estudos para colocar em prática tudo que foi aprendido sobre o padrão Saga Orquestrado. Que é um padrão utilizado quando se trabalha com arquitetura de microsserviços, é um padrão que resolve o problema de inconsistência de dados quando utilizamos transação distribuída nos microsserviços.

O projeto foi pensado para simular o processo de compra e venda de ativos da bolsa de valores, onde toda compra ou venda de um ativo (ação ou fundo imobiliário) gera um novo pedido, e nesse projeto vamos simular esse processo utilizando a arquitetura de microsserviços e o padrão saga onde será garantido a consistência dos dados.

# Desenho da Solução

![Arquitetura](docs/arquitetura.png)


# Tecnologias

- Java
- Spring Boot
- Apache Kafka
- Docker
- PostgreSQL
- MongoDB
- Redpanda Console

# API externa

API utilizada para consultar os preços dos ativos será a [Real-Time Finance Data](https://rapidapi.com/letscrape-6bRBa3QguO5/api/real-time-finance-data/). Ao se cadatrar na plataforma RapidApi podemos utilizar o plano grátis, onde podemos fazer 100 requisições por mês para a API.

# Autor

### Nicolas Rezende