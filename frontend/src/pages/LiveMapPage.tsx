import { useState, useEffect, useRef, useCallback } from "react";
import { busApi, stopApi, locationApi } from "../services/api";

// Pune coordinates
const PUNE_CENTER = { lat: 18.5204, lng: 73.8567 };

interface BusLocation {
  busId: string;
  busNumber: string;
  latitude: number;
  longitude: number;
  speed: number;
  routeNumber?: string;
}

interface Stop {
  id: string;
  name: string;
  latitude: number;
  longitude: number;
}

declare global {
  interface Window {
    L: any;
  }
}

export default function LiveMapPage() {
  const mapRef = useRef<HTMLDivElement>(null);
  const leafletMapRef = useRef<any>(null);
  const markersRef = useRef<Map<string, any>>(new Map());
  const stopMarkersRef = useRef<any[]>([]);
  const [busLocations, setBusLocations] = useState<BusLocation[]>([]);
  const [stops, setStops] = useState<Stop[]>([]);
  const [loading, setLoading] = useState(true);
  const [lastUpdate, setLastUpdate] = useState<Date>(new Date());
  const [mapReady, setMapReady] = useState(false);

  // Initialize Leaflet map
  useEffect(() => {
    if (!mapRef.current || leafletMapRef.current) return;

    const L = window.L;
    if (!L) {
      // Load Leaflet dynamically if not loaded
      const link = document.createElement("link");
      link.rel = "stylesheet";
      link.href = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css";
      document.head.appendChild(link);

      const script = document.createElement("script");
      script.src = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.js";
      script.onload = () => initMap();
      document.head.appendChild(script);
    } else {
      initMap();
    }

    function initMap() {
      const L = window.L;
      if (!mapRef.current || leafletMapRef.current) return;

      const map = L.map(mapRef.current, {
        center: [PUNE_CENTER.lat, PUNE_CENTER.lng],
        zoom: 13,
        zoomControl: true,
      });

      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
      }).addTo(map);

      leafletMapRef.current = map;
      setMapReady(true);
    }
  }, []);

  // Load stops once
  useEffect(() => {
    stopApi
      .getAll(0, 50)
      .then((r) => {
        const data = (r.data.content || [])
          .map((s: any) => ({
            id: s.id,
            name: s.name,
            latitude: parseFloat(s.latitude),
            longitude: parseFloat(s.longitude),
          }))
          .filter((s: Stop) => !isNaN(s.latitude) && !isNaN(s.longitude));
        setStops(data);
      })
      .catch(() => {});
  }, []);

  // Add stop markers to map
  useEffect(() => {
    if (!mapReady || !window.L || stops.length === 0) return;
    const L = window.L;
    const map = leafletMapRef.current;
    if (!map) return;

    // Clear old stop markers
    stopMarkersRef.current.forEach((m) => map.removeLayer(m));
    stopMarkersRef.current = [];

    const stopIcon = L.divIcon({
      className: "",
      html: '<div style="background:#ff6f00;width:10px;height:10px;border-radius:50%;border:2px solid white;box-shadow:0 1px 3px rgba(0,0,0,0.3);"></div>',
      iconSize: [10, 10],
      iconAnchor: [5, 5],
    });

    stops.forEach((stop) => {
      const marker = L.marker([stop.latitude, stop.longitude], { icon: stopIcon })
        .addTo(map)
        .bindPopup(`<strong>🚏 ${stop.name}</strong>`);
      stopMarkersRef.current.push(marker);
    });
  }, [mapReady, stops]);

  // Fetch bus locations
  const fetchBusLocations = useCallback(async () => {
    try {
      const busesRes = await busApi.getAll(0, 20);
      const buses = busesRes.data.content || [];
      const locations: BusLocation[] = [];

      await Promise.allSettled(
        buses.map(async (bus: any) => {
          try {
            const locRes = await locationApi.getBus(bus.id);
            const loc = locRes.data;
            if (loc && loc.latitude && loc.longitude) {
              locations.push({
                busId: bus.id,
                busNumber: bus.busNumber,
                latitude: parseFloat(loc.latitude),
                longitude: parseFloat(loc.longitude),
                speed: loc.speed || 0,
                routeNumber: bus.routeNumber,
              });
            }
          } catch {
            // No GPS data for this bus
          }
        })
      );

      setBusLocations(locations);
      setLastUpdate(new Date());

      // Update map markers
      if (mapReady && window.L && leafletMapRef.current) {
        const L = window.L;
        const map = leafletMapRef.current;

        const busIcon = L.divIcon({
          className: "",
          html: '<div style="background:#1a237e;color:white;width:32px;height:32px;border-radius:50%;display:flex;align-items:center;justify-content:center;font-size:16px;font-weight:bold;box-shadow:0 2px 6px rgba(0,0,0,0.3);border:2px solid white;">🚌</div>',
          iconSize: [32, 32],
          iconAnchor: [16, 16],
        });

        const newIds = new Set(locations.map((l) => l.busId));

        // Remove markers for buses no longer reporting
        markersRef.current.forEach((marker, id) => {
          if (!newIds.has(id)) {
            map.removeLayer(marker);
            markersRef.current.delete(id);
          }
        });

        // Add or update bus markers
        locations.forEach((bus) => {
          const pos = [bus.latitude, bus.longitude] as [number, number];
          if (markersRef.current.has(bus.busId)) {
            markersRef.current.get(bus.busId).setLatLng(pos);
          } else {
            const marker = L.marker(pos, { icon: busIcon })
              .addTo(map)
              .bindPopup(
                `<div>
                  <strong>🚌 ${bus.busNumber}</strong><br>
                  Speed: ${bus.speed} km/h
                  ${bus.routeNumber ? `<br>Route: ${bus.routeNumber}` : ""}
                </div>`
              );
            markersRef.current.set(bus.busId, marker);
          }
        });
      }
    } catch {
      // silently fail
    } finally {
      setLoading(false);
    }
  }, [mapReady]);

  useEffect(() => {
    fetchBusLocations();
    const interval = setInterval(fetchBusLocations, 10000);
    return () => clearInterval(interval);
  }, [fetchBusLocations]);

  return (
    <div>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1rem" }}>
        <div>
          <h1>Live Bus Tracking</h1>
          <p style={{ color: "gray", fontSize: "0.85rem" }}>
            {busLocations.length} buses tracked · Updated {lastUpdate.toLocaleTimeString()}
          </p>
        </div>
        <button className="btn" style={{ background: "#e0e0e0", color: "#333" }} onClick={fetchBusLocations}>
          🔄 Refresh
        </button>
      </div>

      <div className="card" style={{ padding: 0, overflow: "hidden" }}>
        <div ref={mapRef} style={{ height: 550, width: "100%", background: "#e8f4f8" }}></div>
      </div>

      {/* Legend */}
      <div style={{ display: "flex", gap: "1.5rem", marginTop: "1rem", justifyContent: "center" }}>
        <div style={{ display: "flex", alignItems: "center", gap: "0.4rem" }}>
          <div style={{ background: "#1a237e", width: 24, height: 24, borderRadius: "50%", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 12 }}>🚌</div>
          <span style={{ fontSize: "0.85rem" }}>Bus</span>
        </div>
        <div style={{ display: "flex", alignItems: "center", gap: "0.4rem" }}>
          <div style={{ background: "#ff6f00", width: 10, height: 10, borderRadius: "50%", border: "2px solid white", boxShadow: "0 1px 3px rgba(0,0,0,0.3)" }}></div>
          <span style={{ fontSize: "0.85rem" }}>Stop</span>
        </div>
      </div>

      {/* Bus list */}
      {busLocations.length > 0 && (
        <div style={{ marginTop: "1.5rem" }}>
          <h3>Tracked Buses</h3>
          <div className="grid">
            {busLocations.map((bus) => (
              <div className="card" key={bus.busId} style={{ padding: "1rem" }}>
                <strong>{bus.busNumber}</strong>
                <span style={{ marginLeft: "0.5rem", color: "gray" }}>
                  {bus.routeNumber && `Route ${bus.routeNumber}`}
                </span>
                <div style={{ fontSize: "0.85rem", color: "gray", marginTop: 4 }}>
                  {bus.latitude.toFixed(4)}, {bus.longitude.toFixed(4)} · {bus.speed} km/h
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {loading && busLocations.length === 0 && (
        <p style={{ color: "gray", textAlign: "center", marginTop: "1rem" }}>Loading bus positions...</p>
      )}
      {!loading && busLocations.length === 0 && (
        <p style={{ color: "gray", textAlign: "center", marginTop: "1rem" }}>
          No live GPS data yet. Stops are shown on the map. Send GPS updates via the API to track buses.
        </p>
      )}
    </div>
  );
}
