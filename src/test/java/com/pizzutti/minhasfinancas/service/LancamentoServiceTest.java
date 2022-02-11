package com.pizzutti.minhasfinancas.service;

import com.pizzutti.minhasfinancas.exception.RegraNegocioException;
import com.pizzutti.minhasfinancas.model.entity.Lancamento;
import com.pizzutti.minhasfinancas.model.entity.Usuario;
import com.pizzutti.minhasfinancas.model.enums.StatusLancamento;
import com.pizzutti.minhasfinancas.model.repository.LancamentoRepository;
import com.pizzutti.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.pizzutti.minhasfinancas.service.impl.LancamentoServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")

public class LancamentoServiceTest {

    @SpyBean
    LancamentoServiceImpl service;

    @MockBean
    LancamentoRepository repository;

    @Test
    public void deveSalvarUmLancamento() {
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doNothing().when(service).validar(lancamentoASalvar);

        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1l);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

        Lancamento lancamento = service.salvar(lancamentoASalvar);

        Assertions.assertEquals(lancamento, lancamentoSalvo);
    }

    @Test
    public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

        Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.salvar(lancamentoASalvar);
        });

        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
    }

    @Test
    public void deveSAtualizarUmLancamento() {
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1l);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        Mockito.doNothing().when(service).validar(lancamentoSalvo);

        Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        Lancamento lancamento = service.atualizar(lancamentoSalvo);

        Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
    }

    @Test
    public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();

        Assertions.assertThrows(NullPointerException.class, () -> {
            service.atualizar(lancamentoASalvar);
        });

        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
    }

    @Test
    public void deveDeletarUmLancamento() {
        Lancamento lancamentoADeletar = LancamentoRepositoryTest.criarLancamento();
        lancamentoADeletar.setId(1l);

        service.deletar(lancamentoADeletar);

        Mockito.verify(repository).delete(lancamentoADeletar);
    }

    @Test
    public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
        Lancamento lancamentoADeletar = LancamentoRepositoryTest.criarLancamento();

        Assertions.assertThrows(NullPointerException.class, () -> {
            service.deletar(lancamentoADeletar);
        });

        Mockito.verify(repository, Mockito.never()).delete(lancamentoADeletar);
    }

    @Test
    public void deveFiltrarLancamentos() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);

        List<Lancamento> lista = Arrays.asList(lancamento);
        Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

        List<Lancamento> resultado = service.buscar(lancamento);

        Assertions.assertEquals(lista, resultado);
    }

    @Test
    public void deveAtualizaOStatusDeUmLancamento() {
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);
        lancamento.setStatus(StatusLancamento.PENDENTE);

        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
        Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

        service.atualizarStatus(lancamento, novoStatus);

        Assertions.assertEquals(lancamento.getStatus(), novoStatus);
        Mockito.verify(service).atualizar(lancamento);
    }

    @Test
    public void deveObterUmLancamentoPorID() {
        Long id = 1l;

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

        Optional<Lancamento> resultado = service.obterPorId(id);

        Assertions.assertTrue(resultado.isPresent());
    }

    @Test
    public void deveRetornarVazioQuandoLancamentoNaoExiste() {
        Long id = 1l;

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Lancamento> resultado = service.obterPorId(id);

        Assertions.assertFalse(resultado.isPresent());
    }

    @Test
    public void deveLancarErrosAoValidarUmLancamento() {
        Lancamento lancamento = new Lancamento();

        Exception excecao = Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });
        Assertions.assertEquals(excecao.getMessage(), "Informe uma Descrição válida.");

        lancamento.setDescricao("");
        excecao = Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });
        Assertions.assertEquals(excecao.getMessage(), "Informe uma Descrição válida.");


        lancamento.setDescricao("salario");
        excecao = Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });
        Assertions.assertEquals(excecao.getMessage(), "Informe um Mês válido.");

        lancamento.setMes(0);
        excecao = Assertions.assertThrows(RegraNegocioException.class, () -> {
                    service.validar(lancamento);
        });
        Assertions.assertEquals(excecao.getMessage(), "Informe um Mês válido.");

        lancamento.setMes(13);
        excecao = Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });
        Assertions.assertEquals(excecao.getMessage(), "Informe um Mês válido.");

        lancamento.setMes(2);
        excecao = Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });
        Assertions.assertEquals(excecao.getMessage(), "Informe um Ano válido.");

        lancamento.setAno(19900);
        excecao = Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });
        Assertions.assertEquals(excecao.getMessage(), "Informe um Ano válido.");

        lancamento.setAno(1990);
        excecao = Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });
        Assertions.assertEquals(excecao.getMessage(), "Informe um Usuário.");

        Usuario usuario = Usuario.builder().nome("Jose").email("jose@email.com").senha("123").build();
        lancamento.setUsuario(usuario);
        excecao = Assertions.assertThrows(RegraNegocioException.class, () -> {
                    service.validar(lancamento);
        });
        Assertions.assertEquals(excecao.getMessage(), "Informe um Usuário.");

        usuario.setId(1l);
        excecao = Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });
        Assertions.assertEquals(excecao.getMessage(), "Informe um Valor válido.");

        lancamento.setValor(BigDecimal.valueOf(0));
        excecao = Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });
        Assertions.assertEquals(excecao.getMessage(), "Informe um Valor válido.");


        lancamento.setValor(BigDecimal.valueOf(120));
        excecao = Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.validar(lancamento);
        });
        Assertions.assertEquals(excecao.getMessage(),"Informe um tipo de lançamento.");
    }
}
