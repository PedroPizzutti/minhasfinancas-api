package com.pizzutti.minhasfinancas.service;

import com.pizzutti.minhasfinancas.model.entity.Lancamento;
import com.pizzutti.minhasfinancas.model.enums.StatusLancamento;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LancamentoService {

    Lancamento salvar (Lancamento lancamento);

    Lancamento atualizar (Lancamento lancamento);

    void deletar (Lancamento lancamento);

    List<Lancamento> buscar (Lancamento lancamentoFiltro);

    void atualizarStatus (Lancamento lancamento, StatusLancamento status);

    void validar (Lancamento lancamento);

    Optional<Lancamento> obterPorId(Long id);

    BigDecimal obterSaldoPorUsuario(Long id);

}
