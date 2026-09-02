import { useState, useEffect } from "react";
import { tripApi, busApi } from "../services/api";

export default function DashboardPage() {
  const [trips, setTrips] = useState<any[]>([]);
  const [buses, setBuses] = useState<any[]>([]);
  useEffect(() => {
    tripApi.getAll(0).then(r => setTrips(r.data.content || [])).catch(()=>{});
    busApi.getAll(0).then(r => setBuses(r.data.content || [])).catch(()=>{});
  }, []);
  return (
    <div>
      <h1 style={{marginBottom:"1rem"}}>Dashboard</h1>
      <h2>Active Buses</h2>
      <div className="grid">
        {buses.map((b:any)=>(
          <div className="card" key={b.id}><strong>{b.busNumber}</strong> - {b.name}<br/><small>{b.vehicleType} | {b.capacity} seats</small></div>
        ))}
        {buses.length===0&&<p style={{color:"gray"}}>No buses available</p>}
      </div>
      <h2 style={{marginTop:"2rem"}}>Upcoming Trips</h2>
      <div className="grid">
        {trips.map((t:any)=>(
          <div className="card" key={t.id}><strong>{t.routeNumber}</strong> - Bus {t.busNumber}<br/><small>{t.status} | {t.availableSeats} seats</small></div>
        ))}
        {trips.length===0&&<p style={{color:"gray"}}>No trips available</p>}
      </div>
    </div>
  );
}
