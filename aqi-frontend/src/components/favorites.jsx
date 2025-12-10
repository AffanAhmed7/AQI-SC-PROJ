import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import Cookies from "js-cookie";
import { FiArrowLeft, FiStar, FiMapPin, FiThermometer, FiDroplet, FiActivity } from "react-icons/fi";
import "./favorites.css";

export default function Favorites() {
  const location = useLocation();
  const navigate = useNavigate();
  const currentCity = location.state?.cityData || null;

  // Get user email from cookies to scope favorites
  const userEmail = Cookies.get("userEmail") || "default";

  const [favorites, setFavorites] = useState([]);

  // Load favorites from cookies for this user
  useEffect(() => {
    const favCookie = Cookies.get(`favorites_${userEmail}`);
    if (favCookie) setFavorites(JSON.parse(favCookie));
  }, [userEmail]);

  // Save favorites to cookie
  const saveFavorites = (newFavs) => {
    Cookies.set(`favorites_${userEmail}`, JSON.stringify(newFavs), { expires: 30 });
    setFavorites(newFavs);
  };

  // Add current city to favorites
  const addToFavorites = () => {
    if (!currentCity) return;
    if (favorites.find((fav) => fav.city === currentCity.city)) return;
    if (favorites.length >= 4) {
      alert("You can only add up to 4 favorites");
      return;
    }
    const newFavs = [...favorites, currentCity];
    saveFavorites(newFavs);
  };

  // Remove a favorite
  const removeFavorite = (cityName) => {
    const newFavs = favorites.filter((fav) => fav.city !== cityName);
    saveFavorites(newFavs);
  };

  // Go back to home with selected favorite
  const goToHome = (fav) => {
    navigate("/home", { state: { cityData: fav } });
  };

  return (
    <div className="favorites-wrapper">
      <div className="favorites-header">
        <button className="back-btn" onClick={() => navigate(-1)}>
          <FiArrowLeft /> Back
        </button>
        <h2>
          <FiStar /> My Favorite Locations
        </h2>
      </div>

      <p className="favorites-intro">
        Track your preferred cities’ air quality quickly. Click on a city to view detailed AQI and environmental data. You can save up to 4 favorite locations.
      </p>

      {/* Current city add option */}
      {currentCity && (
        <div className="current-city-card">
          <div className="city-info">
            <h3>{currentCity.city}</h3>
            <div className="readings">
              <span><FiActivity /> AQI: {currentCity.aqi}</span>
              <span><FiThermometer /> {currentCity.temperature}°C</span>
              <span><FiDroplet /> {currentCity.humidity}%</span>
              <span><FiActivity /> PM2.5: {currentCity.pm25} µg/m³</span>
            </div>
          </div>
          <button className="add-fav-btn" onClick={addToFavorites}>
            Add to Favorites
          </button>
        </div>
      )}

      {/* List of favorites */}
      <div className="favorites-list">
        {favorites.length === 0 && <p className="empty-msg">No favorites added yet.</p>}
        {favorites.map((fav, idx) => (
          <div key={idx} className="favorite-item">
            <div className="fav-info" onClick={() => goToHome(fav)}>
              <h4>{fav.city}</h4>
              <div className="fav-readings">
                <span><FiActivity /> {fav.aqi}</span>
                <span><FiThermometer /> {fav.temperature}°C</span>
                <span><FiDroplet /> {fav.humidity}%</span>
                <span><FiActivity /> PM2.5: {fav.pm25}</span>
              </div>
            </div>
            <button className="remove-btn" onClick={() => removeFavorite(fav.city)}>Remove</button>
          </div>
        ))}
      </div>
    </div>
  );
}
