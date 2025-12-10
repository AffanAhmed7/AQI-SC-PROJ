import React, { useEffect } from "react";
import "leaflet/dist/leaflet.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Hero from "./components/Hero";
import Features from "./components/Features";
import Signup from "./components/signup";
import SignIn from "./components/signin";
import Home from "./components/home";
import MapView from "./components/mapview";
import Settings from "./components/settings";
import Favorites from "./components/favorites";

import Cookies from "js-cookie"; // For theme persistence
import "./App.css";
import "./components/global-theme.css"; // Import the global dark mode overrides

function App() {
  useEffect(() => {
    // Apply saved theme globally on initial load
    const savedTheme = Cookies.get("theme") || "light";
    document.body.classList.toggle("dark-mode", savedTheme === "dark");
  }, []);

  return (
    <Router>
      <div className="App">
        <Routes>
          <Route
            path="/"
            element={
              <>
                <Hero />
                <Features />
              </>
            }
          />
          <Route path="/signup" element={<Signup />} />
          <Route path="/signin" element={<SignIn />} />
          <Route path="/home" element={<Home />} />
          <Route path="/mapview" element={<MapView />} />
          <Route path="/favorites" element={<Favorites />} />
          <Route path="/settings" element={<Settings />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
