// KhidmatAI Screens 2: Result · Booking Detail · Bookings · Profile
const { useState, useEffect } = React;
const C = window.KT;

// ══════════════════════════════════════════════════════
// RESULT — SUCCESS
// ══════════════════════════════════════════════════════
function ResultSuccessScreen({ navigate, appState }) {
  const isEmergency = appState.urgency === 'emergency';
  const provider = window.MOCK_PROVIDER || {};
  const appt     = window.MOCK_APPOINTMENT || {};
  const steps    = window.MOCK_NEXT_STEPS || [];

  const [orbState, setOrbState]     = useState('thinking');
  const [showNotif, setShowNotif]   = useState(false);
  const [copyFlash, setCopyFlash]   = useState(false);

  useEffect(() => {
    setTimeout(() => setOrbState('done'), 400);
    setTimeout(() => setShowNotif(true), 3000);
    setTimeout(() => setShowNotif(false), 7500);
  }, []);

  const copyId = () => {
    setCopyFlash(true);
    setTimeout(() => setCopyFlash(false), 1200);
  };

  const bannerBg    = isEmergency ? C.Error       : C.SuccessLight;
  const bannerColor = isEmergency ? '#fff'         : C.TextPrimary;
  const bannerBdr   = isEmergency ? 'transparent'  : C.Success;

  return (
    <div style={{ height:'100%', display:'flex', flexDirection:'column', background:C.Background, position:'relative', overflow:'hidden' }}>

      {/* Push notification overlay */}
      {showNotif && (
        <div style={{ position:'absolute', top:10, left:12, right:12, zIndex:200, background:C.Surface, borderRadius:18, padding:'13px 16px', boxShadow:'0 8px 32px rgba(0,0,0,0.18)', animation:'notifSlide 0.4s ease-out', display:'flex', alignItems:'flex-start', gap:10 }}>
          <div style={{ width:36, height:36, borderRadius:10, background:`linear-gradient(135deg,${C.Primary},${C.PrimaryDark})`, display:'flex', alignItems:'center', justifyContent:'center', flexShrink:0 }}>
            <span style={{ fontSize:'18px' }}>✦</span>
          </div>
          <div style={{ flex:1 }}>
            <div style={{ fontSize:'12px', fontWeight:700, color:C.TextPrimary, marginBottom:2 }}>KhidmatAI</div>
            <div style={{ fontSize:'12px', color:C.TextSecondary, lineHeight:'1.5' }}>
              ⏰ Yaad dihani: Aapki AC service 1 ghante mein hai. Kamran Khan aa raha hai.
            </div>
          </div>
          <button onClick={() => setShowNotif(false)} style={{ background:'none', border:'none', color:C.TextSecondary, cursor:'pointer', fontSize:'16px', paddingTop:2 }}>×</button>
        </div>
      )}

      <div style={{ flex:1, overflowY:'auto', padding:'0 0 16px' }}>

        {/* Status Banner */}
        <div style={{ background:bannerBg, borderBottom:`3px solid ${bannerBdr}`, padding:'16px 16px 14px' }}>
          <div style={{ display:'flex', alignItems:'center', gap:10, marginBottom:6 }}>
            <AiOrb state={orbState} size={26} />
            <div style={{ fontSize:'16px', fontWeight:700, color:bannerColor }}>
              {isEmergency ? '🚨 Emergency Booking Confirmed!' : '✅ Booking Confirmed!'}
            </div>
          </div>
          <div style={{ fontSize:'13px', color: isEmergency ? 'rgba(255,255,255,0.85)' : C.TextSecondary, marginBottom:8 }}>
            {provider.name} will arrive at 10:30 AM
          </div>
          <button onClick={copyId} style={{ background:'none', border:'none', cursor:'pointer', padding:0, display:'flex', alignItems:'center', gap:5 }}>
            <span style={{ fontFamily:'monospace', fontSize:'11px', fontWeight:600, color: isEmergency ? 'rgba(255,255,255,0.7)' : C.TextSecondary }}>
              {appt.bookingId}
            </span>
            <span style={{ fontSize:'11px', color: copyFlash ? C.Success : (isEmergency ? 'rgba(255,255,255,0.55)' : C.Primary), transition:'color 0.3s' }}>
              {copyFlash ? '✓ Copied' : '⎘ Copy'}
            </span>
          </button>
        </div>

        <div style={{ padding:'0 16px' }}>

          {/* ── AI Decision Card ─────────────────────────── */}
          <div style={{ background:C.PrimaryLight, borderLeft:`4px solid ${C.Primary}`, borderRadius:14, padding:'14px 16px', marginTop:16, marginBottom:14 }}>
            <div style={{ fontSize:'11px', fontWeight:700, color:C.Primary, textTransform:'uppercase', letterSpacing:'0.6px', marginBottom:8, display:'flex', alignItems:'center', gap:6 }}>
              <span style={{ fontSize:'14px' }}>🤖</span> Kyun chuna? — AI Decision
            </div>
            <div style={{ fontSize:'13px', color:C.TextSecondary, lineHeight:'1.65', fontStyle:'italic', marginBottom:10 }}>
              "{provider.reasoning}"
            </div>
            <div style={{ display:'flex', gap:16, fontSize:'11px', color:C.TextSecondary, fontFamily:'monospace' }}>
              <span>Score: <strong style={{ color:C.TextPrimary }}>12.16</strong></span>
              <span>Ranked <strong style={{ color:C.TextPrimary }}>#1</strong> of 3 providers</span>
            </div>
          </div>

          {/* ── Provider Card ────────────────────────────── */}
          <div style={{ background:C.Surface, borderRadius:16, padding:'16px', boxShadow:'0 2px 10px rgba(0,0,0,0.07)', marginBottom:12 }}>
            <div style={{ display:'flex', alignItems:'flex-start', gap:12, marginBottom:14 }}>
              <Avatar name={provider.name} size={50} />
              <div style={{ flex:1 }}>
                <div style={{ fontSize:'15px', fontWeight:700, color:C.TextPrimary }}>{provider.name}</div>
                <div style={{ fontSize:'12px', color:C.TextSecondary, marginTop:2 }}>
                  {provider.service} · {provider.distanceKm}km away · {provider.experienceYears} yrs exp
                </div>
                <div style={{ display:'flex', alignItems:'center', gap:4, marginTop:4 }}>
                  <span style={{ color:C.Warning, fontSize:'13px' }}>★</span>
                  <span style={{ fontSize:'13px', fontWeight:600, color:C.TextPrimary }}>{provider.rating}</span>
                </div>
              </div>
            </div>
            <div style={{ display:'flex', gap:10 }}>
              <button style={{ flex:1, height:42, borderRadius:10, border:`1.5px solid ${C.Success}`, background:'transparent', color:C.Success, fontSize:'13px', fontWeight:600, cursor:'pointer', display:'flex', alignItems:'center', justifyContent:'center', gap:5 }}>
                📞 Call Now
              </button>
              <button style={{ flex:1, height:42, borderRadius:10, border:`1.5px solid ${C.Border}`, background:'transparent', color:C.TextPrimary, fontSize:'13px', fontWeight:600, cursor:'pointer', display:'flex', alignItems:'center', justifyContent:'center', gap:5 }}>
                💬 WhatsApp
              </button>
            </div>
          </div>

          {/* ── Map ─────────────────────────────────────── */}
          <div style={{ marginBottom:12 }}>
            <MapPlaceholder />
          </div>

          {/* ── Appointment Details ──────────────────────── */}
          <div style={{ background:C.Surface, borderRadius:16, padding:'16px', boxShadow:'0 2px 8px rgba(0,0,0,0.06)', marginBottom:12 }}>
            <div style={{ fontSize:'13px', fontWeight:700, color:C.TextPrimary, marginBottom:12, display:'flex', alignItems:'center', gap:6 }}>
              📅 Appointment Details
            </div>
            {[
              ['Booking ID', <span style={{ fontFamily:'monospace', fontSize:'12px' }}>{appt.bookingId}</span>],
              ['Time',       appt.timeDisplay],
              ['Location',   appt.address],
              ['Cost',       `PKR ${appt.costPerHour} / hr`],
            ].map(([label, val], i, arr) => (
              <div key={label} style={{ display:'flex', justifyContent:'space-between', alignItems:'center', paddingBottom:10, marginBottom: i<arr.length-1 ? 10 : 0, borderBottom: i<arr.length-1 ? `1px solid ${C.Border}` : 'none' }}>
                <span style={{ fontSize:'12px', color:C.TextSecondary }}>{label}</span>
                <span style={{ fontSize:'12px', fontWeight:600, color:C.TextPrimary }}>{val}</span>
              </div>
            ))}
          </div>

          {/* ── Next Steps ───────────────────────────────── */}
          <div style={{ fontSize:'15px', fontWeight:700, color:C.TextPrimary, marginBottom:10 }}>Agle Steps</div>
          <div style={{ display:'flex', flexDirection:'column', gap:8, marginBottom:16 }}>
            {steps.map(s => <NextStepCard key={s.id} step={s} emergency={isEmergency} />)}
          </div>

          {/* ── Bottom CTAs ───────────────────────────────── */}
          <button onClick={() => navigate('booking-detail')} style={{ width:'100%', height:48, borderRadius:13, border:`1.5px solid ${C.Primary}`, background:'transparent', color:C.Primary, fontSize:'14px', fontWeight:600, cursor:'pointer', marginBottom:10 }}>
            View Full Booking Details
          </button>
          <button onClick={() => navigate('home')} style={{ width:'100%', height:44, borderRadius:13, border:'none', background:'transparent', color:C.TextSecondary, fontSize:'14px', cursor:'pointer' }}>
            ← Back to Home
          </button>
        </div>
      </div>
    </div>
  );
}

