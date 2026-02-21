package com.bank.cliente;

import com.bank.cliente.application.port.out.ClienteEventPublisherPort;
import com.bank.cliente.application.port.out.ClienteRepositoryPort;
import com.bank.cliente.application.service.ClienteService;
import com.bank.cliente.domain.entity.Cliente;
import com.bank.cliente.domain.entity.Genero;
import com.bank.cliente.domain.exception.ClienteAlreadyExistsException;
import com.bank.cliente.domain.exception.ClienteNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService Unit Tests")
class ClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @Mock
    private ClienteEventPublisherPort eventPublisher;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clienteValido;

    @BeforeEach
    void setUp() {
        clienteValido = Cliente.builder()
                .id(1L)
                .nombre("Jose Lema")
                .genero(Genero.MASCULINO)
                .edad(30)
                .identificacion("1712345678")
                .direccion("Otavalo sn y principal")
                .telefono("098254785")
                .clienteId("CLI-1712345678")
                .contrasena("1234")
                .estado(true)
                .build();
    }

    @Test
    @DisplayName("Debe crear un cliente exitosamente")
    void debeCrearClienteExitosamente() {
        when(clienteRepository.existsByIdentificacion(anyString())).thenReturn(false);
        when(clienteRepository.existsByClienteId(anyString())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteValido);
        doNothing().when(eventPublisher).publishClienteCreated(any());

        Cliente resultado = clienteService.crearCliente(clienteValido);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Jose Lema");
        assertThat(resultado.getIdentificacion()).isEqualTo("1712345678");
        assertThat(resultado.getEstado()).isTrue();
        verify(clienteRepository).save(any(Cliente.class));
        verify(eventPublisher).publishClienteCreated(any(Cliente.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion si la identificacion ya existe")
    void debeLanzarExcepcionSiIdentificacionExiste() {
        when(clienteRepository.existsByIdentificacion("1712345678")).thenReturn(true);

        assertThatThrownBy(() -> clienteService.crearCliente(clienteValido))
                .isInstanceOf(ClienteAlreadyExistsException.class)
                .hasMessageContaining("1712345678");

        verify(clienteRepository, never()).save(any());
        verify(eventPublisher, never()).publishClienteCreated(any());
    }

    @Test
    @DisplayName("Debe obtener cliente por id exitosamente")
    void debeObtenerClientePorId() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));

        Cliente resultado = clienteService.obtenerClientePorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe lanzar ClienteNotFoundException si cliente no existe")
    void debeLanzarNotFoundSiClienteNoExiste() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.obtenerClientePorId(99L))
                .isInstanceOf(ClienteNotFoundException.class);
    }

    @Test
    @DisplayName("Debe listar todos los clientes")
    void debeListarTodosLosClientes() {
        when(clienteRepository.findAll()).thenReturn(List.of(clienteValido));

        List<Cliente> resultado = clienteService.listarClientes();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Jose Lema");
    }

    @Test
    @DisplayName("Debe eliminar cliente exitosamente")
    void debeEliminarClienteExitosamente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));
        doNothing().when(clienteRepository).deleteById(1L);
        doNothing().when(eventPublisher).publishClienteDeleted(1L);

        assertThatCode(() -> clienteService.eliminarCliente(1L)).doesNotThrowAnyException();

        verify(clienteRepository).deleteById(1L);
        verify(eventPublisher).publishClienteDeleted(1L);
    }

    @Test
    @DisplayName("Debe actualizar parcialmente un cliente")
    void debeActualizarClienteParcialmente() {
        Cliente actualizacion = Cliente.builder()
                .telefono("099999999")
                .estado(false)
                .build();

        Cliente clienteActualizado = Cliente.builder()
                .id(1L)
                .nombre("Jose Lema")
                .identificacion("1712345678")
                .clienteId("CLI-1712345678")
                .contrasena("1234")
                .telefono("099999999")
                .estado(false)
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));
        when(clienteRepository.save(any())).thenReturn(clienteActualizado);

        Cliente resultado = clienteService.actualizarClienteParcial(1L, actualizacion);

        assertThat(resultado.getTelefono()).isEqualTo("099999999");
        assertThat(resultado.getEstado()).isFalse();
    }
}
