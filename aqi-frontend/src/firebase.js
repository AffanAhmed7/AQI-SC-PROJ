// firebase.js
import { initializeApp } from "firebase/app";
import { getAuth, GoogleAuthProvider } from "firebase/auth";

const firebaseConfig = {
  apiKey: "AIzaSyB3ae9kIywu6Uost4H9xOqttRFeF2zc8HA",
  authDomain: "aqi-sc.firebaseapp.com",
  projectId: "aqi-sc",
  appId: "1:315804308924:web:02ba3733daa0ec490707b9"
};

const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
export const googleProvider = new GoogleAuthProvider();