// ══════════════════════════════════════════════════════
// RESULT — UNAVAILABLE
// ══════════════════════════════════════════════════════
function ResultUnavailableScreen({ navigate }) {
  return (
    <div style={{ height:'100%', display:'flex', flexDirection:'column', background:C.Background }}>
      {/* Banner */}
      <div style={{ background:C.WarningLight, borderBottom:`3px solid ${C.Warning}`, padding:'16px' }}>
        <div style={{ display:'flex', alignItems:'center', gap:10, marginBottom:6 }}>
          <AiOrb state="idle" size={24} />
          <div style={{ fontSize:'15px', fontWeight:700, color:C.TextPrimary }}>⚠️ Koi Provider Available Nahi</div>
        </div>
        <div style={{ fontSize:'12px', color:C.TextSecondary }}>
          Filhal is area mein AC Technician available nahi hai
        </div>
      </div>

      <div style={{ flex:1, overflowY:'auto', padding:'20px 16px' }}>
        {/* Illustration */}
        <div style={{ display:'flex', justifyContent:'center', marginBottom:20 }}>
          <svg width="160" height="140" viewBox="0 0 160 140" fill="none">
            <circle cx="80" cy="70" r="55" stroke={C.Border} strokeWidth="2" strokeDasharray="8 6"/>
            <circle cx="80" cy="70" r="36" stroke={C.Border} strokeWidth="1.5" strokeDasharray="5 5"/>
            <circle cx="80" cy="70" r="12" fill={C.Border}/>
            {/* Magnifying glass */}
            <circle cx="68" cy="58" r="22" stroke={C.TextSecondary} strokeWidth="3" fill="none" opacity="0.4"/>
            <line x1="84" y1="74" x2="100" y2="90" stroke={C.TextSecondary} strokeWidth="4" strokeLinecap="round" opacity="0.4"/>
            {/* X */}
            <line x1="60" y1="50" x2="76" y2="66" stroke={C.Error} strokeWidth="2.5" strokeLinecap="round" opacity="0.7"/>
            <line x1="76" y1="50" x2="60" y2="66" stroke={C.Error} strokeWidth="2.5" strokeLinecap="round" opacity="0.7"/>
          </svg>
        </div>

        {/* Error detail */}
        <div style={{ background:C.Surface, borderRadius:16, padding:'16px', boxShadow:'0 2px 8px rgba(0,0,0,0.06)', marginBottom:14 }}>
          <div style={{ fontSize:'14px', fontWeight:700, color:C.TextPrimary, marginBottom:8 }}>Kya hua?</div>
          <div style={{ fontSize:'13px', color:C.TextSecondary, lineHeight:'1.6', marginBottom:12 }}>
            G-13 ke qareeb koi verified provider nahi mila for AC Technician.
          </div>
          <div style={{ fontSize:'13px', fontWeight:600, color:C.TextPrimary, marginBottom:4 }}>Mashwara:</div>
          <div style={{ fontSize:'13px', color:C.TextSecondary, lineHeight:'1.6', fontStyle:'italic' }}>
            "Kuch der baad dobara try karein, ya kisi aur area ke liye search karein."
          </div>
        </div>

        {/* Next steps */}
        <div style={{ display:'flex', flexDirection:'column', gap:8, marginBottom:20 }}>
          {[
            { id:1, title:'Dobara try karein', description:'15-20 minute baad try karein, providers available ho sakte hain.', type:'action', action_label:'Retry', action_value:'retry' },
            { id:2, title:'Area change karein', description:'Neighboring areas mein bhi check karein jaise F-10 ya G-11.', type:'info', action_value:null, action_label:null },
          ].map(s => <NextStepCard key={s.id} step={s} />)}
        </div>

        <button onClick={() => navigate('home')} style={{ width:'100%', height:50, borderRadius:13, border:'none', background:C.Primary, color:'#fff', fontSize:'15px', fontWeight:600, cursor:'pointer', marginBottom:10, boxShadow:`0 4px 16px ${C.Primary}44` }}>
          🔄 Retry Request
        </button>
        <button onClick={() => navigate('home')} style={{ width:'100%', height:46, borderRadius:13, border:`1.5px solid ${C.Border}`, background:'transparent', color:C.TextPrimary, fontSize:'14px', cursor:'pointer' }}>
          Try Different Service
        </button>
      </div>
    </div>
  );
}

