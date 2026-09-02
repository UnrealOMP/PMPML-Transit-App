import { useState, useEffect, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { routeApi, tripApi, fareApi, bookingApi, paymentApi } from "../services/api";

interface StopEntry {
  stopId: string;
  stopName: string;
  sequenceOrder: number;
  distanceFromStartKm: number;
  distanceToNextStopKm: number;
}

interface Route {
  id: string;
  routeNumber: string;
  name: string;
  stops: StopEntry[];
}

interface Trip {
  id: string;
  busNumber: string;
  routeNumber: string;
  routeId: string;
  scheduledDeparture: string;
  scheduledArrival: string;
  status: string;
  availableSeats: number;
}

type Step = "select-route" | "select-stops" | "confirm" | "payment" | "success";

export default function BookingPage() {
  const nav = useNavigate();
  const [step, setStep] = useState<Step>("select-route");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Data
  const [routes, setRoutes] = useState<Route[]>([]);
  const [trips, setTrips] = useState<Trip[]>([]);
  const [selectedRoute, setSelectedRoute] = useState<Route | null>(null);
  const [selectedTrip, setSelectedTrip] = useState<Trip | null>(null);
  const [sourceStopId, setSourceStopId] = useState("");
  const [destStopId, setDestStopId] = useState("");
  const [fare, setFare] = useState<any>(null);
  const [bookingId, setBookingId] = useState("");
  const [paymentId, setPaymentId] = useState("");

  // Load routes and trips
  useEffect(() => {
    setLoading(true);
    Promise.all([routeApi.getAll(0, 50), tripApi.getAll(0, 50)])
      .then(([r, t]) => {
        setRoutes(r.data.content || []);
        setTrips((t.data.content || []).filter((tr: Trip) => tr.status === "SCHEDULED"));
      })
      .catch(() => setError("Failed to load routes"))
      .finally(() => setLoading(false));
  }, []);

  // Filter trips for selected route
  const routeTrips = useMemo(
    () => (selectedRoute ? trips.filter((t) => t.routeId === selectedRoute.id) : []),
    [selectedRoute, trips]
  );

  // Source stop options (can't be the last stop)
  const sourceStops = useMemo(
    () => (selectedRoute?.stops ? selectedRoute.stops.slice(0, -1) : []),
    [selectedRoute]
  );

  // Destination options (must come after source)
  const destStops = useMemo(() => {
    if (!selectedRoute?.stops || !sourceStopId) return [];
    const srcIdx = selectedRoute.stops.findIndex((s) => s.stopId === sourceStopId);
    return selectedRoute.stops.slice(srcIdx + 1);
  }, [selectedRoute, sourceStopId]);

  // Calculate fare
  useEffect(() => {
    if (sourceStopId && destStopId && selectedTrip) {
      fareApi
        .calculate({ sourceStopId, destinationStopId: destStopId, tripId: selectedTrip.id })
        .then((r) => setFare(r.data))
        .catch(() => setFare(null));
    } else {
      setFare(null);
    }
  }, [sourceStopId, destStopId, selectedTrip]);

  const getStopName = (id: string) => {
    const stop = selectedRoute?.stops.find((s) => s.stopId === id);
    return stop?.stopName || "";
  };

  const handleBooking = async () => {
    if (!selectedTrip || !sourceStopId || !destStopId) return;
    setLoading(true);
    setError("");
    try {
      const key = crypto.randomUUID();
      const res = await bookingApi.create(
        { tripId: selectedTrip.id, sourceStopId, destinationStopId: destStopId },
        key
      );
      setBookingId(res.data.id);
      setStep("payment");
    } catch (err: any) {
      setError(err.response?.data?.message || "Booking failed");
    } finally {
      setLoading(false);
    }
  };

  const handlePayment = async () => {
    setLoading(true);
    setError("");
    try {
      const key = crypto.randomUUID();
      const payRes = await paymentApi.initiate(bookingId, key);
      setPaymentId(payRes.data.id);
      // Verify payment (mock provider auto-succeeds)
      await paymentApi.verify(payRes.data.id);
      setStep("success");
    } catch (err: any) {
      setError(err.response?.data?.message || "Payment failed");
    } finally {
      setLoading(false);
    }
  };

  const reset = () => {
    setStep("select-route");
    setSelectedRoute(null);
    setSelectedTrip(null);
    setSourceStopId("");
    setDestStopId("");
    setFare(null);
    setBookingId("");
    setPaymentId("");
    setError("");
  };

  const steps = ["Route", "Stops", "Confirm", "Pay", "Done"];

  return (
    <div style={{ maxWidth: 700, margin: "2rem auto" }}>
      <h1 style={{ marginBottom: "1.5rem" }}>Book a Ticket</h1>

      {/* Step indicator */}
      <div style={{ display: "flex", gap: "0.5rem", marginBottom: "2rem" }}>
        {steps.map((s, i) => {
          const stepNames: Step[] = ["select-route", "select-stops", "confirm", "payment", "success"];
          const currentIdx = stepNames.indexOf(step);
          const isActive = i === currentIdx;
          const isDone = i < currentIdx;
          return (
            <div key={s} style={{ flex: 1, textAlign: "center" }}>
              <div
                style={{
                  width: 32,
                  height: 32,
                  borderRadius: "50%",
                  background: isDone ? "#1b5e20" : isActive ? "#1a237e" : "#e0e0e0",
                  color: "white",
                  display: "inline-flex",
                  alignItems: "center",
                  justifyContent: "center",
                  fontWeight: "bold",
                  fontSize: "0.85rem",
                }}
              >
                {isDone ? "✓" : i + 1}
              </div>
              <div style={{ fontSize: "0.75rem", color: isActive ? "#1a237e" : "gray", marginTop: 4 }}>{s}</div>
            </div>
          );
        })}
      </div>

      {error && (
        <div style={{ color: "red", background: "#ffeef0", padding: "0.75rem", borderRadius: 4, marginBottom: "1rem" }}>
          {error}
        </div>
      )}

      {loading && <p style={{ color: "gray" }}>Loading...</p>}

      {/* STEP 1: Select Route */}
      {step === "select-route" && !loading && (
        <div className="card">
          <h3 style={{ marginBottom: "1rem" }}>Choose a Route</h3>
          {routes.length === 0 && <p style={{ color: "gray" }}>No routes available</p>}
          {routes.map((r) => (
            <div
              key={r.id}
              onClick={() => {
                setSelectedRoute(r);
                setStep("select-stops");
              }}
              style={{
                padding: "1rem",
                border: selectedRoute?.id === r.id ? "2px solid #1a237e" : "1px solid #e0e0e0",
                borderRadius: 8,
                marginBottom: "0.75rem",
                cursor: "pointer",
                background: selectedRoute?.id === r.id ? "#e8eaf6" : "white",
                transition: "all 0.15s",
              }}
            >
              <strong style={{ fontSize: "1.1rem" }}>
                {r.routeNumber} — {r.name}
              </strong>
              <div style={{ color: "gray", fontSize: "0.85rem", marginTop: 4 }}>
                {r.stops?.length || 0} stops · {r.stops?.[0]?.stopName} → {r.stops?.[r.stops.length - 1]?.stopName}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* STEP 2: Select Stops + Trip */}
      {step === "select-stops" && selectedRoute && (
        <div className="card">
          <button
            onClick={() => setStep("select-route")}
            style={{ background: "none", border: "none", color: "#1a237e", cursor: "pointer", marginBottom: "1rem", padding: 0 }}
          >
            ← Back to routes
          </button>
          <h3 style={{ marginBottom: "0.5rem" }}>
            {selectedRoute.routeNumber} — {selectedRoute.name}
          </h3>

          <div className="form-group">
            <label>From (Source Stop)</label>
            <select
              value={sourceStopId}
              onChange={(e) => {
                setSourceStopId(e.target.value);
                setDestStopId("");
              }}
              style={{ width: "100%", padding: "0.5rem", border: "1px solid #ddd", borderRadius: 4, fontSize: "1rem" }}
            >
              <option value="">Select source stop</option>
              {sourceStops.map((s) => (
                <option key={s.stopId} value={s.stopId}>
                  {s.stopName} ({s.distanceFromStartKm} km)
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>To (Destination Stop)</label>
            <select
              value={destStopId}
              onChange={(e) => setDestStopId(e.target.value)}
              disabled={!sourceStopId}
              style={{ width: "100%", padding: "0.5rem", border: "1px solid #ddd", borderRadius: 4, fontSize: "1rem", opacity: !sourceStopId ? 0.5 : 1 }}
            >
              <option value="">Select destination</option>
              {destStops.map((s) => (
                <option key={s.stopId} value={s.stopId}>
                  {s.stopName} ({s.distanceFromStartKm} km)
                </option>
              ))}
            </select>
          </div>

          {/* Select trip */}
          {sourceStopId && destStopId && (
            <div className="form-group">
              <label>Select a Trip</label>
              {routeTrips.length === 0 && <p style={{ color: "gray" }}>No upcoming trips on this route</p>}
              {routeTrips.map((t) => (
                <div
                  key={t.id}
                  onClick={() => setSelectedTrip(t)}
                  style={{
                    padding: "0.75rem 1rem",
                    border: selectedTrip?.id === t.id ? "2px solid #1a237e" : "1px solid #e0e0e0",
                    borderRadius: 6,
                    marginBottom: "0.5rem",
                    cursor: "pointer",
                    background: selectedTrip?.id === t.id ? "#e8eaf6" : "white",
                  }}
                >
                  <strong>Bus {t.busNumber}</strong> · {t.availableSeats} seats
                  <br />
                  <small style={{ color: "gray" }}>
                    Departs: {new Date(t.scheduledDeparture).toLocaleString()} · Arrives:{" "}
                    {new Date(t.scheduledArrival).toLocaleString()}
                  </small>
                </div>
              ))}
            </div>
          )}

          {fare && (
            <div
              style={{
                background: "#e8f5e9",
                padding: "1rem",
                borderRadius: 8,
                marginTop: "1rem",
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <div>
                <div style={{ fontSize: "0.85rem", color: "gray" }}>Fare</div>
                <div style={{ fontSize: "1.5rem", fontWeight: "bold", color: "#1b5e20" }}>₹{fare.fare}</div>
                <div style={{ fontSize: "0.8rem", color: "gray" }}>
                  {fare.distanceKm} km · {fare.vehicleType}
                </div>
              </div>
              <button
                className="btn btn-primary"
                onClick={() => setStep("confirm")}
                disabled={!selectedTrip}
              >
                Continue
              </button>
            </div>
          )}

          {!fare && sourceStopId && destStopId && (
            <p style={{ color: "gray", marginTop: "1rem" }}>Select a trip to calculate fare</p>
          )}
        </div>
      )}

      {/* STEP 3: Confirm Booking */}
      {step === "confirm" && selectedRoute && selectedTrip && fare && (
        <div className="card">
          <button
            onClick={() => setStep("select-stops")}
            style={{ background: "none", border: "none", color: "#1a237e", cursor: "pointer", marginBottom: "1rem", padding: 0 }}
          >
            ← Back
          </button>
          <h3 style={{ marginBottom: "1rem" }}>Confirm Booking</h3>

          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <tbody>
              <tr>
                <td style={{ padding: "0.5rem 0", color: "gray" }}>Route</td>
                <td style={{ padding: "0.5rem 0", textAlign: "right" }}>
                  <strong>{selectedRoute.routeNumber}</strong> — {selectedRoute.name}
                </td>
              </tr>
              <tr>
                <td style={{ padding: "0.5rem 0", color: "gray" }}>Bus</td>
                <td style={{ padding: "0.5rem 0", textAlign: "right" }}>{selectedTrip.busNumber}</td>
              </tr>
              <tr>
                <td style={{ padding: "0.5rem 0", color: "gray" }}>Departure</td>
                <td style={{ padding: "0.5rem 0", textAlign: "right" }}>
                  {new Date(selectedTrip.scheduledDeparture).toLocaleString()}
                </td>
              </tr>
              <tr>
                <td style={{ padding: "0.5rem 0", color: "gray" }}>From</td>
                <td style={{ padding: "0.5rem 0", textAlign: "right" }}>{getStopName(sourceStopId)}</td>
              </tr>
              <tr>
                <td style={{ padding: "0.5rem 0", color: "gray" }}>To</td>
                <td style={{ padding: "0.5rem 0", textAlign: "right" }}>{getStopName(destStopId)}</td>
              </tr>
              <tr style={{ borderTop: "2px solid #1a237e" }}>
                <td style={{ padding: "0.75rem 0", fontWeight: "bold" }}>Total Fare</td>
                <td style={{ padding: "0.75rem 0", textAlign: "right", fontSize: "1.5rem", fontWeight: "bold", color: "#1b5e20" }}>
                  ₹{fare.fare}
                </td>
              </tr>
            </tbody>
          </table>

          <button
            className="btn btn-primary"
            style={{ width: "100%", marginTop: "1.5rem", padding: "0.75rem" }}
            onClick={handleBooking}
            disabled={loading}
          >
            {loading ? "Booking..." : "Proceed to Payment"}
          </button>
        </div>
      )}

      {/* STEP 4: Payment */}
      {step === "payment" && (
        <div className="card" style={{ textAlign: "center" }}>
          <h3 style={{ marginBottom: "1rem" }}>Processing Payment</h3>
          <p style={{ color: "gray", marginBottom: "1.5rem" }}>Booking reference: {bookingId.slice(0, 8)}...</p>
          {loading ? (
            <div>
              <div style={{ fontSize: "2rem", marginBottom: "1rem" }}>⏳</div>
              <p>Processing your payment...</p>
            </div>
          ) : (
            <button className="btn btn-primary" style={{ padding: "0.75rem 2rem" }} onClick={handlePayment}>
              Pay ₹{fare?.fare || "—"} (Mock Payment)
            </button>
          )}
        </div>
      )}

      {/* STEP 5: Success */}
      {step === "success" && (
        <div className="card" style={{ textAlign: "center" }}>
          <div style={{ fontSize: "3rem", marginBottom: "1rem" }}>✅</div>
          <h2 style={{ color: "#1b5e20", marginBottom: "0.5rem" }}>Booking Confirmed!</h2>
          <p style={{ color: "gray", marginBottom: "1.5rem" }}>
            Your ticket has been generated. You can view it in My Tickets.
          </p>
          <div style={{ display: "flex", gap: "1rem", justifyContent: "center" }}>
            <button className="btn btn-primary" onClick={() => nav("/tickets")}>
              View My Tickets
            </button>
            <button
              className="btn"
              style={{ background: "#e0e0e0", color: "#333" }}
              onClick={reset}
            >
              Book Another
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
