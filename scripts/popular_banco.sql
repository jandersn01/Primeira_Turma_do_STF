-- =====================================================
-- Script de População do Banco de Dados
-- Sistema: Primeira Turma do STF
-- =====================================================

-- Resetar sequences para garantir IDs sequenciais
SELECT setval('curso_id_seq', 1, false);
SELECT setval('assunto_id_seq', 1, false);
SELECT setval('aluno_id_seq', 1, false);
SELECT setval('professor_id_seq', 1, false);
SELECT setval('colegiado_id_seq', 1, false);
SELECT setval('processo_id_seq', 1, false);
SELECT setval('reuniao_id_seq', 1, false);
SELECT setval('voto_id_seq', 1, false);
SELECT setval('documento_id_seq', 1, false);
SELECT setval('usuario_id_seq', 1, false);

-- Limpar dados existentes
TRUNCATE TABLE voto CASCADE;
TRUNCATE TABLE documento CASCADE;
TRUNCATE TABLE reuniao_processo CASCADE;
TRUNCATE TABLE reuniao CASCADE;
TRUNCATE TABLE processo CASCADE;
TRUNCATE TABLE colegiado_professor CASCADE;
TRUNCATE TABLE colegiado CASCADE;
TRUNCATE TABLE usuario CASCADE;
TRUNCATE TABLE aluno CASCADE;
TRUNCATE TABLE professor CASCADE;
TRUNCATE TABLE assunto CASCADE;
TRUNCATE TABLE curso CASCADE;

-- =====================================================
-- 1. CURSOS
-- =====================================================
INSERT INTO curso (nome, codigo) VALUES
('Tecnologia em Análise e Desenvolvimento de Sistemas', '001'),
('Tecnologia em Redes de Computadores', '002'),
('Tecnologia em Gestão de Tecnologia da Informação', '003'),
('Bacharelado em Ciência da Computação', '004'),
('Bacharelado em Engenharia de Software', '005');

-- =====================================================
-- 2. ASSUNTOS
-- =====================================================
INSERT INTO assunto (nome, descricao) VALUES
('Revisão de Nota', 'Solicitação de revisão de nota de avaliação, trabalho ou atividade avaliativa.'),
('Trancamento de Disciplina', 'Solicitação para trancar disciplina no período letivo.'),
('Dispensa de Disciplina', 'Solicitação de dispensa de disciplina com base em estudos anteriores ou equivalência.'),
('Aproveitamento de Estudos', 'Solicitação de aproveitamento de estudos realizados em outras instituições.'),
('Alteração de Turma', 'Solicitação para alterar a turma de uma disciplina.'),
('Justificativa de Falta', 'Solicitação de justificativa de faltas em aulas ou avaliações.'),
('Reabertura de Matrícula', 'Solicitação para reabrir matrícula em disciplina já encerrada.'),
('Transferência de Curso', 'Solicitação de transferência entre cursos do IFPB.');

-- =====================================================
-- 3. ALUNOS
-- =====================================================
INSERT INTO aluno (matricula, nome, fone, senha, id_curso) VALUES
('AL2024001', 'Ana Silva Santos', '(83) 98765-4321', '123456', 1),
('AL2024002', 'Bruno Oliveira Costa', '(83) 98876-5432', '123456', 1),
('AL2024003', 'Carlos Eduardo Lima', '(83) 98987-6543', '123456', 1),
('AL2024004', 'Daniela Ferreira Alves', '(83) 99098-7654', '123456', 1),
('AL2024005', 'Eduardo Martins Pereira', '(83) 99109-8765', '123456', 1),
('AL2024006', 'Fernanda Souza Rocha', '(83) 99210-9876', '123456', 2),
('AL2024007', 'Gabriel Henrique Silva', '(83) 99321-0987', '123456', 2),
('AL2024008', 'Helena Maria Oliveira', '(83) 99432-1098', '123456', 2),
('AL2024009', 'Igor Santos Carvalho', '(83) 99543-2109', '123456', 2),
('AL2024010', 'Juliana Costa Mendes', '(83) 98765-4321', '123456', 2),
('AL2024011', 'Lucas Rodrigues Araújo', '(83) 98876-5432', '123456', 3),
('AL2024012', 'Mariana Alves Barbosa', '(83) 98987-6543', '123456', 3),
('AL2024013', 'Nicolas Pereira Gomes', '(83) 99098-7654', '123456', 3),
('AL2024014', 'Patricia Santos Lima', '(83) 99109-8765', '123456', 3),
('AL2024015', 'Rafael Henrique Silva', '(83) 99210-9876', '123456', 3),
('AL2024016', 'Sabrina Oliveira Costa', '(83) 99321-0987', '123456', 4),
('AL2024017', 'Thiago Martins Souza', '(83) 99432-1098', '123456', 4),
('AL2024018', 'Vanessa Ferreira Lima', '(83) 99543-2109', '123456', 4),
('AL2024019', 'Wagner Silva Santos', '(83) 98765-4321', '123456', 4),
('AL2024020', 'Yasmin Costa Oliveira', '(83) 98876-5432', '123456', 4),
('AL2024021', 'Zeca Santos Silva', '(83) 98987-6543', '123456', 5),
('AL2024022', 'Amanda Rocha Lima', '(83) 99098-7654', '123456', 5),
('AL2024023', 'Beatriz Alves Costa', '(83) 99109-8765', '123456', 5),
('AL2024024', 'Caio Henrique Silva', '(83) 99210-9876', '123456', 5),
('AL2024025', 'Débora Oliveira Santos', '(83) 99321-0987', '123456', 5),
('AL2024026', 'Felipe Martins Costa', '(83) 99432-1098', '123456', 1),
('AL2024027', 'Giovanna Silva Lima', '(83) 99543-2109', '123456', 2);

