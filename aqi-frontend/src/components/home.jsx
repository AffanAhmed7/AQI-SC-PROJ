import React, { useState, useEffect } from "react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  BarChart,
  Bar,
} from "recharts";
import {
  FiWind,
  FiMap,
  FiStar,
  FiSettings,
  FiLogOut,
  FiMapPin,
  FiThermometer,
  FiDroplet,
  FiActivity,
  FiCrosshair,
  FiSun,
  FiCloud,
  FiCloudRain,
  FiCloudSnow,
  FiCloudLightning,
  FiUmbrella,
} from "react-icons/fi";
import { useNavigate, useLocation } from "react-router-dom";
import Cookies from "js-cookie";
import "./home.css";

export default function Home() {
  const navigate = useNavigate();
  const location = useLocation();

  const [cityInput, setCityInput] = useState("");
  const [loading, setLoading] = useState(false);

  const [data, setData] = useState({
    city: "Select a city",
    lat: 0,
    lon: 0,
    aqi: 0,
    temperature: 0,
    humidity: 0,
    pm25: 0,
    aqiTrend: [],
    pollutants: {},
    forecast: [],
  });

  const [healthMsg, setHealthMsg] = useState("");
  const [healthDetail, setHealthDetail] = useState("");
  const [healthList, setHealthList] = useState([]);
  const [healthColor, setHealthColor] = useState("#28a745");
  const [pm25Status, setPm25Status] = useState("");

  // ------------------------ FETCH DATA ------------------------
  const fetchCityData = async (cityName) => {
    if (!cityName.trim()) return alert("Enter a valid city name.");

    try {
      setLoading(true);

      const res = await fetch(
        `http://localhost:8080/api/data?city=${encodeURIComponent(
          cityName
        )}&units=metric`
      );

      if (!res.ok) {
        alert("City not found in backend.");
        setLoading(false);
        return;
      }

      const result = await res.json();

      // Extract backend fields
      const aqiData = result.aqi;
      const weather = result.currentWeather;
      const forecastRaw = result.forecast;

      // Map to frontend structure
      const mapped = {
        city: weather?.city || cityName,
        lat: aqiData?.latitude || 0,
        lon: aqiData?.longitude || 0,
        aqi: aqiData?.aqiIndex || 0,
        temperature: weather?.temperature || 0,
        humidity: weather?.humidity || 0,
        pm25: aqiData?.pm25 || 0,

        // Take PM2.5 values from forecast for 7-day trend
        aqiTrend:
          forecastRaw?.forecastList
            ?.slice(0, 7)
            ?.map((d) => Math.round(d.temperature)) || [],

        pollutants: {
          CO: aqiData?.co || 0,
          NO2: aqiData?.no2 || 0,
          SO2: aqiData?.so2 || 0,
          O3: aqiData?.o3 || 0,
        },
        forecast: (() => {
          if (!forecastRaw?.forecastList) return [];
          const daily = {};
          forecastRaw.forecastList.forEach((item) => {
            const date = item.dateTime.split(" ")[0];
            if (!daily[date]) {
              daily[date] = item;
            } else {
              const currentDiff = Math.abs(parseInt(daily[date].dateTime.split(" ")[1]) - 12);
              const newDiff = Math.abs(parseInt(item.dateTime.split(" ")[1]) - 12);
              if (newDiff < currentDiff) {
                daily[date] = item;
              }
            }
          });
          return Object.values(daily).slice(0, 5);
        })() || [],
      };

      setData(mapped);
      setLoading(false);
    } catch (err) {
      console.error(err);
      alert("Backend is not reachable!");
      setLoading(false);
    }
  };

  const handleSearch = () => fetchCityData(cityInput);

  const detectLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          const { latitude, longitude } = pos.coords;
          fetchCityData(`${latitude},${longitude}`);
        },
        (err) => alert("Unable to get your location.")
      );
    } else {
      alert("Geolocation is not supported.");
    }
  };

  // ------------------------ HEALTH LOGIC ------------------------
  useEffect(() => {
    const aqi = data.aqi;

    if (aqi < 2) {
      setHealthMsg("Air quality is good");
      setHealthDetail("It’s a great time for outdoor activities.");
      setHealthColor("#28a745");
      setHealthList([
        "✔ Outdoor activities are safe",
        "✔ Everyone can enjoy being outside",
      ]);
    } else if (aqi < 3) {
      setHealthMsg("Air quality is acceptable");
      setHealthDetail("Most people can enjoy outdoor activities normally.");
      setHealthColor("#ffc107");
      setHealthList([
        "✔ Outdoor activities are safe",
        "⚠ Sensitive individuals should take caution",
      ]);
    } else if (aqi <= 5) {
      setHealthMsg("Unhealthy for sensitive groups");
      setHealthDetail("Reduce prolonged outdoor exertion.");
      setHealthColor("#fd7e14");
      setHealthList([
        "⚠ Sensitive people should reduce outdoor activity",
        "⚠ Children and elderly should stay indoors",
      ]);
    } else {
      setHealthMsg("Unhealthy");
      setHealthDetail("Everyone may experience health effects.");
      setHealthColor("#dc3545");
      setHealthList([
        "❌ Limit outdoor activities",
        "❌ Avoid outdoor exercise",
      ]);
    }

    const pm = data.pm25;
    if (pm <= 12) setPm25Status("Good");
    else if (pm <= 35.4) setPm25Status("Moderate");
    else if (pm <= 55.4) setPm25Status("Unhealthy for sensitive groups");
    else if (pm <= 150.4) setPm25Status("Unhealthy");
    else setPm25Status("Very Unhealthy");
  }, [data]);

  // ------------------------ CHART DATA ------------------------
  const trendChartData = data.aqiTrend.map((v, i) => ({
    day: `Day ${i + 1}`,
    aqi: v,
  }));

  const pollutantChartData = Object.entries(data.pollutants).map(
    ([key, value]) => ({
      pollutant: key.toUpperCase(),
      value,
    })
  );

  // ------------------------ NAVIGATION ------------------------
  const goToMap = () => navigate("/mapview", { state: { cityData: data } });
  const goToFavorites = () =>
    navigate("/favorites", { state: { cityData: data } });
  const goToSettings = () =>
    navigate("/settings", { state: { cityData: data } });
  const signOut = () => {
    Cookies.remove("token");
    navigate("/");
  };

  const getWeatherIcon = (description) => {
    const desc = description?.toLowerCase() || "";
    if (desc.includes("clear") || desc.includes("sun")) return <FiSun className="weather-icon sunny" />;
    if (desc.includes("rain") || desc.includes("drizzle")) return <FiCloudRain className="weather-icon rainy" />;
    if (desc.includes("snow")) return <FiCloudSnow className="weather-icon snowy" />;
    if (desc.includes("storm") || desc.includes("thunder")) return <FiCloudLightning className="weather-icon stormy" />;
    if (desc.includes("cloud")) return <FiCloud className="weather-icon cloudy" />;
    return <FiUmbrella className="weather-icon" />;
  };

  return (
    <>
      <div className="home-wrapper">
        {/* Sidebar */}
        <aside className="sidebar">
          <div className="sidebar-top">
            <h2>
              <FiWind className="wind-icon" /> AQI Monitor
            </h2>
            <button className="sidebar-btn" onClick={goToMap}>
              <FiMap /> Map View
            </button>
            <button className="sidebar-btn" onClick={goToFavorites}>
              <FiStar /> Favorites
            </button>
            <button className="sidebar-btn" onClick={goToSettings}>
              <FiSettings /> Settings
            </button>
          </div>

          <div className="sidebar-bottom">
            <button className="signout-btn" onClick={signOut}>
              <FiLogOut /> Sign Out
            </button>
          </div>
        </aside>

        {/* Main Dashboard */}
        <main className="dashboard">
          {/* Search */}
          <div className="search-section">
            <h1>Search Location</h1>
            <p>Enter a city name or detect your current location</p>

            <div className="search-box">
              <input
                type="text"
                placeholder="Search for a city..."
                value={cityInput}
                onChange={(e) => setCityInput(e.target.value)}
              />
              <button onClick={handleSearch} disabled={loading}>
                {loading ? "Loading..." : "Search"}
              </button>

              <button className="detect-btn" onClick={detectLocation}>
                <FiCrosshair /> Detect My Location
              </button>
            </div>
          </div>

          {/* Stats Grid */}
          <div className="stats-grid">
            <div className="info-card aqi-box">
              <p className="card-label">
                <FiMapPin /> {data.city}
              </p>
              <h2 className="aqi-number">{data.aqi}</h2>
              <span className="aqi-tag" style={{ background: healthColor }}>
                {healthMsg}
              </span>
            </div>

            <div className="info-card">
              <p className="card-label">
                <FiThermometer /> Temperature
              </p>
              <h2>{data.temperature}°C</h2>
            </div>

            <div className="info-card">
              <p className="card-label">
                <FiDroplet /> Humidity
              </p>
              <h2>{data.humidity}%</h2>
            </div>

            <div className="info-card">
              <p className="card-label">
                <FiActivity /> PM2.5
              </p>
              <h2>{data.pm25} µg/m³</h2>
              <p>{pm25Status}</p>
            </div>
          </div>

          {/* Charts */}
          <div className="charts-row">
            <div className="chart-card">
              <h2>7-Day Temperature Trend</h2>
              <LineChart width={450} height={250} data={trendChartData}>
                <XAxis dataKey="day" />
                <YAxis />
                <Tooltip />
                <CartesianGrid strokeDasharray="3 3" />
                <Line
                  type="monotone"
                  dataKey="aqi"
                  stroke="#007b83"
                  strokeWidth={3}
                />
              </LineChart>
            </div>

            <div className="chart-card">
              <h2>Pollutants</h2>
              <BarChart width={450} height={250} data={pollutantChartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="pollutant" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="value" fill="#007b83" />
              </BarChart>
            </div>
          </div>



          {/* 5-Day Forecast */}
          {data.forecast.length > 0 && (
            <div className="forecast-section">
              <h2><FiSun /> 5-Day Forecast</h2>
              <div className="forecast-grid">
                {data.forecast.map((day, idx) => {
                  const dateObj = new Date(day.dateTime);
                  const dayName = dateObj.toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' });
                  return (
                    <div className="forecast-card" key={idx}>
                      <div className="forecast-day">{dayName}</div>
                      <div className="weather-icon-wrapper">
                        {getWeatherIcon(day.weather)}
                      </div>
                      <div className="forecast-temp">{Math.round(day.temperature)}°C</div>
                      <div style={{ fontSize: "13px", opacity: 0.8, marginTop: "4px" }}>{day.weather}</div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {/* Health Recommendations */}
          <div className="health-section">
            <h2>
              <FiActivity /> Health Recommendations
            </h2>
            <div
              className="health-box"
              style={{ background: healthColor, color: "#fff" }}
            >
              <h3>{healthMsg}</h3>
              <p>{healthDetail}</p>
            </div>

            <ul className="health-list">
              {healthList.map((item, i) => (
                <li key={i}>{item}</li>
              ))}
            </ul>
          </div>
        </main >
      </div >

      {/* Footer */}
      < footer className="site-footer" >
        <p>© MIRA. All Rights Reserved</p>
      </footer >
    </>
  );
}
