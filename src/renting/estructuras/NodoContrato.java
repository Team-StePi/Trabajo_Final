package renting.estructuras;

import renting.modelo.ContratoRenting;

public class NodoContrato {

    private ContratoRenting dato;
    private NodoContrato siguiente;

    public NodoContrato(ContratoRenting dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public ContratoRenting getDato() {
        return dato;
    }

    public NodoContrato getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoContrato siguiente) {
        this.siguiente = siguiente;
    }
}