-- =====================================================
-- 4. PROFESSORES
-- =====================================================
INSERT INTO professor (nome, matricula, fone, senha, coordenador, id_curso) VALUES
('Joao Carlos Silva', 'PROF1001', '(83) 99911-2233', '123456', true, 1),
('Maria Eduarda Santos', 'PROF1002', '(83) 99922-3344', '123456', true, 2),
('Pedro Henrique Oliveira', 'PROF1003', '(83) 99933-4455', '123456', true, 3),
('Ana Paula Costa', 'PROF1004', '(83) 99944-5566', '123456', false, 1),
('Roberto Alves Lima', 'PROF1005', '(83) 99955-6677', '123456', false, 1),
('Claudia Ferreira', 'PROF1006', '(83) 99966-7788', '123456', false, 2),
('Fernando Souza Rocha', 'PROF1007', '(83) 99911-2233', '123456', false, 2),
('Luciana Martins', 'PROF1008', '(83) 99922-3344', '123456', false, 3),
('Ricardo Pereira Gomes', 'PROF1009', '(83) 99933-4455', '123456', false, 4),
('Sandra Alves', 'PROF1010', '(83) 99944-5566', '123456', false, 4),
('Marcos Antonio Silva', 'PROF1011', '(83) 99955-6677', '123456', false, 5),
('Patricia Santos', 'PROF1012', '(83) 99966-7788', '123456', false, 5);

-- =====================================================
-- 5. COLEGIADOS
-- =====================================================
INSERT INTO colegiado (data_inicio, data_fim, portaria, id_curso, aluno_id) VALUES
('2024-01-15', NULL, 'Portaria 100/2024', 1, 1),
('2024-02-01', NULL, 'Portaria 101/2024', 2, 6),
('2024-01-20', '2024-12-31', 'Portaria 102/2024', 3, 11),
('2024-02-15', '2024-11-30', 'Portaria 103/2024', 4, 16),
('2024-01-10', '2024-10-31', 'Portaria 104/2024', 5, 21);

-- =====================================================
-- 6. COLEGIADO_PROFESSOR (Relacionamento Many-to-Many)
-- =====================================================
INSERT INTO colegiado_professor (colegiado_id, professor_id) VALUES
-- Colegiado 1 (Curso 1)
(1, 1), (1, 4), (1, 5),
-- Colegiado 2 (Curso 2)
(2, 2), (2, 6), (2, 7),
-- Colegiado 3 (Curso 3)
(3, 3), (3, 8),
-- Colegiado 4 (Curso 4)
(4, 9), (4, 10),
-- Colegiado 5 (Curso 5)
(5, 11), (5, 12);

