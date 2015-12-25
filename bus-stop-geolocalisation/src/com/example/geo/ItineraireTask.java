package com.example.geo;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * ItineraireTask
 * @author Ludovic
 */
public class ItineraireTask extends AsyncTask<Void, Integer, PolylineOptions> {
  /*******************************************************/
  /** CONSTANTES.
 /*******************************************************/
  private static final String TOAST_MSG = "Recherche de ligne(s) disponibles...";
  private static final String TOAST_ERR_MAJ = "Impossible de trouver un itinéraire";

  /*******************************************************/
  /** ATTRIBUTS.
 /*******************************************************/
  private Context context;
  private GoogleMap gMap;
  private String editDepart;
  private String editArrivee; 
  private final ArrayList<LatLng> lstLatLng = new ArrayList<LatLng>();
  //private DatabaseBackend databaseBackend ;
  private ProgressDialog mProgressDialog;

  /*******************************************************/
  /** METHODES / FONCTIONS.
 /*******************************************************/
  /**
  * Constructeur.
  * @param context
  * @param gMap
  * @param editDepart
  * @param editArrivee
  */
  public ItineraireTask(final Context context, final String editDepart, final String editArrivee) {
      this.context = context;
      this.gMap= gMap;
      this.editDepart = editDepart;
      this.editArrivee = editArrivee;;
  }

  /**
  * {@inheritDoc}
  */
  @Override
    protected void onPreExecute() {
	  
      Toast.makeText(context, TOAST_MSG, Toast.LENGTH_LONG).show();
  }

  /***
  * {@inheritDoc}
  */
  @Override
  protected PolylineOptions doInBackground(Void... params) {
	  
      try {
          //Construction de l'url à appeler         
          final StringBuilder url = new StringBuilder("http://maps.googleapis.com/maps/api/directions/xml?sensor=false&language=fr");
          url.append("&origin=");
          url.append(editDepart.replace(' ', '+'));
          url.append("&destination=");
          url.append(editArrivee.replace(' ', '+'));

          //Appel du web service
          final InputStream stream = new URL(url.toString()).openStream();

          //Traitement des données
          final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
          documentBuilderFactory.setIgnoringComments(true);

          final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

          final Document document = documentBuilder.parse(stream);
          document.getDocumentElement().normalize();

          //On récupère d'abord le status de la requête
          final String status = document.getElementsByTagName("status").item(0).getTextContent();
          if(!"OK".equals(status)) {
              return null;
          }

          //On récupère les steps
          final Element elementLeg = (Element) document.getElementsByTagName("leg").item(0);
          final NodeList nodeListStep = elementLeg.getElementsByTagName("step");
          final int length = nodeListStep.getLength();

          for(int i=0; i<length; i++) {        
              final Node nodeStep = nodeListStep.item(i);

              if(nodeStep.getNodeType() == Node.ELEMENT_NODE) {
                  final Element elementStep = (Element) nodeStep;

                  //On décode les points du XML
                  decodePolylines(elementStep.getElementsByTagName("points").item(0).getTextContent());
              }
          }
          
        //On déclare le polyline, c'est-à-dire le trait (ici bleu) que l'on ajoute sur la carte pour tracer l'itinéraire
          final PolylineOptions polylines = new PolylineOptions();
          polylines.color(Color.BLUE).width(12).geodesic(true);
          
          

          //On construit le polyline
          for(final LatLng latLng : lstLatLng) {
                  polylines.add(latLng);
                  
          }

          return polylines ;          
      }
      
      
      catch(final Exception e) {
          return null;
      }
      
      
  }

  /**
  * Méthode qui décode les points en latitudes et longitudes
  * @param points
  */
  private void decodePolylines(final String encodedPoints) {
      int index = 0;
      int lat = 0, lng = 0;

      while (index < encodedPoints.length()) {
          int b, shift = 0, result = 0;

          do {
              b = encodedPoints.charAt(index++) - 63;
              result |= (b & 0x1f) << shift;
              shift += 5;
          } while (b >= 0x20);

          int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
          lat += dlat;
          shift = 0;
          result = 0;

          do {
              b = encodedPoints.charAt(index++) - 63;
              result |= (b & 0x1f) << shift;
              shift += 5;
          } while (b >= 0x20);

          int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
          lng += dlng;

          lstLatLng.add(new LatLng((double)lat/1E5, (double)lng/1E5));
      }
  }

  /**
  * {@inheritDoc}
  */
  @Override
  protected void onPostExecute(final PolylineOptions result) { 
	  
	  super.onPostExecute(result);
	  
  }
}
