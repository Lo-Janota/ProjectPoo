# BET PERFECT – Sistema de Apostas

## Participantes

| Nome | RA |
|------|----|
| Lorenzo de Andrade Souza Janota | 838633 |
| João Vitor Bernardes Vieira | 837901 |

---

## Sobre o projeto

Sistema de apostas esportivas desenvolvido em Java com interface gráfica Swing e persistência de dados via banco SQLite.

---

## Como rodar

### Pré-requisitos

- Java 21 ou superior
- Maven instalado (ou IntelliJ IDEA, que já possui Maven embutido)

### No IntelliJ IDEA

1. Abra o IntelliJ e vá em **File → Open**
2. Selecione a pasta do projeto
3. O IntelliJ detectará o `pom.xml` automaticamente e fará o download do SQLite
4. Execute via **Run → Main**

### No terminal

```bash
mvn compile exec:java -Dexec.mainClass="Main"
```

Para gerar um JAR executável:

```bash
mvn package
java -jar target/ProjectPoo-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## Banco de dados

O arquivo `betperfect.db` é criado automaticamente na primeira execução. Nas execuções seguintes, todos os dados cadastrados (campeonato, clubes, participantes, partidas e apostas) são carregados automaticamente do banco.
