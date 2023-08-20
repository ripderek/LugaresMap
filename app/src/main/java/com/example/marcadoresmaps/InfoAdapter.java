package com.example.marcadoresmaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class InfoAdapter extends AppCompatActivity implements GoogleMap.InfoWindowAdapter {
    private static final String API_KEY = "AIzaSyC39iWbZDWCGeLftIRdIU8c96KdSnK0Org";
    private Context context;

    public InfoAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View infoView = LayoutInflater.from(context).inflate(R.layout.infoview, null);

        TextView titleTextView = infoView.findViewById(R.id.Titulo);
        TextView snippetTextView = infoView.findViewById(R.id.Info);
        ImageView Imagen=infoView.findViewById(R.id.Imagen);


        Bitmap foto = (Bitmap) marker.getTag();
        titleTextView.setText(marker.getTitle());
        snippetTextView.setText(marker.getSnippet());
        Imagen.setImageBitmap(foto);

        return infoView;
    }
    public void Cerrar(){
        View infoView = LayoutInflater.from(context).inflate(R.layout.infoview, null);
        infoView.setVisibility(View.INVISIBLE);
    }
}

