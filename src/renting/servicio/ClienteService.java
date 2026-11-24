
package renting.servicio;

import renting.modelo.Cliente;
import renting.estructuras.NodoClientes;
import renting.util.Validador;
import java.util.Scanner;

public class ClienteService {
    Validador validador = new Validador();

    private NodoClientes cabeza;

    public ClienteService() {
        this.cabeza = null;
    }

    private ContratoService contratoService;

    public ClienteService(ContratoService contratoService) {
        this.cabeza = null;
        this.contratoService = contratoService;
    }

    public void setContratoService(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    // Registrar un cliente
    public String registrarCliente(Scanner sc, Validador v) {

        boolean continuar = false;

        String cedula = "";
        System.out.print("Ingrese la cédula: ");
        while (!continuar) {
            cedula = v.validarTexto(sc, 3);
            Cliente clienteExistente = buscarCliente(cedula);
            if (clienteExistente != null) {
                System.out.println("Error: Ya existe un cliente registrado con la cédula " + cedula + ".");
                System.out.print("Ingrese otra cédula: ");
            } else {
                continuar = true;
            }
        }

        System.out.print("Ingrese el nombre: ");
        String nombre = v.validarTexto(sc, 2);

        System.out.print("Ingrese el apellido: ");
        String apellido = v.validarTexto(sc, 2);

        System.out.print("Ingrese el teléfono: ");
        String telefono = v.validarTexto(sc, 3);

        System.out.print("Ingrese la dirección: ");
        String direccion = v.validarTexto(sc, 4);

        System.out.print("Ingrese la licencia de conducción: ");
        String licencia = v.validarTexto(sc, 4);

        Cliente c = new Cliente(
                cedula, nombre, apellido, telefono, direccion, licencia);

        NodoClientes nuevo = new NodoClientes(c);

        // Si la lista está vacía, el nuevo nodo será el primero
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            // Recorrer hasta encontrar el último nodo
            NodoClientes actual = cabeza;

            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }

            // Enlazar el último nodo con el nuevo
            actual.setSiguiente(nuevo);
        }

        return "Cliente registrado correctamente.";
    }

    // Retornar cliente por su cédula. Se usa en el menú para buscar un cliente y mostrar sus datos en el informe
    public Cliente buscarCliente(Scanner sc, Validador v) {

        System.out.print("Ingrese la cédula del cliente a buscar: ");
        String cedula = v.validarTexto(sc, 3);

        NodoClientes actual = cabeza;

        while (actual != null) {

            Cliente c = actual.getDato();

            if (c.getCedula().equalsIgnoreCase(cedula)) {
                return c;
            }

            actual = actual.getSiguiente();
        }

        System.out.println("Cliente con la cédula " + cedula + " no encontrado.");
        return null;
    }

    //Se usa en la validación de la cédula al registrar un cliente, para evitar duplicados
    public Cliente buscarCliente(String cedula) {

        NodoClientes actual = cabeza;

        while (actual != null) {

            Cliente c = actual.getDato();

            if (c.getCedula().equalsIgnoreCase(cedula)) {
                return c;
            }

            actual = actual.getSiguiente();
            System.out.println("Cliente con la cédula " + cedula + " no encontrado.");
        }

        return null;
    }

    // Modificar un cliente por su cédula
    public String modificarCliente(Scanner sc) {
        Validador v = new Validador();

        System.out.print("Ingrese la cédula del cliente a modificar: ");
        String cedula = v.validarTexto(sc, 3);

        NodoClientes actual = cabeza;

        // Buscar el cliente en la lista
        while (actual != null) {

            Cliente c = actual.getDato();

            if (c.getCedula().equalsIgnoreCase(cedula)) {

                System.out.println("Cliente encontrado.");
                System.out.println("Ingrese los nuevos datos:");

                System.out.print("Nuevo nombre: ");
                c.setNombre(validador.validarTexto(sc, 2));

                System.out.print("Nuevo apellido: ");
                c.setApellido(validador.validarTexto(sc, 2));

                System.out.print("Nuevo teléfono: ");
                c.setTelefono(validador.validarTexto(sc, 3));

                System.out.print("Nueva dirección: ");
                c.setDireccion(validador.validarTexto(sc, 4));

                System.out.print("Nueva licencia de conducción: ");
                c.setLicenciaConduccion(validador.validarTexto(sc, 4));

                return "Cliente modificado correctamente.";
            }

            actual = actual.getSiguiente();
        }

        return "Cliente con la cédula " + cedula + " no encontrado.";
    }

    // Eliminar cliente por su cédula
    public String eliminarCliente(Scanner sc, Validador v) {

        System.out.print("Ingrese la cédula del cliente a eliminar: ");
        String cedula = v.validarTexto(sc, 3);

        if (cabeza == null) {
            return "No hay clientes registrados.";
        }

        // Caso 1: el cliente está en el primer nodo
        if (cabeza.getDato().getCedula().equalsIgnoreCase(cedula)) {

            cabeza = cabeza.getSiguiente();

            contratoService.eliminarContratosPorCedula(cedula);

            return "Cliente y sus contratos asociados eliminados correctamente.";
        }

        // Caso 2: el cliente está en otro nodo
        NodoClientes actual = cabeza;

        while (actual.getSiguiente() != null) {

            Cliente c = actual.getSiguiente().getDato();

            if (c.getCedula().equalsIgnoreCase(cedula)) {

                // Eliminar el nodo del cliente
                actual.setSiguiente(actual.getSiguiente().getSiguiente());

                // Eliminar sus contratos asociados
                contratoService.eliminarContratosPorCedula(cedula);

                return "Cliente y sus contratos asociados eliminados correctamente.";
            }

            actual = actual.getSiguiente();
        }

        return "Cliente con la cédula " + cedula + " no encontrado.";
    }

    public NodoClientes getClientes() {
        return cabeza;
    }
}
