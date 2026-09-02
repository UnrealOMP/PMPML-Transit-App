import { useState, useEffect } from "react";
import { ticketApi } from "../services/api";

export default function TicketsPage() {
  const [tickets, setTickets] = useState<any[]>([]);
  useEffect(()=>{ ticketApi.getAll(0).then(r=>setTickets(r.data.content||[])).catch(()=>{}); },[]);
  return (
    <div>
      <h1 style={{marginBottom:"1rem"}}>My Tickets</h1>
      <div className="grid">
        {tickets.map((t:any)=>(
          <div className="card" key={t.id}>
            <h3>{t.ticketNumber}</h3>
            <p>{t.busNumber} - {t.routeNumber}</p>
            <p>{t.sourceStopName} to {t.destinationStopName}</p>
            <p>&#8377;{t.fare} | <span style={{color:t.status==="ACTIVE"?"green":"red"}}>{t.status}</span></p>
            <small>Issued: {new Date(t.issuedAt).toLocaleString()}</small>
          </div>
        ))}
        {tickets.length===0&&<p style={{color:"gray"}}>No tickets yet. Book a trip to get started!</p>}
      </div>
    </div>
  );
}
