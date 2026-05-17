// KhidmatAI — Main App + Tweaks
const { useState } = React;
const C = window.KT;

function App() {
  const defaults = (typeof TWEAK_DEFAULTS !== 'undefined') ? TWEAK_DEFAULTS : { scenario:'success', emergency:false, speed:'normal' };
  const [tweaks, setTweak] = window.useTweaks ? window.useTweaks(defaults) : [defaults, () => {}];

  const [screen, setScreen]       = useState('onboarding');
  const [activeTab, setActiveTab] = useState('home');
  const [appState, setAppState]   = useState({
    query: '', urgency: tweaks.emergency ? 'emergency' : 'medium',
    language: 'en', location: 'G-13, Islamabad',
  });

  const navigate = (target) => {
    // Demo scenario override at result stage
    if (target === 'result-success' && tweaks.scenario === 'unavailable') {
      setScreen('result-unavailable');
      return;
    }
    setScreen(target);
    if (['home','bookings','profile'].includes(target)) setActiveTab(target);
  };

  const handleTabChange = (tab) => {
    setActiveTab(tab);
    setScreen(tab);
  };

  const noNav  = ['onboarding','voice','processing'].includes(screen);
  const hasNav = !noNav;

  const screenProps = { navigate, appState, setAppState };

  const renderScreen = () => {
    switch(screen) {
      case 'onboarding':       return <OnboardingScreen {...screenProps} />;
      case 'home':             return <HomeScreen {...screenProps} />;
      case 'voice':            return <VoiceInputScreen {...screenProps} />;
      case 'processing':       return <ProcessingScreen {...screenProps} />;
      case 'result-success':   return <ResultSuccessScreen {...screenProps} />;
      case 'result-unavailable': return <ResultUnavailableScreen {...screenProps} />;
      case 'booking-detail':   return <BookingDetailScreen {...screenProps} />;
      case 'bookings':         return <BookingsScreen {...screenProps} />;
      case 'profile':          return <ProfileScreen {...screenProps} />;
      default:                 return <HomeScreen {...screenProps} />;
    }
  };

  return (
    <div style={{ display:'flex', alignItems:'center', justifyContent:'center', minHeight:'100vh', padding:'20px', background:'#080c14', position:'relative' }}>
      {/* Background glow */}
      <div style={{ position:'fixed', top:'20%', left:'50%', transform:'translateX(-50%)', width:600, height:400, background:`radial-gradient(ellipse, ${C.Primary}18 0%, transparent 70%)`, pointerEvents:'none', zIndex:0 }} />

      {/* Phone frame */}
      <IOSDevice width={390} height={844} dark={false}>
        {/* 65px status-bar spacer so our content starts below the clock/icons */}
        <div style={{ height:'100%', display:'flex', flexDirection:'column', overflow:'hidden', fontFamily:"'Inter', sans-serif", WebkitFontSmoothing:'antialiased' }}>
          <div style={{ height:65, flexShrink:0 }} />
          <div style={{ flex:1, overflow:'hidden', display:'flex', flexDirection:'column' }}>
            {renderScreen()}
          </div>
          {/* 28px padding clears the home-indicator pill */}
          {hasNav && (
            <div style={{ paddingBottom:28, background:C.Surface, borderTop:`1px solid ${C.Border}`, flexShrink:0 }}>
              <BottomNav active={activeTab} onChange={handleTabChange} />
            </div>
          )}
        </div>
      </IOSDevice>

      {/* Tweaks Panel */}
      {window.TweaksPanel && (
        <TweaksPanel>
          <TweakSection title="Demo Scenario">
            <TweakRadio id="scenario" label="Result" options={['success','unavailable']} value={tweaks.scenario} onChange={v => setTweak('scenario', v)} />
          </TweakSection>
          <TweakSection title="Settings">
            <TweakToggle id="emergency" label="Emergency Mode" value={tweaks.emergency} onChange={v => { setTweak('emergency',v); setAppState(p=>({...p, urgency: v?'emergency':'medium'})); }} />
            <TweakRadio id="speed" label="Anim Speed" options={['normal','fast']} value={tweaks.speed} onChange={v => setTweak('speed',v)} />
          </TweakSection>
          <TweakSection title="Navigate To">
            {[['🏠 Home','home'],['⚙️ Processing','processing'],['✅ Result','result-success'],['📋 Bookings','bookings'],['📄 Detail','booking-detail']].map(([lbl,tgt]) => (
              <TweakButton key={tgt} label={lbl} onClick={() => navigate(tgt)} />
            ))}
          </TweakSection>
        </TweaksPanel>
      )}
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
