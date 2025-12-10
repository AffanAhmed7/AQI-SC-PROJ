import React, { useState } from "react";
import Cookies from "js-cookie";
import "./signin.css";

export default function SignIn() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();

    if (email && password) {
      Cookies.set("session_token", "dummy_token", { expires: 7 });
      window.location.href = "/home";
    } else {
      alert("Please enter email and password");
    }
  };

  return (
    <div className="signin-wrapper">
      <div className="signin-box">

        <h1 className="signin-title">Sign In</h1>

        {/* Form starts */}
        <form onSubmit={handleSubmit} className="signin-form">
          <input
            type="email"
            placeholder="Email Address"
            className="signin-input"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />

          <input
            type="password"
            placeholder="Password"
            className="signin-input"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <button type="submit" className="signin-btn">
            Continue
          </button>
        </form>
        {/* Form ends */}

        {/* Bottom link placed OUTSIDE form but inside box */}
        <p className="bottom-link">
          Don't have an account? <a href="/signup">Sign Up</a>
        </p>

      </div>
    </div>
  );
}
