package renting.servicio;

import renting.modelo.CamionetaSUV;
import renting.modelo.CarroSedan;
import renting.modelo.Vehiculo;
import renting.modelo.Vehiculo.EstadoVehiculo;
import renting.estructuras.NodoVehiculo;
import java.util.Scanner;

public class VehiculoService {

    private NodoVehiculo cabeza;

    public VehiculoService() {
        this.cabeza = null;
    }

    // Agregar un vehículo al final de la lista
    public String registrarVehiculo(Scanner sc) {

        System.out.println("¿Qué tipo de vehículo desea registrar?");
        System.out.println("1. Carro Sedán");
        System.out.println("2. Camioneta SUV");
        int tipo = sc.nextInt();
        sc.nextLine();

        System.out.print("Ingrese la placa: ");
        String placa = sc.nextLine();

        System.out.print("Ingrese la marca: ");
        String marca = sc.nextLine();

        System.out.print("Ingrese el modelo (año): ");
        int modelo = sc.nextInt();
        sc.nextLine();

        System.out.print("Ingrese el precio diario: ");
        float precio = sc.nextFloat();
        sc.nextLine();

        System.out.println("Seleccione el estado:");
        System.out.println("1. DISPONIBLE");
        System.out.println("2. ALQUILADO");
        int opcionEstado = sc.nextInt();
        sc.nextLine();
        EstadoVehiculo estado;
        if (opcionEstado == 1) {
            estado = EstadoVehiculo.DISPONIBLE;
        } else {
            estado = EstadoVehiculo.ALQUILADO;
        }

        Vehiculo v = null;

        if (tipo == 1) {
            // --- CARRO SEDAN ---
            System.out.println("Seleccione el tipo de combustible:");
            System.out.println("1. GASOLINA");
            System.out.println("2. DIESEL");
            System.out.println("3. ELECTRICO");
            int opCombustible = sc.nextInt();
            sc.nextLine();

            CarroSedan.Combustible combustible;
            switch (opCombustible) {
                case 1:
                    combustible = CarroSedan.Combustible.GASOLINA;
                    break;
                case 2:
                    combustible = CarroSedan.Combustible.DIESEL;
                    break;
                default:
                    combustible = CarroSedan.Combustible.ELECTRICO;
                    break;
            }

            System.out.println("Seleccione el tipo de transmisión:");
            System.out.println("1. MANUAL");
            System.out.println("2. AUTOMATICA");
            int opTransmision = sc.nextInt();
            sc.nextLine();

            CarroSedan.TipoTransmision transmision;
            if (opTransmision == 1) {
                transmision = CarroSedan.TipoTransmision.MANUAL;
            } else {
                transmision = CarroSedan.TipoTransmision.AUTOMATICA;
            }

            v = new CarroSedan(placa, marca, modelo, precio, estado, combustible, transmision);

        } else if (tipo == 2) {
            // --- CAMIONETA SUV ---

            System.out.println("Seleccione el tipo de tracción:");
            System.out.println("1. 4X2");
            System.out.println("2. 4X4");
            int opTraccion = sc.nextInt();
            sc.nextLine();

            CamionetaSUV.TipoTraccion traccion;
            if (opTraccion == 1) {
                traccion = CamionetaSUV.TipoTraccion._4x2;
            } else {
                traccion = CamionetaSUV.TipoTraccion._4x4;
            }

            System.out.print("Ingrese la capacidad del maletero (en litros): ");
            float capacidadMaletero = sc.nextFloat();
            sc.nextLine();

            v = new CamionetaSUV(placa, marca, modelo, precio, estado, traccion, capacidadMaletero);
        } else {
            System.out.println("No está disponible ese tipo de automóvil.");
        }

        NodoVehiculo nuevo = new NodoVehiculo(v);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            NodoVehiculo actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo);
        }

        return "Vehículo registrado correctamente.";
    }

    // Buscar un vehículo por su placa
    public String buscarVehiculo(Scanner sc) {
        System.out.print("Ingrese la placa del vehículo: ");
        String placa = sc.nextLine();

        NodoVehiculo actual = cabeza;

        while (actual != null) {

            Vehiculo v = actual.getDato();

            if (v.getPlaca().equalsIgnoreCase(placa)) {
                return "--- VEHÍCULO ENCONTRADO ---\n" +
                        "Placa: " + v.getPlaca() + "\n" +
                        "Marca: " + v.getMarca() + "\n" +
                        "Modelo: " + v.getModelo() + "\n" +
                        "Precio diario: " + v.getPrecioDiario() + "\n" +
                        "Estado: " + v.getEstado();
            }

            actual = actual.getSiguiente();
        }
        return "Vehículo con la placa " + placa + " no encontrado.";
    }

    // Eliminar un vehículo por su placa
    public String eliminarVehiculo(Scanner sc) {

        System.out.print("Ingrese la placa del vehículo a eliminar: ");
        String placa = sc.nextLine();

        if (cabeza == null) {
            return "No hay vehículos registrados.";
        }

        // Caso 1: El vehículo está en la cabeza
        if (cabeza.getDato().getPlaca().equalsIgnoreCase(placa)) {

            cabeza = cabeza.getSiguiente();

            return "Vehículo eliminado correctamente.";
        }

        // Caso 2: El vehículo está en otro nodo
        NodoVehiculo actual = cabeza;

        while (actual.getSiguiente() != null) {

            if (actual.getSiguiente().getDato()
                    .getPlaca().equalsIgnoreCase(placa)) {

                actual.setSiguiente(
                        actual.getSiguiente().getSiguiente());

                return "Vehículo eliminado correctamente.";
            }

            actual = actual.getSiguiente();
        }

        return "Vehículo con la placa " + placa + " no encontrado.";
    }

    // Mostrar todos los vehículos
    public String mostrarVehiculos() {

        if (cabeza == null) {
            return "No hay vehículos registrados.";
        }

        NodoVehiculo actual = cabeza;

        while (actual != null) {

            Vehiculo ve = actual.getDato();

            if (ve instanceof CarroSedan) {

                CarroSedan sedan = (CarroSedan) ve;

                System.out.println("--- CARRO SEDÁN ---");
                System.out.println("Placa: " + sedan.getPlaca());
                System.out.println("Marca: " + sedan.getMarca());
                System.out.println("Modelo: " + sedan.getModelo());
                System.out.println("Precio diario: " + sedan.getPrecioDiario());
                System.out.println("Estado: " + sedan.getEstado());
                System.out.println("Combustible: " + sedan.getTipoCombustible());
                System.out.println("Transmisión: " + sedan.getTransmision());

            } else if (ve instanceof CamionetaSUV) {

                CamionetaSUV suv = (CamionetaSUV) ve;

                System.out.println("--- CAMIONETA SUV ---");
                System.out.println("Placa: " + suv.getPlaca());
                System.out.println("Marca: " + suv.getMarca());
                System.out.println("Modelo: " + suv.getModelo());
                System.out.println("Precio diario: " + suv.getPrecioDiario());
                System.out.println("Estado: " + suv.getEstado());
                System.out.println("Capacidad del maletero: " + suv.getCapacidadMaletero());
                System.out.println("Tipo de tracción: " + suv.getTraccion());
            }

            System.out.println("-------------------------");

            // Avanzar al siguiente nodo
            actual = actual.getSiguiente();
        }

        return "Vehículos mostrados correctamente.";
    }
}
