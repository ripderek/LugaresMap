package com.example.marcadoresmaps;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    GoogleMap Mapa;
    private RequestQueue requestQueue;
    private static final String API_KEY = "AIzaSyC39iWbZDWCGeLftIRdIU8c96KdSnK0Org";

    //-1.0541879047436529, -79.48528789344708
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        requestQueue = Volley.newRequestQueue(this);
    }
    //cuando el mapa esta listo
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Mapa = googleMap;
        //esta conectado al mapa
        Mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Mapa.getUiSettings().setZoomControlsEnabled(true);
        //Mover el mapa a una ubicacion
        CameraUpdate camUpd1 =
                CameraUpdateFactory
                        .newLatLngZoom(new LatLng(-1.012529191970843, -79.46946021380661), 19);
        Mapa.moveCamera(camUpd1);
        LatLng madrid = new LatLng(-1.012529191970843, -79.46946021380661);
        CameraPosition camPos = new CameraPosition.Builder()
                .target(madrid)
                .zoom(15)
                .bearing(10)
                .tilt(10)
                .build();
        CameraUpdate camUpd3 =
                CameraUpdateFactory.newCameraPosition(camPos);

        InfoAdapter infoWindowAdapter = new  InfoAdapter (this);
        Mapa.setInfoWindowAdapter(infoWindowAdapter);
        Mapa.animateCamera(camUpd3);
        Mapa.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        this.Mapa.clear();

        //dibujar el marcador
        LatLng punto = new LatLng(latLng.latitude, latLng.longitude);
        Mapa.addMarker(new MarkerOptions().position(punto));

        Mapa.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                .title("Marcador Actual"));
        //dibujar el rectagunlo
        this.drawRectangle(latLng, 500);

        //cargar los lugares
        this.Lugares(latLng, 500);
    }
    private void drawRectangle(LatLng center, double radius) {
        // Crear un objeto LatLngBounds.Builder
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        // Calcular las ubicaciones de los cuatro vértices
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        LatLng northwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 315);
        LatLng southeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 135);
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        // Crear un objeto PolygonOptions para el rectángulo
        PolygonOptions rectOptions = new PolygonOptions()
                .add(southeast) // Añadir el vértice inferior-izquierdo
                .add(northeast) // Añadir el vértice superior-izquierdo
                .add(northwest) // Añadir el vértice superior-derecho
                .add(southwest) // Añadir el vértice inferior-derecho
                .strokeColor(Color.GREEN) // Establecer el color del borde del rectángulo
                .fillColor(Color.TRANSPARENT); // Establecer el color de relleno del rectángulo (transparente)
        // Añadir el polígono al mapa
        Mapa.addPolygon(rectOptions);
    }
    public void Lugares(LatLng latLng, double radius){

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?fields=name&location=" + latLng.latitude + "," + latLng.longitude + "&radius="+radius+"&type=bar&key=" + API_KEY;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject place = results.getJSONObject(i);
                                String name = place.getString("name");
                                String placeId = place.getString("place_id");
                                String Ubicacion= place.getString("vicinity");
                                JSONObject geometry = place.getJSONObject("geometry");
                                JSONObject location = geometry.getJSONObject("location");
                                double placeLat = location.getDouble("lat");
                                double placeLng = location.getDouble("lng");
                                JSONArray photos = place.optJSONArray("photos");
                                if (photos != null && photos.length() > 0) {
                                    JSONObject firstPhoto = photos.getJSONObject(0);
                                    String photoReference = firstPhoto.getString("photo_reference");
                                    String detailsUrl = "https://maps.googleapis.com/maps/api/place/details/json?" +
                                            "fields=name%2Crating%2Cformatted_phone_number" +
                                            "&place_id=" + placeId +
                                            "&key=" + API_KEY;

                                    JsonObjectRequest detailsRequest = new JsonObjectRequest(Request.Method.GET, detailsUrl, null,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    try {
                                                        //String name = response.getJSONObject("result").getString("name");
                                                        String rating = response.getJSONObject("result").getString("rating");
                                                        String phoneNumber = response.getJSONObject("result").getString("formatted_phone_number");
                                                        String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                                                                "maxwidth=200" +
                                                                "&photo_reference=" + photoReference +
                                                                "&key=" + API_KEY;
                                                        ImageRequest imageRequest = new ImageRequest(photoUrl,
                                                                new Response.Listener<android.graphics.Bitmap>() {
                                                                    @Override
                                                                    public void onResponse(android.graphics.Bitmap response) {
                                                                        LatLng placeLatLng = new LatLng(placeLat, placeLng);
                                                                        MarkerOptions markerOptions = new MarkerOptions()
                                                                                .position(placeLatLng)
                                                                                .title(name)
                                                                                .snippet("Calificacion: " + rating + "\nTelefono: " + phoneNumber
                                                                                );
                                                                        Marker marker = Mapa.addMarker(markerOptions);
                                                                        marker.setTag(response);

                                                                    }
                                                                }, 0, 0, ImageView.ScaleType.CENTER_CROP, null,
                                                                new Response.ErrorListener() {
                                                                    @Override
                                                                    public void onErrorResponse(VolleyError error) {
                                                                        error.printStackTrace();
                                                                    }
                                                                });
                                                        requestQueue.add(imageRequest);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Log.i("Error Volley", "onErrorResponse: "+error.toString());
                                                }
                                            });
                                    requestQueue.add(detailsRequest);
                                }
                            }

                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(request);
    }
}