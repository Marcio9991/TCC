# Aplicação Web para Gestão de Clínica de Estética

**Marcio Andrez da Silva Júnior; Roberta Vanessa Rojo Parcianello**  
Instituto Federal do Paraná — Campus Cascavel (IFPR)  
m20233005680@gmail.com; roberta.parcianello@ifpr.edu.br

## Abstract

Small aesthetic clinics may use spreadsheets, paper forms and appointment books to organize administrative records. This work develops a web application to support patient and professional records, service management and appointment scheduling in one clinic. Development follows an incremental process guided by documented requirements and models. The system uses Java and Spring Boot on the back end and HTML, CSS and JavaScript on the front end. MySQL is the selected database, while the reported automated tests use H2. Implemented functions include session authentication, patient registration and search, professional and service management, and appointment operations that check professional qualification and schedule conflicts. The test suite passed six tests, with no failures, errors or skipped tests. Clinic acceptance, heuristic usability inspection and validation with MySQL remain pending. The results therefore describe a functional increment rather than a completed deployment or measured operational impact.

**Keywords:** aesthetic clinic; web application; appointment management; usability; incremental development.

## Resumo

Pequenas clínicas de estética podem utilizar planilhas, fichas em papel e agendas para organizar informações administrativas. Este trabalho desenvolve uma aplicação web para apoiar cadastros de pacientes e profissionais, serviços e agendamentos em uma clínica. O desenvolvimento é incremental e orientado por requisitos e modelos documentados. O sistema utiliza Java e Spring Boot no back-end e HTML, CSS e JavaScript no front-end. O MySQL foi definido como banco de dados, enquanto os testes automatizados relatados usam H2. As funções implementadas incluem autenticação por sessão, cadastro e busca de pacientes, manutenção de profissionais e serviços e operações de agendamento com verificação de habilitação profissional e conflitos de horário. A suíte passou em seis testes, sem falhas, erros ou testes ignorados. O aceite da clínica, a inspeção heurística de usabilidade e a validação com MySQL continuam pendentes. Assim, os resultados descrevem um incremento funcional, não uma implantação concluída ou impacto operacional mensurado.

**Palavras-chave:** clínica de estética; aplicação web; gestão de agendamentos; usabilidade; desenvolvimento incremental.

## 1. Introdução

A organização de cadastros e horários é parte importante da rotina administrativa de uma clínica de estética. Quando essas informações ficam distribuídas entre agendas físicas, fichas e planilhas, a equipe pode precisar repetir registros, consultar diferentes fontes e conferir manualmente a disponibilidade de horários. Essas situações podem aumentar o esforço administrativo e favorecer inconsistências, embora sua frequência e seus efeitos específicos devam ser confirmados junto à clínica.

Este trabalho trata do desenvolvimento de uma aplicação web para uma clínica específica. O escopo concentra-se no apoio administrativo ao cadastro e à consulta de pacientes, à manutenção de profissionais e serviços e ao gerenciamento de agendamentos. Para registrar um atendimento, a aplicação associa paciente, profissional, serviço, data e horário; também verifica se o profissional está relacionado ao serviço escolhido e se há conflito de agenda.

O sistema não tem como finalidade substituir um prontuário, orientar procedimentos estéticos, realizar diagnósticos ou gerenciar pagamentos. Tampouco contempla, nesta versão, operação para múltiplas clínicas. A questão que orienta o trabalho é: como uma aplicação web de escopo administrativo delimitado pode apoiar a organização de cadastros e agendamentos de uma clínica de estética?

O objetivo geral é desenvolver essa aplicação e verificar, por meio de testes automatizados disponíveis, parte das regras implementadas. A validação de aceite pela clínica e a avaliação de usabilidade são etapas posteriores e não são apresentadas como concluídas.

### 1.1 Justificativa e delimitação

A centralização de informações em uma aplicação pode reduzir a necessidade de consultar registros dispersos e facilitar a recuperação de dados administrativos. No entanto, a existência do sistema, por si só, não comprova redução de erros, economia de tempo ou diminuição do uso de papel. Esses benefícios dependem da adoção pela equipe e devem ser avaliados com dados coletados em condições definidas.

A aplicação mantém dados cadastrais e de agenda necessários às funções aprovadas. Informações pessoais devem ser limitadas ao escopo acordado com a clínica. Os campos e regras que ainda dependem de decisão — por exemplo, finalidade de determinados dados cadastrais, duração dos serviços e política completa de disponibilidade — não são tratados como requisitos definitivamente validados. O foco deste artigo é o avanço técnico documentado, sem alegar conformidade legal integral ou implantação em produção.

## 2. Objetivos

### 2.1 Objetivo geral

Desenvolver uma aplicação web para apoiar a gestão administrativa de uma clínica de estética, com foco no cadastro de pacientes e no gerenciamento de profissionais, serviços e agendamentos.

### 2.2 Objetivos específicos

