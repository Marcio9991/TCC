# Requisitos do sistema — Gestão de Clínica de Estética

**Projeto:** Aplicação Web para Gestão de Clínica de Estética  
**Versão:** 1.0  
**Status:** Especificação inicial para validação com a clínica e orientação

## 1. Propósito e contexto

Este documento especifica os requisitos funcionais e não funcionais da aplicação web destinada a apoiar a gestão administrativa de uma clínica de estética específica. O sistema busca centralizar o cadastro e a consulta de pacientes e profissionais, a organização de serviços e a gestão de agendamentos, reduzindo dependência de registros dispersos e ajudando a evitar conflitos de horário.

O escopo aqui registrado considera a decisão confirmada de que o projeto é para uma **clínica de estética**. O arquivo `ideia` existente no repositório é considerado obsoleto e não é fonte para definir o domínio ou os requisitos deste documento. A especificação toma como referência o contexto do TCC e os diagramas disponibilizados, sem presumir regras clínicas ou operacionais que ainda não tenham sido confirmadas.

## 2. Escopo, limites e suposições

A aplicação atenderá uma única clínica, sem suporte a múltiplas clínicas ou isolamento multi-tenant. Seu foco é administrativo: manter informações cadastrais básicas e organizar a agenda. **Não é prontuário médico, não substitui avaliação profissional, não recomenda tratamentos e não registra histórico clínico detalhado.** O campo de alergias previsto para o cadastro do paciente é um dado cadastral mínimo informado no escopo inicial; seu uso e necessidade devem ser validados com a clínica, e não implica funcionalidades clínicas.

A aplicação terá acesso autenticado para equipe autorizada. O sistema deverá coletar e manter somente os dados pessoais necessários às funcionalidades aprovadas. A clínica deverá definir quem pode acessar os dados, por quanto tempo serão mantidos e como atender solicitações relativas a dados pessoais, observando a legislação aplicável, inclusive a LGPD. Este documento não constitui parecer jurídico.

A stack confirmada para a especificação é **Java com Spring Boot no back-end, MySQL para persistência e HTML, CSS e JavaScript no front-end**. Embora o PDF contenha uma menção isolada a React em uma seção de desenvolvimento, ela diverge do resumo, do escopo escolhido e da decisão aqui confirmada; React não é considerado parte da stack especificada.

### 2.1 Atores

- **Usuário autenticado da clínica:** integrante autorizado da equipe que utiliza as funções disponíveis no sistema. O perfil e os níveis de permissão ainda não foram detalhados; não se pressupõe uma matriz de papéis.
- **Paciente:** pessoa cujos dados cadastrais são mantidos e a quem se associa um agendamento. Não se pressupõe que o paciente acesse o sistema.
- **Profissional:** integrante cadastrado da clínica, associado aos serviços que está habilitado a realizar e aos agendamentos correspondentes.

## 3. Requisitos funcionais

Os critérios abaixo descrevem resultados observáveis que podem ser verificados por teste. Mensagens e rótulos exatos da interface poderão ser definidos durante o desenvolvimento, desde que comuniquem o resultado de forma clara.

### RF01 — Autenticar usuário

O sistema deve permitir que um usuário autorizado inicie sessão com usuário e senha para acessar as funções administrativas.

**Critérios de aceitação:**
1. Dadas credenciais válidas de uma conta habilitada, ao enviar o formulário de autenticação, o usuário acessa a área protegida.
2. Dadas credenciais inválidas, o sistema recusa o acesso e apresenta uma mensagem que não revela se o usuário ou a senha estavam incorretos.
3. Sem sessão autenticada, uma tentativa de acessar uma função protegida não exibe dados administrativos nem permite a operação.
4. Ao encerrar a sessão, as páginas protegidas deixam de estar acessíveis até nova autenticação.

### RF02 — Cadastrar paciente

O sistema deve permitir cadastrar paciente com os campos previstos no escopo: nome, telefone, CPF, idade e alergias, além da data de criação do registro. Campos obrigatórios e regras de unicidade devem ser confirmados conforme a seção de pendências; até validação, nome, telefone e CPF são tratados como obrigatórios por corresponderem aos requisitos originais.

**Critérios de aceitação:**
1. Com os campos obrigatórios preenchidos em formato aceito, o sistema salva o paciente e apresenta confirmação.
2. O registro armazenado contém os dados informados e uma data de criação associada.
3. Se faltar campo obrigatório ou houver formato inválido, o registro não é salvo e o sistema identifica os campos que precisam de correção.
4. O sistema não inventa nem exige dados clínicos além dos campos definidos e aprovados.

### RF03 — Consultar e listar pacientes

O sistema deve permitir consultar pacientes por nome ou CPF e apresentar a listagem em ordem alfabética pelo nome.

