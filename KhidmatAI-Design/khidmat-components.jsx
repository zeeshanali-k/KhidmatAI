// KhidmatAI Shared Components
const { useState } = React;
const C = window.KT;

// ─── AI Orb ─────────────────────────────────────────────────────────
function AiOrb({ state = 'idle', size = 24 }) {
  const anims = {
    idle:     'orbIdle 2s ease-in-out infinite',
    thinking: 'orbThink 1.2s linear infinite',
    done:     'orbDone 0.4s ease-out forwards',
    error:    'tracePulse 0.5s ease-in-out infinite alternate',
  };
  const bgs = {
    idle:     `conic-gradient(from 0deg, ${C.Primary}, #7B9EFF, ${C.Primary})`,
    thinking: `conic-gradient(from 0deg, ${C.Primary}, ${C.PrimaryDark}, #5B8FFF, ${C.Primary})`,
    done:     `radial-gradient(circle at 40% 40%, #6EE7B7, ${C.Success})`,
    error:    `radial-gradient(circle at 40% 40%, #FDA29B, ${C.Error})`,
  };
  const glows = {
    idle:     `0 0 ${size/3}px ${C.Primary}55`,
    thinking: `0 0 ${size/2}px ${C.Primary}66`,
    done:     `0 0 ${size}px ${C.Success}99`,
    error:    `0 0 ${size/2}px ${C.Error}66`,
  };
  return (
    <div style={{
      width: size, height: size, borderRadius: '50%',
      background: bgs[state] || bgs.idle,
      animation: anims[state] || anims.idle,
      boxShadow: glows[state] || glows.idle,
      flexShrink: 0, display: 'inline-block',
    }} />
  );
}

// ─── Status Badge ────────────────────────────────────────────────────
function StatusBadge({ status }) {
  const cfg = {
    completed: { bg: C.Success,  label: 'Mukammal'  },
    pending:   { bg: C.Warning,  label: 'Jari hai'  },
    failed:    { bg: C.Error,    label: 'Nakam'      },
    upcoming:  { bg: C.Primary,  label: 'Aane wala' },
    confirmed: { bg: C.Success,  label: 'Confirmed'  },
    cancelled: { bg: C.Error,    label: 'Cancelled'  },
  };
  const { bg, label } = cfg[status] || cfg.upcoming;
  return (
    <div style={{
      display: 'inline-flex', alignItems: 'center',
      padding: '2px 8px', borderRadius: 999,
      background: bg, color: '#fff',
      fontSize: '10px', fontWeight: 600, flexShrink: 0,
    }}>
      {label}
    </div>
  );
}

// ─── Trace Row ───────────────────────────────────────────────────────
function TraceRow({ item, emergency = false }) {
  const labels = {
    intent_detection:       'Apki request samjhi',
    llm_analysis:           'AI analysis',
    service_classification: 'Service identify ki',
    urgency_classification: 'Urgency level set',
    provider_discovery:     'Providers dhundhe',
    provider_ranking:       'Best match chuna',
    provider_selection:     'Provider select kiya',
    booking_execution:      'Booking confirm ki',
    followup:               'Reminders set kiye',
  };
  const accent = emergency ? C.Error : C.Primary;
  const isWaiting = item.status === 'waiting';

  const Icon = () => {
    if (item.status === 'completed') return (
      <div style={{ width:20, height:20, borderRadius:'50%', background:C.Success, display:'flex', alignItems:'center', justifyContent:'center', animation:'tracePopIn 0.35s ease-out', flexShrink:0 }}>
        <svg width="10" height="8" viewBox="0 0 10 8" fill="none">
          <path d="M1 4l3 3 5-6" stroke="#fff" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"/>
        </svg>
      </div>
    );
    if (item.status === 'pending') return (
      <div style={{ width:20, height:20, borderRadius:'50%', background:accent, animation:'tracePulse 0.8s ease-in-out infinite alternate', flexShrink:0 }} />
    );
    if (item.status === 'failed') return (
      <div style={{ width:20, height:20, borderRadius:'50%', background:C.Error, display:'flex', alignItems:'center', justifyContent:'center', flexShrink:0 }}>
        <svg width="8" height="8" viewBox="0 0 8 8" fill="none">
          <path d="M1 1l6 6M7 1L1 7" stroke="#fff" strokeWidth="1.8" strokeLinecap="round"/>
        </svg>
      </div>
    );
    return <div style={{ width:20, height:20, borderRadius:'50%', border:`2px solid ${C.Border}`, flexShrink:0 }} />;
  };

  return (
    <div style={{
      display: 'flex', gap: 10, alignItems: 'flex-start',
      padding: '9px 12px', background: C.Surface, borderRadius: 12,
      border: `1.5px solid ${isWaiting ? C.Border : 'rgba(0,0,0,0.09)'}`,
      boxShadow: isWaiting ? 'none' : '0 2px 6px rgba(0,0,0,0.08)',
      opacity: isWaiting ? 0.4 : 1,
      transition: 'opacity 0.3s, box-shadow 0.3s',
    }}>
      <Icon />
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ display:'flex', alignItems:'center', justifyContent:'space-between', gap:8 }}>
          <span style={{ fontSize:'12px', fontWeight:600, color:C.TextPrimary }}>
            {labels[item.stage] || item.stage}
          </span>
          {!isWaiting && <StatusBadge status={item.status} />}
        </div>
        <div style={{ fontSize:'10px', color:C.TextSecondary, marginTop:2, fontFamily:'monospace', overflow:'hidden', textOverflow:'ellipsis', whiteSpace:'nowrap' }}>
          {isWaiting ? 'Waiting...' : item.message}
        </div>
      </div>
    </div>
  );
}