- Implementar autenticação para restringir o acesso às funções administrativas.
- Permitir o cadastro e a consulta de pacientes.
- Manter registros de profissionais e serviços e suas associações pertinentes.
- Registrar e consultar agendamentos, verificando a habilitação do profissional para o serviço e a ocorrência de conflitos de horário.
- Organizar requisitos e modelos do sistema e executar os testes automatizados existentes.
- Planejar a inspeção de usabilidade das telas e tarefas representativas com base nas dez heurísticas de Nielsen, sem antecipar seus resultados.

## 3. Fundamentação teórica

### 3.1 Aplicações web e separação de responsabilidades

Uma aplicação web pode organizar a interação em uma interface apresentada no navegador e uma camada de servidor responsável por processar operações e regras de negócio. Neste projeto, a interface foi construída com HTML, CSS e JavaScript, enquanto o back-end utiliza Java e Spring Boot. A documentação do Spring Boot descreve recursos para desenvolver e executar aplicações baseadas no ecossistema Spring; ela serve como referência técnica do framework, não como evidência de que todos os requisitos de produção tenham sido atendidos (Spring, s.d.).

A separação entre interface, serviços de aplicação e persistência favorece a organização do código e permite testar regras de negócio sem depender exclusivamente da interação visual. A arquitetura adotada neste projeto segue essa divisão em nível compatível com o escopo acadêmico. A avaliação de manutenibilidade e de implantação em ambiente real permanece sujeita a revisão técnica adicional.

### 3.2 Persistência de dados

O projeto define o MySQL como banco de dados de referência. A documentação oficial do MySQL descreve a instalação, configuração, linguagem SQL e recursos do sistema gerenciador de banco de dados (Oracle, 2026). Para facilitar a execução dos testes automatizados, o projeto também utiliza H2. Como os testes relatados foram executados com H2, seus resultados não devem ser interpretados como verificação de persistência, implantação, backup ou restauração em ambiente MySQL.

### 3.3 Usabilidade e heurísticas de Nielsen

As dez heurísticas de Nielsen são princípios gerais para orientar a análise de interfaces. Elas abrangem visibilidade do estado do sistema; correspondência entre sistema e mundo real; controle e liberdade; consistência e padrões; prevenção de erros; reconhecimento em vez de memorização; flexibilidade e eficiência; design estético e minimalista; reconhecimento, diagnóstico e recuperação de erros; e ajuda e documentação (Nielsen, 1994/2024). Por serem heurísticas gerais, servem para identificar possíveis problemas, mas não constituem, isoladamente, uma medida quantitativa de usabilidade nem substituem a validação com usuários.

No projeto, algumas alterações de interface foram orientadas por esses princípios, como foco de teclado visível, indicação semântica da seção atual, anúncios acessíveis de mensagens e bloqueio temporário de envios repetidos. Essas mudanças são decisões de implementação. A inspeção sistemática das telas e tarefas, com registro de evidências e severidade dos problemas, continua pendente.

## 4. Metodologia

### 4.1 Abordagem

O trabalho caracteriza-se como desenvolvimento aplicado de uma solução de software, organizado incrementalmente. Cada incremento parte dos requisitos documentados, resulta em alterações verificáveis e é seguido pela revisão de pendências. Esse procedimento permite distinguir funcionalidades implementadas de decisões ainda dependentes de validação. Não se considera que o ciclo de desenvolvimento esteja encerrado, pois ainda são necessárias validações com a clínica e verificações de implantação.

### 4.2 Levantamento de requisitos e modelagem

O escopo foi organizado em requisitos funcionais e não funcionais e em regras de negócio referentes à autenticação, aos cadastros, aos serviços e aos agendamentos. Os requisitos e decisões pendentes estão registrados em `docs/requisitos.md`. Diagramas de casos de uso, de classes e entidade-relacionamento foram utilizados para representar atores, funcionalidades, entidades e relações previstas.

Os requisitos servem como base para orientar a implementação e estruturar os testes. Entretanto, a documentação identifica decisões que ainda precisam de confirmação pela clínica e pela orientação do trabalho. Tais itens devem ser validados antes de serem tratados como regras operacionais definitivas.

### 4.3 Desenvolvimento e implementação

O back-end foi desenvolvido em Java com Spring Boot. A interface utiliza HTML, CSS e JavaScript. A configuração do projeto define o MySQL como banco de dados de referência; a execução automatizada descrita neste artigo utilizou H2. Essa distinção é mantida para não atribuir aos testes uma validação que não foi realizada no banco de referência.

A versão examinada contempla autenticação por sessão, cadastro e consulta de pacientes, manutenção de profissionais e serviços e operações de criação, consulta e atualização de agendamentos, incluindo alteração de situação. Nas regras de agenda, verifica-se a associação do profissional ao serviço selecionado e a existência de conflito de horário. A implementação também recebeu melhorias de interface relacionadas a foco visível, indicação da navegação atual, feedback acessível e prevenção de envios repetidos. A existência dessas funções no repositório não implica aceite da clínica, segurança de produção ou prontidão para implantação.