**Critérios de aceitação:**
1. Uma busca por nome apresenta os registros correspondentes ao critério digitado.
2. Uma busca por CPF apresenta o paciente correspondente, quando cadastrado.
3. A listagem sem filtro é exibida em ordem alfabética crescente pelo nome.
4. Uma busca sem correspondências informa que nenhum resultado foi encontrado, sem gerar erro.

### RF04 — Atualizar paciente

O sistema deve permitir localizar e atualizar os dados de um paciente já cadastrado.

**Critérios de aceitação:**
1. Ao selecionar um paciente, o sistema apresenta os dados atuais disponíveis para edição.
2. Ao salvar alterações válidas, a consulta subsequente apresenta os valores atualizados.
3. Dados inválidos ou obrigatórios ausentes impedem a gravação e recebem indicação para correção.
4. A atualização não cria inadvertidamente um segundo paciente.

### RF05 — Cadastrar e atualizar profissional

O sistema deve permitir registrar e atualizar informações cadastrais de profissionais, bem como associar cada profissional aos serviços que está habilitado a realizar.

**Critérios de aceitação:**
1. Um usuário autenticado pode cadastrar um profissional com os dados cadastrais definidos para a versão implementada.
2. O usuário pode associar e desassociar serviços do profissional e consultar as associações salvas.
3. Um profissional selecionado para agendamento só pode ser associado a um serviço que conste como habilitado para ele.
4. Campos, obrigatoriedade, formato e possibilidade de inativar ou excluir profissional devem seguir decisões aprovadas antes da implementação.

### RF06 — Manter serviços e tipos de serviço

O sistema deve permitir manter o catálogo de serviços oferecidos pela clínica e, caso a entidade/taxonomia de tipos seja confirmada, os respectivos tipos de serviço.

**Critérios de aceitação:**
1. Um usuário autenticado pode cadastrar e consultar serviços com os atributos aprovados para o catálogo.
2. Quando tipos de serviço forem aprovados, um serviço pode ser associado ao tipo correspondente e essa associação permanece após nova consulta.
3. Serviços oferecidos para seleção em novos agendamentos devem ser distinguíveis dos indisponíveis, conforme a política de ativação/inativação a definir.
4. A existência e o significado da entidade “tipo de serviço” nos diagramas são pendentes de validação; o requisito não determina uma taxonomia.

### RF07 — Registrar agendamento

O sistema deve permitir registrar um agendamento com data e hora, paciente, profissional, serviço e observações, mantendo referências aos registros correspondentes. Cada agendamento deve estar associado a um único paciente, um único profissional e um único serviço, conforme o escopo do projeto.

**Critérios de aceitação:**
1. O formulário permite selecionar paciente, profissional, serviço, data e hora e informar observações, quando aplicável.
2. O sistema não salva agendamento sem as associações e os dados obrigatórios definidos para a operação.
3. O sistema valida que o profissional selecionado está associado ao serviço selecionado; caso contrário, impede a gravação e informa o motivo.
4. A data e a hora salvas são apresentadas corretamente na consulta posterior.
5. O sistema verifica disponibilidade e não permite conflito de horário conforme RF09.

### RF08 — Consultar e listar agendamentos

O sistema deve permitir consultar agendamentos por paciente e por data, além de apresentar listagens conforme esses critérios.

**Critérios de aceitação:**
1. A consulta filtrada por paciente retorna os agendamentos associados àquele paciente.
2. A consulta filtrada por data retorna os agendamentos correspondentes à data selecionada.
3. Cada resultado identifica, no mínimo, paciente, profissional, serviço e data/hora, conforme os dados cadastrados.
4. Uma consulta sem resultados informa que não há agendamentos correspondentes.

### RF09 — Verificar disponibilidade e impedir conflitos

Antes de salvar um agendamento, o sistema deve verificar a disponibilidade do profissional para o horário solicitado e impedir sobreposição com outro agendamento incompatível.

**Critérios de aceitação:**
1. Uma tentativa de agendar profissional em horário já ocupado, segundo a duração e a política de conflito aprovadas, não é gravada.
2. O sistema comunica que há conflito e permite corrigir a seleção de horário.
3. Um horário sem conflito, para profissional habilitado no serviço, pode ser salvo.
4. A duração do serviço é um dado configurável e obrigatório no cadastro para possibilitar a verificação de sobreposição; não há duração padrão no sistema. A definição de intervalo de preparação ainda precisa ser confirmada.

### RF10 — Validar dados e apresentar feedback

O sistema deve validar campos obrigatórios e os formatos de CPF e telefone previstos, apresentando feedback compreensível ao usuário.