-- =====================================================
-- 7. PROCESSOS
-- =====================================================
INSERT INTO processo (numero, data_recepcao, data_distribuicao, data_parecer, parecer, texto_requerimento, status, decisao_relator, assunto_id, interessado_id, relator_id, colegiado_id) VALUES
('2024001/2024', '2024-01-10', '2024-01-12', '2024-01-17', 'Analisando o pedido, verifico que há fundamento na solicitação. O aluno apresentou documentação adequada e o pedido está de acordo com a legislação vigente.', 'Venho por meio deste solicitar a revisão da nota da avaliação da disciplina de Programação Web 2, pois acredito que houve um equívoco na correção.', 'JULGADO', 'DEFERIMENTO', 1, 1, 1, 1),
('2024002/2024', '2024-01-15', '2024-01-17', '2024-01-22', 'Após análise detalhada, verifico que o requerimento não atende aos critérios estabelecidos no regimento interno. O pedido não pode ser deferido.', 'Solicito o trancamento da disciplina de Banco de Dados devido a questões pessoais que me impedem de acompanhar o curso neste período.', 'JULGADO', 'INDEFERIMENTO', 2, 2, 4, 1),
('2024003/2024', '2024-01-20', '2024-01-22', NULL, NULL, 'Peço a dispensa da disciplina de Estrutura de Dados, pois já cursei disciplina equivalente em outra instituição de ensino.', 'EM_JULGAMENTO', 'DEFERIMENTO', 3, 3, 5, 1),
('2024004/2024', '2024-01-25', '2024-01-27', NULL, NULL, 'Solicito o aproveitamento de estudos da disciplina de Engenharia de Software realizada na Universidade Federal da Paraíba.', 'EM_JULGAMENTO', NULL, 4, 4, 1, 1),
('2024005/2024', '2024-02-01', NULL, NULL, NULL, 'Venho solicitar a alteração de turma da disciplina de Redes de Computadores devido a conflito de horário com outra disciplina obrigatória.', 'DISTRIBUIDO', NULL, 5, 5, 4, 1),
('2024006/2024', '2024-02-05', NULL, NULL, NULL, 'Solicito a justificativa de faltas ocorridas nos dias 15, 16 e 17 do mês corrente devido a problemas de saúde comprovados por atestado médico.', 'DISTRIBUIDO', NULL, 6, 6, 2, 2),
('2024007/2024', '2024-02-10', '2024-02-12', '2024-02-17', 'Considerando os documentos apresentados e a legislação aplicável, entendo que o pedido deve ser deferido parcialmente, conforme especificado no parecer.', 'Peço a reabertura da matrícula na disciplina de Inteligência Artificial que foi encerrada por engano.', 'JULGADO', 'DEFERIMENTO', 7, 7, 6, 2),
('2024008/2024', '2024-02-15', NULL, NULL, NULL, 'Solicito a transferência do curso de Tecnologia em Análise e Desenvolvimento de Sistemas para o Bacharelado em Ciência da Computação.', 'DISTRIBUIDO', NULL, 8, 8, 7, 2),
('2024009/2024', '2024-02-20', '2024-02-22', NULL, NULL, 'Venho por meio deste solicitar a revisão da nota da avaliação da disciplina de Programação Web 2, pois acredito que houve um equívoco na correção.', 'EM_PAUTA', NULL, 1, 9, 2, 2),
('2024010/2024', '2024-02-25', NULL, NULL, NULL, 'Solicito o trancamento da disciplina de Banco de Dados devido a questões pessoais que me impedem de acompanhar o curso neste período.', 'CRIADO', NULL, 2, 10, NULL, 2),
('2024011/2024', '2024-03-01', '2024-03-03', '2024-03-08', 'O requerimento apresenta vícios formais que impedem sua análise. É necessário que o interessado apresente a documentação complementar solicitada.', 'Peço a dispensa da disciplina de Estrutura de Dados, pois já cursei disciplina equivalente em outra instituição de ensino.', 'JULGADO', 'INDEFERIMENTO', 3, 11, 3, 3),
('2024012/2024', '2024-03-05', '2024-03-07', NULL, NULL, 'Solicito o aproveitamento de estudos da disciplina de Engenharia de Software realizada na Universidade Federal da Paraíba.', 'EM_JULGAMENTO', NULL, 4, 12, 8, 3),
('2024013/2024', '2024-03-10', NULL, NULL, NULL, 'Venho solicitar a alteração de turma da disciplina de Redes de Computadores devido a conflito de horário com outra disciplina obrigatória.', 'DISTRIBUIDO', NULL, 5, 13, 3, 3),
('2024014/2024', '2024-03-15', '2024-03-17', NULL, NULL, 'Solicito a justificativa de faltas ocorridas nos dias 15, 16 e 17 do mês corrente devido a problemas de saúde comprovados por atestado médico.', 'EM_PAUTA', NULL, 6, 14, 8, 3),
('2024015/2024', '2024-03-20', NULL, NULL, NULL, 'Peço a reabertura da matrícula na disciplina de Inteligência Artificial que foi encerrada por engano.', 'CRIADO', NULL, 7, 15, NULL, 3),
('2024016/2024', '2024-03-25', '2024-03-27', '2024-04-01', 'Analisando o pedido, verifico que há fundamento na solicitação. O aluno apresentou documentação adequada e o pedido está de acordo com a legislação vigente.', 'Solicito a transferência do curso de Tecnologia em Análise e Desenvolvimento de Sistemas para o Bacharelado em Ciência da Computação.', 'JULGADO', 'DEFERIMENTO', 8, 16, 9, 4),
('2024017/2024', '2024-04-01', '2024-04-03', NULL, NULL, 'Venho por meio deste solicitar a revisão da nota da avaliação da disciplina de Programação Web 2, pois acredito que houve um equívoco na correção.', 'EM_JULGAMENTO', NULL, 1, 17, 10, 4),
('2024018/2024', '2024-04-05', NULL, NULL, NULL, 'Solicito o trancamento da disciplina de Banco de Dados devido a questões pessoais que me impedem de acompanhar o curso neste período.', 'DISTRIBUIDO', NULL, 2, 18, 9, 4),
('2024019/2024', '2024-04-10', '2024-04-12', NULL, NULL, 'Peço a dispensa da disciplina de Estrutura de Dados, pois já cursei disciplina equivalente em outra instituição de ensino.', 'EM_PAUTA', NULL, 3, 19, 10, 4),
('2024020/2024', '2024-04-15', NULL, NULL, NULL, 'Solicito o aproveitamento de estudos da disciplina de Engenharia de Software realizada na Universidade Federal da Paraíba.', 'CRIADO', NULL, 4, 20, NULL, 4),
('2024021/2024', '2024-04-20', '2024-04-22', '2024-04-27', 'Após análise detalhada, verifico que o requerimento não atende aos critérios estabelecidos no regimento interno. O pedido não pode ser deferido.', 'Venho solicitar a alteração de turma da disciplina de Redes de Computadores devido a conflito de horário com outra disciplina obrigatória.', 'JULGADO', 'INDEFERIMENTO', 5, 21, 11, 5),
('2024022/2024', '2024-04-25', '2024-04-27', NULL, NULL, 'Solicito a justificativa de faltas ocorridas nos dias 15, 16 e 17 do mês corrente devido a problemas de saúde comprovados por atestado médico.', 'EM_JULGAMENTO', NULL, 6, 22, 12, 5),
('2024023/2024', '2024-05-01', NULL, NULL, NULL, 'Peço a reabertura da matrícula na disciplina de Inteligência Artificial que foi encerrada por engano.', 'DISTRIBUIDO', NULL, 7, 23, 11, 5),
('2024024/2024', '2024-05-05', '2024-05-07', NULL, NULL, 'Solicito a transferência do curso de Tecnologia em Análise e Desenvolvimento de Sistemas para o Bacharelado em Ciência da Computação.', 'EM_PAUTA', NULL, 8, 24, 12, 5),
('2024025/2024', '2024-05-10', NULL, NULL, NULL, 'Venho por meio deste solicitar a revisão da nota da avaliação da disciplina de Programação Web 2, pois acredito que houve um equívoco na correção.', 'CRIADO', NULL, 1, 25, NULL, 1),
('2024026/2024', '2024-05-15', '2024-05-17', '2024-05-22', 'Considerando os documentos apresentados e a legislação aplicável, entendo que o pedido deve ser deferido parcialmente, conforme especificado no parecer.', 'Solicito o trancamento da disciplina de Banco de Dados devido a questões pessoais que me impedem de acompanhar o curso neste período.', 'JULGADO', 'DEFERIMENTO', 2, 26, 1, 1),
('2024027/2024', '2024-05-20', '2024-05-22', NULL, NULL, 'Peço a dispensa da disciplina de Estrutura de Dados, pois já cursei disciplina equivalente em outra instituição de ensino.', 'EM_JULGAMENTO', NULL, 3, 27, 4, 2),
('2024028/2024', '2024-05-25', NULL, NULL, NULL, 'Solicito o aproveitamento de estudos da disciplina de Engenharia de Software realizada na Universidade Federal da Paraíba.', 'DISTRIBUIDO', NULL, 4, 1, 5, 1),
('2024029/2024', '2024-06-01', '2024-06-03', NULL, NULL, 'Venho solicitar a alteração de turma da disciplina de Redes de Computadores devido a conflito de horário com outra disciplina obrigatória.', 'EM_PAUTA', NULL, 5, 2, 1, 1),
('2024030/2024', '2024-06-05', NULL, NULL, NULL, 'Solicito a justificativa de faltas ocorridas nos dias 15, 16 e 17 do mês corrente devido a problemas de saúde comprovados por atestado médico.', 'CRIADO', NULL, 6, 3, NULL, 1),
('2024031/2024', '2024-06-10', '2024-06-12', '2024-06-17', 'O requerimento apresenta vícios formais que impedem sua análise. É necessário que o interessado apresente a documentação complementar solicitada.', 'Peço a reabertura da matrícula na disciplina de Inteligência Artificial que foi encerrada por engano.', 'JULGADO', 'INDEFERIMENTO', 7, 4, 4, 1),
('2024032/2024', '2024-06-15', '2024-06-17', NULL, NULL, 'Solicito a transferência do curso de Tecnologia em Análise e Desenvolvimento de Sistemas para o Bacharelado em Ciência da Computação.', 'EM_JULGAMENTO', NULL, 8, 5, 5, 1),
('2024033/2024', '2024-06-20', NULL, NULL, NULL, 'Venho por meio deste solicitar a revisão da nota da avaliação da disciplina de Programação Web 2, pois acredito que houve um equívoco na correção.', 'DISTRIBUIDO', NULL, 1, 6, 2, 2),
('2024034/2024', '2024-06-25', '2024-06-27', NULL, NULL, 'Solicito o trancamento da disciplina de Banco de Dados devido a questões pessoais que me impedem de acompanhar o curso neste período.', 'EM_PAUTA', NULL, 2, 7, 6, 2),
('2024035/2024', '2024-07-01', NULL, NULL, NULL, 'Peço a dispensa da disciplina de Estrutura de Dados, pois já cursei disciplina equivalente em outra instituição de ensino.', 'CRIADO', NULL, 3, 8, NULL, 2),
('2024036/2024', '2024-07-05', '2024-07-07', '2024-07-12', 'Analisando o pedido, verifico que há fundamento na solicitação. O aluno apresentou documentação adequada e o pedido está de acordo com a legislação vigente.', 'Solicito o aproveitamento de estudos da disciplina de Engenharia de Software realizada na Universidade Federal da Paraíba.', 'JULGADO', 'DEFERIMENTO', 4, 9, 7, 2),
('2024037/2024', '2024-07-10', '2024-07-12', NULL, NULL, 'Venho solicitar a alteração de turma da disciplina de Redes de Computadores devido a conflito de horário com outra disciplina obrigatória.', 'EM_JULGAMENTO', NULL, 5, 10, 2, 2),
('2024038/2024', '2024-07-15', NULL, NULL, NULL, 'Solicito a justificativa de faltas ocorridas nos dias 15, 16 e 17 do mês corrente devido a problemas de saúde comprovados por atestado médico.', 'DISTRIBUIDO', NULL, 6, 11, 3, 3),
('2024039/2024', '2024-07-20', '2024-07-22', NULL, NULL, 'Peço a reabertura da matrícula na disciplina de Inteligência Artificial que foi encerrada por engano.', 'EM_PAUTA', NULL, 7, 12, 8, 3),
('2024040/2024', '2024-07-25', NULL, NULL, NULL, 'Solicito a transferência do curso de Tecnologia em Análise e Desenvolvimento de Sistemas para o Bacharelado em Ciência da Computação.', 'CRIADO', NULL, 8, 13, NULL, 3);

