package com.example.manosadomicilio.controller;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.*;

public class SupabaseClient {

    private static final String BASE_URL = "https://ipofxhlkuqrvqhcpnveu.supabase.co/rest/v1/";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imlwb2Z4aGxrdXFydnFoY3BudmV1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQzNjY5NDAsImV4cCI6MjA3OTk0Mjk0MH0.UbLE9bg6Zq3L45FOW4lLLGYdCJQ8FJXn9d6Y5TCsrII";

    private static final OkHttpClient client = new OkHttpClient();

    public void getUsers(Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "users") // la tabla que quieres
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static Call insertUser(String name, String email, String password) {

        String json = "{ " +
                "\"name\": \"" + name + "\"," +
                "\"email\": \"" + email + "\"," +
                "\"password\": \"" + password + "\"" +
                " }";

        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "usuarios")
                .post(body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        return client.newCall(request);
    }

    public static void loginUser(String email, String password, Callback callback) {

        String url = BASE_URL + "usuarios?email=eq." + email + "&password=eq." + password;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void getCategorias(Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "categorias?select=id,nombre")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void insertTrabajador(int usuarioId, int categoriaId, boolean disponibilidad, String descripcion, Callback callback) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("usuario_id", usuarioId);
            jsonObject.put("categoria_id", categoriaId);
            jsonObject.put("disponibilidad", disponibilidad);
            jsonObject.put("descripcion", (descripcion == null || descripcion.isEmpty()) ? JSONObject.NULL : descripcion);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "trabajadores")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void getTrabajadoresPorCategoria(int categoriaId, Callback callback) {

        String url = BASE_URL + "trabajadores?select=id,descripcion,disponibilidad,usuarios!usuario_id(name)&categoria_id=eq." + categoriaId;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void checkTrabajadorExiste(int usuarioId, Callback callback) {
        String url = BASE_URL + "trabajadores?usuario_id=eq." + usuarioId + "&select=id";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void getDireccionesUsuario(int usuarioId, Callback callback) {
        String url = BASE_URL + "direcciones_cliente?usuario_id=eq." + usuarioId + "&select=id,calle,numero,colonia,municipio";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void insertDireccion(int usuarioId, int zonaId, String calle, String numero, String colonia, String municipio, String estado, String pais, String referencias, Callback callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("usuario_id", usuarioId);
            jsonObject.put("zona_id", zonaId);
            jsonObject.put("calle", calle);
            jsonObject.put("numero", numero);
            jsonObject.put("colonia", colonia);
            jsonObject.put("municipio", municipio);
            jsonObject.put("estado", estado);
            jsonObject.put("pais", pais);
            jsonObject.put("referencias", referencias);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "direcciones_cliente")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void insertServicio(int usuarioId, int trabajadorId, int categoriaId, int direccionId, String fecha, String hora, String descripcion, Callback callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("usuario_id", usuarioId);
            jsonObject.put("trabajador_id", trabajadorId);
            jsonObject.put("categoria_id", categoriaId); // <-- DESCOMENTADO
            jsonObject.put("direccion_cliente_id", direccionId);
            jsonObject.put("fecha", fecha);
            jsonObject.put("hora_inicial", hora);
            jsonObject.put("descripcion", descripcion);
            jsonObject.put("estado", "pendiente");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "servicios")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void getServiciosPorUsuario(int usuarioId, Callback callback) {
        String url = BASE_URL + "servicios?usuario_id=eq." + usuarioId + "&select=*,trabajadores(usuarios(name))";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void getServicioPorId(int servicioId, Callback callback) {
        String url = BASE_URL + "servicios?id=eq." + servicioId + "&select=*,trabajadores(usuarios(name))&limit=1";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void updateServicioEstado(int servicioId, String nuevoEstado, Callback callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("estado", nuevoEstado);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "servicios?id=eq." + servicioId)
                .patch(body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void updateServicioCalificacion(int servicioId, int calificacion, String comentarios, Callback callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("calificacion_trabajador", calificacion);
            jsonObject.put("comentarios_cliente", comentarios);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "servicios?id=eq." + servicioId)
                .patch(body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void getZonas(Callback callback) {
        String url = BASE_URL + "zonas?select=id,nombre";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void isFavorito(int usuarioId, int trabajadorId, Callback callback) {
        String url = BASE_URL + "trabajadores_favoritos?usuario_id=eq." + usuarioId + "&trabajador_id=eq." + trabajadorId + "&select=id";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void addFavorito(int usuarioId, int trabajadorId, Callback callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("usuario_id", usuarioId);
            jsonObject.put("trabajador_id", trabajadorId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL + "trabajadores_favoritos")
                .post(body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void removeFavorito(int usuarioId, int trabajadorId, Callback callback) {
        String url = BASE_URL + "trabajadores_favoritos?usuario_id=eq." + usuarioId + "&trabajador_id=eq." + trabajadorId;

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Prefer", "return=minimal")
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void getFavoritos(int usuarioId, Callback callback) {
        String url = BASE_URL + "trabajadores_favoritos?usuario_id=eq." + usuarioId + "&select=trabajadores(*,usuarios(name),categorias(nombre))";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
