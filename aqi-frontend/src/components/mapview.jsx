import React from "react";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import { useLocation, useNavigate } from "react-router-dom";
import "leaflet/dist/leaflet.css";
import L from "leaflet";
import "./mapview.css";

// Fixing marker issue
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl:
    "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png",
  iconUrl:
    "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png",
  shadowUrl:
    "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png",
});

export default function MapView() {
  const location = useLocation();
  const navigate = useNavigate();

  // Extract safely
  const cityData =
    location.state?.cityData || {
      city: "New York",
      lat: 40.7128,
      lon: -74.006,
    };

  return (
    <div className="map-wrapper">
      <button
        onClick={() => navigate(-1)}
        style={{
          position: "absolute",
          zIndex: 1000,
          top: 10,
          left: 10,
          padding: "10px 15px",
          background: "#007b83",
          color: "#fff",
          border: "none",
          borderRadius: "8px",
          cursor: "pointer",
        }}
      >
        Back
      </button>

      <MapContainer
        center={[cityData.lat, cityData.lon]}
        zoom={13}
        style={{ height: "100%", width: "100%" }}
      >
        <TileLayer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          attribution="&copy; OpenStreetMap contributors"
        />

        <Marker position={[cityData.lat, cityData.lon]}>
          <Popup>{cityData.city}</Popup>
        </Marker>
      </MapContainer>
    </div>
  );
}
