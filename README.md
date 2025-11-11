# Primeira Turma do STF

Projeto Spring Boot desenvolvido para a disciplina de Programação Web 2 do IFPB.

## 📁 Estrutura de Pastas

```
Primeira_Turma_do_STF/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/edu/ifpb/pweb2/primeiraturmadostf/
│   │   │       ├── PrimeiraturmadostfApplication.java    # Classe principal
│   │   │       └── model/
│   │   │           └── TesteConexao.java                # Entidade JPA de exemplo
│   │   └── resources/
│   │       └── application.properties                   # Configurações da aplicação
│   └── test/
│       └── java/                                        # Testes unitários
├── pom.xml                                              # Dependências Maven
├── .env                                                 # Variáveis de ambiente (não versionado)
└── README.md
```

## ⚙️ Configuração Antes de Executar

### 1. Pré-requisitos

- **Java 21** ou superior
- **Maven** 3.6+ (ou use o Maven Wrapper incluído)
- **PostgreSQL** instalado e em execução

### 2. Criar o Banco de Dados

Crie um banco de dados PostgreSQL chamado `primeiraturmadostf`:

**Via pgAdmin:**

1. Abra o pgAdmin
2. Clique com botão direito em **Databases** → **Create** → **Database...**
3. Nome: `primeiraturmadostf`
4. Clique em **Save**

**Via linha de comando:**

```bash
psql -U postgres
CREATE DATABASE primeiraturmadostf;
\q
```

### 3. Configurar o arquivo `.env`

Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis:

```env
DB_URL=jdbc:postgresql://localhost:5432/primeiraturmadostf
DB_USERNAME=postgres
DB_PASSWORD=sua_senha_do_postgres
```

**Importante:**

- Substitua `sua_senha_do_postgres` pela senha que você configurou na instalação do PostgreSQL
- Se o PostgreSQL estiver em outro servidor, ajuste o `DB_URL` com o IP/hostname correto
- O arquivo `.env` não deve ser commitado no repositório (já está no `.gitignore`)

## 🚀 Como Executar

### Opção 1: Maven Wrapper (Recomendado)

**Windows:**

```bash
.\mvnw.cmd spring-boot:run
```

**Linux/Mac:**

```bash
./mvnw spring-boot:run
```

### Opção 2: Maven (se estiver instalado)

```bash
mvn spring-boot:run
```

### Opção 3: Executando pela IDE

1. Abra o projeto na sua IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)
2. Localize a classe `PrimeiraturmadostfApplication.java`
3. Execute como aplicação Java

## ✅ Verificação

Após iniciar a aplicação, você verá logs no console indicando que o Spring Boot está rodando. A aplicação estará disponível em:

- **URL:** http://localhost:8080

## 🛠️ Tecnologias Utilizadas

- **Spring Boot** 3.5.6
- **Java** 21
- **PostgreSQL**
- **Spring Data JPA**
- **Maven**
- **Lombok**
- **dotenv-java**

## 📚 Desenvolvido por

Projeto desenvolvido para a disciplina de Programação Web 2 - IFPB.
