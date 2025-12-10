import React from "react";
import "./Features.css";

export default function Features() {
  return (
    <>
      <section className="features">
        <h2>Why Choose AQI Monitor?</h2>
        <div className="feature-boxes">
          <div className="feature">
            <h3>Global Coverage</h3>
            <p>Access real-time air quality data from thousands of monitoring stations worldwide.</p>
          </div>
          <div className="feature">
            <h3>Health Insights</h3>
            <p>Get personalized health recommendations based on current air quality conditions.</p>
          </div>
          <div className="feature">
            <h3>Historical Trends</h3>
            <p>Analyze air quality trends over time with interactive charts and visualizations.</p>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="features-footer">
        <p>© MIRA. All rights reserved.</p>
      </footer>
    </>
  );
}