-- =====================================================
-- 8. REUNIÕES
-- =====================================================
INSERT INTO reuniao (data_reuniao, status, ata, colegiado_id) VALUES
('2024-01-20', 'ENCERRADA', 'Reunião realizada para análise de processos em pauta. Presentes todos os membros do colegiado.', 1),
('2024-02-15', 'ENCERRADA', 'Reunião ordinária do colegiado para julgamento de processos. Discussão sobre os casos apresentados.', 1),
('2024-03-10', 'PROGRAMADA', NULL, 1),
('2024-02-20', 'ENCERRADA', 'Reunião extraordinária convocada para análise de processos urgentes. Decisões tomadas por maioria.', 2),
('2024-03-15', 'PROGRAMADA', NULL, 2),
('2024-04-05', 'ENCERRADA', 'Reunião de julgamento de processos. Ata registrada com as decisões e votos dos membros presentes.', 2),
('2024-03-20', 'ENCERRADA', 'Reunião realizada para análise de processos em pauta. Presentes todos os membros do colegiado.', 3),
('2024-04-10', 'PROGRAMADA', NULL, 3),
('2024-04-20', 'ENCERRADA', 'Reunião ordinária do colegiado para julgamento de processos. Discussão sobre os casos apresentados.', 4),
('2024-05-10', 'PROGRAMADA', NULL, 4),
('2024-05-20', 'ENCERRADA', 'Reunião extraordinária convocada para análise de processos urgentes. Decisões tomadas por maioria.', 5),
('2024-06-10', 'PROGRAMADA', NULL, 5);

