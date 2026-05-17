// KhidmatAI Screens 1: Onboarding · Home · Voice · Processing
const { useState, useEffect, useRef } = React;
const C = window.KT;

// ══════════════════════════════════════════════════════
// ONBOARDING SCREEN
// ══════════════════════════════════════════════════════
function OnboardingScreen({ navigate }) {
  return (
    <div style={{ height:'100%', display:'flex', flexDirection:'column', background:C.Background, padding:'0 24px' }}>
      {/* Illustration */}
      <div style={{ flex:1, display:'flex', alignItems:'center', justifyContent:'center', paddingTop:24 }}>
        <svg width="220" height="200" viewBox="0 0 220 200" fill="none">
          {/* City grid base */}
          <rect x="10" y="110" width="200" height="70" rx="6" fill={C.PrimaryLight}/>
          {/* Buildings */}
          <rect x="22" y="118" width="44" height="42" rx="4" fill={C.Primary} opacity="0.22"/>
          <rect x="74" y="124" width="32" height="36" rx="4" fill={C.Primary} opacity="0.3"/>
          <rect x="114" y="114" width="50" height="46" rx="4" fill={C.Primary} opacity="0.2"/>
          <rect x="172" y="120" width="30" height="40" rx="4" fill={C.Primary} opacity="0.25"/>
          {/* Roads */}
          <rect x="10" y="148" width="200" height="6" rx="3" fill={C.Border}/>
          <rect x="68" y="110" width="5" height="70" rx="2" fill={C.Border}/>
          <rect x="110" y="110" width="5" height="70" rx="2" fill={C.Border}/>
          <rect x="165" y="110" width="5" height="70" rx="2" fill={C.Border}/>
          {/* Pin shadow */}
          <ellipse cx="110" cy="174" rx="18" ry="5" fill={C.Primary} opacity="0.18"/>
          {/* Map pin body */}
          <path d="M110 18 C88 18 70 36 70 58 C70 88 110 130 110 130 C110 130 150 88 150 58 C150 36 132 18 110 18Z" fill={C.Primary}/>
          {/* Inner circle */}
          <circle cx="110" cy="58" r="16" fill="white"/>
          <circle cx="110" cy="58" r="9" fill={C.Primary} opacity="0.55"/>
          {/* Sparkles */}
          <circle cx="60" cy="40" r="3" fill={C.Warning} opacity="0.7"/>
          <circle cx="160" cy="30" r="2.5" fill={C.Success} opacity="0.6"/>
          <circle cx="155" cy="75" r="2" fill={C.Primary} opacity="0.5"/>
        </svg>
      </div>

      {/* Title */}
      <div style={{ textAlign:'center', paddingBottom:32 }}>
        <div style={{ fontSize:'22px', fontWeight:700, color:C.TextPrimary, marginBottom:10, lineHeight:'1.3' }}>
          Pehle apni location batayein
        </div>
        <div style={{ fontSize:'14px', color:C.TextSecondary, lineHeight:'1.65' }}>
          Taake hum aapke qareeb ke best service providers dhundh sakein
        </div>
      </div>

      {/* CTA */}
      <div style={{ paddingBottom:44 }}>
        <button onClick={() => navigate('home')} style={{
          width:'100%', height:52, borderRadius:13, background:C.Primary,
          color:'#fff', border:'none', fontSize:'15px', fontWeight:600,
          cursor:'pointer', display:'flex', alignItems:'center', justifyContent:'center', gap:8,
          boxShadow:`0 4px 16px ${C.Primary}55`,
        }}>
          <span>📍</span> Location use karne ki ijazat dein
        </button>
        <div style={{ textAlign:'center', marginTop:11, fontSize:'11px', color:C.TextSecondary, lineHeight:'1.6' }}>
          Sirf service matching ke liye use hogi.<br/>Koi data share nahi hoga.
        </div>
        <button onClick={() => navigate('home')} style={{
          display:'block', width:'100%', marginTop:14, background:'transparent',
          border:'none', color:C.TextSecondary, fontSize:'13px', cursor:'pointer', padding:'6px',
        }}>
          Skip for now
        </button>
      </div>
    </div>
  );
}

