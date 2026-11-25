# Sistema de gestão para clínica de estética

Aplicação acadêmica para apoiar a gestão administrativa de uma clínica de estética. O sistema reúne cadastro e consulta de pacientes, profissionais e serviços, além do gerenciamento de agendamentos. Não é prontuário clínico e não está preparado para uso produtivo com dados reais.

## Arquitetura atual

O projeto foi convertido para **MVC (Model–View–Controller)** com páginas HTML renderizadas no servidor. As requisições do navegador chegam aos controladores Spring MVC; os serviços de aplicação aplicam as regras de negócio; entidades e repositórios JPA representam e persistem os dados; as páginas Thymeleaf são renderizadas pelo servidor e enviadas ao navegador. A navegação utiliza páginas Thymeleaf renderizadas no servidor e formulários HTML tratados pelos controladores Spring MVC.

A estrutura principal é:

- `back_end/src/main/java/.../web/`: controladores de páginas MVC.
- `back_end/src/main/java/.../web/form/`: objetos de formulário e objetos de entrada utilizados pelos fluxos da aplicação.
- `back_end/src/main/java/.../service/`: regras de cadastro, relacionamento e agendamento.
- `back_end/src/main/java/.../domain/` e `repository/`: entidades e persistência JPA.
- `back_end/src/main/resources/templates/`: páginas e fragmentos Thymeleaf.
- `front_end/css/app.css`: estilos compartilhados pelas páginas do servidor.
- `docs/requisitos.md`: requisitos e decisões que ainda precisam de validação.

A autenticação é feita por formulário e sessão, com senhas protegidas por BCrypt e proteção CSRF nos formulários de alteração. Os serviços preservam as validações de CPF, a associação entre profissionais e serviços e a prevenção de conflitos de agenda.

## Tecnologias e banco de dados

O projeto usa Java 21, Spring Boot 3.3.5, Spring MVC, Thymeleaf, Spring Security, Spring Data JPA e Hibernate. O modo local padrão usa H2 em arquivo (`data/clinica.mv.db`) para persistir os dados entre reinicializações. O perfil `mysql` continua disponível como alternativa; os testes automatizados usam um banco H2 isolado em memória. A execução e os testes desta revisão não comprovam a configuração do MySQL.

## Pré-requisitos

- JDK 21.
- Internet na primeira execução do Maven Wrapper para baixar Maven e dependências, salvo se já estiverem em cache.
- MySQL 8 ou compatível somente se optar pelo perfil alternativo MySQL.

## Executar localmente no Linux com H2

Na raiz do projeto, use o Maven Wrapper incluído. Não é necessário instalar Maven globalmente, mas a primeira execução requer rede e permissão para baixar os arquivos necessários.

```bash
chmod +x mvnw run-local.sh
export APP_ADMIN_USERNAME=admin
export APP_ADMIN_PASSWORD='defina-uma-senha-para-a-demonstracao'
./run-local.sh
```

Mantenha o terminal aberto e acesse `http://localhost:8080`. Se não definir `APP_ADMIN_PASSWORD`, o sistema gera uma senha temporária e a informa no log. A conta inicial só é criada quando o banco ainda não contém usuários; mudar a variável depois não redefine uma senha já salva.

O banco fica em `data/clinica.mv.db`. Para apagar os dados de demonstração, pare a aplicação e remova esse arquivo. Não o apague enquanto o sistema estiver em execução. Use somente dados fictícios, especialmente em computadores escolares compartilhados.

O Wrapper não elimina a necessidade de internet na primeira execução e não funciona offline sem Maven e dependências já disponíveis em cache.

## Executar com MySQL (alternativa)

Crie previamente o banco `clinica_estetica` e configure um usuário com as permissões necessárias. Depois, na raiz do projeto:

```bash
SPRING_PROFILES_ACTIVE=mysql \
DB_URL='jdbc:mysql://localhost:3306/clinica_estetica?serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8' \
DB_USERNAME='usuario_da_aplicacao' \
DB_PASSWORD='senha_local' \
APP_ADMIN_USERNAME='admin' \
APP_ADMIN_PASSWORD='defina-uma-senha-forte' \
./mvnw spring-boot:run
```

Não registre senhas no Git. O perfil é uma opção de configuração e ainda requer validação em um ambiente MySQL real antes de afirmar que o sistema foi testado com esse banco.

## Funcionalidades disponíveis nas páginas

- Acesso autenticado, encerramento de sessão e proteção das páginas administrativas.
- Visão geral com contadores e agendamentos do dia.
- Pacientes: cadastro, consulta por nome ou CPF e edição.
- Profissionais: cadastro, edição e associação a serviços ativos.
- Serviços: cadastro, edição, tipo, duração e ativação/inativação.
- Agendamentos: cadastro, edição, consulta, filtros por paciente/data e alteração para cancelado ou concluído.
- Validação server-side e feedback de sucesso, falha ou ausência de resultados.
- Verificação de que o profissional está habilitado para o serviço e prevenção de sobreposição de horários segundo a duração cadastrada.

## Usabilidade: aplicação das dez heurísticas de Nielsen