// ─── Next Step Card ──────────────────────────────────────────────────
function NextStepCard({ step, emergency = false }) {
  const isWarning = step.type === 'warning';
  const isAction  = step.type === 'action';
  const isEmergencyCall = emergency && step.action_label === 'Call Now';
  return (
    <div style={{
      background: C.Surface, borderRadius: 14, padding: '12px 14px',
      borderLeft: isWarning ? `4px solid ${C.Warning}` : isEmergencyCall ? `4px solid ${C.Error}` : 'none',
      boxShadow: '0 1px 4px rgba(0,0,0,0.05)',
    }}>
      {isWarning && <span style={{ marginRight:4 }}>⚠️</span>}
      <div style={{ fontSize:'13px', fontWeight:600, color:C.TextPrimary, marginBottom:4 }}>
        {step.title}
      </div>
      <div style={{ fontSize:'12px', color:C.TextSecondary, lineHeight:'1.55' }}>
        {step.description}
      </div>
      {isAction && step.action_label && (
        <button style={{
          display:'block', width:'100%', marginTop:10, padding:'10px',
          borderRadius:10, border:'none',
          background: isEmergencyCall ? C.Error : step.action_label === 'Call Now' ? C.Success : C.Primary,
          color:'#fff', fontSize:'13px', fontWeight:600, cursor:'pointer',
        }}>
          {step.action_label === 'Call Now' ? '📞 ' : ''}{step.action_label}
        </button>
      )}
    </div>
  );
}

// ─── Bottom Nav ──────────────────────────────────────────────────────
function BottomNav({ active, onChange }) {
  const tabs = [
    { id:'home',     label:'Home',     icon:'🏠' },
    { id:'bookings', label:'Bookings', icon:'📋' },
    { id:'profile',  label:'Profile',  icon:'👤' },
  ];
  return (
    <div style={{ display:'flex', height:52 }}>
      {tabs.map(t => (
        <button key={t.id} onClick={() => onChange(t.id)} style={{
          flex:1, display:'flex', flexDirection:'column', alignItems:'center', justifyContent:'center', gap:2,
          border:'none', background:'transparent', cursor:'pointer', padding:'4px 0',
        }}>
          <span style={{ fontSize:'19px' }}>{t.icon}</span>
          <span style={{ fontSize:'10px', fontWeight: active===t.id ? 700 : 400, color: active===t.id ? C.Primary : C.TextSecondary, transition:'color 0.2s' }}>
            {t.label}
          </span>
          {active===t.id && <div style={{ width:16, height:2.5, borderRadius:99, background:C.Primary, marginTop:1 }} />}
        </button>
      ))}
    </div>
  );
}

// ─── Language Toggle ─────────────────────────────────────────────────
function LanguageToggle({ selected, onChange }) {
  const langs = [{ id:'en', label:'EN' }, { id:'ru', label:'RU' }, { id:'ur', label:'اردو' }];
  return (
    <div style={{ display:'flex', background:'#E8ECF0', borderRadius:999, padding:2, gap:2 }}>
      {langs.map(l => (
        <button key={l.id} onClick={() => onChange && onChange(l.id)} style={{
          padding:'3px 9px', borderRadius:999, border:'none',
          background: selected===l.id ? C.Primary : 'transparent',
          color: selected===l.id ? '#fff' : C.TextSecondary,
          fontSize:'10px', fontWeight:600, cursor:'pointer', transition:'all 0.2s',
        }}>
          {l.label}
        </button>
      ))}
    </div>
  );
}