// ══════════════════════════════════════════════════════
// HOME SCREEN
// ══════════════════════════════════════════════════════
function HomeScreen({ navigate, appState, setAppState }) {
  const [query, setQuery]       = useState(appState.query || '');
  const [urgency, setUrgency]   = useState(appState.urgency || 'medium');
  const [language, setLanguage] = useState(appState.language || 'en');
  const [showSheet, setShowSheet]   = useState(false);
  const [isSubmitting, setSubmitting] = useState(false);
  const [shakeInput, setShakeInput]   = useState(false);

  const isEmergency = urgency === 'emergency';

  const quickServices = [
    { label:'❄️ AC Tech',      phrase:'Mujhe AC technician chahiye'                  },
    { label:'🔧 Plumber',      phrase:'Mujhe plumber chahiye, pipe leak hai'         },
    { label:'⚡ Electrician', phrase:'Bijli ki problem hai, electrician chahiye'    },
    { label:'📚 Tutor',        phrase:'Bacche ko tutor chahiye math ke liye'         },
    { label:'💅 Beautician',   phrase:'Ghar par beautician chahiye'                  },
    { label:'🏠 Carpenter',    phrase:'Carpenter chahiye furniture repair ke liye'   },
  ];

  const urgencies = [
    { id:'low',       label:'🟢 Low'       },
    { id:'medium',    label:'🟡 Medium'    },
    { id:'high',      label:'🔴 High'      },
    { id:'emergency', label:'🚨 Emergency' },
  ];

  const handleSubmit = () => {
    if (!query.trim()) {
      setShakeInput(true);
      setTimeout(() => setShakeInput(false), 500);
      return;
    }
    setSubmitting(true);
    setAppState(prev => ({ ...prev, query, urgency, language }));
    setTimeout(() => navigate('processing'), 650);
  };

  return (
    <div style={{ height:'100%', display:'flex', flexDirection:'column', background: isEmergency ? C.EmergencyBg : C.Background, transition:'background 0.5s ease', position:'relative' }}>

      {/* AppBar */}
      <div style={{ display:'flex', alignItems:'center', justifyContent:'space-between', padding:'10px 16px 8px', background:C.Surface, borderBottom:`1px solid ${C.Border}`, flexShrink:0 }}>
        <div style={{ display:'flex', alignItems:'center', gap:8 }}>
          <div style={{ width:30, height:30, borderRadius:9, background:`linear-gradient(135deg, ${C.Primary}, ${C.PrimaryDark})`, display:'flex', alignItems:'center', justifyContent:'center' }}>
            <span style={{ fontSize:'15px' }}>✦</span>
          </div>
          <span style={{ fontSize:'16px', fontWeight:700, color:C.TextPrimary, letterSpacing:'-0.3px' }}>KhidmatAI</span>
        </div>
        <LanguageToggle selected={language} onChange={setLanguage} />
      </div>

      {/* Scrollable body */}
      <div style={{ flex:1, overflowY:'auto', padding:'14px 16px', display:'flex', flexDirection:'column', gap:0 }}>

        {/* Emergency banner */}
        {isEmergency && (
          <div style={{ background:C.ErrorLight, borderLeft:`4px solid ${C.Error}`, borderRadius:12, padding:'10px 14px', marginBottom:12, animation:'fadeSlideIn 0.3s ease-out' }}>
            <div style={{ fontSize:'12px', fontWeight:700, color:C.Error, marginBottom:3 }}>⚠️ Emergency Mode Active</div>
            <div style={{ fontSize:'11px', color:C.TextSecondary, lineHeight:'1.5' }}>
              Sirf genuine emergencies ke liye use karein. Misuse se bachein.
            </div>
          </div>
        )}

        {/* Greeting */}
        <div style={{ marginBottom:16 }}>
          <div style={{ fontSize:'20px', fontWeight:700, color:C.TextPrimary }}>Assalam o Alaikum! 👋</div>
          <div style={{ fontSize:'13px', color:C.TextSecondary, marginTop:3 }}>Aaj kya chahiye aapko?</div>
        </div>

        {/* Input Card */}
        <div style={{
          background:C.Surface, borderRadius:16, padding:'14px',
          boxShadow: shakeInput ? `0 0 0 2px ${C.Error}` : '0 2px 10px rgba(0,0,0,0.08)',
          marginBottom:10, animation: shakeInput ? 'shakeX 0.4s ease-out' : 'none',
          border: `1.5px solid ${shakeInput ? C.Error : 'transparent'}`,
          transition:'box-shadow 0.2s, border 0.2s',
        }}>
          <div style={{ display:'flex', gap:10, alignItems:'flex-start' }}>
            <textarea value={query} onChange={e => setQuery(e.target.value.slice(0,300))}
              placeholder={'Apni zaroorat likhen...\nUrdu, Roman Urdu ya English'}
              style={{ flex:1, border:'none', outline:'none', resize:'none', fontSize:'14px', color:C.TextPrimary, background:'transparent', height:76, fontFamily:'Inter,sans-serif', lineHeight:'1.55', paddingTop:2 }}
            />
            <button onClick={() => navigate('voice')} style={{ width:40, height:40, borderRadius:'50%', background:C.PrimaryLight, border:'none', cursor:'pointer', display:'flex', alignItems:'center', justifyContent:'center', flexShrink:0, fontSize:'17px' }}>
              🎤
            </button>
          </div>
          <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', borderTop:`1px solid ${C.Border}`, paddingTop:8, marginTop:8 }}>
            <span style={{ fontSize:'11px', fontWeight:500, color: query.length>=300 ? C.Error : query.length>=250 ? C.Warning : C.TextSecondary }}>
              {query.length} / 300
            </span>
            {query && (
              <button onClick={() => setQuery('')} style={{ background:'none', border:'none', cursor:'pointer', color:C.TextSecondary, fontSize:'11px', padding:'2px 6px' }}>
                ✕ Clear
              </button>
            )}
          </div>
        </div>

        {/* Location Row */}
        <div onClick={() => setShowSheet(true)} style={{ display:'flex', alignItems:'center', justifyContent:'space-between', background:C.Surface, borderRadius:13, padding:'11px 14px', border:`1px solid ${C.Border}`, marginBottom:16, cursor:'pointer' }}>
          <div style={{ display:'flex', alignItems:'center', gap:8 }}>
            <span style={{ fontSize:'16px' }}>📍</span>
            <span style={{ fontSize:'13px', color:C.TextPrimary, fontWeight:500 }}>G-13, Islamabad</span>
          </div>
          <span style={{ fontSize:'12px', color:C.Primary, fontWeight:600 }}>Change</span>
        </div>

        {/* Urgency */}
        <div style={{ marginBottom:16 }}>
          <div style={{ fontSize:'11px', color:C.TextSecondary, fontWeight:600, textTransform:'uppercase', letterSpacing:'0.5px', marginBottom:9 }}>Kitni zaroorat hai?</div>
          <div style={{ display:'flex', gap:7, overflowX:'auto', paddingBottom:2 }}>
            {urgencies.map(u => {
              const isSelected = urgency === u.id;
              const isEmg = u.id === 'emergency';
              return (
                <button key={u.id} onClick={() => setUrgency(u.id)} style={{
                  padding:'7px 14px', borderRadius:999, border:'none', flexShrink:0,
                  background: isSelected ? (isEmg ? C.Error : C.Primary) : C.Background,
                  color: isSelected ? '#fff' : C.TextPrimary,
                  fontSize:'12px', fontWeight: isSelected ? 600 : 400,
                  cursor:'pointer', transition:'all 0.2s', whiteSpace:'nowrap',
                  animation: isSelected && isEmg ? 'emergencyPulse 0.8s ease-in-out infinite' : 'none',
                  boxShadow: isSelected ? (isEmg ? `0 0 0 3px ${C.Error}33` : `0 0 0 3px ${C.Primary}22`) : 'none',
                }}>
                  {u.label}
                </button>
              );
            })}
          </div>
        </div>

        {/* Quick Service Chips */}
        <div style={{ marginBottom:18 }}>
          <div style={{ fontSize:'11px', color:C.TextSecondary, fontWeight:600, textTransform:'uppercase', letterSpacing:'0.5px', marginBottom:9 }}>Kya chahiye?</div>
          <div style={{ display:'flex', gap:8, overflowX:'auto', paddingBottom:4 }}>
            {quickServices.map(s => (
              <button key={s.label} onClick={() => setQuery(s.phrase)} style={{
                padding:'8px 14px', borderRadius:999, flexShrink:0,
                border:`1.5px solid ${C.Border}`, background:C.Surface,
                color:C.TextPrimary, fontSize:'12px', fontWeight:500,
                cursor:'pointer', whiteSpace:'nowrap', transition:'all 0.15s',
              }}>
                {s.label}
              </button>
            ))}
          </div>
        </div>

        {/* Submit */}
        <button onClick={handleSubmit} disabled={isSubmitting} style={{
          width:'100%', height:52, borderRadius:13, border:'none',
          background: isEmergency ? C.Error : (!query.trim() ? `${C.Primary}66` : C.Primary),
          color:'#fff', fontSize:'15px', fontWeight:600,
          cursor: !query.trim() ? 'not-allowed' : 'pointer',
          display:'flex', alignItems:'center', justifyContent:'center', gap:10,
          transition:'background 0.3s',
          boxShadow: query.trim() ? (isEmergency ? `0 4px 18px ${C.Error}55` : `0 4px 18px ${C.Primary}44`) : 'none',
        }}>
          {isSubmitting ? (
            <><AiOrb state="thinking" size={20} /> Processing...</>
          ) : (
            isEmergency ? 'Find Emergency Service 🚨' : 'Find Service  →'
          )}
        </button>

        <div style={{ height:16 }} />
      </div>

      {/* Location Bottom Sheet */}
      {showSheet && (
        <div style={{ position:'absolute', inset:0, background:'rgba(0,0,0,0.5)', zIndex:300, display:'flex', alignItems:'flex-end' }} onClick={() => setShowSheet(false)}>
          <div style={{ width:'100%', background:C.Surface, borderRadius:'20px 20px 0 0', padding:'20px 16px 40px', animation:'slideUp 0.3s ease-out' }} onClick={e => e.stopPropagation()}>
            <div style={{ width:40, height:4, borderRadius:2, background:C.Border, margin:'0 auto 18px' }} />
            <div style={{ fontSize:'15px', fontWeight:700, color:C.TextPrimary, marginBottom:14 }}>📍 Location chunein</div>
            {['G-13, Islamabad','F-10, Islamabad','Blue Area, Islamabad'].map(loc => (
              <div key={loc} onClick={() => setShowSheet(false)} style={{ padding:'13px 4px', borderBottom:`1px solid ${C.Border}`, fontSize:'14px', color:C.TextPrimary, cursor:'pointer', display:'flex', alignItems:'center', gap:10 }}>
                <span>📍</span> {loc}
              </div>
            ))}
            <button onClick={() => setShowSheet(false)} style={{ width:'100%', marginTop:14, padding:'13px', borderRadius:13, border:'none', background:C.Primary, color:'#fff', fontSize:'14px', fontWeight:600, cursor:'pointer' }}>
              📍 Use Current Location
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

// ══════════════════════════════════════════════════════
// VOICE INPUT SCREEN
// ══════════════════════════════════════════════════════
function VoiceInputScreen({ navigate, setAppState }) {
  const [secs, setSecs]   = useState(0);
  const [phase, setPhase] = useState('listening');
  useEffect(() => {
    const t = setInterval(() => setSecs(s => s+1), 1000);
    return () => clearInterval(t);
  }, []);

  const handleUse = () => {
    setPhase('transcribing');
    setTimeout(() => {
      setAppState(prev => ({ ...prev, query:'Mujhe kal subah G-13 mein AC technician chahiye' }));
      navigate('home');
    }, 1100);
  };

  const BARS = 22;
  return (
    <div style={{ height:'100%', display:'flex', flexDirection:'column', alignItems:'center', justifyContent:'center', background:'rgba(16,24,40,0.97)', padding:'0 28px', position:'relative' }}>
      {/* Dismiss handle */}
      <div style={{ position:'absolute', top:16, left:'50%', transform:'translateX(-50%)', width:40, height:4, borderRadius:2, background:'rgba(255,255,255,0.25)' }} />

      {/* Orb */}
      <div style={{ animation:'bobbing 2s ease-in-out infinite', marginBottom:24 }}>
        <AiOrb state="thinking" size={76} />
      </div>

      {/* Status text */}
      <div style={{ textAlign:'center', marginBottom:28 }}>
        {phase === 'listening' ? (
          <>
            <div style={{ fontSize:'20px', fontWeight:700, color:'#fff', marginBottom:8 }}>Bol kar batayein...</div>
            <div style={{ fontSize:'13px', color:'rgba(255,255,255,0.55)' }}>Urdu, Roman Urdu ya English mein bolein</div>
          </>
        ) : (
          <div style={{ fontSize:'16px', color:'rgba(255,255,255,0.75)', animation:'tracePulse 0.6s ease-in-out infinite alternate' }}>
            Transcribing...
          </div>
        )}
      </div>

      {/* Waveform */}
      <div style={{ display:'flex', alignItems:'center', gap:3, height:64, marginBottom:20 }}>
        {Array.from({length:BARS},(_,i) => (
          <div key={i} style={{
            width:3, borderRadius:2, background:C.Primary,
            animationName:'waveBar',
            animationDuration:`${0.4+(i%6)*0.08}s`,
            animationTimingFunction:'ease-in-out',
            animationIterationCount:'infinite',
            animationDirection: i%2===0 ? 'alternate' : 'alternate-reverse',
            height: phase==='listening' ? 4 : 2,
          }} />
        ))}
      </div>

      {/* Timer */}
      <div style={{ fontSize:'13px', color:'rgba(255,255,255,0.4)', marginBottom:40, fontVariantNumeric:'tabular-nums' }}>
        {Math.floor(secs/60)}:{String(secs%60).padStart(2,'0')}
      </div>

      {/* Buttons */}
      <div style={{ display:'flex', gap:12, width:'100%' }}>
        <button onClick={() => navigate('home')} style={{ flex:1, height:50, borderRadius:13, border:`1.5px solid ${C.Error}`, background:'transparent', color:C.Error, fontSize:'14px', fontWeight:600, cursor:'pointer' }}>
          ✕ Cancel
        </button>
        <button onClick={handleUse} style={{ flex:2, height:50, borderRadius:13, border:'none', background:C.Primary, color:'#fff', fontSize:'14px', fontWeight:600, cursor:'pointer', boxShadow:`0 4px 14px ${C.Primary}55` }}>
          ⏹ Stop & Use
        </button>
      </div>
    </div>
  );
}

// ══════════════════════════════════════════════════════
// PROCESSING SCREEN
// ══════════════════════════════════════════════════════
function ProcessingScreen({ navigate, appState }) {
  const isEmergency = appState.urgency === 'emergency';
  const DELAY = isEmergency ? 160 : 370;
  const accent = isEmergency ? C.Error : C.Primary;
  const allStages = (window.MOCK_TRACE || []).map(t => ({ ...t, status:'waiting' }));

  const [stages, setStages]         = useState(allStages);
  const [currentStep, setCurrentStep] = useState(0);
  const [showFlash, setShowFlash]   = useState(false);
  const [confetti, setConfetti]     = useState([]);

  useEffect(() => {
    const total = allStages.length;
    let step = 0;
    const timer = setInterval(() => {
      if (step < total) {
        const s = step;
        setStages(prev => prev.map((item, i) => {
          if (i < s)   return { ...item, status:'completed' };
          if (i === s) return { ...item, status:'pending' };
          return item;
        }));
        setCurrentStep(s+1);
        step++;
      } else {
        // All complete
        setStages(prev => prev.map(item => ({ ...item, status:'completed' })));
        setCurrentStep(total);
        clearInterval(timer);
        setTimeout(() => {
          setShowFlash(true);
          setConfetti(Array.from({length:24},(_,i) => ({
            id:i, x:Math.random()*100,
            color:[C.Success,C.Primary,C.Warning,'#7C3AED'][i%4],
            dur: 0.7+Math.random()*0.9, delay: Math.random()*0.4,
          })));
          setTimeout(() => navigate('result-success'), 1700);
        }, 300);
      }
    }, DELAY);
    return () => clearInterval(timer);
  }, []);

  const progress = allStages.length ? currentStep / allStages.length : 0;

  return (
    <div style={{ height:'100%', display:'flex', flexDirection:'column', background:'#E8EDF5', position:'relative', overflow:'hidden' }}>

      {/* Success flash overlay */}
      {showFlash && (
        <div style={{ position:'absolute', inset:0, zIndex:100, background:C.Success, display:'flex', alignItems:'center', justifyContent:'center', animation:'successFlash 0.7s ease-out forwards' }}>
          <span style={{ fontSize:'64px', animation:'tracePopIn 0.4s ease-out' }}>✅</span>
        </div>
      )}

      {/* Confetti */}
      {confetti.map(c => (
        <div key={c.id} style={{ position:'absolute', top:-10, left:`${c.x}%`, width:7, height:7, borderRadius: c.id%3===0 ? '50%' : 2, background:c.color, zIndex:99, animation:`confettiDrop ${c.dur}s ease-out ${c.delay}s forwards` }} />
      ))}

      <div style={{ flex:1, overflowY:'auto', padding:'24px 16px' }}>

        {/* Header */}
        <div style={{ textAlign:'center', marginBottom:22 }}>
          <div style={{ display:'flex', justifyContent:'center', marginBottom:14, animation:'bobbing 2s ease-in-out infinite' }}>
            <AiOrb state="thinking" size={54} />
          </div>
          <div style={{ fontSize:'17px', fontWeight:700, color:C.TextPrimary, marginBottom:4, lineHeight:'1.3' }}>
            {isEmergency ? '🚨 Emergency — Priority Processing' : 'Agent chal raha hai...'}
          </div>
          <div style={{ fontSize:'12px', color:C.TextSecondary }}>AI is orchestrating your request</div>
        </div>

        {/* Progress bar */}
        <div style={{ marginBottom:22 }}>
          <div style={{ display:'flex', justifyContent:'space-between', fontSize:'11px', color:C.TextSecondary, marginBottom:7, fontWeight:500 }}>
            <span>Stage {currentStep} of {allStages.length}</span>
            <span>{Math.round(progress*100)}%</span>
          </div>
          <div style={{ height:6, background:C.Border, borderRadius:3, overflow:'hidden' }}>
            <div style={{ height:'100%', borderRadius:3, background:accent, width:`${progress*100}%`, transition:'width 0.4s ease' }} />
          </div>
        </div>

        {/* Trace timeline */}
        <div style={{ display:'flex', flexDirection:'column', gap:4 }}>
          {stages.map((stage, i) => (
            <div key={stage.stage}>
              {i > 0 && (
                <div style={{ width:2, height:6, background: stage.status!=='waiting' ? accent : C.Border, margin:'0 auto', transition:'background 0.3s', opacity: stage.status==='waiting' ? 0.3 : 0.6 }} />
              )}
              <TraceRow item={stage} emergency={isEmergency} />
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

Object.assign(window, { OnboardingScreen, HomeScreen, VoiceInputScreen, ProcessingScreen });
