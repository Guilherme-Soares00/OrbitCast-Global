# Configuração Oracle

O projeto continua usando H2 em memória no modo padrão. Para conectar no Oracle, use o perfil `oracle` e preencha as variáveis de ambiente com os dados da sua conexão do SQL Developer.

## Variáveis

```powershell
$env:ORACLE_JDBC_URL="jdbc:oracle:thin:@//localhost:1521/XEPDB1"
$env:ORACLE_USERNAME="seu_usuario"
$env:ORACLE_PASSWORD="sua_senha"
```

Formatos comuns para a URL:

```text
jdbc:oracle:thin:@//host:porta/service_name
jdbc:oracle:thin:@host:porta:sid
```

## Rodar Com Oracle

```powershell
mvn quarkus:dev -Dquarkus.profile=oracle
```

## Criar Tabelas

Não rode este arquivo no Oracle:

```text
src/main/resources/db/schema.sql
```

Ele é usado pelo H2 local.

Se quiser limpar tabelas antigas do OrbitCast, rode este arquivo no SQL Developer com F5:

```text
src/main/resources/db/oracle/drop.sql
```

Rode este arquivo no SQL Developer:

```text
src/main/resources/db/oracle/schema.sql
```

Depois rode os dados iniciais:

```text
src/main/resources/db/oracle/data.sql
```

## Inicialização Automática

Por padrão, o perfil Oracle não recria as tabelas automaticamente. Se quiser que a aplicação execute os scripts ao iniciar, use:

```powershell
$env:ORBITCAST_DATABASE_INITIALIZE="true"
```

Use essa opção apenas com banco vazio.