### 4.4 Testes e validação funcional

A suíte automatizada foi executada com o comando `mvn clean test`, após a correção do POM raiz para localizar as pastas reais de código, testes e recursos. O resultado foi de seis testes executados, sem falhas, erros ou testes ignorados. A suíte inclui testes de validação de CPF e testes de integração relacionados à autenticação obrigatória, ao cadastro e à busca de pacientes, à resposta a dados inválidos e à rejeição de agendamentos conflitantes para o mesmo profissional.

Esse resultado confirma a aprovação dos cenários automatizados existentes na data da execução. Ele não demonstra cobertura integral dos requisitos, não comprova ausência de defeitos e não substitui testes de aceitação. A validação com representante da clínica, a avaliação das regras operacionais pendentes e as verificações de persistência e implantação em MySQL ainda devem ser realizadas. Medidas comparativas de tempo, erros ou esforço administrativo só poderão ser apresentadas após coleta documentada de dados em condições comparáveis.

### 4.5 Avaliação de usabilidade

Está planejada uma inspeção das telas implementadas a partir de tarefas representativas, como localizar ou cadastrar um paciente e criar, consultar ou corrigir um agendamento. A análise deverá considerar as dez heurísticas de Nielsen e registrar, para cada achado, a tela ou o fluxo, a evidência observada, o problema, sua severidade e o ajuste recomendado. Após as correções, os fluxos afetados deverão ser inspecionados novamente.

O repositório contém alterações de interface inspiradas em princípios de usabilidade, mas não foi localizado relatório de inspeção heurística concluída nem registro de avaliação com usuários da clínica. Assim, esta seção descreve o procedimento planejado, não resultados de uma avaliação já realizada.

## 5. Resultados e discussão

Como resultado do incremento analisado, o repositório contém uma aplicação web com autenticação por sessão e funções administrativas para pacientes, profissionais, serviços e agendamentos. As regras implementadas verificam a habilitação do profissional para o serviço e impedem conflitos de horário nos cenários cobertos. A interface também recebeu ajustes de acessibilidade e feedback operacional descritos na seção de implementação.

A verificação automatizada registrou seis testes aprovados. O resultado é objetivo, mas restrito ao conjunto de cenários efetivamente testados, executados com H2. Não foram apresentados dados de uso pela equipe, medidas de desempenho em ambiente representativo, comparação antes e depois, nem validação da integração com MySQL. Por isso, não é possível concluir que a aplicação já reduziu o tempo de atendimento, eliminou conflitos ou diminuiu erros administrativos.

Os resultados representam um incremento funcional do projeto e uma base para as próximas etapas. A aceitação pela clínica, a inspeção de usabilidade, a resolução das decisões pendentes de requisitos e a validação em ambiente de implantação permanecem necessárias para avaliar a adequação da solução à rotina real.

## 6. Considerações finais

Este trabalho apresenta o desenvolvimento incremental de uma aplicação web voltada à gestão administrativa de uma clínica de estética. A versão analisada reúne autenticação, cadastro e consulta de pacientes, manutenção de profissionais e serviços e gerenciamento de agendamentos, com verificações de habilitação profissional e conflito de horário. A configuração de build foi ajustada para localizar corretamente as fontes, os testes e os recursos; a suíte automatizada concluiu com seis testes aprovados, sem falhas, erros ou testes ignorados.

O objetivo de desenvolver uma base funcional foi atendido no escopo técnico documentado, mas a conclusão do projeto depende de etapas ainda não realizadas. Entre elas estão o aceite com a clínica, a inspeção de usabilidade baseada nas heurísticas de Nielsen, a confirmação de requisitos pendentes e a verificação da operação com MySQL. Também será necessário coletar dados de uso antes de afirmar benefícios como redução de tempo, erros ou esforço administrativo.

Como continuidade, recomenda-se validar os fluxos prioritários com a responsável pela clínica, registrar os ajustes resultantes e ampliar os testes para cobrir critérios de aceitação ainda não verificados. A implantação deverá ser tratada separadamente da demonstração acadêmica, com configuração segura, rotinas de backup e procedimentos de recuperação definidos. Até que essas etapas sejam concluídas, a aplicação deve ser apresentada como uma solução em desenvolvimento, e não como sistema validado para uso clínico em produção.

## Referências

NIELSEN, Jakob. *10 usability heuristics for user interface design*. Nielsen Norman Group, 24 abr. 1994. Revisado em 30 jan. 2024. Disponível em: https://www.nngroup.com/articles/ten-usability-heuristics/. Acesso em: 23 nov. 2026.

ORACLE. *MySQL 8.4 Reference Manual*. 2026. Disponível em: https://dev.mysql.com/doc/refman/8.4/en/. Acesso em: 23 nov. 2026.

SPRING. *Spring Boot Reference Documentation*. s.d. Disponível em: https://docs.spring.io/spring-boot/reference/index.html. Acesso em: 23 nov. 2026.