-- =====================================================
-- 9. REUNIAO_PROCESSO (Relacionamento Many-to-Many)
-- =====================================================
INSERT INTO reuniao_processo (reuniao_id, processo_id) VALUES
-- Reunião 1 (Colegiado 1)
(1, 1), (1, 2), (1, 3),
-- Reunião 2 (Colegiado 1)
(2, 4), (2, 5),
-- Reunião 4 (Colegiado 2)
(4, 6), (4, 7), (4, 8),
-- Reunião 6 (Colegiado 2)
(6, 9), (6, 10),
-- Reunião 7 (Colegiado 3)
(7, 11), (7, 12), (7, 13),
-- Reunião 9 (Colegiado 4)
(9, 16), (9, 17), (9, 18),
-- Reunião 11 (Colegiado 5)
(11, 21), (11, 22), (11, 23);

-- =====================================================
-- 10. VOTOS
-- =====================================================
INSERT INTO voto (tipo, ausente, justificativa, data_voto, professor_id, processo_id, reuniao_id) VALUES
-- Votos da Reunião 1 (Processo 1)
('COM_RELATOR', false, 'Voto fundamentado conforme análise do processo 2024001/2024.', '2024-01-20 14:30:00', 1, 1, 1),
('COM_RELATOR', false, NULL, '2024-01-20 14:32:00', 4, 1, 1),
('DIVERGENTE', false, NULL, '2024-01-20 14:35:00', 5, 1, 1),
-- Votos da Reunião 1 (Processo 2)
('COM_RELATOR', false, NULL, '2024-01-20 15:00:00', 1, 2, 1),
('DIVERGENTE', false, 'Voto fundamentado conforme análise do processo 2024002/2024.', '2024-01-20 15:02:00', 4, 2, 1),
('COM_RELATOR', false, NULL, '2024-01-20 15:05:00', 5, 2, 1),
-- Votos da Reunião 1 (Processo 3)
('COM_RELATOR', false, NULL, '2024-01-20 15:30:00', 1, 3, 1),
('COM_RELATOR', false, NULL, '2024-01-20 15:32:00', 4, 3, 1),
('COM_RELATOR', false, NULL, '2024-01-20 15:35:00', 5, 3, 1),
-- Votos da Reunião 2 (Processo 4)
('COM_RELATOR', false, NULL, '2024-02-15 14:00:00', 1, 4, 2),
('DIVERGENTE', false, NULL, '2024-02-15 14:02:00', 4, 4, 2),
('COM_RELATOR', false, NULL, '2024-02-15 14:05:00', 5, 4, 2),
-- Votos da Reunião 2 (Processo 5)
('COM_RELATOR', false, NULL, '2024-02-15 14:30:00', 1, 5, 2),
('COM_RELATOR', false, NULL, '2024-02-15 14:32:00', 4, 5, 2),
('COM_RELATOR', false, NULL, '2024-02-15 14:35:00', 5, 5, 2),
-- Votos da Reunião 4 (Processo 6)
('COM_RELATOR', false, NULL, '2024-02-20 14:00:00', 2, 6, 4),
('DIVERGENTE', false, NULL, '2024-02-20 14:02:00', 6, 6, 4),
('COM_RELATOR', false, NULL, '2024-02-20 14:05:00', 7, 6, 4),
-- Votos da Reunião 4 (Processo 7)
('COM_RELATOR', false, 'Voto fundamentado conforme análise do processo 2024007/2024.', '2024-02-20 14:30:00', 2, 7, 4),
('COM_RELATOR', false, NULL, '2024-02-20 14:32:00', 6, 7, 4),
('COM_RELATOR', false, NULL, '2024-02-20 14:35:00', 7, 7, 4),
-- Votos da Reunião 4 (Processo 8)
('COM_RELATOR', false, NULL, '2024-02-20 15:00:00', 2, 8, 4),
('DIVERGENTE', false, NULL, '2024-02-20 15:02:00', 6, 8, 4),
('COM_RELATOR', false, NULL, '2024-02-20 15:05:00', 7, 8, 4),
-- Votos da Reunião 6 (Processo 9)
('COM_RELATOR', false, NULL, '2024-04-05 14:00:00', 2, 9, 6),
('COM_RELATOR', false, NULL, '2024-04-05 14:02:00', 6, 9, 6),
('COM_RELATOR', false, NULL, '2024-04-05 14:05:00', 7, 9, 6),
-- Votos da Reunião 6 (Processo 10)
('COM_RELATOR', false, NULL, '2024-04-05 14:30:00', 2, 10, 6),
('DIVERGENTE', false, NULL, '2024-04-05 14:32:00', 6, 10, 6),
('COM_RELATOR', false, NULL, '2024-04-05 14:35:00', 7, 10, 6),
-- Votos da Reunião 7 (Processo 11)
('COM_RELATOR', false, NULL, '2024-03-20 14:00:00', 3, 11, 7),
('COM_RELATOR', false, NULL, '2024-03-20 14:02:00', 8, 11, 7),
-- Votos da Reunião 7 (Processo 12)
('DIVERGENTE', false, NULL, '2024-03-20 14:30:00', 3, 12, 7),
('COM_RELATOR', false, NULL, '2024-03-20 14:32:00', 8, 12, 7),
-- Votos da Reunião 7 (Processo 13)
('COM_RELATOR', false, NULL, '2024-03-20 15:00:00', 3, 13, 7),
('COM_RELATOR', false, NULL, '2024-03-20 15:02:00', 8, 13, 7),
-- Votos da Reunião 9 (Processo 16)
('COM_RELATOR', false, NULL, '2024-04-20 14:00:00', 9, 16, 9),
('COM_RELATOR', false, NULL, '2024-04-20 14:02:00', 10, 16, 9),
-- Votos da Reunião 9 (Processo 17)
('DIVERGENTE', false, NULL, '2024-04-20 14:30:00', 9, 17, 9),
('COM_RELATOR', false, NULL, '2024-04-20 14:32:00', 10, 17, 9),
-- Votos da Reunião 9 (Processo 18)
('COM_RELATOR', false, NULL, '2024-04-20 15:00:00', 9, 18, 9),
('COM_RELATOR', false, NULL, '2024-04-20 15:02:00', 10, 18, 9),
-- Votos da Reunião 11 (Processo 21)
('COM_RELATOR', false, NULL, '2024-05-20 14:00:00', 11, 21, 11),
('COM_RELATOR', false, NULL, '2024-05-20 14:02:00', 12, 21, 11),
-- Votos da Reunião 11 (Processo 22)
('DIVERGENTE', false, NULL, '2024-05-20 14:30:00', 11, 22, 11),
('COM_RELATOR', false, NULL, '2024-05-20 14:32:00', 12, 22, 11),
-- Votos da Reunião 11 (Processo 23)
('COM_RELATOR', false, NULL, '2024-05-20 15:00:00', 11, 23, 11),
('COM_RELATOR', false, NULL, '2024-05-20 15:02:00', 12, 23, 11);

