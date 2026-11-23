
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

        System.out.print("Ingrese la cédula: ");
        String cedula = v.validarTexto(sc, 3);

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

    // Retornar cliente por su cédula
    public String buscarCliente(Scanner sc, Validador v) {

        System.out.print("Ingrese la cédula del cliente a buscar: ");
        String cedula = v.validarTexto(sc, 3);

        NodoClientes actual = cabeza;

        while (actual != null) {

            Cliente c = actual.getDato();

            if (c.getCedula().equalsIgnoreCase(cedula)) {
                return "Cliente encontrado: " + c.getNombre() + " " + c.getApellido();
            }

            actual = actual.getSiguiente();
        }

        System.out.println("Cliente con la cédula " + cedula + " no encontrado.");
        return "Cliente no encontrado.";
    }

    // Retornar cliente por su cédula
    public Cliente retornarCliente(Scanner sc, Validador v) {

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

    // Modificar un cliente por su cédula
    public String modificarCliente(Scanner sc) {

        System.out.print("Ingrese la cédula del cliente a modificar: ");
        String cedula = sc.nextLine();

        NodoClientes actual = cabeza;

        // Buscar el cliente en la lista
        while (actual != null) {

            Cliente c = actual.getDato();

            if (c.getCedula().equalsIgnoreCase(cedula)) {

                System.out.println("Cliente encontrado.");
                System.out.println("Ingrese los nuevos datos:");

                System.out.print("Nuevo nombre: ");
                c.setNombre(sc.nextLine());

                System.out.print("Nuevo apellido: ");
                c.setApellido(sc.nextLine());

                System.out.print("Nuevo teléfono: ");
                c.setTelefono(sc.nextLine());

                System.out.print("Nueva dirección: ");
                c.setDireccion(sc.nextLine());

                System.out.print("Nueva licencia de conducción: ");
                c.setLicenciaConduccion(sc.nextLine());

                return "Cliente modificado correctamente.";
            }

            actual = actual.getSiguiente();
        }

        return "Cliente con la cédula " + cedula + " no encontrado.";
    }

    // Eliminar cliente por su cédula
    /*
     * public String eliminarCliente(Scanner sc, Validador v) {
     * 
     * System.out.print("Ingrese la cédula del cliente a eliminar: ");
     * String cedula = v.validarTexto(sc, 3);
     * 
     * if (cabeza == null) {
     * return "No hay clientes registrados.";
     * }
     * 
     * // Caso 1: el cliente está en el primer nodo
     * if (cabeza.getDato().getCedula().equalsIgnoreCase(cedula)) {
     * cabeza = cabeza.getSiguiente();
     * return "Cliente eliminado correctamente.";
     * }
     * 
     * // Caso 2: el cliente está en otro nodo
     * NodoClientes actual = cabeza;
     * 
     * while (actual.getSiguiente() != null) {
     * 
     * Cliente c = actual.getSiguiente().getDato();
     * 
     * if (c.getCedula().equalsIgnoreCase(cedula)) {
     * 
     * // Saltar el nodo que se quiere eliminar
     * actual.setSiguiente(actual.getSiguiente().getSiguiente());
     * 
     * return "Cliente eliminado correctamente.";
     * }
     * 
     * actual = actual.getSiguiente();
     * }
     * 
     * return "Cliente con la cédula " + cedula + " no encontrado.";
     * }
     */

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

    // Mostrar todos los clientes
    public String mostrarClientes() {

        if (cabeza == null) {
            System.out.println("No hay clientes registrados.");
            return "No hay clientes registrados.";
        }

        NodoClientes actual = cabeza;

        while (actual != null) {

            Cliente c = actual.getDato();

            System.out.println("Cédula: " + c.getCedula());
            System.out.println("Nombre: " + c.getNombre() + " " + c.getApellido());
            System.out.println("Teléfono: " + c.getTelefono());
            System.out.println("Dirección: " + c.getDireccion());
            System.out.println("Licencia: " + c.getLicenciaConduccion());
            System.out.println("-------------------------");

            actual = actual.getSiguiente();
        }

        return "Clientes mostrados correctamente.";
    }
}
