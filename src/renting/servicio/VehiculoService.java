package renting.servicio;

import renting.modelo.CamionetaSUV;
import renting.modelo.CarroSedan;
import renting.modelo.Vehiculo;
import renting.estructuras.NodoVehiculo;
import java.util.Scanner;
import renting.util.Validador;

public class VehiculoService {

    private NodoVehiculo cabeza;

    public VehiculoService() {
        this.cabeza = null;
    }

    // Agregar un vehículo al final de la lista
    public String registrarVehiculo(Scanner sc, Validador validador) {

        System.out.println("¿Qué tipo de vehículo desea registrar?");
        System.out.println("1. Carro Sedán");
        System.out.println("2. Camioneta SUV");
        int tipo = validador.validarOpcionMenu(sc, 1, 2);

        String placa = "";
        boolean continuar = false;
        while (!continuar) {
            System.out.print("Ingrese la placa: ");
            placa = validador.validarTexto(sc, 1);
            Vehiculo vehiculoExistente = buscarVehiculo(placa);
            if (vehiculoExistente != null) {
                System.out.println("Error: Ya existe un vehículo registrado con la placa " + placa + ".");
                System.out.print("Ingrese otra placa: ");
            } else {
                continuar = true;
            }
        }

        System.out.print("Ingrese la marca: ");
        String marca = validador.validarTexto(sc, 2);

        System.out.print("Ingrese el modelo (año): ");
        int modelo = validador.validarEntero(sc);

        System.out.print("Ingrese el precio diario: ");
        float precio = validador.validarDecimal(sc);

        Vehiculo v = null;

        if (tipo == 1) {
            // --- CARRO SEDAN ---
            System.out.println("Seleccione el tipo de combustible:");
            System.out.println("1. GASOLINA");
            System.out.println("2. DIESEL");
            System.out.println("3. ELECTRICO");
            int opCombustible = validador.validarOpcionMenu(sc, 1, 3);

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
            int opTransmision = validador.validarOpcionMenu(sc, 1, 2);

            CarroSedan.TipoTransmision transmision;
            if (opTransmision == 1) {
                transmision = CarroSedan.TipoTransmision.MANUAL;
            } else {
                transmision = CarroSedan.TipoTransmision.AUTOMATICA;
            }

            v = new CarroSedan(placa, marca, modelo, precio, combustible, transmision);

        } else if (tipo == 2) {
            // --- CAMIONETA SUV ---

            System.out.println("Seleccione el tipo de tracción:");
            System.out.println("1. 4X2");
            System.out.println("2. 4X4");
            int opTraccion = validador.validarOpcionMenu(sc, 1, 2);

            CamionetaSUV.TipoTraccion traccion;
            if (opTraccion == 1) {
                traccion = CamionetaSUV.TipoTraccion._4x2;
            } else {
                traccion = CamionetaSUV.TipoTraccion._4x4;
            }

            System.out.print("Ingrese la capacidad del maletero (en litros): ");
            float capacidadMaletero = validador.validarDecimal(sc);

            v = new CamionetaSUV(placa, marca, modelo, precio, traccion, capacidadMaletero);
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

    //Buscar un vehículo por su placa -- Para el contrato
    public Vehiculo buscarVehiculo(String placa) {

        NodoVehiculo actual = cabeza;

        while (actual != null) {

            Vehiculo v = actual.getDato();

            if (v.getPlaca().equalsIgnoreCase(placa)) {
                return v;
            }

            actual = actual.getSiguiente();
        }
        return null;
    }

    //Buscar un vehículo por su placa -- Para el menú
    public Vehiculo buscarVehiculo(Scanner sc, Validador validador) {

        System.out.print("Ingrese la placa del vehículo: ");
        String placa = validador.validarTexto(sc, 1);

        NodoVehiculo actual = cabeza;

        while (actual != null) {

            Vehiculo v = actual.getDato();

            if (v.getPlaca().equalsIgnoreCase(placa)) {
                return v;
            }

            actual = actual.getSiguiente();
        }
        System.out.println("Vehículo con la placa " + placa + " no encontrado.");
        return null;
    }

    // Modificar un vehículo por su placa
    public String modificarVehiculo(Scanner sc, Validador validador) {

        System.out.print("Ingrese la placa del vehículo a modificar: ");
        String placa = validador.validarTexto(sc, 1);

        NodoVehiculo actual = cabeza;

        // Buscar el vehículo en la lista
        while (actual != null) {

            Vehiculo v = actual.getDato();

            if (v.getPlaca().equalsIgnoreCase(placa)) {

                System.out.println("Vehículo encontrado.");
                System.out.println("Ingrese los nuevos datos:");

                System.out.print("Nueva marca: ");
                v.setMarca(validador.validarTexto(sc, 2));

                System.out.print("Nuevo modelo (año): ");
                int modelo = validador.validarEntero(sc);
                v.setModelo(modelo);

                System.out.print("Nuevo precio diario: ");
                float precio = validador.validarDecimal(sc);

                v.setPrecioDiario(precio);

                // Modificar atributos específicos del vehículo
                if (v instanceof CarroSedan) {

                    CarroSedan sedan = (CarroSedan) v;

                    System.out.println("Seleccione el tipo de combustible:");
                    System.out.println("1. GASOLINA");
                    System.out.println("2. DIESEL");
                    System.out.println("3. ELECTRICO");
                    int opCombustible = validador.validarOpcionMenu(sc, 1, 3);

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

                    sedan.setTipoCombustible(combustible);

                    System.out.println("Seleccione el tipo de transmisión:");
                    System.out.println("1. MANUAL");
                    System.out.println("2. AUTOMATICA");
                    int opTransmision = validador.validarOpcionMenu(sc, 1, 2);

                    CarroSedan.TipoTransmision transmision;

                    if (opTransmision == 1) {
                        transmision = CarroSedan.TipoTransmision.MANUAL;
                    } else {
                        transmision = CarroSedan.TipoTransmision.AUTOMATICA;
                    }

                    sedan.setTransmision(transmision);

                } else if (v instanceof CamionetaSUV) {

                    CamionetaSUV suv = (CamionetaSUV) v;

                    System.out.println("Seleccione el tipo de tracción:");
                    System.out.println("1. 4X2");
                    System.out.println("2. 4X4");
                    int opTraccion = validador.validarOpcionMenu(sc, 1, 2);

                    CamionetaSUV.TipoTraccion traccion;

                    if (opTraccion == 1) {
                        traccion = CamionetaSUV.TipoTraccion._4x2;
                    } else {
                        traccion = CamionetaSUV.TipoTraccion._4x4;
                    }

                    suv.setTraccion(traccion);

                    System.out.print("Nueva capacidad del maletero (en litros): ");
                    float capacidadMaletero = validador.validarDecimal(sc);

                    suv.setCapacidadMaletero(capacidadMaletero);
                }

                return "Vehículo modificado correctamente.";
            }

            actual = actual.getSiguiente();
        }

        return "Vehículo con la placa " + placa + " no encontrado.";
    }

    // Eliminar un vehículo por su placa
    public String eliminarVehiculo(Scanner sc, Validador validador) {

        System.out.print("Ingrese la placa del vehículo a eliminar: ");
        String placa = validador.validarTexto(sc, 1);

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

    public NodoVehiculo getVehiculos() {
        return cabeza;
    }
}
