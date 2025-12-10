import React from "react";
import { useNavigate } from "react-router-dom";  // <-- ADD THIS
import "./Hero.css";

export default function Hero() {
  const navigate = useNavigate(); // <-- create navigate function

  return (
    <section className="hero">
      <div className="stars"></div>

      <div className="hero-content">
        <h1>Monitor Air Quality Anywhere</h1>
        <p>Real-time data. Global coverage. Health insights.</p>

        <div className="wind-icon">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="60"
            height="60"
            viewBox="0 0 24 24"
            fill="none"
            stroke="white"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M4 12h16M4 6h16M4 18h12" />
          </svg>
        </div>

        <div className="hero-buttons">
          <button
            className="btn signup"
            onClick={() => navigate("/signup")}
          >
            Sign Up
          </button>

          <button
            className="btn signin"
            onClick={() => navigate("/signin")} // we will create later
          >
            Sign In
          </button>
        </div>
      </div>
    </section>
  );
}
