// Ubicación: com/example/manosadomicilio/controller/DataParser.java
package com.example.manosadomicilio.controller;

import com.example.manosadomicilio.model.Categoria;
import com.example.manosadomicilio.model.DireccionCliente;
import com.example.manosadomicilio.model.Servicio;
import com.example.manosadomicilio.model.Trabajador;
import com.example.manosadomicilio.model.Usuario;
import com.example.manosadomicilio.model.Zona;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DataParser {

    public static Usuario parseUsuario(String jsonResponse) throws JSONException {
        JSONArray jsonArray = new JSONArray(jsonResponse);
        if (jsonArray.length() > 0) {
            JSONObject userObj = jsonArray.getJSONObject(0);
            int id = userObj.getInt("id");
            String nombre = userObj.optString("name", "Usuario");
            String email = userObj.optString("email", "");
            return new Usuario(id, nombre, email);
        }
        return null;
    }

    public static List<Trabajador> parseTrabajadores(String jsonResponse) throws JSONException {
        List<Trabajador> trabajadores = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonResponse);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            int id = obj.getInt("id");
            String descripcion = obj.optString("descripcion", "Sin descripción");
            int categoriaId = obj.optInt("categoria_id");

            String nombreUsuario = "Trabajador #" + id;
            if (obj.has("usuarios") && !obj.isNull("usuarios")) {
                JSONObject usuarioObj = obj.getJSONObject("usuarios");
                nombreUsuario = usuarioObj.optString("name", "Sin nombre");
            }

            Trabajador trabajador = new Trabajador(id, -1, categoriaId, true, descripcion);
            trabajador.setNombreUsuario(nombreUsuario);
            trabajadores.add(trabajador);
        }
        return trabajadores;
    }

    public static List<Servicio> parseServicios(String jsonResponse) throws JSONException {
        List<Servicio> servicios = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonResponse);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            int id = obj.getInt("id");
            int usuarioId = obj.getInt("usuario_id");
            int trabajadorId = obj.getInt("trabajador_id");
            int categoriaId = obj.getInt("categoria_id");
            int direccionClienteId = obj.getInt("direccion_cliente_id");
            String fecha = obj.getString("fecha");
            String horaInicial = obj.getString("hora_inicial");
            String descripcion = obj.getString("descripcion");
            String estado = obj.getString("estado");

            BigDecimal precio = null;
            if (!obj.isNull("precio")) {
                precio = new BigDecimal(obj.getString("precio"));
            }

            Integer calificacionTrabajador = null;
            if (!obj.isNull("calificacion_trabajador")) {
                calificacionTrabajador = obj.getInt("calificacion_trabajador");
            }

            Integer calificacionCliente = null;
            if (!obj.isNull("calificacion_cliente")) {
                calificacionCliente = obj.getInt("calificacion_cliente");
            }

            Servicio servicio = new Servicio(id, usuarioId, trabajadorId, categoriaId, direccionClienteId,
                    fecha, horaInicial, descripcion, estado, precio,
                    calificacionTrabajador, calificacionCliente);

            if (obj.has("trabajadores") && !obj.isNull("trabajadores")) {
                JSONObject trabajadorObj = obj.getJSONObject("trabajadores");
                if (trabajadorObj.has("usuarios") && !trabajadorObj.isNull("usuarios")) {
                    JSONObject usuarioObj = trabajadorObj.getJSONObject("usuarios");
                    servicio.setNombreTrabajador(usuarioObj.optString("name", "N/A"));
                }
            }

            servicios.add(servicio);
        }
        return servicios;
    }

    public static List<Trabajador> parseFavoritos(String jsonResponse) throws JSONException {
        List<Trabajador> favoritos = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonResponse);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject favoritoObj = jsonArray.getJSONObject(i);
            JSONObject trabajadorObj = favoritoObj.getJSONObject("trabajadores");

            int id = trabajadorObj.getInt("id");
            int usuarioId = trabajadorObj.getInt("usuario_id");
            int categoriaId = trabajadorObj.getInt("categoria_id");
            boolean disponibilidad = trabajadorObj.getBoolean("disponibilidad");
            String descripcion = trabajadorObj.getString("descripcion");

            Trabajador trabajador = new Trabajador(id, usuarioId, categoriaId, disponibilidad, descripcion);

            if (trabajadorObj.has("usuarios") && !trabajadorObj.isNull("usuarios")) {
                JSONObject usuarioObj = trabajadorObj.getJSONObject("usuarios");
                trabajador.setNombreUsuario(usuarioObj.optString("name", "N/A"));
            }

            if (trabajadorObj.has("categorias") && !trabajadorObj.isNull("categorias")) {
                JSONObject categoriaObj = trabajadorObj.getJSONObject("categorias");
                // Asumiendo que quieres guardar el nombre de la categoría en el modelo Trabajador
                // necesitarás añadir un campo y sus getter/setter en el modelo.
            }

            favoritos.add(trabajador);
        }
        return favoritos;
    }

    public static List<Categoria> parseCategorias(String jsonResponse) throws JSONException {
        List<Categoria> categorias = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonResponse);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            int id = obj.getInt("id");
            String nombre = obj.getString("nombre");
            String descripcion = obj.getString("descripcion");
            categorias.add(new Categoria(id, nombre, descripcion));
        }
        return categorias;
    }

    public static List<DireccionCliente> parseDirecciones(String jsonResponse) throws JSONException {
        List<DireccionCliente> direcciones = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonResponse);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            int id = obj.getInt("id");
            String calle = obj.optString("calle", "N/A");
            String numero = obj.optString("numero", "S/N");
            String colonia = obj.optString("colonia", "");
            String municipio = obj.optString("municipio", "");

            DireccionCliente direccion = new DireccionCliente(id, 0, 0, calle, numero, colonia, "", "", municipio, "");
            direcciones.add(direccion);
        }
        return direcciones;
    }

    public static List<Zona> parseZonas(String jsonResponse) throws JSONException {
        List<Zona> zonas = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonResponse);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            int id = obj.getInt("id");
            String nombre = obj.getString("nombre");
            zonas.add(new Zona(id, nombre));
        }
        return zonas;
    }
}
