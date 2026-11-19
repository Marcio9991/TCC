# Sistema de gestão para clínica de estética

Aplicação administrativa em desenvolvimento para organizar pacientes, profissionais, serviços e agendamentos de uma clínica de estética. O escopo é uma única clínica. O sistema não é um prontuário clínico e não substitui avaliação ou documentação profissional.

## Tecnologias e estrutura

- `back_end/`: API REST em Java 21 e Spring Boot, organizada em `api`, `config`, `domain`, `repository` e `service`.
- `front_end/`: interface responsiva em HTML, CSS e JavaScript puro, servida pelo Spring Boot.
- `docs/requisitos.md`: requisitos funcionais e não funcionais, critérios de aceitação e decisões ainda pendentes.
- MySQL é o banco de dados de referência. H2 em memória é usado no modo local rápido e nos testes; os dados desse modo desaparecem ao encerrar a aplicação.

## Pré-requisitos

- JDK 21 (ou compatível com o nível configurado no `pom.xml`).
- Maven 3.8+.
- Para persistência local: MySQL 8+ com um banco que o usuário da aplicação possa criar/atualizar.

## Executar rapidamente (H2 em memória)

Na raiz do repositório:

```bash
make run
```

Ou diretamente:

```bash
mvn -f back_end/pom.xml spring-boot:run
```

Abra `http://localhost:8080`. No primeiro início, uma conta `admin` é criada. Se `APP_ADMIN_PASSWORD` não tiver sido definida, uma senha temporária aleatória aparece no log do servidor. Copie-a para o login; ela fica armazenada somente como hash no banco local. H2 em memória não preserva os dados após parar a aplicação.

Para fornecer uma senha local conhecida antes de executar:

```bash
APP_ADMIN_USERNAME=admin APP_ADMIN_PASSWORD='troque-esta-senha' make run
```

Não use credenciais de demonstração em produção. Se a base de dados já contém um usuário, o bootstrap não altera nem redefine as credenciais.

## Executar com MySQL

Crie o banco `clinica_estetica` e configure um usuário e senha. Em seguida:

```bash
SPRING_PROFILES_ACTIVE=mysql \\
DB_URL='jdbc:mysql://localhost:3306/clinica_estetica?serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8' \\
DB_USERNAME='usuario_da_aplicacao' \\
DB_PASSWORD='senha_local' \\
APP_ADMIN_USERNAME='admin' \\
APP_ADMIN_PASSWORD='defina-uma-senha-forte' \\
mvn -f back_end/pom.xml spring-boot:run
```

Não coloque senhas reais no Git. Para ambientes além de demonstração, configure TLS, permissões mínimas do banco, backups, gestão de credenciais e revisão de segurança. O perfil MySQL ainda usa `ddl-auto=update` por conveniência acadêmica; antes de produção, substituir por migrações versionadas (Flyway/Liquibase) e validar política de backup/restauração.

## Testes e build

```bash
make test
make package
```

Os testes de integração usam H2 e verificam bloqueio de endpoints sem login, autenticação, cadastro/consulta de paciente e retorno de validação. Execute `make test` antes de enviar mudanças.

## Funcionalidades já implementadas

- Autenticação por sessão; hash de senha BCrypt; conta inicial criada uma única vez.
- Cadastro, consulta alfabética, pesquisa por nome/CPF e atualização de pacientes; CPF único e validação dos campos.
- Cadastro/atualização de profissionais e associação com os serviços que podem executar.
- Catálogo de serviços e categorias, duração configurável e ativação/inativação.
- Criação, atualização, consulta e filtros de agendamentos por data e paciente.
- Checagem de habilitação e conflito de horários por profissional no servidor; cancelamento/conclusão por status.
- Interface para login, visão geral, cadastros e filtros de agenda.

## API (sessão autenticada)

- `POST /api/auth/login` e `POST /api/auth/logout`.
- `/api/patients` (GET, POST), `/api/patients/{id}` (GET, PUT); `?q=` busca.
- `/api/professionals` (GET, POST), `/api/professionals/{id}` (GET, PUT).
- `/api/services` (GET, POST), `/api/services/{id}` (GET, PUT); `?activeOnly=true`.
- `/api/appointments` (GET, POST), `/api/appointments/{id}` (GET, PUT), `/api/appointments/{id}/status` (PATCH).
- `GET /api/appointments/availability?professionalId=1&serviceId=1&startsAt=2026-12-01T10:00:00`.

As datas usam o horário local do servidor. A API retorna `400` para campos inválidos, `401` para sessão ausente, `404` para registro inexistente e `409` para CPF duplicado ou conflito de agenda.

## Limites e decisões para validação acadêmica

A versão atual é uma base funcional de TCC, não uma implantação clínica pronta para produção. Confirmar com a orientadora/responsável clínica a duração padrão dos serviços, expediente e feriados, campos/endereço, política de cancelamento e conclusão, retenção/backup, dados estritamente necessários, perfis de acesso, e regras aplicáveis à LGPD. O modelo inicial implementa a entidade separada `Address`, com localização opcional vinculada a paciente ou profissional, seguindo os diagramas; a necessidade e os detalhes finais do endereço ainda devem ser validados. A duração do serviço é informada no cadastro (não há duração-padrão).
