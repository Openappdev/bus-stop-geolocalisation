package com.example.geo;

import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * This shows how to create a simple activity with a map and a marker on the
 * map.
 */
public class MapsActivity extends FragmentActivity implements
		OnMarkerClickListener, OnInfoWindowClickListener, OnMarkerDragListener,
		OnSeekBarChangeListener, OnMapReadyCallback {
	
	
	
	
	

	//Déclaration et création des poins 
	private static final LatLng ARRET_BUS_AGADIR = new LatLng(
			30.42777731607213, -9.59789838641882);
	private static final LatLng ARRET_BUS_2 = new LatLng(30.422473, -9.588331);
	private static final LatLng ARRET_BUS_3 = new LatLng(30.437091,
			-9.620969071);
	private static final LatLng ARRET_BUS_4 = new LatLng(30.4377183,
			-9.57857239);

	private static final LatLng ARRET_LIGNE2 = new LatLng(30.397664, -9.5318694);
	private static final LatLng ARRET_LIGNE3 = new LatLng(30.4449393,
			-9.5576817);
	
	// Declaration d'autres variables
	
	private PolylineOptions ligne_terminus_bus2, ligne_terminus_bus03;
	private static final String TOAST_ERR_MAJ = "Impossible de trouver un ou plusieurs ligne(s)";

	// private static final LatLng PERTH = new LatLng(30.465468, -9.466138);

	private GoogleMap mMap;
	
	// identificateur temoin de marqueur 

	private int markerId = 0;

	// Déclaration des marqueurs arrets bus
	private Marker arret_bus_1;
	private Marker arret_bus_2;
	private Marker arret_bus_3;
	private Marker arret_bus_4;
	private Button itineraireButton ;

	// Déclaration des différentes caméras de ligne
	public static final CameraPosition Camera_Agadir = new CameraPosition.Builder()
	.target(ARRET_BUS_AGADIR).zoom(11.5f).bearing(0).tilt(25).build();
	public static final CameraPosition Camera_Terminus_ligne_1 = new CameraPosition.Builder()
			.target(ARRET_LIGNE2).zoom(15.5f).bearing(0).tilt(25).build();
	public static final CameraPosition Camera_Terminus_ligne_2 = new CameraPosition.Builder()
			.target(ARRET_LIGNE3).zoom(15.5f).bearing(0).tilt(25).build();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_location_demo);

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		
		itineraireButton = (Button) findViewById(R.id.itineraireButton);
		
		// Création du bouton Rouge
		InitialisationDuBouton();

	}


	// Définition et création de la carte et ses composants (marqueurs)
	@Override
	public void onMapReady(GoogleMap map) {
		
		// Initialisation de la carte

		mMap = map;

		// Hide the zoom controls as the button panel will cover it.
		mMap.getUiSettings().setZoomControlsEnabled(false);

		// Ajout des marqueurs points à la carte.
		addMarkersToMap();
		
		map.setContentDescription("Map with lots of markers.");

		// Set listeners for marker events. See the bottom of this class for
		// their behavior.
		mMap.setOnMarkerClickListener(this);
		mMap.setOnInfoWindowClickListener(this);
		mMap.setOnMarkerDragListener(this);

		// Pan to see all markers in view.
		// Cannot zoom to bounds until the map has a size.
		final View mapView = getSupportFragmentManager().findFragmentById(
				R.id.map).getView();
		if (mapView.getViewTreeObserver().isAlive()) {
			mapView.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {
						@SuppressWarnings("deprecation")
						// We use the new method when supported
						@SuppressLint("NewApi")
						// We check which build version we are using.
						@Override
						public void onGlobalLayout() {
							LatLngBounds bounds = new LatLngBounds.Builder()
									.include(ARRET_BUS_AGADIR)
									.include(ARRET_BUS_2).include(ARRET_BUS_3)
									.include(ARRET_BUS_4).build();
							if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
								mapView.getViewTreeObserver()
										.removeGlobalOnLayoutListener(this);
							} else {
								mapView.getViewTreeObserver()
										.removeOnGlobalLayoutListener(this);
							}
							
							// Initialement la campéra est zoomée sur 50
							mMap.moveCamera(CameraUpdateFactory
									.newLatLngBounds(bounds, 50));
						}
					});
		}

	}

	// Methode chargée d'animer la caméra
	private void changeCamera(CameraUpdate update, CancelableCallback callback) {

		mMap.animateCamera(update, callback);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// Do nothing.
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// Do nothing.
	}

	//
	// Evénement déclenché lors des click sur les marqueurs.
	//

	@Override
	public boolean onMarkerClick(final Marker marker) {

		if (marker.equals(arret_bus_1)) {

			markerId = 1;
		} else if (marker.equals(arret_bus_2)) {
			markerId = 2;
		} else if (marker.equals(arret_bus_3)) {
			markerId = 3;
		} else {
			// cas arret_bus_4 
			markerId = 4;

		}

		return false;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Toast.makeText(this, "Click Info Window", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		// mTopText.setText("onMarkerDragStart");
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		// mTopText.setText("onMarkerDragEnd");
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		// mTopText.setText("onMarkerDrag.  Current Position: " +
		// marker.getPosition());
	}



	private void addMarkersToMap() {

		// Création et ajout des marqueurs des 4 arrêts Bus à la carte
		
		// Ajout de l'arret¨bus 1 "Arret_bus_Agadir" 
		arret_bus_1 = mMap.addMarker(new MarkerOptions()
				.position(ARRET_BUS_AGADIR)
				.title("Terminus")
				.snippet("Arrêt Bus 1")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

		// Ajout de l'arret¨bus 2
		arret_bus_2 = mMap.addMarker(new MarkerOptions()
				.position(ARRET_BUS_2)
				.title("Terminus")
				.snippet("Arrêt Bus 2")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.infoWindowAnchor(0.5f, 0.5f));

		// Ajout de l'arret¨bus 3
		arret_bus_3 = mMap.addMarker(new MarkerOptions().position(ARRET_BUS_3)
				.title("Terminus").snippet("Arrêt Bus 3").draggable(true));

		// Ajout de l'arret¨bus 4
		arret_bus_4 = mMap.addMarker(new MarkerOptions().position(ARRET_BUS_4)
				.title("Terminus").snippet("Arrêt Bus 4"));

	}
	
	public void InitialisationDuBouton() {

		

		// Début implémentation de l'événement Ecouteur click sur le bouton 
		itineraireButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (markerId == 0) {

					Toast.makeText(getBaseContext(),
							"Veuillez cliquer sur un arrêt de bus dabord !",
							Toast.LENGTH_SHORT).show();
				} else {
					// Construction des lignes 1 et 2
					ConstructionDesLignes();
					// Mise en mouvement des caméras sur la ligne 1 et 2
					AnimationCameraLigne();

				}
			}
		});
		// Fin implémentation de l'événement Ecouteur click sur le bouton Rouge

		// Fin de construction et initialisation du bouton Rouge
	}

	public void ConstructionDesLignes() {

		try {
			
			// N.B : la méthode "ItineraireTask(...)" est un webservice de Google API qui retourne une liste de points Latitude_Longitude 
		    //        sous forme de Polyline entre deux localitées sur la carte Google Maps 
			
			// construction de la ligne 1 "ligne_terminus_bus2" allant d'Agadir au "Terminus Bus 2, Agadir"
			ligne_terminus_bus2 = new ItineraireTask(this, "Agadir, Maroc",
					"Terminus Bus 2, Agadir, Maroc").execute().get();
			
			// construction de la ligne 2 "ligne_terminus_bus03" allant d'Agadir au "Terminus Bus 03, Agadir"
			ligne_terminus_bus03 = new ItineraireTask(this, "Agadir, Maroc",
					"Terminus bus 03,Agadir, Maroc").execute().get();


		} catch (InterruptedException e) {

				Toast.makeText(this, TOAST_ERR_MAJ, Toast.LENGTH_SHORT).show();

			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block

			Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();

		}
		
		// Déclaration des marqueurs d'arrêt de ligne

		// On déclare un marker vert pour l'arrêt de la ligne "ligne_terminus_bus2"
		final MarkerOptions  Terminus_Bus2 = new MarkerOptions();
		Terminus_Bus2.position(ligne_terminus_bus2.getPoints().get(
				ligne_terminus_bus2.getPoints().size() - 1));
		Terminus_Bus2.icon(
				BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
				.title("Ligne 1 - Terminus Bus 2").snippet("3,2 km - 2h 20mn - 12 DHN");
		
		// On déclare un marker vert pour l'arrêt de la ligne "ligne_terminus_bus03"
		final MarkerOptions Terminus_Bus03 = new MarkerOptions();
		Terminus_Bus03.position(ligne_terminus_bus03.getPoints().get(
				ligne_terminus_bus03.getPoints().size() - 1));
		Terminus_Bus03.icon(
				BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
				.title("Ligne 2 - Terminus Bus 03").snippet("2,3 km - 1h 40mn- 7 DHN");
			
		
		mMap.addPolyline(ligne_terminus_bus2); // "ligne_terminus_bus2" est un liste de point Latitude_Longitude
		mMap.addMarker(Terminus_Bus2);

		mMap.addPolyline(ligne_terminus_bus03); // "ligne_terminus_bus03" est un liste de point Latitude_Longitude
		mMap.addMarker(Terminus_Bus03);

	}

	public void AnimationCameraLigne() {

		// Explication des scénarios (mouvements) de camera
		
		// 1 -> la camera est zoomée sur Arrêt de la ligne 1 "Terminus_ligne_2"
		changeCamera(CameraUpdateFactory.newCameraPosition(Camera_Terminus_ligne_1),
				new CancelableCallback() {
					@Override
					public void onFinish() {
						// Après ça 
						// 2 -> la camera change ensuite de position et se dirige  sur Arrêt de la ligne 2 "Terminus_ligne_03"
						changeCamera(
								CameraUpdateFactory.newCameraPosition(Camera_Terminus_ligne_2),
								new CancelableCallback() {
									@Override
									public void onFinish() {

										// Enfin 
										// 3 -> la camera rechange de position et dimunie le niveau de zoom pour montrer tous les lignes trouvées (Terminus2 et Terminus03)
										//      pour cet arrêt de bus , ici Agadir
										changeCamera(CameraUpdateFactory
												.newCameraPosition(Camera_Agadir),
												new CancelableCallback() {
													@Override
													public void onFinish() {

													}

													@Override
													public void onCancel() {
														Toast.makeText(
																getBaseContext(),
																"Animation d'ensemble annulée !",
																Toast.LENGTH_SHORT).show();
													}
												});
									}

									@Override
									public void onCancel() {
										Toast.makeText(
												getBaseContext(),
												"Animation  annulée vers Terminus_ligne_03 !",
												Toast.LENGTH_SHORT).show();
									}
								});

					}

					@Override
					public void onCancel() {
						Toast.makeText(getBaseContext(),
								"Animation  annulée vers Terminus_ligne_02 !",
								Toast.LENGTH_SHORT).show();
					}
				});
	}


}