// ─── Avatar ──────────────────────────────────────────────────────────
function Avatar({ name, size = 48 }) {
  const palette = ['#1A6BFF','#12B76A','#F79009','#7C3AED','#DB2777','#0891B2'];
  const bg = palette[name.charCodeAt(0) % palette.length];
  return (
    <div style={{ width:size, height:size, borderRadius:'50%', background:bg, display:'flex', alignItems:'center', justifyContent:'center', color:'#fff', fontSize:size*0.38, fontWeight:700, flexShrink:0 }}>
      {name.charAt(0).toUpperCase()}
    </div>
  );
}

// ─── Map Placeholder ─────────────────────────────────────────────────
function MapPlaceholder() {
  return (
    <div style={{ background:'#DFE9DF', borderRadius:14, overflow:'hidden', position:'relative', height:160 }}>
      <svg width="100%" height="100%" style={{ position:'absolute', inset:0 }} preserveAspectRatio="none">
        <defs>
          <pattern id="mapgrid" width="28" height="28" patternUnits="userSpaceOnUse">
            <path d="M 28 0 L 0 0 0 28" fill="none" stroke="#C8D8C8" strokeWidth="0.7"/>
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill="#DFE9DF"/>
        <rect width="100%" height="100%" fill="url(#mapgrid)"/>
        <rect x="0" y="72" width="100%" height="10" fill="#C5D5C5" opacity="0.8"/>
        <rect x="33%" y="0" width="8" height="100%" fill="#C5D5C5" opacity="0.6"/>
        <rect x="66%" y="0" width="6" height="100%" fill="#C5D5C5" opacity="0.5"/>
        <rect x="20" y="20" width="55" height="40" rx="4" fill="#B8D4B8" opacity="0.6"/>
        <rect x="85" y="25" width="40" height="35" rx="4" fill="#B8D4B8" opacity="0.5"/>
        <rect x="55" y="90" width="60" height="50" rx="4" fill="#B8D4B8" opacity="0.55"/>
        <rect x="140" y="10" width="70" height="55" rx="4" fill="#B8D4B8" opacity="0.45"/>
      </svg>
      {/* User pin */}
      <div style={{ position:'absolute', left:'28%', top:'55%', transform:'translate(-50%,-100%)', textAlign:'center', filter:'drop-shadow(0 2px 4px rgba(0,0,0,0.3))' }}>
        <div style={{ fontSize:'22px' }}>📍</div>
        <div style={{ fontSize:'8px', background:C.Primary, color:'#fff', borderRadius:4, padding:'1px 5px', whiteSpace:'nowrap', fontWeight:600 }}>You</div>
      </div>
      {/* Provider pin */}
      <div style={{ position:'absolute', left:'66%', top:'40%', transform:'translate(-50%,-100%)', textAlign:'center', filter:'drop-shadow(0 2px 4px rgba(0,0,0,0.3))' }}>
        <div style={{ fontSize:'22px' }}>📌</div>
        <div style={{ fontSize:'8px', background:C.Error, color:'#fff', borderRadius:4, padding:'1px 5px', whiteSpace:'nowrap', fontWeight:600 }}>Provider</div>
      </div>
      {/* Distance badge */}
      <div style={{ position:'absolute', bottom:8, left:'50%', transform:'translateX(-50%)', background:'rgba(16,24,40,0.82)', color:'#fff', fontSize:'11px', fontWeight:600, padding:'4px 14px', borderRadius:999, whiteSpace:'nowrap' }}>
        1.2 km away
      </div>
      <div style={{ position:'absolute', bottom:8, right:10 }}>
        <button style={{ padding:'5px 10px', borderRadius:8, background:'#fff', border:'none', fontSize:'10px', fontWeight:600, color:C.Primary, cursor:'pointer', boxShadow:'0 1px 4px rgba(0,0,0,0.18)' }}>
          Directions ↗
        </button>
      </div>
    </div>
  );
}

// ─── Section Header ──────────────────────────────────────────────────
function SectionHeader({ children }) {
  return (
    <div style={{ fontSize:'15px', fontWeight:700, color:C.TextPrimary, marginBottom:10, marginTop:18 }}>
      {children}
    </div>
  );
}

// ─── Divider line ────────────────────────────────────────────────────
function Divider() {
  return <div style={{ height:1, background:C.Border, margin:'12px 0' }} />;
}

Object.assign(window, {
  AiOrb, StatusBadge, TraceRow, NextStepCard, BottomNav,
  LanguageToggle, Avatar, MapPlaceholder, SectionHeader, Divider,
});
