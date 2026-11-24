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
mvn spring-boot:run
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
mvn spring-boot:run
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

## Registro documental do TCC — 21/11/2026

A documentação do artigo foi alinhada ao estado descrito neste repositório. A aplicação tem uma base funcional implementada, mas esta documentação não a caracteriza como pronta para implantação clínica. O código inclui testes unitários e de integração; a execução da suíte precisa ser confirmada antes de relatar testes aprovados. Nesta sessão, não foi possível executá-la porque Maven não está instalado no ambiente, e `make test` não está definido como alvo no arquivo `MakeFile` presente no repositório.

### Artigo atualizado
- [Artigo do TCC — DOCX](docs/Artigo_TCC_atualizado.docx)
- [Artigo do TCC — PDF](docs/Artigo_TCC_atualizado.pdf)
- [Texto integral revisado — Markdown](docs/Artigo_TCC_revisado.md)

O artigo mantém as seções fora da metodologia sem alterações. O resumo e o abstract foram atualizados por necessidade para não descrever a aplicação como estando em fase inicial, agora que o repositório documenta implementação funcional. A metodologia diferencia os componentes implementados, os testes que existem no código mas não foram executados nesta revisão, e as validações ainda pendentes.

### Protocolo de avaliação conforme as 10 heurísticas de Nielsen
A avaliação é futura; não se afirma que a inspeção tenha ocorrido. Quando realizada, registrar tarefa e tela/fluxo, evidência, heurística, problema, severidade e ajuste proposto; depois verificar as correções em nova inspeção.

1. **Visibilidade do estado do sistema:** confirmar que salvamento, carregamento, sucesso e falhas sejam comunicados.
2. **Correspondência com o mundo real:** avaliar vocabulário, formatos de data/hora e conceitos familiares à clínica.
3. **Controle e liberdade:** avaliar cancelamento, retorno e recuperação de ações sem perda inesperada.
4. **Consistência e padrões:** verificar rótulos, componentes, formatos e comportamentos uniformes.
5. **Prevenção de erros:** verificar validações, disponibilidade profissional e prevenção de conflitos de horário.
6. **Reconhecimento em vez de memorização:** verificar se opções, contexto e regras necessárias estão visíveis no fluxo.
7. **Flexibilidade e eficiência de uso:** observar a eficiência dos fluxos frequentes sem dificultar o uso inicial.
8. **Design estético e minimalista:** verificar se as telas priorizam informação essencial para a tarefa.
9. **Ajudar a reconhecer, diagnosticar e recuperar-se de erros:** verificar se mensagens identificam o problema e orientam a correção.
10. **Ajuda e documentação:** verificar se há instruções acessíveis para tarefas e regras pouco evidentes.

### Registro do que foi feito nesta sincronização
- Revisei o README atual, os requisitos do projeto e as classes de teste existentes.
- Atualizei somente a metodologia do artigo e, por necessidade de consistência de estágio, o resumo e o abstract.
- Acrescentei este registro e o checklist de Nielsen ao README existente, preservando as instruções de execução e as funcionalidades já documentadas.
- Não alterei código-fonte. A execução dos testes não foi realizada nesta sessão.


### Auditoria heurística estática — 22/11/2026
A auditoria foi feita pela leitura do HTML, CSS e JavaScript; não houve inspeção interativa nem teste com usuários. Os itens abaixo são evidências no código ou pontos a validar, não uma certificação de conformidade.

1. **Visibilidade do estado:** há mensagens transitórias de sucesso/erro e aviso de disponibilidade; falta indicador claro durante o carregamento geral de dados.
2. **Correspondência com o mundo real:** termos e formatos pt-BR são familiares; confirmar se o campo “idade” atende à rotina e não fica desatualizado em registros persistentes.
3. **Controle e liberdade:** existem edição, limpeza de formulários e confirmação para cancelamento/conclusão; ainda não há aviso antes de descartar dados não salvos.
4. **Consistência e padrões:** botões e telas seguem componentes comuns; pesquisa por paciente aceita Enter, enquanto filtros da agenda dependem do botão “Filtrar”.
5. **Prevenção de erros:** HTML e servidor validam campos e conflitos; não há máscara/instrução consistente para CPF em todos os formulários.
6. **Reconhecimento em vez de memorização:** seletores mostram nomes e há dica para seleção múltipla de serviços; formatos de CPF/telefone e regras da agenda poderiam ter ajuda contextual consistente.
7. **Flexibilidade e eficiência:** busca de pacientes e filtros de agenda existem; a tela não oferece busca rápida equivalente para profissionais/serviços.
8. **Design estético e minimalista:** painéis separam formulário e listagem e o dashboard prioriza agenda; os breakpoints responsivos precisam de conferência visual em navegador/dispositivos.
9. **Reconhecimento e recuperação de erros:** há feedback de servidor e disponibilidade; a clareza da orientação para corrigir cada falha requer validação prática.
10. **Ajuda e documentação:** há instrução inline para selecionar vários serviços e delimitação do propósito da ferramenta; ainda faltam explicações contextuais para duração, conflito e status de agendamento.

## Melhorias de usabilidade — 22/11/2026