**Critérios de aceitação:**
1. Campo obrigatório vazio é identificado antes da confirmação da operação correspondente.
2. CPF que não atenda à validação definida pela aplicação é recusado com indicação do campo; a regra adotada deve ser documentada e testável.
3. Telefone fora do formato aceito é recusado com indicação do campo.
4. Em caso de falha de validação, o sistema preserva os dados já preenchidos sempre que tecnicamente possível e não confirma uma gravação não realizada.

### RF11 — Atualizar agendamento

O sistema deve permitir localizar e atualizar os dados de um agendamento existente, preservando suas associações e aplicando novamente as regras de habilitação e disponibilidade.

**Critérios de aceitação:**
1. Ao selecionar um agendamento marcado, o sistema apresenta paciente, profissional, serviço, data/hora e observações atualmente salvos.
2. Alterações válidas são persistidas no mesmo registro e aparecem na consulta subsequente.
3. A atualização é recusada se o profissional não estiver habilitado para o serviço ou se o novo intervalo conflitar com outro agendamento ativo.
4. O usuário recebe feedback se a operação não puder ser salva.

### RF12 — Alterar situação do agendamento

O sistema deve permitir que o usuário autorizado altere a situação administrativa de um agendamento, conforme a política de status a ser validada pela clínica.

**Critérios de aceitação:**
1. O sistema apresenta a situação atual do agendamento.
2. Na implementação inicial, um agendamento marcado pode ser cancelado ou marcado como concluído; essas opções são provisórias até validação da política clínica.
3. Um agendamento cancelado não bloqueia a disponibilidade do profissional.
4. O sistema confirma a mudança e apresenta a nova situação ao consultar novamente o agendamento.

## 4. Requisitos não funcionais e restrições

### RNF01 — Escopo de implantação

A solução deve atender uma única clínica e não deve implementar suporte multi-tenant nesta versão.

**Verificação:** revisão da arquitetura e teste confirmam que não existe configuração, cadastro ou isolamento de dados para múltiplas clínicas.

### RNF02 — Usabilidade

A interface deve ser simples, consistente e adequada ao uso administrativo por equipe com treinamento mínimo.

**Verificação:** em teste de aceitação com representante da clínica, usuários conseguem concluir as tarefas prioritárias de cadastrar/localizar paciente e registrar/consultar agendamento com instruções introdutórias, sem depender de conhecimento técnico; problemas encontrados são registrados para correção.

### RNF03 — Disponibilidade e recuperação operacional

O sistema deve estar disponível durante o horário de atendimento da clínica, exceto em manutenções planejadas comunicadas. A solução deve comunicar falhas operacionais sem indicar falsamente que operações foram concluídas.

**Verificação:** em testes de indisponibilidade ou erro de persistência, a interface informa que a operação falhou e não apresenta confirmação de gravação; procedimentos de reinício e restauração devem ser documentados. Meta quantitativa de disponibilidade e tempo de recuperação permanece pendente.

### RNF04 — Desempenho

Em ambiente local ou servidor simples compatível com a implantação prevista, as operações comuns de autenticação, consulta, cadastro e atualização devem responder em tempo aceitável para uso administrativo.

**Verificação:** em ambiente de teste documentado e com volume de dados de teste representativo, medir as operações comuns; como meta provisória para validação, pelo menos 95% devem concluir em até 3 segundos, excluindo indisponibilidade de rede externa. A clínica/orientação deve confirmar ou ajustar essa meta antes da validação final.

### RNF05 — Manutenibilidade e organização

O código deve manter separação clara entre interface, lógica de aplicação/back-end e persistência de dados, com nomes e estrutura consistentes e documentação suficiente para manutenção acadêmica e evolução do projeto.

**Verificação:** revisão do repositório confirma a separação das responsabilidades e a existência de instruções para configurar, executar e testar a aplicação.

### RNF06 — Segurança e controle de acesso

O sistema deve exigir autenticação para dados e operações administrativas, armazenar senhas em forma protegida por hash apropriado e validar entradas no front-end e no back-end. Deve evitar exposição desnecessária de dados pessoais e aplicar medidas básicas contra acessos não autorizados e falhas comuns de entrada.

**Verificação:** inspeção da persistência confirma que senhas não são armazenadas em texto puro; testes confirmam bloqueio de rotas sem autenticação e validação no servidor mesmo quando a validação do navegador é contornada. HTTPS, MFA e controles avançados não são requisitos desta versão, salvo decisão posterior.

### RNF07 — Privacidade e minimização de dados

O sistema deve restringir a coleta aos dados necessários e aprovados, apresentar dados pessoais apenas a usuários autenticados e evitar seu uso em mensagens, logs ou telas sem necessidade funcional. A clínica deve definir responsabilidades e procedimentos de tratamento, retenção e atendimento de direitos aplicáveis segundo a LGPD.

**Verificação:** revisão dos formulários, telas e logs confirma que não foram acrescentados campos pessoais além do escopo aprovado e que dados administrativos não são apresentados em páginas públicas. Este requisito não presume que a aplicação, isoladamente, garanta conformidade legal integral.