-- =====================================================
-- 11. DOCUMENTOS
-- =====================================================
INSERT INTO documento (nome_original, nome_arquivo, caminho, tipo_mime, tamanho, data_upload, descricao, processo_id) VALUES
('Documento_Identidade.pdf', 'doc_1_0.pdf', 'uploads/processos/1/doc_1_0.pdf', 'application/pdf', 245678, '2024-01-10 10:00:00', 'Documento anexado ao processo 2024001/2024', 1),
('Atestado_Medico.pdf', 'doc_1_1.pdf', 'uploads/processos/1/doc_1_1.pdf', 'application/pdf', 189234, '2024-01-10 10:05:00', 'Documento anexado ao processo 2024001/2024', 1),
('Historico_Escolar.pdf', 'doc_2_0.pdf', 'uploads/processos/2/doc_2_0.pdf', 'application/pdf', 312456, '2024-01-15 11:00:00', 'Documento anexado ao processo 2024002/2024', 2),
('Declaracao.pdf', 'doc_3_0.pdf', 'uploads/processos/3/doc_3_0.pdf', 'application/pdf', 156789, '2024-01-20 09:00:00', 'Documento anexado ao processo 2024003/2024', 3),
('Comprovante.pdf', 'doc_3_1.pdf', 'uploads/processos/3/doc_3_1.pdf', 'application/pdf', 98765, '2024-01-20 09:05:00', 'Documento anexado ao processo 2024003/2024', 3),
('Requerimento.pdf', 'doc_4_0.pdf', 'uploads/processos/4/doc_4_0.pdf', 'application/pdf', 223456, '2024-01-25 14:00:00', 'Documento anexado ao processo 2024004/2024', 4),
('Documento_Identidade.pdf', 'doc_5_0.pdf', 'uploads/processos/5/doc_5_0.pdf', 'application/pdf', 278901, '2024-02-01 10:00:00', 'Documento anexado ao processo 2024005/2024', 5),
('Atestado_Medico.pdf', 'doc_6_0.pdf', 'uploads/processos/6/doc_6_0.pdf', 'application/pdf', 198765, '2024-02-05 11:00:00', 'Documento anexado ao processo 2024006/2024', 6),
('Historico_Escolar.pdf', 'doc_6_1.pdf', 'uploads/processos/6/doc_6_1.pdf', 'application/pdf', 345678, '2024-02-05 11:05:00', 'Documento anexado ao processo 2024006/2024', 6),
('Declaracao.pdf', 'doc_7_0.pdf', 'uploads/processos/7/doc_7_0.pdf', 'application/pdf', 167890, '2024-02-10 09:00:00', 'Documento anexado ao processo 2024007/2024', 7),
('Comprovante.pdf', 'doc_8_0.pdf', 'uploads/processos/8/doc_8_0.pdf', 'application/pdf', 234567, '2024-02-15 10:00:00', 'Documento anexado ao processo 2024008/2024', 8),
('Requerimento.pdf', 'doc_8_1.pdf', 'uploads/processos/8/doc_8_1.pdf', 'application/pdf', 189012, '2024-02-15 10:05:00', 'Documento anexado ao processo 2024008/2024', 8),
('Documento_Identidade.pdf', 'doc_9_0.pdf', 'uploads/processos/9/doc_9_0.pdf', 'application/pdf', 256789, '2024-02-20 14:00:00', 'Documento anexado ao processo 2024009/2024', 9),
('Atestado_Medico.pdf', 'doc_10_0.pdf', 'uploads/processos/10/doc_10_0.pdf', 'application/pdf', 178901, '2024-02-25 11:00:00', 'Documento anexado ao processo 2024010/2024', 10),
('Historico_Escolar.pdf', 'doc_11_0.pdf', 'uploads/processos/11/doc_11_0.pdf', 'application/pdf', 298765, '2024-03-01 09:00:00', 'Documento anexado ao processo 2024011/2024', 11),
('Declaracao.pdf', 'doc_11_1.pdf', 'uploads/processos/11/doc_11_1.pdf', 'application/pdf', 145678, '2024-03-01 09:05:00', 'Documento anexado ao processo 2024011/2024', 11),
('Comprovante.pdf', 'doc_12_0.pdf', 'uploads/processos/12/doc_12_0.pdf', 'application/pdf', 212345, '2024-03-05 10:00:00', 'Documento anexado ao processo 2024012/2024', 12),
('Requerimento.pdf', 'doc_13_0.pdf', 'uploads/processos/13/doc_13_0.pdf', 'application/pdf', 187654, '2024-03-10 14:00:00', 'Documento anexado ao processo 2024013/2024', 13),
('Documento_Identidade.pdf', 'doc_14_0.pdf', 'uploads/processos/14/doc_14_0.pdf', 'application/pdf', 267890, '2024-03-15 11:00:00', 'Documento anexado ao processo 2024014/2024', 14),
('Atestado_Medico.pdf', 'doc_15_0.pdf', 'uploads/processos/15/doc_15_0.pdf', 'application/pdf', 198765, '2024-03-20 09:00:00', 'Documento anexado ao processo 2024015/2024', 15),
('Historico_Escolar.pdf', 'doc_16_0.pdf', 'uploads/processos/16/doc_16_0.pdf', 'application/pdf', 312456, '2024-03-25 10:00:00', 'Documento anexado ao processo 2024016/2024', 16),
('Declaracao.pdf', 'doc_16_1.pdf', 'uploads/processos/16/doc_16_1.pdf', 'application/pdf', 156789, '2024-03-25 10:05:00', 'Documento anexado ao processo 2024016/2024', 16),
('Comprovante.pdf', 'doc_17_0.pdf', 'uploads/processos/17/doc_17_0.pdf', 'application/pdf', 223456, '2024-04-01 14:00:00', 'Documento anexado ao processo 2024017/2024', 17),
('Requerimento.pdf', 'doc_18_0.pdf', 'uploads/processos/18/doc_18_0.pdf', 'application/pdf', 189234, '2024-04-05 11:00:00', 'Documento anexado ao processo 2024018/2024', 18),
('Documento_Identidade.pdf', 'doc_19_0.pdf', 'uploads/processos/19/doc_19_0.pdf', 'application/pdf', 245678, '2024-04-10 09:00:00', 'Documento anexado ao processo 2024019/2024', 19),
('Atestado_Medico.pdf', 'doc_20_0.pdf', 'uploads/processos/20/doc_20_0.pdf', 'application/pdf', 178901, '2024-04-15 10:00:00', 'Documento anexado ao processo 2024020/2024', 20),
('Historico_Escolar.pdf', 'doc_21_0.pdf', 'uploads/processos/21/doc_21_0.pdf', 'application/pdf', 298765, '2024-04-20 14:00:00', 'Documento anexado ao processo 2024021/2024', 21),
('Declaracao.pdf', 'doc_21_1.pdf', 'uploads/processos/21/doc_21_1.pdf', 'application/pdf', 145678, '2024-04-20 14:05:00', 'Documento anexado ao processo 2024021/2024', 21),
('Comprovante.pdf', 'doc_22_0.pdf', 'uploads/processos/22/doc_22_0.pdf', 'application/pdf', 212345, '2024-04-25 11:00:00', 'Documento anexado ao processo 2024022/2024', 22),
('Requerimento.pdf', 'doc_23_0.pdf', 'uploads/processos/23/doc_23_0.pdf', 'application/pdf', 187654, '2024-05-01 09:00:00', 'Documento anexado ao processo 2024023/2024', 23),
('Documento_Identidade.pdf', 'doc_24_0.pdf', 'uploads/processos/24/doc_24_0.pdf', 'application/pdf', 267890, '2024-05-05 10:00:00', 'Documento anexado ao processo 2024024/2024', 24),
('Atestado_Medico.pdf', 'doc_25_0.pdf', 'uploads/processos/25/doc_25_0.pdf', 'application/pdf', 198765, '2024-05-10 14:00:00', 'Documento anexado ao processo 2024025/2024', 25);

-- =====================================================
-- FIM DO SCRIPT
-- =====================================================
