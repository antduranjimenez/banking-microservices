package com.bank.cuenta.application.service;

import com.bank.cuenta.application.port.in.CuentaUseCase;
import com.bank.cuenta.application.port.out.CuentaRepositoryPort;
import com.bank.cuenta.domain.entity.Cuenta;
import com.bank.cuenta.domain.exception.CuentaNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CuentaService implements CuentaUseCase {

    private final CuentaRepositoryPort cuentaRepository;

    @Override
    public Cuenta crearCuenta(Cuenta cuenta) {
        cuenta.setSaldoDisponible(cuenta.getSaldoInicial());
        return cuentaRepository.save(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public Cuenta obtenerCuentaPorId(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Cuenta obtenerCuentaPorNumero(String numeroCuenta) {
        return cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new CuentaNotFoundException(numeroCuenta));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> listarCuentas() {
        return cuentaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> listarCuentasPorCliente(String clienteId) {
        return cuentaRepository.findByClienteId(clienteId);
    }

    @Override
    public Cuenta actualizarCuenta(Long id, Cuenta cuentaData) {
        Cuenta existing = obtenerCuentaPorId(id);
        existing.setNumeroCuenta(cuentaData.getNumeroCuenta());
        existing.setTipoCuenta(cuentaData.getTipoCuenta());
        existing.setSaldoInicial(cuentaData.getSaldoInicial());
        existing.setEstado(cuentaData.getEstado());
        return cuentaRepository.save(existing);
    }

    @Override
    public Cuenta actualizarCuentaParcial(Long id, Cuenta cuentaData) {
        Cuenta existing = obtenerCuentaPorId(id);
        if (cuentaData.getTipoCuenta() != null) existing.setTipoCuenta(cuentaData.getTipoCuenta());
        if (cuentaData.getEstado() != null) existing.setEstado(cuentaData.getEstado());
        return cuentaRepository.save(existing);
    }

    @Override
    public void eliminarCuenta(Long id) {
        if (cuentaRepository.findById(id).isEmpty()) {
            throw new CuentaNotFoundException(id);
        }
        cuentaRepository.deleteById(id);
    }
}
