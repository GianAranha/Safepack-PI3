package br.edu.puccampinas.safepack.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import br.edu.puccampinas.safepack.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import br.edu.puccampinas.safepack.databinding.ActivityMapsNoLoginBinding
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.model.Marker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MapsNoLoginActivity : AppCompatActivity(), OnMapReadyCallback, OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsNoLoginBinding
    private val places = arrayListOf(
        PlaceNoLogin("PUCC Campus 1", LatLng(-22.8360456,-47.0564085),
            "Av. Reitor Benedito José Barreto Fonseca, H15 - Parque dos Jacarandás, Campinas - SP"),
        PlaceNoLogin("Oxxo PUCC", LatLng(-22.8363415,-47.0531125),
            "Av. Profa. Ana Maria Silvestre Adade, 607 - Parque das Universidades, Campinas - SP, 13086-130")
    )
    private lateinit var mapsLoginButton: Button
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsNoLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapsLoginButton = findViewById(R.id.mapsLoginButton)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        firestore = Firebase.firestore

        mapsLoginButton.setOnClickListener {
            val iLogin = Intent(this, MainActivity::class.java)
            startActivity(iLogin)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnInfoWindowClickListener(this)

        addMarkers(mMap)
    }

    private fun addMarkers(googleMap: GoogleMap) {
        /*
        places.forEach { place ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title("Informações do armário")
                    .position(place.latLng)
            )
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(places[0].latLng, 15F))
        */
        mMap = googleMap

        firestore.collection("unidadeLocacao")
            .get()
            .addOnSuccessListener { unidades ->
                for (unidade in unidades) {
                    val geoPoint = unidade.getGeoPoint("geoLocalizacao")
                    if(geoPoint != null) {
                        val latLng = LatLng(geoPoint.latitude, geoPoint.longitude)
                        val marker = mMap.addMarker(
                            MarkerOptions().position(latLng).title("Informações do armário")
                        )
                    }
                }
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(places[0].latLng, 15F))
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erro ao recuperar dados: ${exception.message}")
            }
    }

    override fun onInfoWindowClick(marker: Marker) {
        val iInfoArmario = Intent(this, InfoArmarioActivity::class.java)
        startActivity(iInfoArmario)
    }

}

data class PlaceNoLogin (
    val name: String,
    val latLng: LatLng,
    val address: String,
)