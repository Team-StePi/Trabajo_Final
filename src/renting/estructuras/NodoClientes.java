package renting.estructuras;

import renting.modelo.Cliente;

public class NodoClientes {

    private Cliente dato;
    private NodoClientes siguiente;

    public NodoClientes(Cliente dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public Cliente getDato() {
        return dato;
    }

    public NodoClientes getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoClientes siguiente) {
        this.siguiente = siguiente;
    }
}
