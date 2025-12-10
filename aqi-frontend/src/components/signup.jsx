import React, { useState } from "react";
import Cookies from "js-cookie";
import { auth, googleProvider } from "../firebase"; 
import { signInWithPopup, GoogleAuthProvider } from "firebase/auth";
import { useNavigate } from "react-router-dom"; 
import "./signup.css"; 

export default function Signup() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate(); 

  const handleSubmit = (e) => { 
    e.preventDefault();

    if (!email || !password) {
      alert("Please enter both email and password");
      return;
    }

    Cookies.set("session_token", "dummy_token", { expires: 7 });
    navigate("/home");
  };

  const handleGoogleSignup = async () => {
    try {
      const result = await signInWithPopup(auth, googleProvider);

      // Get the credential and token
      const credential = GoogleAuthProvider.credentialFromResult(result);
      const token = credential.accessToken; // Firebase OAuth token
      const user = result.user;

      Cookies.set("session_token", token, { expires: 7 });
      navigate("/home");
    } catch (error) {
      console.error("Google Signup Error:", error);
      alert("Google signup failed. Try again.");
    }
  };

  return (
    <div className="signup-wrapper">
      <div className="signup-box">
        <h1 className="signup-title">Create Your Account</h1>

        <form onSubmit={handleSubmit} className="signup-form">
          <input
            type="email"
            placeholder="Email Address"
            className="signup-input"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <input
            type="password"
            placeholder="Password"
            className="signup-input"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <button type="submit" className="signup-btn">
            Continue
          </button>
        </form>

        <div className="divider">or</div>

        <button onClick={handleGoogleSignup} className="google-btn">
          <img
            src="https://www.svgrepo.com/show/475656/google-color.svg"
            className="google-icon"
            alt="Google Icon"
          />
          Continue with Google
        </button>

        <div className="bottom-link">
          Already have an account? <a href="/signin">Sign In</a>
        </div>
      </div>
    </div>
  );
}