### Alterações implementadas
- **Indicação da navegação atual (Nielsen 1 e 4):** o botão da seção ativa agora recebe `aria-current="page"`, atualizado ao trocar de seção; os símbolos decorativos da navegação são ocultados das tecnologias assistivas.
- **Acessibilidade de teclado (boa prática complementar):** links e botões recebem contorno visível ao navegar por teclado; botões durante o envio mostram cursor de espera e menor opacidade. A indicação semântica da seção atual também facilita reconhecê-la na navegação.
- **Feedback acessível de operação (Nielsen 1 e 9):** notificações de sucesso são anunciadas como status polido e erros como alerta assertivo.
- **Prevenção de envio duplicado (Nielsen 5):** enquanto cadastros e agendamentos são processados, o botão é temporariamente desabilitado, apresenta “Salvando…” e o formulário indica `aria-busy`; o botão volta ao estado original ao terminar, inclusive após erro.
- **Metodologia do artigo:** atualizei somente o parágrafo de implementação dentro da seção de metodologia para registrar estas mudanças, deixando explícito que não equivalem à inspeção heurística formal nem à validação com usuários. Resumo, abstract e outras seções permaneceram inalterados.
- **Versões do artigo:** [DOCX](docs/Artigo_TCC_atualizado.docx) e [PDF](docs/Artigo_TCC_atualizado.pdf). O PDF foi regenerado a partir do DOCX e está sincronizado nesta revisão. O resumo e o abstract registram agora os seis testes aprovados; a avaliação Nielsen e o aceite clínico continuam pendentes.

### Heurísticas consideradas neste ciclo
O ciclo implementa melhorias pontuais relacionadas sobretudo a visibilidade do estado, consistência, prevenção de erro e diagnóstico/recuperação de erro. As demais heurísticas permanecem no protocolo de inspeção descrito acima e precisam ser avaliadas nas telas e tarefas reais; não se declara conformidade integral nem inspeção concluída.

### Limites da revisão heurística deste ciclo
A leitura estática identificou pendências que ainda exigem avaliação prática: estado de carregamento explícito durante chamadas de dados; confirmação antes de descartar formulários alterados; exemplos/ajuda consistentes para formatos de CPF e telefone; e avaliação das mensagens e da ajuda contextual nas tarefas reais. O impacto dessas lacunas é uma hipótese até inspeção interativa e validação com representante da clínica. A mudança semântica/visual feita nesta rodada não equivale à conformidade global com as dez heurísticas.

### Verificações realizadas
- Verificar sintaxe JavaScript com `node --check`.
- Revisar visualmente o diff e executar `git diff --check`.
- Na revisão registrada em 22/11, a suíte Maven ainda não havia sido executada; a execução posterior de 23/11 está documentada na seção “Correção do build e testes”. A validação manual da interface em navegador continua pendente.

## Correção do build e testes — 23/11/2026

### O que foi ajustado
- Corrigi `pom.xml` para compilar as fontes e testes localizados em `back_end/src/main/java` e `back_end/src/test/java`, além de carregar os recursos de `back_end/src/main/resources` e disponibilizar a interface em `front_end`.
- Criei um `Makefile` convencional com alvos `run`, `test`, `package` e `clean`. O arquivo legado `MakeFile` foi preservado sem alteração.
- Corrigi no README o comando direto de inicialização, que apontava para `back_end/pom.xml`, inexistente; agora usa `mvn spring-boot:run` na raiz.
- A mudança corrige a configuração de compilação e a descoberta dos testes; não altera regras de negócio.

### Verificação nesta rodada
- O primeiro `mvn test` na raiz havia terminado com “No tests to run”, porque a configuração padrão não alcançava `back_end/src`.
- Após a correção do POM e a instalação do JDK 21 completo, `mvn clean test` concluiu com **6 testes, 0 falhas, 0 erros e 0 ignorados**. A suíte abrange validação de CPF, autenticação obrigatória, cadastro e consulta de pacientes, feedback de dados inválidos e conflito de agendamento por profissional. O alvo `make test` foi executado novamente e também passou com os mesmos seis testes.
- `node --check front_end/js/app.js` e `git diff --check` foram executados sem apontar problemas.
- O aceite com a clínica e a avaliação heurística com usuários continuam pendentes.


## Revisão integral do artigo — 23/11/2026

- Revisei a redação de todas as seções do artigo, não apenas da metodologia. Atualizei introdução, justificativa e delimitação, objetivos, fundamentação teórica, metodologia, resultados e discussão, considerações finais e resumos bilíngues.
- Corrigi a divergência de stack: o artigo passa a descrever HTML, CSS e JavaScript no front-end, sem afirmar o uso de React. Distingui o MySQL definido para o projeto do H2 usado nos testes automatizados.
- Reorganizei o estado do trabalho para separar implementação existente, seis testes automatizados aprovados e validações ainda pendentes. O aceite da clínica, a inspeção de usabilidade com Nielsen, a validação com MySQL e a medição de benefícios não são apresentados como concluídos.
- Reformulei resultados esperados como resultados e discussão, sem afirmar redução de tempo, erros ou conflitos sem dados medidos. Delimitei o sistema como ferramenta administrativa de uma clínica, não prontuário nem solução clínica.
- Corrigi a apresentação das referências com fontes oficiais consultadas para Spring Boot, MySQL e heurísticas de Nielsen.
- Gerei versões novas do DOCX e do PDF, em formato A4, e incluí uma versão integral em Markdown para facilitar leitura e revisão textual.