As dez heurísticas são princípios para orientar a avaliação da interface; citá-las não significa que uma avaliação formal ou teste com usuários tenha sido concluído. Nesta versão, as páginas aplicam esses princípios de forma inicial: (1) mensagens visíveis de sucesso e erro comunicam o resultado; (2) rótulos em português usam os conceitos do domínio administrativo; (3) links de retorno e cancelamento de edição permitem abandonar a edição; (4) navegação, formulários e tabelas compartilham a mesma estrutura visual; (5) validações de servidor e restrições dos campos impedem dados inválidos; (6) seleções mostram opções disponíveis para reconhecimento, sem exigir memorização de IDs; (7) filtros de paciente e data facilitam as tarefas frequentes; (8) cada tela prioriza cadastro, consulta ou agenda sem elementos decorativos necessários; (9) erros de campo e de regra aparecem junto ao formulário; e (10) instruções curtas são apresentadas nos campos que precisam de orientação.

A implementação não foi submetida a avaliação interativa com usuários, inspeção heurística formal documentada com severidade ou aceite da clínica. A adequação dos rótulos, fluxos e mensagens ainda precisa ser verificada com a orientadora e a responsável pela clínica. As heurísticas servem como orientação e não como prova de conformidade ou de usabilidade.

## Testes

Execute na raiz:

```bash
./mvnw clean test
```

Os testes usam H2 em memória. A suíte tem seis testes aprovados: quatro cenários de integração MVC que verificam redirecionamento para login e renderização das páginas, autenticação por formulário, cadastro e busca de paciente, feedback de validação e conflito de horário no formulário de agendamento; e dois testes de validação de CPF. O resultado deve ser confirmado no ambiente em que o comando for executado; não se deve inferir cobertura completa dos requisitos a partir de poucos cenários.

## Artigo do TCC e pacote para Overleaf

O artigo atualizado inclui tabelas com os 12 requisitos funcionais (RF01–RF12) e os 10 não funcionais (RNF01–RNF10), com a situação na versão MVC e as pendências de validação, além dos diagramas de casos de uso, classes e entidade-relacionamento. Os modelos são identificados como artefatos iniciais, não como prova de validação integral.

- [Pacote ZIP pronto para importar no Overleaf](Artigo_TCC_Overleaf.zip) — contém `main.tex` e a pasta `figuras/` necessária à compilação.
- [Fonte LaTeX](Artigo_TCC_Overleaf.tex) — requer os arquivos de imagem ao lado, portanto não deve ser enviada isoladamente.
- [Artigo em PDF](docs/Artigo_TCC_atualizado.pdf) e [versão editável DOCX](docs/Artigo_TCC_atualizado.docx).
- [Texto integral em Markdown](docs/Artigo_TCC_revisado.md) e [documento detalhado de requisitos](docs/requisitos.md).

No Overleaf, escolha **New Project > Upload Project**, envie `Artigo_TCC_Overleaf.zip` e compile com **pdfLaTeX**.

Para gerar o pacote executável:

```bash
./mvnw package
```

## Alterações realizadas nesta conversão — 25/11/2026

- Reestruturei a aplicação como MVC simples, com páginas Thymeleaf renderizadas no servidor e formulários HTML tradicionais.
- Adicionei Thymeleaf e um controlador `@Controller` para entregar as páginas renderizadas no servidor.
- Criei páginas para login, visão geral, pacientes, profissionais, serviços e agendamentos, com formulários HTML tradicionais e mensagens de validação.
- Configurei autenticação por formulário e sessão, preservando BCrypt e CSRF.
- Mantive as regras já existentes nos serviços de negócio, sem transferi-las para a apresentação.
- O fluxo administrativo principal agora é realizado por formulários HTML submetidos aos controladores Spring MVC; o JavaScript fica restrito a interações pontuais da interface.
- Atualizei os testes de integração para exercitar o login e os formulários MVC em vez do contrato JSON.
- Atualizei o nome e a descrição do projeto Maven para identificá-lo como aplicação MVC.
- Adaptei os estilos existentes para a navegação, tabelas e formulários das páginas renderizadas no servidor.
- Localizei as mensagens de validação em português e alinhei o intervalo de duração apresentado no formulário com a regra do serviço.
- `./mvnw clean test` passou com seis testes (quatro cenários MVC e dois de CPF); `./mvnw clean package` gerou o JAR. Um smoke test HTTP confirmou `200` em `/login` e `/css/app.css` e redirecionamento `302` de `/patients` sem sessão.
- Revisei integralmente o artigo: alinhei título, problema, pergunta de pesquisa, objetivos, fundamentação, metodologia, resultados e conclusão à versão MVC e às evidências disponíveis; acrescentei fontes oficiais sobre Spring MVC e Nielsen e um excerto do teste automatizado de conflito de horário. O texto distingue verificações técnicas de avaliação com usuários, aceite da clínica e validação MySQL, que continuam pendentes.
- Nesta atualização, incluí as tabelas RF01–RF12 e RNF01–RNF10, inseri os diagramas de casos de uso, classes e DER, gerei DOCX/PDF A4 atualizados e montei um ZIP completo para o Overleaf.

## Limites conhecidos e próximos pontos de validação

A suíte automatizada não substitui teste completo por tarefa, teste de aceitação, teste com a clínica ou validação em MySQL. A aplicação não foi caracterizada como pronta para produção. Antes de uso real, ainda precisam ser definidos e verificados os requisitos operacionais e de privacidade, a gestão de credenciais, os procedimentos de backup e restauração, a implantação e a política de acesso. A duração de cada serviço é cadastrada; não há duração padrão ou regras completas de expediente e feriados.
