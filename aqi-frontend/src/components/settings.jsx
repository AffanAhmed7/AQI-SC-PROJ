import React, { useEffect, useState } from "react";
import Cookies from "js-cookie";
import "./settings.css";

// ✅ Local default avatar
import defaultAvatar from "./OIP.jpg"; // make sure this file is in the same folder

const Settings = () => {
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
  const [profilePic, setProfilePic] = useState("");
  const [theme, setTheme] = useState(Cookies.get("theme") || "light");

  const [newPass, setNewPass] = useState("");
  const [confirmPass, setConfirmPass] = useState("");

  useEffect(() => {
    const storedName = Cookies.get("fullName") || "New User";
    const storedEmail = Cookies.get("email") || "";
    const storedUser = Cookies.get("username") || "user123";
    const storedPic = Cookies.get("profilePic") || "";

    setFullName(storedName);
    setEmail(storedEmail);
    setUsername(storedUser);
    setProfilePic(storedPic || defaultAvatar);

    document.body.classList.toggle("dark-mode", theme === "dark");
  }, []);

  useEffect(() => {
    document.body.classList.toggle("dark-mode", theme === "dark");
  }, [theme]);

  const handleSaveName = () => {
    Cookies.set("fullName", fullName, { expires: 365 });
    Cookies.set("username", username, { expires: 365 });
    alert("Profile information saved.");
  };

  const handleThemeToggle = () => {
    const newTheme = theme === "light" ? "dark" : "light";
    setTheme(newTheme);
    Cookies.set("theme", newTheme, { expires: 365 });
    document.body.classList.toggle("dark-mode", newTheme === "dark");
  };

  const handleProfileUpload = (e) => {
    const file = e.target.files && e.target.files[0];
    if (!file) return;

    if (!file.type.startsWith("image/")) {
      alert("Please upload an image file.");
      return;
    }

    const reader = new FileReader();
    reader.onloadend = () => {
      const img = new Image();
      img.src = reader.result;

      img.onload = () => {
        const MAX_DIM = 200;
        let { width, height } = img;

        if (width > height) {
          if (width > MAX_DIM) {
            height = Math.round((height * MAX_DIM) / width);
            width = MAX_DIM;
          }
        } else {
          if (height > MAX_DIM) {
            width = Math.round((width * MAX_DIM) / height);
            height = MAX_DIM;
          }
        }

        const canvas = document.createElement("canvas");
        canvas.width = width;
        canvas.height = height;
        const ctx = canvas.getContext("2d");
        ctx.drawImage(img, 0, 0, width, height);

        const compressed = canvas.toDataURL("image/jpeg", 0.75);
        setProfilePic(compressed);
      };

      img.onerror = () => {
        alert("Could not load image. Try a different file.");
      };
    };

    reader.readAsDataURL(file);
  };

  const handleSaveProfilePic = () => {
    if (!profilePic) return alert("No image to save.");
    if (profilePic === defaultAvatar) {
      Cookies.remove("profilePic");
      alert("Default avatar active.");
      return;
    }
    try {
      Cookies.set("profilePic", profilePic, { expires: 365 });
      alert("Profile picture saved.");
    } catch (err) {
      console.error(err);
      alert("Image too large. Try a smaller file.");
    }
  };

  const handleDeleteProfile = () => {
    Cookies.remove("profilePic");
    setProfilePic(defaultAvatar);
    alert("Profile picture removed.");
  };

  const handlePasswordUpdate = () => {
    if (newPass.length < 4) {
      alert("Password must be at least 4 characters.");
      return;
    }
    if (newPass !== confirmPass) {
      alert("Passwords do not match.");
      return;
    }
    Cookies.set("password", newPass, { expires: 365 });
    alert("Password updated successfully!");
    setNewPass("");
    setConfirmPass("");
  };

  const onImgError = (e) => {
    if (e?.target) e.target.src = defaultAvatar;
  };

  return (
    <div className="settings-container">
      <div className="settings-header">
        <i className="icon">⚙</i>
        <h1>Settings</h1>
      </div>

      <div className="settings-card">
        <h2 className="card-title">Profile Picture</h2>
        <p className="card-subtext">Upload, preview, and save your avatar</p>

        <div className="profile-pic-section">
          <img
            src={profilePic || defaultAvatar}
            alt="Profile"
            className="profile-pic-preview"
            onError={onImgError}
          />

          <div className="pic-buttons">
            <input
              type="file"
              accept="image/*"
              onChange={handleProfileUpload}
              className="file-input"
            />

            <div className="btn-row">
              <button className="small-btn" onClick={handleSaveProfilePic}>
                Save
              </button>

              <button className="small-btn delete-btn" onClick={handleDeleteProfile}>
                Delete
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="settings-card">
        <h2 className="card-title">Profile Information</h2>
        <p className="card-subtext">Update your personal information</p>

        <label className="label">Full Name</label>
        <input
          type="text"
          className="input"
          value={fullName}
          onChange={(e) => setFullName(e.target.value)}
        />

        <label className="label">Username</label>
        <input
          type="text"
          className="input"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />

        <label className="label">Email</label>
        <input type="text" className="input disabled" value={email} disabled />
        <p className="disabled-text">Email cannot be changed</p>

        <button className="save-btn" onClick={handleSaveName}>
          Save Changes
        </button>
      </div>

      <div className="settings-card">
        <h2 className="card-title">Change Password</h2>
        <p className="card-subtext">Update your account password</p>

        <label className="label">New Password</label>
        <input
          type="password"
          className="input"
          placeholder="Enter new password"
          value={newPass}
          onChange={(e) => setNewPass(e.target.value)}
        />

        <label className="label">Confirm Password</label>
        <input
          type="password"
          className="input"
          placeholder="Re-enter password"
          value={confirmPass}
          onChange={(e) => setConfirmPass(e.target.value)}
        />

        <button className="save-btn" onClick={handlePasswordUpdate}>
          Update Password
        </button>
      </div>

      <div className="settings-card">
        <h2 className="card-title">Appearance</h2>
        <p className="card-subtext">Customize your interface theme</p>

        <div className="theme-toggle">
          <span>Current Theme: {theme === "light" ? "Light" : "Dark"}</span>
          <button className="save-btn" onClick={handleThemeToggle}>
            Toggle Theme
          </button>
        </div>
      </div>

    </div>
  );
};

export default Settings;
