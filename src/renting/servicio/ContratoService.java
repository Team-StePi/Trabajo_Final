
package renting.servicio;

import renting.modelo.Cliente;
import renting.modelo.ContratoRenting;
import renting.modelo.Vehiculo;
import renting.estructuras.NodoContrato;
import renting.util.Validador;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class ContratoService {
    Validador validador = new Validador();

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private Scanner sc = new Scanner(System.in);
    private NodoContrato cabeza;
    private ClienteService clienteService;
    private VehiculoService vehiculoService;

    public ContratoService(ClienteService clienteService,
                           VehiculoService vehiculoService) {
        this.cabeza = null;
        this.clienteService = clienteService;
        this.vehiculoService = vehiculoService;
    }

    public ContratoService() {
        this.cabeza = null;
    }

    public String registrarContrato() {

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
        Cliente cliente = clienteService.retornarCliente(sc, validador);

        if (cliente == null) {
            return "Error: El cliente ingresado no está registrado en el sistema.";
        }

        if (clienteTieneContratoActivo(cliente.getCedula())) {
            return "Error: El cliente C.C. " + cliente.getCedula()
                    + " ya posee un contrato ACTIVO.";
        }

        System.out.println("--- BÚSQUEDA Y ASIGNACIÓN DE VEHÍCULO ---");
        Vehiculo vehiculo = vehiculoService.retornarVehiculo(sc, validador);

        if (vehiculo == null) {
            return "Error: El vehículo ingresado no existe en el inventario.";
        }

        if (Vehiculo.EstadoVehiculo.ALQUILADO.equals(vehiculo.getEstado())) {
            return "Error: El vehículo con placa " + vehiculo.getPlaca()
                    + " ya se encuentra ALQUILADO.";
        }

        LocalDate inicio = pedirFecha(
                "Ingrese la fecha de inicio (DD-MM-AAAA): ");

        LocalDate fin = pedirFecha(
                "Ingrese la fecha de fin (DD-MM-AAAA): ");

        while (!fin.isAfter(inicio)) {
            System.out.println(
                    "La fecha de fin debe ser posterior a la fecha de inicio.");

            fin = pedirFecha(
                    "Ingrese la fecha de fin (DD-MM-AAAA): ");
        }

        int totalDias = (int) ChronoUnit.DAYS.between(inicio, fin);

        ContratoRenting nuevoContrato = new ContratoRenting(
                idContrato,
                cliente.getCedula(),
                vehiculo.getPlaca(),
                inicio.format(FORMATO_FECHA),
                fin.format(FORMATO_FECHA),
                totalDias,
                vehiculo.getPrecioDiario()
        );

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

    private LocalDate pedirFecha(String mensaje) {

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

    public String modificarContrato() {

        ContratoRenting contrato = buscarContrato();

        if (contrato == null) {
            return "Error: No se encontró el contrato solicitado.";
        }

        if ("Finalizado".equalsIgnoreCase(contrato.getEstado())) {
            return "Error: No se puede modificar un contrato que ya fue FINALIZADO.";
        }

        LocalDate nuevaInicio = pedirFecha(
                "Ingrese la nueva fecha de inicio (DD-MM-AAAA): ");

        LocalDate nuevaFin = pedirFecha(
                "Ingrese la nueva fecha de fin (DD-MM-AAAA): ");

        while (!nuevaFin.isAfter(nuevaInicio)) {
            System.out.println(
                    "La fecha de fin debe ser posterior a la fecha de inicio.");

            nuevaFin = pedirFecha(
                    "Ingrese la nueva fecha de fin (DD-MM-AAAA): ");
        }

        int nuevosDias =
                (int) ChronoUnit.DAYS.between(nuevaInicio, nuevaFin);

        float precioDiario =
                contrato.getValorTotal() / contrato.getTotalDias();

        contrato.setFechaInicio(nuevaInicio.format(FORMATO_FECHA));
        contrato.setFechaFin(nuevaFin.format(FORMATO_FECHA));
        contrato.setTotalDias(nuevosDias);
        contrato.calcularValorTotal(precioDiario);

        return "Contrato " + contrato.getIdContrato()
                + " modificado (" + nuevosDias
                + " días). Nuevo valor total: $"
                + contrato.getValorTotal();
    }

    public String devolverContrato() {

        ContratoRenting contrato = buscarContrato();

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
            while (v != null) {
                if (v.getPlaca().equalsIgnoreCase(
                        contrato.getPlacaVehiculo())) {

                    v.setEstado(Vehiculo.EstadoVehiculo.DISPONIBLE);
                    break;
                }
            }
        }

        return "Devolución procesada. Contrato "
                + contrato.getIdContrato()
                + " finalizado y vehículo "
                + contrato.getPlacaVehiculo()
                + " disponible.";
    }

    // Buscar contrato por su ID
    public ContratoRenting buscarContrato() {

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

        return null;
    }

    public boolean validarDisponibilidad() {

        System.out.print("Ingrese la placa del vehículo: ");
        String placa = validador.validarTexto(sc, 1);

        NodoContrato actual = cabeza;

        while (actual != null) {

            ContratoRenting c = actual.getDato();

            if (c.getPlacaVehiculo().equalsIgnoreCase(placa)
                    && "Activo".equalsIgnoreCase(c.getEstado())) {
                return false;
            }

            actual = actual.getSiguiente();
        }

        return true;
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
}
