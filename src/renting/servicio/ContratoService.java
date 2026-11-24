
package renting.servicio;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import renting.estructuras.NodoContrato;
import renting.modelo.Cliente;
import renting.modelo.ContratoRenting;
import renting.modelo.Vehiculo;
import renting.util.Validador;

public class ContratoService {
    Validador validador = new Validador();

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private NodoContrato cabeza;
    private ClienteService clienteService;
    private VehiculoService vehiculoService;

    public ContratoService(ClienteService clienteService,
            VehiculoService vehiculoService) {
        this.cabeza = null;
        this.clienteService = clienteService;
        this.vehiculoService = vehiculoService;
    }

    public String registrarContrato(Scanner sc) {

        System.out.print("Ingrese el ID del contrato: ");
        String idContrato = validador.validarTexto(sc, 1);

        // Verificar si el ID ya existe
        NodoContrato actual = cabeza;

        while (actual != null) {
            ContratoRenting c = actual.getDato();

            if (c.getIdContrato().equalsIgnoreCase(idContrato)) {
                return "Error: Ya existe un contrato registrado con el ID "
                        + idContrato + ".";
            }

            actual = actual.getSiguiente();
        }

        System.out.println("--- BÚSQUEDA Y ASIGNACIÓN DE CLIENTE ---");
        Cliente cliente = clienteService.buscarCliente(sc, validador);

        if (cliente == null) {
            return "Error: El cliente ingresado no está registrado en el sistema.";
        }

        if (clienteTieneContratoActivo(cliente.getCedula())) {
            return "Error: El cliente C.C. " + cliente.getCedula()
                    + " ya posee un contrato ACTIVO.";
        }

        System.out.println("--- BÚSQUEDA Y ASIGNACIÓN DE VEHÍCULO ---");
        Vehiculo vehiculo = vehiculoService.buscarVehiculo(sc, validador);

        if (vehiculo == null) {
            return "Error: El vehículo ingresado no existe en el inventario.";
        }

        if (Vehiculo.EstadoVehiculo.ALQUILADO.equals(vehiculo.getEstado())) {
            return "Error: El vehículo con placa " + vehiculo.getPlaca()
                    + " ya se encuentra ALQUILADO.";
        }

        LocalDate inicio = pedirFecha(sc, "Ingrese la fecha de inicio (DD-MM-AAAA): ");
        LocalDate fin = pedirFecha(sc, "Ingrese la fecha de fin (DD-MM-AAAA): ");

        while (!fin.isAfter(inicio)) {
            System.out.println("La fecha de fin debe ser posterior a la fecha de inicio.");

            fin = pedirFecha(sc, "Ingrese la fecha de fin (DD-MM-AAAA): ");
        }

        int totalDias = (int) ChronoUnit.DAYS.between(inicio, fin);

        ContratoRenting nuevoContrato = new ContratoRenting(
                idContrato,
                cliente.getCedula(),
                vehiculo.getPlaca(),
                inicio.format(FORMATO_FECHA),
                fin.format(FORMATO_FECHA),
                totalDias,
                vehiculo.getPrecioDiario());

        // Crear el nuevo nodo
        NodoContrato nuevo = new NodoContrato(nuevoContrato);

        // Agregar el nodo a la lista
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            actual = cabeza;

            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }

            actual.setSiguiente(nuevo);
        }

        vehiculo.setEstado(Vehiculo.EstadoVehiculo.ALQUILADO);

        return "Contrato " + idContrato + " registrado (" + totalDias
                + " días). Vehículo marcado como 'ALQUILADO'.";
    }

    private LocalDate pedirFecha(Scanner sc, String mensaje) {

        while (true) {
            System.out.print(mensaje);
            String fechaTexto = sc.nextLine().trim();

            if (fechaTexto.isEmpty()) {
                System.out.println(
                        "Error: La fecha no puede estar vacía. Intente de nuevo.");
                continue;
            }

            try {
                return LocalDate.parse(fechaTexto, FORMATO_FECHA);
            } catch (DateTimeParseException e) {
                System.out.println(
                        "Error: Formato o fecha inválida. Debe usar el formato "
                                + "DD-MM-AAAA (ejemplo: 25-10-2026).");
            }
        }
    }

    public String modificarContrato(Scanner sc) {

        ContratoRenting contrato = buscarContrato(sc);

        if (contrato == null) {
            return "Error: No se encontró el contrato solicitado.";
        }

        if ("Finalizado".equalsIgnoreCase(contrato.getEstado())) {
            return "Error: No se puede modificar un contrato que ya fue FINALIZADO.";
        }

        LocalDate nuevaInicio = pedirFecha(sc, "Ingrese la nueva fecha de inicio (DD-MM-AAAA): ");

        LocalDate nuevaFin = pedirFecha(sc, "Ingrese la nueva fecha de fin (DD-MM-AAAA): ");

        while (!nuevaFin.isAfter(nuevaInicio)) {
            System.out.println(
                    "La fecha de fin debe ser posterior a la fecha de inicio.");

            nuevaFin = pedirFecha(sc, "Ingrese la nueva fecha de fin (DD-MM-AAAA): ");
        }

        int nuevosDias = (int) ChronoUnit.DAYS.between(nuevaInicio, nuevaFin);

        float precioDiario = contrato.getValorTotal() / contrato.getTotalDias();

        contrato.setFechaInicio(nuevaInicio.format(FORMATO_FECHA));
        contrato.setFechaFin(nuevaFin.format(FORMATO_FECHA));
        contrato.setTotalDias(nuevosDias);
        contrato.calcularValorTotal(precioDiario);

        return "Contrato " + contrato.getIdContrato()
                + " modificado (" + nuevosDias
                + " días). Nuevo valor total: $"
                + contrato.getValorTotal();
    }

    public String devolverContrato(Scanner sc) {

        ContratoRenting contrato = buscarContrato(sc);

        if (contrato == null) {
            return "Error: No se encontró el contrato a devolver.";
        }

        if ("Finalizado".equalsIgnoreCase(contrato.getEstado())) {
            return "Error: El contrato ya fue devuelto y finalizado anteriormente.";
        }

        contrato.setEstado("Finalizado");

        if (vehiculoService != null
                && vehiculoService.getVehiculos() != null) {
            Vehiculo v = vehiculoService.buscarVehiculo(contrato.getPlacaVehiculo());
            if (v.getPlaca().equalsIgnoreCase(contrato.getPlacaVehiculo())) {
                v.setEstado(Vehiculo.EstadoVehiculo.DISPONIBLE);
            }
        }

        return "Devolución procesada. Contrato "
                + contrato.getIdContrato()
                + " finalizado y vehículo "
                + contrato.getPlacaVehiculo()
                + " disponible.";
    }

    // Buscar contrato por su ID
    public ContratoRenting buscarContrato(Scanner sc) {

        System.out.print("Ingrese el ID del contrato: ");
        String idContrato = validador.validarTexto(sc, 1);

        NodoContrato actual = cabeza;

        while (actual != null) {

            ContratoRenting c = actual.getDato();

            if (c.getIdContrato().equalsIgnoreCase(idContrato)) {
                return c;
            } 
            actual = actual.getSiguiente();
        }
        System.out.println("No se encontró un contrato con el ID "
            + idContrato + ". Intente de nuevo.");
        return null;
    }

    private boolean clienteTieneContratoActivo(String cedulaCliente) {

        NodoContrato actual = cabeza;

        while (actual != null) {

            ContratoRenting c = actual.getDato();

            if (c.getCedulaCliente().equalsIgnoreCase(cedulaCliente)
                    && "Activo".equalsIgnoreCase(c.getEstado())) {
                return true;
            }

            actual = actual.getSiguiente();
        }

        return false;
    }

    public NodoContrato getContratos() {
        return cabeza;
    }

    // Eliminar los contratos asociados a una cédula de cliente
    public String eliminarContratosPorCedula(String cedula) {

        int eliminados = 0;

        // Caso 1: los contratos que se deben eliminar están al inicio
        while (cabeza != null &&
                cabeza.getDato().getCedulaCliente().equalsIgnoreCase(cedula)) {

            cabeza = cabeza.getSiguiente();
            eliminados++;
        }

        // Caso 2: los contratos están en otras posiciones
        NodoContrato actual = cabeza;

        while (actual != null && actual.getSiguiente() != null) {

            if (actual.getSiguiente().getDato()
                    .getCedulaCliente().equalsIgnoreCase(cedula)) {

                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                eliminados++;

            } else {
                actual = actual.getSiguiente();
            }
        }

        if (eliminados > 0) {
            return "Contratos asociados eliminados correctamente.";
        } else {
            return "El cliente no tenía contratos registrados.";
        }
    }
}
