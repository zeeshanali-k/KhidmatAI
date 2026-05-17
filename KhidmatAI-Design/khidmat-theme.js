// KhidmatAI Design Tokens + Mock Data
window.KT = {
  Primary:       '#1A6BFF',
  PrimaryDark:   '#0D4ECC',
  PrimaryLight:  '#EEF4FF',
  Success:       '#12B76A',
  SuccessLight:  '#ECFDF5',
  Warning:       '#F79009',
  WarningLight:  '#FFFAEB',
  Error:         '#F04438',
  ErrorLight:    '#FEF3F2',
  Background:    '#F8F9FC',
  Surface:       '#FFFFFF',
  TextPrimary:   '#101828',
  TextSecondary: '#667085',
  Border:        '#EAECF0',
  EmergencyBg:   'rgba(240,68,56,0.06)',
};

window.MOCK_TRACE = [
  { stage:'intent_detection',      message:'Input Query received and parsed',          status:'completed' },
  { stage:'llm_analysis',          message:'intent=ac_technician language=roman_urdu', status:'completed' },
  { stage:'service_classification',message:'Service Detected: AC_TECHNICIAN',          status:'completed' },
  { stage:'provider_discovery',    message:'Found 3 verified providers near G-13',     status:'completed' },
  { stage:'provider_ranking',      message:'Kamran Khan selected with score 12.16',    status:'completed' },
  { stage:'booking_execution',     message:'Booking BK-1747391234 confirmed in DB',    status:'completed' },
  { stage:'followup',              message:'Reminder scheduled for 09:30 AM',          status:'completed' },
];

window.MOCK_PROVIDER = {
  name:'Kamran Khan', phone:'+923001234567', rating:4.7,
  distanceKm:1.2, experienceYears:8, service:'AC Technician',
  reasoning:'Kamran Khan is the top match with rating 4.7, located 1.2km from you. Ranked #1 of 3 providers with an AI score of 12.16.',
};

window.MOCK_APPOINTMENT = {
  bookingId:'BK-1747391234', timeDisplay:'10:30 AM, 17 May',
  address:'G-13, Islamabad', costPerHour:1500, currency:'PKR',
};

window.MOCK_NEXT_STEPS = [
  { id:1, title:'Provider call karega',  description:'Kamran Khan aapko 15 minutes ke andar call karega.',                    type:'action', action_value:'+923001234567', action_label:'Call Now'    },
  { id:2, title:'Jagah saaf karein',     description:'Service ke liye relevant area clear karein.',                            type:'info',   action_value:null,             action_label:null         },
  { id:3, title:'Reminder mil jayega',   description:'Appointment se 1 ghanta pehle (09:30 AM) aapko reminder milega.',       type:'info',   action_value:'09:30 AM',       action_label:null         },
  { id:4, title:'Booking track karein',  description:'App mein apni booking ka real-time status dekh sakte hain.',            type:'info',   action_value:'BK-1747391234',  action_label:'Track'      },
];

window.MOCK_BOOKINGS = [
  { id:'BK-1747391234', service:'AC Technician', provider:'Kamran Khan', rating:4.7, time:'10:30 AM, 17 May', address:'G-13, Islamabad',     status:'upcoming',  emoji:'❄️' },
  { id:'BK-1747391111', service:'Plumber',       provider:'Ahmed Ali',   rating:4.5, time:'2:00 PM, 15 May',  address:'F-10, Islamabad',     status:'completed', emoji:'🔧' },
  { id:'BK-1747391000', service:'Electrician',   provider:'Usman Baig',  rating:4.8, time:'9:00 AM, 12 May',  address:'Blue Area, Islamabad', status:'completed', emoji:'⚡' },
];