// ══════════════════════════════════════════════════════
// BOOKING DETAIL SCREEN
// ══════════════════════════════════════════════════════
function BookingDetailScreen({ navigate }) {
  const provider = window.MOCK_PROVIDER || {};
  const appt     = window.MOCK_APPOINTMENT || {};
  const steps    = window.MOCK_NEXT_STEPS || [];
  const trace    = window.MOCK_TRACE || [];
  const [traceOpen, setTraceOpen] = useState(false);

  return (
    <div style={{ height:'100%', display:'flex', flexDirection:'column', background:C.Background }}>
      {/* AppBar */}
      <div style={{ display:'flex', alignItems:'center', justifyContent:'space-between', padding:'10px 16px', background:C.Surface, borderBottom:`1px solid ${C.Border}`, flexShrink:0 }}>
        <button onClick={() => navigate('result-success')} style={{ background:'none', border:'none', cursor:'pointer', fontSize:'18px', padding:'4px 8px 4px 0', color:C.TextPrimary }}>←</button>
        <span style={{ fontSize:'15px', fontWeight:700, color:C.TextPrimary }}>Booking Detail</span>
        <button style={{ background:'none', border:'none', cursor:'pointer', fontSize:'18px', color:C.Primary }}>↑</button>
      </div>

      <div style={{ flex:1, overflowY:'auto', padding:'0 16px 24px' }}>

        {/* Status Banner */}
        <div style={{ background:C.PrimaryLight, borderRadius:14, padding:'12px 16px', margin:'14px 0', display:'flex', alignItems:'center', justifyContent:'space-between' }}>
          <div style={{ fontSize:'13px', fontWeight:600, color:C.Primary }}>Upcoming Booking</div>
          <StatusBadge status="upcoming" />
        </div>

        {/* AI Decision Card */}
        <div style={{ background:C.PrimaryLight, borderLeft:`4px solid ${C.Primary}`, borderRadius:14, padding:'14px 16px', marginBottom:12 }}>
          <div style={{ fontSize:'11px', fontWeight:700, color:C.Primary, textTransform:'uppercase', letterSpacing:'0.6px', marginBottom:8, display:'flex', alignItems:'center', gap:6 }}>
            <span>🤖</span> Kyun chuna? — AI Decision
          </div>
          <div style={{ fontSize:'13px', color:C.TextSecondary, lineHeight:'1.65', fontStyle:'italic', marginBottom:8 }}>
            "{provider.reasoning}"
          </div>
          <div style={{ fontSize:'11px', color:C.TextSecondary, fontFamily:'monospace' }}>
            Score: <strong style={{ color:C.TextPrimary }}>12.16</strong> · Ranked <strong style={{ color:C.TextPrimary }}>#1</strong> of 3 providers
          </div>
        </div>

        {/* Provider Card */}
        <div style={{ background:C.Surface, borderRadius:16, padding:'14px', boxShadow:'0 2px 8px rgba(0,0,0,0.06)', marginBottom:12 }}>
          <div style={{ display:'flex', alignItems:'center', gap:12, marginBottom:12 }}>
            <Avatar name={provider.name} size={46} />
            <div>
              <div style={{ fontSize:'14px', fontWeight:700, color:C.TextPrimary }}>{provider.name}</div>
              <div style={{ fontSize:'12px', color:C.TextSecondary }}>{provider.service} · {provider.distanceKm}km · {provider.experienceYears} yrs</div>
              <div style={{ display:'flex', alignItems:'center', gap:3, marginTop:3 }}>
                <span style={{ color:C.Warning }}>★</span>
                <span style={{ fontSize:'12px', fontWeight:600, color:C.TextPrimary }}>{provider.rating}</span>
              </div>
            </div>
          </div>
          <div style={{ display:'flex', gap:8 }}>
            <button style={{ flex:1, height:40, borderRadius:10, border:`1.5px solid ${C.Success}`, background:'transparent', color:C.Success, fontSize:'12px', fontWeight:600, cursor:'pointer' }}>📞 Call</button>
            <button style={{ flex:1, height:40, borderRadius:10, border:`1.5px solid ${C.Border}`, background:'transparent', color:C.TextPrimary, fontSize:'12px', fontWeight:600, cursor:'pointer' }}>💬 WhatsApp</button>
          </div>
        </div>

        {/* Map */}
        <div style={{ marginBottom:12 }}><MapPlaceholder /></div>

        {/* Appointment Details */}
        <div style={{ background:C.Surface, borderRadius:16, padding:'14px', boxShadow:'0 2px 8px rgba(0,0,0,0.06)', marginBottom:12 }}>
          <div style={{ fontSize:'13px', fontWeight:700, color:C.TextPrimary, marginBottom:10 }}>📅 Appointment Details</div>
          {[['Booking ID',<span style={{fontFamily:'monospace',fontSize:'11px'}}>{appt.bookingId}</span>],['Time',appt.timeDisplay],['Location',appt.address],['Cost',`PKR ${appt.costPerHour}/hr`]].map(([k,v],i,a)=>(
            <div key={k} style={{ display:'flex', justifyContent:'space-between', paddingBottom:8, marginBottom:i<a.length-1?8:0, borderBottom:i<a.length-1?`1px solid ${C.Border}`:'none' }}>
              <span style={{ fontSize:'12px', color:C.TextSecondary }}>{k}</span>
              <span style={{ fontSize:'12px', fontWeight:600, color:C.TextPrimary }}>{v}</span>
            </div>
          ))}
        </div>

        {/* Next Steps */}
        <div style={{ fontSize:'14px', fontWeight:700, color:C.TextPrimary, marginBottom:10 }}>Agle Steps</div>
        <div style={{ display:'flex', flexDirection:'column', gap:8, marginBottom:12 }}>
          {steps.map(s => <NextStepCard key={s.id} step={s} />)}
        </div>

        {/* Follow-up Info */}
        <div style={{ background:C.Surface, borderRadius:16, padding:'14px', boxShadow:'0 2px 8px rgba(0,0,0,0.06)', marginBottom:12 }}>
          <div style={{ fontSize:'13px', fontWeight:700, color:C.TextPrimary, marginBottom:10 }}>🔔 Follow-up Info</div>
          {[['Reminder set','✅ Yes'],['Reminder time','09:30 AM'],['Status','Booking Confirmed'],['Completion','Pending']].map(([k,v],i,a)=>(
            <div key={k} style={{ display:'flex', justifyContent:'space-between', paddingBottom:8, marginBottom:i<a.length-1?8:0, borderBottom:i<a.length-1?`1px solid ${C.Border}`:'none' }}>
              <span style={{ fontSize:'12px', color:C.TextSecondary }}>{k}</span>
              <span style={{ fontSize:'12px', fontWeight:600, color:C.TextPrimary }}>{v}</span>
            </div>
          ))}
        </div>

        {/* Agent Trace Accordion */}
        <div style={{ background:C.Surface, borderRadius:16, boxShadow:'0 2px 8px rgba(0,0,0,0.06)', overflow:'hidden', marginBottom:8 }}>
          <button onClick={() => setTraceOpen(o => !o)} style={{ width:'100%', display:'flex', alignItems:'center', justifyContent:'space-between', padding:'14px 16px', background:'transparent', border:'none', cursor:'pointer' }}>
            <div style={{ display:'flex', alignItems:'center', gap:8 }}>
              <span style={{ fontSize:'14px' }}>🔍</span>
              <span style={{ fontSize:'13px', fontWeight:700, color:C.TextPrimary }}>AI Agent Log</span>
            </div>
            <span style={{ fontSize:'18px', color:C.TextSecondary, transition:'transform 0.3s', transform: traceOpen ? 'rotate(180deg)' : 'rotate(0deg)' }}>▾</span>
          </button>
          {traceOpen && (
            <div style={{ padding:'0 12px 14px', display:'flex', flexDirection:'column', gap:4, animation:'fadeSlideIn 0.25s ease-out', borderTop:`1px solid ${C.Border}`, paddingTop:12 }}>
              {trace.map((item, i) => (
                <div key={item.stage}>
                  {i > 0 && <div style={{ width:2, height:5, background:C.Success, margin:'0 auto', opacity:0.4 }} />}
                  <TraceRow item={item} />
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

// ══════════════════════════════════════════════════════
// BOOKINGS SCREEN
// ══════════════════════════════════════════════════════
function BookingsScreen({ navigate }) {
  const [activeFilter, setFilter] = useState('all');
  const bookings = window.MOCK_BOOKINGS || [];
  const filters = [{ id:'all', label:'Tamam' },{ id:'upcoming', label:'Aane wale' },{ id:'completed', label:'Mukammal' },{ id:'cancelled', label:'Cancel' }];
  const filtered = activeFilter === 'all' ? bookings : bookings.filter(b => b.status === activeFilter);

  return (
    <div style={{ height:'100%', display:'flex', flexDirection:'column', background:C.Background }}>
      {/* AppBar */}
      <div style={{ padding:'12px 16px 0', background:C.Surface, borderBottom:`1px solid ${C.Border}`, flexShrink:0 }}>
        <div style={{ fontSize:'17px', fontWeight:700, color:C.TextPrimary, marginBottom:12 }}>My Bookings</div>
        {/* Filter tabs */}
        <div style={{ display:'flex', gap:0, overflowX:'auto' }}>
          {filters.map(f => (
            <button key={f.id} onClick={() => setFilter(f.id)} style={{
              padding:'8px 14px', border:'none', background:'transparent',
              fontSize:'12px', fontWeight: activeFilter===f.id ? 700 : 400,
              color: activeFilter===f.id ? C.Primary : C.TextSecondary,
              borderBottom: activeFilter===f.id ? `2.5px solid ${C.Primary}` : '2.5px solid transparent',
              cursor:'pointer', whiteSpace:'nowrap', transition:'all 0.2s',
            }}>
              {f.label}
            </button>
          ))}
        </div>
      </div>

      <div style={{ flex:1, overflowY:'auto', padding:'12px 16px' }}>
        {filtered.length === 0 ? (
          <div style={{ display:'flex', flexDirection:'column', alignItems:'center', justifyContent:'center', height:'60%', gap:14 }}>
            <span style={{ fontSize:'48px' }}>📋</span>
            <div style={{ fontSize:'15px', fontWeight:700, color:C.TextPrimary }}>Abhi tak koi booking nahi</div>
            <div style={{ fontSize:'13px', color:C.TextSecondary }}>Apni pehli service book karein!</div>
            <button onClick={() => navigate('home')} style={{ marginTop:8, padding:'11px 28px', borderRadius:13, border:'none', background:C.Primary, color:'#fff', fontSize:'14px', fontWeight:600, cursor:'pointer' }}>
              Service Dhundho
            </button>
          </div>
        ) : (
          <div style={{ display:'flex', flexDirection:'column', gap:10 }}>
            {filtered.map(b => (
              <div key={b.id} onClick={() => navigate('booking-detail')} style={{ background:C.Surface, borderRadius:16, padding:'14px', boxShadow:'0 1px 6px rgba(0,0,0,0.06)', cursor:'pointer', display:'flex', alignItems:'flex-start', gap:12, transition:'box-shadow 0.2s' }}>
                <div style={{ width:44, height:44, borderRadius:12, background:C.PrimaryLight, display:'flex', alignItems:'center', justifyContent:'center', fontSize:'20px', flexShrink:0 }}>
                  {b.emoji}
                </div>
                <div style={{ flex:1, minWidth:0 }}>
                  <div style={{ display:'flex', alignItems:'flex-start', justifyContent:'space-between', gap:8, marginBottom:4 }}>
                    <div style={{ fontSize:'13px', fontWeight:700, color:C.TextPrimary }}>{b.service}</div>
                    <StatusBadge status={b.status} />
                  </div>
                  <div style={{ fontSize:'12px', color:C.TextSecondary, marginBottom:2 }}>{b.provider} · ★{b.rating}</div>
                  <div style={{ fontSize:'11px', color:C.TextSecondary }}>{b.time}</div>
                  <div style={{ fontSize:'11px', color:C.TextSecondary }}>📍 {b.address}</div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

// ══════════════════════════════════════════════════════
// PROFILE SCREEN
// ══════════════════════════════════════════════════════
function ProfileScreen() {
  const rows = [
    ['👤','Ali Raza','ali@example.com'],
  ];
  const menuItems = [
    { icon:'🔔', label:'Notifications', sub:'Reminders on' },
    { icon:'🌐', label:'Language',      sub:'Roman Urdu' },
    { icon:'📍', label:'Saved Locations', sub:'2 saved' },
    { icon:'🔐', label:'Privacy',       sub:'' },
    { icon:'ℹ️', label:'About KhidmatAI', sub:'v1.0' },
  ];

  return (
    <div style={{ height:'100%', display:'flex', flexDirection:'column', background:C.Background }}>
      {/* Header */}
      <div style={{ background:C.Surface, borderBottom:`1px solid ${C.Border}`, padding:'14px 16px', flexShrink:0 }}>
        <div style={{ fontSize:'17px', fontWeight:700, color:C.TextPrimary }}>Profile</div>
      </div>

      <div style={{ flex:1, overflowY:'auto', padding:'20px 16px' }}>
        {/* User card */}
        <div style={{ background:C.Surface, borderRadius:18, padding:'20px', boxShadow:'0 2px 10px rgba(0,0,0,0.07)', marginBottom:20, display:'flex', flexDirection:'column', alignItems:'center', gap:10 }}>
          <div style={{ width:70, height:70, borderRadius:'50%', background:`linear-gradient(135deg, ${C.Primary}, ${C.PrimaryDark})`, display:'flex', alignItems:'center', justifyContent:'center', fontSize:'28px', fontWeight:700, color:'#fff' }}>
            A
          </div>
          <div style={{ textAlign:'center' }}>
            <div style={{ fontSize:'16px', fontWeight:700, color:C.TextPrimary }}>Ali Raza</div>
            <div style={{ fontSize:'12px', color:C.TextSecondary, marginTop:2 }}>+92 300 1234567</div>
          </div>
          <div style={{ display:'flex', gap:24, marginTop:4 }}>
            {[['3','Bookings'],['4.8','My Rating'],['100%','Verified']].map(([val,lbl]) => (
              <div key={lbl} style={{ textAlign:'center' }}>
                <div style={{ fontSize:'16px', fontWeight:700, color:C.Primary }}>{val}</div>
                <div style={{ fontSize:'10px', color:C.TextSecondary }}>{lbl}</div>
              </div>
            ))}
          </div>
        </div>

        {/* Menu */}
        <div style={{ background:C.Surface, borderRadius:16, overflow:'hidden', boxShadow:'0 1px 6px rgba(0,0,0,0.05)' }}>
          {menuItems.map((item, i) => (
            <div key={item.label} style={{ display:'flex', alignItems:'center', padding:'14px 16px', borderBottom: i<menuItems.length-1?`1px solid ${C.Border}`:'none', cursor:'pointer' }}>
              <span style={{ fontSize:'18px', marginRight:12 }}>{item.icon}</span>
              <div style={{ flex:1 }}>
                <div style={{ fontSize:'13px', fontWeight:600, color:C.TextPrimary }}>{item.label}</div>
                {item.sub && <div style={{ fontSize:'11px', color:C.TextSecondary, marginTop:1 }}>{item.sub}</div>}
              </div>
              <span style={{ color:C.Border, fontSize:'16px' }}>›</span>
            </div>
          ))}
        </div>

        <button style={{ width:'100%', marginTop:20, height:46, borderRadius:13, border:`1.5px solid ${C.Error}`, background:'transparent', color:C.Error, fontSize:'14px', fontWeight:600, cursor:'pointer' }}>
          Sign Out
        </button>
      </div>
    </div>
  );
}

Object.assign(window, {
  ResultSuccessScreen, ResultUnavailableScreen,
  BookingDetailScreen, BookingsScreen, ProfileScreen,
});
