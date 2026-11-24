package renting.servicio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import renting.estructuras.NodoClientes;
import renting.estructuras.NodoContrato;
import renting.estructuras.NodoVehiculo;
import renting.modelo.Cliente;
import renting.modelo.ContratoRenting;
import renting.modelo.Vehiculo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InformeService {
    private final Gson gson;
    private ClienteService clienteService;
    private VehiculoService vehiculoService;
    private ContratoService contratoService;

    public InformeService() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
    }

    public InformeService(ClienteService clienteService,
                          VehiculoService vehiculoService,
                          ContratoService contratoService) {
        this();
        this.clienteService = clienteService;
        this.vehiculoService = vehiculoService;
        this.contratoService = contratoService;
    }

    public void imprimir(Cliente cliente) {
        System.out.println(cliente == null ? "" : gson.toJson(cliente));
    }

    public void imprimir(Vehiculo vehiculo) {
        System.out.println(vehiculo == null ? "" : gson.toJson(vehiculo));
    }

    public void imprimir(ContratoRenting contrato) {
        System.out.println(contrato == null ? "" : gson.toJson(contrato));
    }

    public String imprimirInformeGeneral() {
        return imprimirInformeGeneral(clienteService, vehiculoService, contratoService);
    }

    public String imprimirInformeGeneral(ClienteService clienteService,
                                         VehiculoService vehiculoService,
                                         ContratoService contratoService) {
        Map<String, Object> informe = new LinkedHashMap<>();

        List<Cliente> clientes = new ArrayList<>();
        if (clienteService != null && clienteService.getClientes() != null) {
            NodoClientes actual = clienteService.getClientes();
            while (actual != null) {
                clientes.add(actual.getDato());
                actual = actual.getSiguiente();
            }
        }

        List<Vehiculo> vehiculos = new ArrayList<>();
        if (vehiculoService != null && vehiculoService.getVehiculos() != null) {
            NodoVehiculo actual = vehiculoService.getVehiculos();
            while (actual != null) {
                vehiculos.add(actual.getDato());
                actual = actual.getSiguiente();
            }
        }

        List<ContratoRenting> activos = new ArrayList<>();
        List<ContratoRenting> finalizados = new ArrayList<>();
        float totalIngresos = 0f;

        if (contratoService != null && contratoService.getContratos() != null) {
            NodoContrato actual = contratoService.getContratos();
            while (actual != null) {
                ContratoRenting contrato = actual.getDato();
                if (contrato != null) {
                    totalIngresos += contrato.getValorTotal();
                    if ("Activo".equalsIgnoreCase(contrato.getEstado())) {
                        activos.add(contrato);
                    } else if ("Finalizado".equalsIgnoreCase(contrato.getEstado())) {
                        finalizados.add(contrato);
                    }
                }
                actual = actual.getSiguiente();
            }
        }

        informe.put("clientes", clientes);
        informe.put("vehiculos", vehiculos);

        Map<String, Object> contratos = new LinkedHashMap<>();
        contratos.put("activos", activos);
        contratos.put("finalizados", finalizados);
        informe.put("contratos", contratos);

        informe.put("totalIngresos", totalIngresos);

        return gson.toJson(informe);


    }
}