### RNF08 — Persistência e integridade

Os dados devem ser persistidos em banco de dados relacional MySQL. Relações entre pacientes, profissionais, serviços e agendamentos devem manter integridade referencial. Devem existir orientações de backup e restauração adequadas ao ambiente de implantação.

**Verificação:** configuração e testes confirmam uso do MySQL e preservação das associações após reinício; executar e registrar um teste de restauração a partir de backup antes da entrega final, se o ambiente permitir.

### RNF09 — Portabilidade e implantação

A aplicação deve poder ser instalada em servidor local ou VPS simples compatível com Java/Spring Boot e MySQL, com passos de configuração reproduzíveis. A interface deve ser acessível por navegador web moderno.

**Verificação:** seguir as instruções de implantação em ambiente limpo compatível e confirmar inicialização do back-end, conexão ao banco e acesso às telas principais. Não se exige infraestrutura em nuvem específica.

### RNF10 — Compatibilidade tecnológica

A implementação deve respeitar a stack definida para este projeto: Java e Spring Boot no back-end, MySQL para persistência e HTML, CSS e JavaScript no front-end.

**Verificação:** inspeção do repositório e execução da aplicação confirmam as tecnologias adotadas. A menção a React em uma seção do PDF é uma inconsistência documental e não altera esta decisão de stack.

## 5. Restrições e exclusões explícitas

- O produto é administrativo e destinado a uma clínica; não é prontuário, sistema de diagnóstico ou sistema de recomendação de procedimentos.
- Não se especificam protocolos clínicos, contraindicações, recomendações de tratamento ou regras de saúde.
- Não estão incluídos pagamentos, faturamento, estoque, comissões, relatórios gerenciais avançados, comunicação automática com pacientes ou portal do paciente, salvo aprovação de mudança de escopo.
- Não estão definidos múltiplos perfis e permissões além da necessidade de usuário autenticado.
- A aplicação deve tratar com cautela dados pessoais, em especial CPF, telefone e informação de alergias; coleta e acesso devem se limitar ao necessário e autorizado.
- As referências aos diagramas não resolvem por si só as dúvidas de modelo descritas a seguir; mudanças de modelo devem ser aprovadas e refletidas nos diagramas e nesta especificação.

## 6. Decisões pendentes de validação

Antes de fechar o modelo de dados e os testes de aceitação, validar com a clínica e a orientação:

1. **Serviços:** atributos necessários (por exemplo, nome e descrição), possibilidade de ativar/inativar, e se a entidade “tipo de serviço” existe como classificação distinta. O diagrama sugere tipo de serviço, mas seu significado e necessidade não estão claros.
2. **Duração e disponibilidade:** duração de cada serviço, horários de funcionamento, intervalos, sobreposição, margem entre atendimentos, fusos e tratamento de agendamentos que cruzem horários. Não assumir valores padrão sem aprovação.
3. **Profissionais:** campos cadastrais obrigatórios, disponibilidade individual, situação ativa/inativa e regras para alterações quando houver agendamentos relacionados.
4. **Endereço:** a implementação inicial segue o diagrama com entidade `Address` (campo `localização`) e vínculos opcionais com paciente e profissional. Confirmar com o orientador se essas relações e o dado são necessários e quais componentes de endereço devem ser coletados; não foram acrescentados campos além da localização simples.
5. **Paciente:** confirmar obrigatoriedade e finalidade de idade e alergias, forma de registrar idade (valor informado ou derivado de data de nascimento), formato de telefone e se CPF deve ser único. Evitar coletar data de nascimento ou outros dados sem necessidade aprovada.
6. **Agendamentos:** estados necessários (por exemplo, marcado, cancelado ou concluído), política e responsável por cancelamento, reagendamento, exclusão e histórico; confirmar se observações são opcionais e quais conteúdos devem ser desestimulados por privacidade.
7. **Autenticação:** processo de criação e recuperação de contas, encerramento de sessão e eventual necessidade de papéis/permissões. O fluxo de login está no escopo, mas a administração de contas não está detalhada.
8. **Disponibilidade e operação:** horário efetivo de uso, meta de disponibilidade, cópias de segurança, frequência, retenção e procedimento de recuperação.
9. **Validação e aceite:** regras exatas de validação de CPF/telefone, metas de desempenho e pessoas responsáveis pela aprovação final dos requisitos.
10. **Modelo dos diagramas:** validar com o orientador as entidades e cardinalidades de tipo de serviço e endereço e resolver eventuais divergências sem introduzir regras não aprovadas.

Até que essas decisões sejam aprovadas, a implementação deve limitar-se às regras explícitas deste documento e registrar qualquer pressuposto temporário para revisão.