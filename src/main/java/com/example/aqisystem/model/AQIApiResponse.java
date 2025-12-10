package com.example.aqisystem.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AQIApiResponse {

    private Data data;

    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }

    public static class Data {
        private int aqi;
        private String category;
        private Pollutants pollutants;

        public int getAqi() { return aqi; }
        public void setAqi(int aqi) { this.aqi = aqi; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public Pollutants getPollutants() { return pollutants; }
        public void setPollutants(Pollutants pollutants) { this.pollutants = pollutants; }
    }

    public static class Pollutants {
        @JsonProperty("pm25") private double pm25;
        @JsonProperty("pm10") private double pm10;
        private double co;
        private double no;
        private double no2;
        private double o3;
        private double so2;
        private double nh3;

        // getters and setters for all
        public double getPm25() { return pm25; }
        public void setPm25(double pm25) { this.pm25 = pm25; }
        public double getPm10() { return pm10; }
        public void setPm10(double pm10) { this.pm10 = pm10; }
        public double getCo() { return co; }
        public void setCo(double co) { this.co = co; }
        public double getNo() { return no; }
        public void setNo(double no) { this.no = no; }
        public double getNo2() { return no2; }
        public void setNo2(double no2) { this.no2 = no2; }
        public double getO3() { return o3; }
        public void setO3(double o3) { this.o3 = o3; }
        public double getSo2() { return so2; }
        public void setSo2(double so2) { this.so2 = so2; }
        public double getNh3() { return nh3; }
        public void setNh3(double nh3) { this.nh3 = nh3; }
    }
}

