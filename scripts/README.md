# Scripts SQL - População do Banco de Dados

Este diretório contém scripts SQL para popular o banco de dados do sistema Primeira Turma do STF.

## 📋 Arquivos

- `popular_banco.sql` - Script completo para inserir dados de exemplo no banco

## 🚀 Como Usar

### Opção 1: Usando DBeaver

1. Abra o DBeaver
2. Conecte-se ao banco de dados:
   - **Host:** localhost
   - **Porta:** 5432
   - **Database:** primeiraturmadostf
   - **Usuário:** admin
   - **Senha:** admin
3. Abra o arquivo `popular_banco.sql`
4. Execute o script completo (Ctrl+Enter ou botão "Execute SQL Script")

### Opção 2: Usando psql (linha de comando)

```bash
# Conecte-se ao banco
psql -h localhost -p 5432 -U admin -d primeiraturmadostf

# Execute o script
\i scripts/popular_banco.sql

# Ou diretamente:
psql -h localhost -p 5432 -U admin -d primeiraturmadostf -f scripts/popular_banco.sql
```

### Opção 3: Usando Docker

```bash
# Copie o script para o container
docker cp scripts/popular_banco.sql primeiraturmadostf-postgres:/tmp/popular_banco.sql

# Execute dentro do container
docker exec -i primeiraturmadostf-postgres psql -U admin -d primeiraturmadostf < scripts/popular_banco.sql
```

## 📊 Dados Inseridos

O script popula o banco com:

- **5 Cursos** - Tecnologia em ADS, Redes, Gestão de TI, Ciência da Computação, Engenharia de Software
- **8 Assuntos** - Revisão de Nota, Trancamento, Dispensa, Aproveitamento, etc.
- **27 Alunos** - Distribuídos entre os cursos
- **12 Professores** - 3 coordenadores e 9 professores regulares
- **5 Colegiados** - Um por curso, com membros e representantes
- **40 Processos** - Em diferentes status (CRIADO, DISTRIBUIDO, EM_PAUTA, EM_JULGAMENTO, JULGADO)
- **12 Reuniões** - Algumas programadas, outras encerradas com atas
- **Votos** - Votos dos membros do colegiado nos processos das reuniões encerradas
- **Documentos** - Documentos anexados aos processos

## ⚠️ Importante

- **Senhas padrão:** Todos os alunos e professores têm senha `123456`
- **Limpar dados:** Se quiser limpar os dados antes de inserir, descomente as linhas TRUNCATE no início do script
- **Ordem de execução:** O script já está na ordem correta (tabelas sem FK primeiro)

## 🔄 Repopular o Banco

Se quiser repopular o banco do zero:

1. Limpe os dados (descomente as linhas TRUNCATE no início do script)
2. Execute o script novamente

Ou use este comando SQL para limpar tudo:

```sql
TRUNCATE TABLE voto CASCADE;
TRUNCATE TABLE documento CASCADE;
TRUNCATE TABLE reuniao_processo CASCADE;
TRUNCATE TABLE reuniao CASCADE;
TRUNCATE TABLE processo CASCADE;
TRUNCATE TABLE colegiado_professor CASCADE;
TRUNCATE TABLE colegiado CASCADE;
TRUNCATE TABLE aluno CASCADE;
TRUNCATE TABLE professor CASCADE;
TRUNCATE TABLE assunto CASCADE;
TRUNCATE TABLE curso CASCADE;
```
