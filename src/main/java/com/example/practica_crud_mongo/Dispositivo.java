package com.example.practica_crud_mongo;

public class Dispositivo {
    private String mac, marca, modelo, tipo;

    public Dispositivo(String mac, String marca, String modelo, String tipo) {
        this.mac = mac;
        this.marca = marca;
        this.modelo = modelo;
        this.tipo = tipo;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Dispositivo{" +
                "mac='" + mac + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
